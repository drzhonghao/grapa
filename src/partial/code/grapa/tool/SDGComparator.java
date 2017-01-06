package partial.code.grapa.tool;

import java.util.Hashtable;
import java.util.Iterator;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.NodeFinder;

import com.ibm.wala.cast.java.loader.JavaSourceLoaderImpl.ConcreteJavaMethod;
import com.ibm.wala.ipa.slicer.NormalStatement;
import com.ibm.wala.ipa.slicer.Statement;
import com.ibm.wala.ipa.slicer.Slicer.ControlDependenceOptions;
import com.ibm.wala.ipa.slicer.Slicer.DataDependenceOptions;
import com.ibm.wala.ssa.IR;

import edu.uci.ics.jung.graph.DirectedSparseGraph;
import partial.code.grapa.commit.MethodDelta;
import partial.code.grapa.delta.graph.ChangeGraphBuilder;
import partial.code.grapa.delta.graph.xml.LabelTool;
import partial.code.grapa.delta.graph.xml.XmlEdge;
import partial.code.grapa.delta.graph.xml.XmlNode;
import partial.code.grapa.dependency.graph.SDGwithPredicate;
import partial.code.grapa.mapping.ClientMethod;

public class SDGComparator {

	private IR lir;
	private IR rir;
	private boolean bResolveAst;
	private ASTNode lAst;
	private ASTNode rAst;
	private Hashtable<XmlNode, ASTNode> astTable = new Hashtable<XmlNode, ASTNode>();

	public SDGComparator(IR lir, IR rir, boolean bResolveAst, ClientMethod oldMethod, ClientMethod newMethod) {
		// TODO Auto-generated constructor stub
		this.lir = lir;
		this.rir = rir;
		this.bResolveAst = bResolveAst;
		lAst = oldMethod.ast;
		rAst = newMethod.ast;
	}

	public MethodDelta compare(SDGwithPredicate lfg, SDGwithPredicate rfg) {
		// TODO Auto-generated method stub
		DirectedSparseGraph<XmlNode, XmlEdge> graph = null;
		if(lfg!=null&&rfg!=null){
			LabelTool llt = new LabelTool(); 
			llt.setIR(lir);
			DirectedSparseGraph<XmlNode, XmlEdge> lg = translateSDGToXml(lfg, llt, lAst);
			
			LabelTool rlt = new LabelTool(); 
			rlt.setIR(rir);
			DirectedSparseGraph<XmlNode, XmlEdge> rg = translateSDGToXml(rfg, rlt, rAst);
			
			graph = extractChangeGraph(lg, rg);
			
		}
		DirectedSparseGraph<XmlNode, XmlEdge> deltaGraph = extractDelta(graph); 
		
		MethodDelta md = new MethodDelta(graph, deltaGraph, astTable);
		return md;
	}
	
	private DirectedSparseGraph<XmlNode, XmlEdge> translateSDGToXml(SDGwithPredicate flowGraph, LabelTool lt, ASTNode ast) {
		// TODO Auto-generated method stub
		DirectedSparseGraph<XmlNode, XmlEdge> graph = new DirectedSparseGraph<XmlNode, XmlEdge>(); 
		Hashtable<Statement, XmlNode> table = new Hashtable<Statement, XmlNode>();
		Iterator<Statement> it = flowGraph.iterator();
		while(it.hasNext()){
			Statement statement = it.next();
			XmlNode node = new XmlNode(lt.getVisualLabel(statement));
			table.put(statement, node);
			graph.addVertex(node);
			if(bResolveAst){
				ASTNode astnode = resolveAst(ast,statement);
				if(astnode!=null){
					this.astTable.put(node, astnode);
				}
			}
		}
		
		flowGraph.reConstruct(DataDependenceOptions.NO_BASE_NO_EXCEPTIONS, ControlDependenceOptions.NONE);
		it = flowGraph.iterator();
		while(it.hasNext()){
			Statement s1 = it.next();
		
			Iterator<Statement> nodes = flowGraph.getSuccNodes(s1);
			while(nodes.hasNext()){
				Statement s2 = nodes.next();
				XmlNode from = table.get(s1);
				XmlNode to = table.get(s2);
				graph.addEdge(new XmlEdge(from, to, XmlEdge.DATA_FLOW), from, to);
			}
		}
		
		flowGraph.reConstruct(DataDependenceOptions.NONE, ControlDependenceOptions.FULL);
		it = flowGraph.iterator();
		while(it.hasNext()){
			Statement s1 = it.next();
			Iterator<Statement> nodes = flowGraph.getSuccNodes(s1);
			while(nodes.hasNext()){
				Statement s2 = nodes.next();
				XmlNode from = table.get(s1);
				XmlNode to = table.get(s2);
				XmlEdge edge = graph.findEdge(from, to);
				if(edge==null){
					graph.addEdge(new XmlEdge(from, to, XmlEdge.CONTROL_FLOW), from, to);
				}
			}
		}
		return graph;
	}



	private DirectedSparseGraph<XmlNode, XmlEdge> extractChangeGraph(//zhh
			DirectedSparseGraph<XmlNode, XmlEdge> leftGraph,
			DirectedSparseGraph<XmlNode, XmlEdge> rightGraph) {
		// TODO Auto-generated method stub
		ChangeGraphBuilder builder = new ChangeGraphBuilder(leftGraph,  rightGraph);
		DirectedSparseGraph<XmlNode, XmlEdge> graph = builder.extractChangeGraph();
		return graph;
	}

	private DirectedSparseGraph<XmlNode, XmlEdge> extractDelta(
			DirectedSparseGraph<XmlNode, XmlEdge> graph) {
		// TODO Auto-generated method stub
		DirectedSparseGraph<XmlNode, XmlEdge> deltaGraph = new DirectedSparseGraph<XmlNode, XmlEdge>();
		//add nodes
		for(XmlNode node:graph.getVertices()){
			if(node.bModified){
				deltaGraph.addVertex(node);
			}
		}
		
		for(XmlNode n1:deltaGraph.getVertices()){
			for(XmlNode n2:deltaGraph.getVertices()){
				XmlEdge edge = graph.findEdge(n1, n2);
				if(edge != null){
					deltaGraph.addEdge(edge, n1, n2);
				}			
			}
		}
		return deltaGraph;
	}
	

	private ASTNode resolveAst(ASTNode ast, Statement statement) {
		// TODO Auto-generated method stub
		ASTNode node = null;
		if(statement.getKind() == Statement.Kind.NORMAL){
			int index = ((NormalStatement)statement).getInstructionIndex();
			try {
				ConcreteJavaMethod method = (ConcreteJavaMethod)statement.getNode().getMethod();
				int src_line_number = method.getLineNumber(index);				
				CompilationUnit cu = (CompilationUnit)ast;
			    int startPos = cu.getPosition(src_line_number, 0);
			    int endPos = cu.getPosition(src_line_number+1, 0)-1;
			    NodeFinder finder = new NodeFinder(ast, startPos, endPos);
				 node = finder.getCoveredNode();
			} catch (Exception e ) {
			    System.err.println("it's probably not a source code method (e.g. it's a fakeroot method)");
			    System.err.println(e.getMessage());
			}
		}
		return node;
	}
}
