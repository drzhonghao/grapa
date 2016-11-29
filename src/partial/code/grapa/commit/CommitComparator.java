package partial.code.grapa.commit;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.NodeFinder;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.internal.compiler.lookup.CompilationUnitScope;
import org.eclipse.jdt.internal.core.util.ASTNodeFinder;

import partial.code.grapa.delta.graph.AbstractEdit;
import partial.code.grapa.delta.graph.ChangeGraphBuilder;
import partial.code.grapa.delta.graph.DeleteNode;
import partial.code.grapa.delta.graph.GraphEditScript;
import partial.code.grapa.delta.graph.InsertNode;
import partial.code.grapa.delta.graph.UpdateNode;
import partial.code.grapa.delta.xmlgraph.data.XmlNode;

import partial.code.grapa.dependency.graph.DataFlowAnalysisEngine;
import partial.code.grapa.dependency.graph.DeltaGraphDecorator;
import partial.code.grapa.dependency.graph.DependencyGraphDotUtil;
import partial.code.grapa.dependency.graph.SDGwithPredicate;
import partial.code.grapa.dependency.graph.StatementEdge;
import partial.code.grapa.dependency.graph.StatementNode;
import partial.code.grapa.mapping.AstTreeComparator;
import partial.code.grapa.mapping.ClientMethod;
import partial.code.grapa.tool.GraphUtil;
import partial.code.grapa.tool.SdgDotUtil;
import partial.code.grapa.tool.visual.JGraphTViewer;
import partial.code.grapa.version.detect.VersionDetector;
import partial.code.grapa.version.detect.VersionPair;

import com.ibm.wala.cast.ir.ssa.AstAssertInstruction;
import com.ibm.wala.cast.java.loader.JavaSourceLoaderImpl.ConcreteJavaMethod;
import com.ibm.wala.cast.java.ssa.AstJavaInvokeInstruction;
import com.ibm.wala.classLoader.CallSiteReference;
import com.ibm.wala.classLoader.IMethod;
import com.ibm.wala.classLoader.NewSiteReference;
import com.ibm.wala.classLoader.ShrikeBTMethod;
import com.ibm.wala.examples.drivers.PDFTypeHierarchy;
import com.ibm.wala.ipa.callgraph.CGNode;
import com.ibm.wala.ipa.callgraph.impl.ExplicitCallGraph;
import com.ibm.wala.ipa.cha.IClassHierarchy;
import com.ibm.wala.ipa.slicer.HeapStatement;
import com.ibm.wala.ipa.slicer.NormalStatement;
import com.ibm.wala.ipa.slicer.PDG.Dependency;
import com.ibm.wala.ipa.slicer.ParamCallee;
import com.ibm.wala.ipa.slicer.ParamCaller;
import com.ibm.wala.ipa.slicer.SDG;
import com.ibm.wala.ipa.slicer.Statement;
import com.ibm.wala.ipa.slicer.Slicer.ControlDependenceOptions;
import com.ibm.wala.ipa.slicer.Slicer.DataDependenceOptions;
import com.ibm.wala.ssa.IR;
import com.ibm.wala.ssa.SSAArrayStoreInstruction;
import com.ibm.wala.ssa.SSABinaryOpInstruction;
import com.ibm.wala.ssa.SSACheckCastInstruction;
import com.ibm.wala.ssa.SSAConditionalBranchInstruction;
import com.ibm.wala.ssa.SSAGetInstruction;
import com.ibm.wala.ssa.SSAGotoInstruction;
import com.ibm.wala.ssa.SSAInstanceofInstruction;
import com.ibm.wala.ssa.SSAInstruction;
import com.ibm.wala.ssa.SSANewInstruction;
import com.ibm.wala.ssa.SSAPutInstruction;
import com.ibm.wala.ssa.SSAUnaryOpInstruction;
import com.ibm.wala.types.TypeReference;
import com.ibm.wala.util.WalaException;
import com.ibm.wala.util.graph.Graph;
import com.ibm.wala.viz.DotUtil;
import com.ibm.wala.viz.NodeDecorator;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.StaxDriver;



import edu.uci.ics.jung.graph.DirectedSparseGraph;

public class CommitComparator {

	private String pName;
	private String elementListDir;
	
	private String libDir;
	private String j2seDir;

	private String resultDir;
	
	private DataFlowAnalysisEngine leftEngine;
	private DataFlowAnalysisEngine rightEngine;
	private String otherLibDir;
	private String exclusionsFile;
	private String bugName;
	
	public void setBugName(String bugName) {
		this.bugName = bugName;
	}


	
	public ArrayList<MethodDelta> analyzeCommit(File d, boolean bResolveAst) {
		// TODO Auto-generated method stub
		
		VersionDetector detector = new VersionDetector();
		detector.setProject(pName);
		detector.readElementList(elementListDir);
		File fd = new File(d.getAbsolutePath()+"/from");
		ArrayList<File> oldfiles = new ArrayList<File>();
		for(File f:fd.listFiles()){
			if(f.getName().endsWith(".java")&&f.getName().indexOf("Test")<0){
				oldfiles.add(f);
			}
		}
		fd = new File(d.getAbsolutePath()+"/to");
		ArrayList<File> newfiles = new ArrayList<File>();
		for(File f:fd.listFiles()){
			if(f.getName().endsWith(".java")&&f.getName().indexOf("Test")<0){
				newfiles.add(f);
			}
		}
		VersionPair pair = detector.run(oldfiles, newfiles);
		
		for(int i=oldfiles.size()-1; i>=0; i--){
			File file = oldfiles.get(i);
			if(pair.left.testFiles.contains(file)){
				oldfiles.remove(i);
			}
		}
		
		for(int i=newfiles.size()-1; i>=0; i--){
			File file = newfiles.get(i);
			if(pair.right.testFiles.contains(file)){
				newfiles.remove(i);
			}
		}		
		
		ArrayList<MethodDelta> info = null;
		if(pair.left.versions.size()!=0&&pair.right.versions.size()!=0){
			info = compareVersions(pair, oldfiles, newfiles, bResolveAst);
		}
		return info;
	}

	private void writeToLog(String version, String name) {
		// TODO Auto-generated method stub
		File file = new File(this.resultDir+"/bugNames.txt");
		FileWriter fw = null;
		BufferedWriter writer = null;
		try{
	         fw = new FileWriter(file, true);
	         writer = new BufferedWriter(fw);
	         writer.write(version+"\t"+name);
	         writer.newLine();
	         writer.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }catch (IOException e) {
            e.printStackTrace();
        }finally{
            try {
                writer.close();
                fw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
	}


	public void setDotExe(String dotExe) {
		GraphUtil.dotExe = dotExe;
	}

	public ArrayList<MethodDelta> compareVersions(VersionPair pair, ArrayList<File> oldfiles,
			ArrayList<File> newfiles, boolean bResolveAst) {
		// TODO Auto-generated method stub\
		ArrayList<MethodDelta> methods = new ArrayList<MethodDelta>();
		boolean bLeftSuccess = false;
		ArrayList<ASTNode> leftTrees = null; 
		for(String oldVersion:pair.left.versions){
			System.out.print(oldVersion+",");
			try{
				leftEngine = new DataFlowAnalysisEngine();
				leftEngine.setExclusionsFile(this.exclusionsFile);
				leftTrees = leftEngine.addtoScope("left", pair.left.pTable, j2seDir, libDir, otherLibDir, oldVersion, oldfiles);
				leftEngine.initClassHierarchy();
				bLeftSuccess = true;
			}catch(Exception e){
				e.printStackTrace();
				bLeftSuccess = false;
			}
			if(bLeftSuccess){
				break;
			}
		}
		
		if(!bLeftSuccess){
			System.out.println("Fail to parse the left side project");
		}
		
		boolean bRightSuccess = false;
		System.out.print("->");
		
		ArrayList<ASTNode> rightTrees = null;
		for(String newVersion:pair.right.versions){
			System.out.print(newVersion+",");
			try{
				rightEngine = new DataFlowAnalysisEngine();
				rightEngine.setExclusionsFile(this.exclusionsFile);	
				rightTrees = rightEngine.addtoScope("right", pair.right.pTable, j2seDir, libDir, otherLibDir,  newVersion, newfiles);
				rightEngine.initClassHierarchy();
				bRightSuccess = true;
			}catch(Exception e){
				e.printStackTrace();
				bRightSuccess = false;
			}
			if(bRightSuccess){
				break;
			}
		}
		
		if(!bRightSuccess){
			System.out.println("Fail to parse the right side project");
		}
//		try {
//			Thread.sleep(200);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
		if(bLeftSuccess&&bRightSuccess){
			AstTreeComparator comparator = new AstTreeComparator(leftTrees, rightTrees);
			Hashtable<ClientMethod, ClientMethod> mps = comparator.extractMappings();
			
			for(ClientMethod oldMethod:mps.keySet()){
				ClientMethod newMethod = mps.get(oldMethod);					
				MethodDelta md = compareMethodPair(oldMethod, newMethod, bResolveAst);				
				methods.add(md);
			}
		}else{
			System.out.println("Error:"+bugName);
		}
		return methods;
	}
	
	private MethodDelta compareMethodPair(ClientMethod oldMethod,
			ClientMethod newMethod, boolean bResolveAst) {
		// TODO Auto-generated method stub
		System.out.println(oldMethod.methodName);
		SDGwithPredicate lfg = leftEngine.buildSystemDependencyGraph(oldMethod);
		IR lir = leftEngine.getCurrentIR();
		DirectedSparseGraph<StatementNode, StatementEdge> leftGraph = GraphUtil.translateToJungGraph(lfg);
		
		SDGwithPredicate rfg = rightEngine.buildSystemDependencyGraph(newMethod);
		IR rir = rightEngine.getCurrentIR();
		DirectedSparseGraph<StatementNode, StatementEdge> rightGraph = GraphUtil.translateToJungGraph(rfg);
		
		DirectedSparseGraph<StatementNode, StatementEdge> graph = null;
		if(leftGraph!=null&&rightGraph!=null){
			graph = extractChangeGraph(leftGraph, lir, rightGraph, rir);
			if(bResolveAst){
				resolveAst(oldMethod.ast, newMethod.ast, graph);
			}
		}
		DirectedSparseGraph<StatementNode, StatementEdge> deltaGraph = extractDelta(graph); 
		MethodDelta md = new MethodDelta(oldMethod, newMethod, graph, deltaGraph, lir, rir);
		return md;
	}
	
	private DirectedSparseGraph<StatementNode, StatementEdge> extractDelta(
			DirectedSparseGraph<StatementNode, StatementEdge> graph) {
		// TODO Auto-generated method stub
		DirectedSparseGraph<StatementNode, StatementEdge> deltaGraph = new DirectedSparseGraph<StatementNode, StatementEdge>();
		//add nodes
		for(StatementNode node:graph.getVertices()){
			if(node.bModified){
				deltaGraph.addVertex(node);
			}
		}
		
		for(StatementNode n1:deltaGraph.getVertices()){
			for(StatementNode n2:deltaGraph.getVertices()){
				StatementEdge edge = graph.findEdge(n1, n2);
				if(edge != null){
					deltaGraph.addEdge(edge, n1, n2);
				}			
			}
		}
		return deltaGraph;
	}
	
	private void resolveAst(ASTNode oldAst, ASTNode newAst, DirectedSparseGraph<StatementNode, StatementEdge> graph) {
		// TODO Auto-generated method stub
		for(StatementNode node:graph.getVertices()){
			if(node.side == StatementNode.LEFT){
				resolveAst(oldAst, node);
			}else{
				resolveAst(newAst, node);
			}
		}
	}

	public String getResultDir() {
		return resultDir;
	}


	public String getBugName() {
		return bugName;
	}


	private void resolveAst(ASTNode ast, StatementNode node) {
		// TODO Auto-generated method stub
		if(node.statement.getKind() == Statement.Kind.NORMAL){
			int index = ((NormalStatement)node.statement).getInstructionIndex();
			try {
				ConcreteJavaMethod method = (ConcreteJavaMethod)node.statement.getNode().getMethod();
				int src_line_number = method.getLineNumber(index);				
				node.lineNumber = src_line_number;
			    CompilationUnit cu = (CompilationUnit)ast;
			    int startPos = cu.getPosition(src_line_number, 0);
			    int endPos = cu.getPosition(src_line_number+1, 0)-1;
			    NodeFinder finder = new NodeFinder(ast, startPos, endPos);
				node.node = finder.getCoveredNode();
			} catch (Exception e ) {
			    System.err.println("it's probably not a source code method (e.g. it's a fakeroot method)");
			    System.err.println(e.getMessage());
			}
		}
	}


	public void writeSDGraph(
			DirectedSparseGraph<StatementNode, StatementEdge> graph,IR ir, 
			String filename) {
		// TODO Auto-generated method stub
		GraphUtil.writeGraphXMLFile(graph, ir, filename);
//		GraphUtil.writePdfSDGraph(graph, ir, filename);
	}	
	
	public void  writeDependencyGraph(
			DirectedSparseGraph<StatementNode, StatementEdge> graph, IR lir,
			IR rir, String filename) {
		// TODO Auto-generated method stub
		GraphUtil.writeDeltaGraphXMLFile(graph, rir, rir, filename);
		GraphUtil.writePdfDeltaGraph(graph, lir, rir, filename);
	}
	
	private GraphEditScript extractEditScript(
			DirectedSparseGraph<StatementNode, StatementEdge> leftGraph,
			IR lir, DirectedSparseGraph<StatementNode, StatementEdge> rightGraph, IR rir) {
		// TODO Auto-generated method stub
		GraphEditScript script = new GraphEditScript(leftGraph, lir, rightGraph, rir);

		ArrayList<AbstractEdit> edits = script.extractChanges();
		for(AbstractEdit edit:edits){
			if(edit instanceof UpdateNode||edit instanceof DeleteNode||edit instanceof InsertNode)
			System.out.println(edit);
		}
		System.out.println("---------------------------------------------");
		
		return script;
	}

	private DirectedSparseGraph<StatementNode, StatementEdge> extractChangeGraph(
			DirectedSparseGraph<StatementNode, StatementEdge> leftGraph,
			IR lir, DirectedSparseGraph<StatementNode, StatementEdge> rightGraph, IR rir) {
		// TODO Auto-generated method stub
		ChangeGraphBuilder builder = new ChangeGraphBuilder(leftGraph, lir, rightGraph, rir);
		DirectedSparseGraph<StatementNode, StatementEdge> graph = builder.extractChangeGraph();
		return graph;
	}


	public void setProject(String name) {
		// TODO Auto-generated method stub
		pName = name;	
	}

	public void setElementListDir(String eld) {
		// TODO Auto-generated method stub
		elementListDir = eld+pName+"/";
	}

	

	public void setLibDir(String dir) {
		// TODO Auto-generated method stub
		libDir = dir+pName+"/";
	}

	public void setJ2seDir(String dir) {
		// TODO Auto-generated method stub
		j2seDir = dir;
	}

	public void setResultDir(String dir) {
		// TODO Auto-generated method stub
		resultDir = dir+pName+"/";
	}

	public void setOtherLibDir(String dir) {
		// TODO Auto-generated method stub
		otherLibDir = dir+pName+"/";
	}

	public void setExclusionFile(String file) {
		// TODO Auto-generated method stub
		exclusionsFile = file;
	}

}
