package partial.code.grapa.delta.graph;

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
import partial.code.grapa.dependency.graph.SDGwithPredicate;
import partial.code.grapa.mapping.ClientMethod;
import partial.code.grapa.tool.LabelUtil;

public class SDGComparator {

	private IR lir;
	private IR rir;
	private boolean bResolveAst;
	private Hashtable<DeltaNode, ASTNode> astTable = new Hashtable<DeltaNode, ASTNode>();
	private ClientMethod oldMethod;
	private ClientMethod newMethod;

	public SDGComparator(IR lir, IR rir, boolean bResolveAst, ClientMethod oldMethod, ClientMethod newMethod) {
		// TODO Auto-generated constructor stub
		this.lir = lir;
		this.rir = rir;
		this.bResolveAst = bResolveAst;
		this.oldMethod = oldMethod;
		this.newMethod = newMethod;
	}

	public MethodDelta compare(SDGwithPredicate lfg, SDGwithPredicate rfg) {
		// TODO Auto-generated method stub
		DirectedSparseGraph<DeltaNode, DeltaEdge> graph = null;
		if(lfg!=null&&rfg!=null){
			LabelUtil llt = new LabelUtil(); 
			llt.setIR(lir);
			DirectedSparseGraph<DeltaNode, DeltaEdge> lg = translateSDGToXml(lfg, llt, oldMethod.ast);
			
			LabelUtil rlt = new LabelUtil(); 
			rlt.setIR(rir);
			DirectedSparseGraph<DeltaNode, DeltaEdge> rg = translateSDGToXml(rfg, rlt, newMethod.ast);
			
			graph = extractChangeGraph(lg, rg);
			
		}
		DirectedSparseGraph<DeltaNode, DeltaEdge> smallGraph = extractDelta(graph); 
		
		MethodDelta md = new MethodDelta(graph, smallGraph, astTable, this.oldMethod, this.newMethod);
		return md;
	}
	
	private DirectedSparseGraph<DeltaNode, DeltaEdge> translateSDGToXml(SDGwithPredicate flowGraph, LabelUtil lt, ASTNode ast) {
		// TODO Auto-generated method stub
		DirectedSparseGraph<DeltaNode, DeltaEdge> graph = new DirectedSparseGraph<DeltaNode, DeltaEdge>(); 
		Hashtable<Statement, DeltaNode> table = new Hashtable<Statement, DeltaNode>();
		Iterator<Statement> it = flowGraph.iterator();
		while(it.hasNext()){
			Statement statement = it.next();
			DeltaNode node = new DeltaNode(lt.getVisualLabel(statement));
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
				DeltaNode from = table.get(s1);
				DeltaNode to = table.get(s2);
				graph.addEdge(new DeltaEdge(from, to, DeltaEdge.DATA_FLOW), from, to);
			}
		}
		
		flowGraph.reConstruct(DataDependenceOptions.NONE, ControlDependenceOptions.FULL);
		it = flowGraph.iterator();
		while(it.hasNext()){
			Statement s1 = it.next();
			Iterator<Statement> nodes = flowGraph.getSuccNodes(s1);
			while(nodes.hasNext()){
				Statement s2 = nodes.next();
				DeltaNode from = table.get(s1);
				DeltaNode to = table.get(s2);
				DeltaEdge edge = graph.findEdge(from, to);
				if(edge==null){
					graph.addEdge(new DeltaEdge(from, to, DeltaEdge.CONTROL_FLOW), from, to);
				}
			}
		}
		return graph;
	}



	private DirectedSparseGraph<DeltaNode, DeltaEdge> extractChangeGraph(//zhh
			DirectedSparseGraph<DeltaNode, DeltaEdge> leftGraph,
			DirectedSparseGraph<DeltaNode, DeltaEdge> rightGraph) {
		// TODO Auto-generated method stub
		ChangeGraphBuilder builder = new ChangeGraphBuilder(leftGraph,  rightGraph,
				GraphComparator.ABSTRACT);
		DirectedSparseGraph<DeltaNode, DeltaEdge> graph = builder.extractChangeGraph();
		return graph;
	}

	private DirectedSparseGraph<DeltaNode, DeltaEdge> extractDelta(
			DirectedSparseGraph<DeltaNode, DeltaEdge> graph) {
		// TODO Auto-generated method stub
		DirectedSparseGraph<DeltaNode, DeltaEdge> deltaGraph = new DirectedSparseGraph<DeltaNode, DeltaEdge>();
		//add nodes
		for(DeltaNode node:graph.getVertices()){
			if(node.bModified){
				deltaGraph.addVertex(node);
			}
		}
		
		for(DeltaNode n1:deltaGraph.getVertices()){
			for(DeltaNode n2:deltaGraph.getVertices()){
				DeltaEdge edge = graph.findEdge(n1, n2);
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
