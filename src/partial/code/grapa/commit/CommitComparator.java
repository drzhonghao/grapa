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
import partial.code.grapa.delta.graph.data.AbstractNode;
import partial.code.grapa.delta.graph.data.Connector;
import partial.code.grapa.delta.graph.data.Edge;
import partial.code.grapa.delta.graph.data.GetInst;
import partial.code.grapa.delta.graph.data.MethodInvoc;
import partial.code.grapa.delta.graph.data.NewInst;
import partial.code.grapa.delta.graph.data.PutInst;
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
import com.ibm.wala.cast.java.ssa.AstJavaInvokeInstruction;
import com.ibm.wala.classLoader.CallSiteReference;
import com.ibm.wala.classLoader.NewSiteReference;
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


import ca.mcgill.cs.swevo.ppa.PPAOptions;
import edu.uci.ics.jung.graph.DirectedSparseGraph;

public class CommitComparator {

	private String pName;
	private String elementListDir;
	private String commitDir;
	private String libDir;
	private String j2seDir;
	private VersionDetector detector;
	private String resultDir;
	
	private DataFlowAnalysisEngine leftEngine;
	private DataFlowAnalysisEngine rightEngine;
	private String otherLibDir;
	private String exclusionsFile;
	private String bugName;

	private boolean bVisited = false;	

	
	public void run() {
		// TODO Auto-generated method stub
		detector = new VersionDetector();
		detector.setProject(pName);
		detector.readElementList(elementListDir);
		File d = new File(commitDir); 
		for(File c:d.listFiles()){
			if(c.isDirectory()){
				bugName = c.getName();
				System.out.println(bugName);
//				if(bVisited){
					analyzeCommit(c);
//					break;
//				}
//				if(bugName.compareTo("963509_DERBY-3844")==0){
//					bVisited = true;
//				}				
			}
		}
		System.out.println("Done!");
	}

	
	private void analyzeCommit(File d) {
		// TODO Auto-generated method stub
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
		
		
		if(pair.left.versions.size()!=0&&pair.right.versions.size()!=0){
			compareVersions(pair, oldfiles, newfiles);
		}
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

	private void compareVersions(VersionPair pair, ArrayList<File> oldfiles,
			ArrayList<File> newfiles) {
		// TODO Auto-generated method stub\
		
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
			File file = new File(resultDir+this.bugName+"/");
			if(!file.exists()){
				file.mkdir();
			}
			AstTreeComparator comparator = new AstTreeComparator(leftTrees, rightTrees);
			Hashtable<ClientMethod, ClientMethod> mps = comparator.extractMappings();
			try {
				compareMethods(mps);			
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else{
			System.out.println("Error:"+bugName);
		}
		
	}


	
	private void compareMethods(Hashtable<ClientMethod, ClientMethod> mps) throws InterruptedException {
		// TODO Auto-generated method stub
		for(ClientMethod oldMethod:mps.keySet()){
			ClientMethod newMethod = mps.get(oldMethod);
			System.out.println(oldMethod.methodName);
			SDGwithPredicate lfg = leftEngine.buildSystemDependencyGraph(oldMethod);
			IR lir = leftEngine.getCurrentIR();
			DirectedSparseGraph<StatementNode, StatementEdge> leftGraph = GraphUtil.translateToJungGraph(lfg);
			writeSDGraph(leftGraph, lir, resultDir + bugName+"/" + "left_"+oldMethod.getTypeName()+"_"+oldMethod.methodName);
			
			SDGwithPredicate rfg = rightEngine.buildSystemDependencyGraph(newMethod);
			IR rir = rightEngine.getCurrentIR();
			DirectedSparseGraph<StatementNode, StatementEdge> rightGraph = GraphUtil.translateToJungGraph(rfg);
			writeSDGraph(rightGraph, rir, resultDir +  bugName+"/" + "right_"+newMethod.getTypeName()+"_"+oldMethod.methodName);
			
			if(leftGraph!=null&&rightGraph!=null){
				DirectedSparseGraph<StatementNode, StatementEdge> graph = compareGraphs(leftGraph, lir, rightGraph, rir);
				if(graph.getVertexCount()>0){
					writeDependencyGraph(graph, lir, rir,  resultDir +  bugName+"/" + "_"+oldMethod.getTypeName()+"_"+oldMethod.methodName);
				}
			}
		}
	}

	private void writeSDGraph(
			DirectedSparseGraph<StatementNode, StatementEdge> graph,IR ir, 
			String filename) {
		// TODO Auto-generated method stub
		GraphUtil.writeGraphXMLFile(graph, ir, filename);
		GraphUtil.writePdfSDGraph(graph, ir, filename);
	}	
	
	private void  writeDependencyGraph(
			DirectedSparseGraph<StatementNode, StatementEdge> graph, IR lir,
			IR rir, String filename) {
		// TODO Auto-generated method stub
		GraphUtil.writeDeltaGraphXMLFile(graph, rir, rir, filename);
		GraphUtil.writePdfDeltaGraph(graph, lir, rir, filename);
	}

	private DirectedSparseGraph<StatementNode, StatementEdge> compareGraphs(
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

	public void setCommitDir(String cd) {
		// TODO Auto-generated method stub
		commitDir = cd+pName+"/";
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
