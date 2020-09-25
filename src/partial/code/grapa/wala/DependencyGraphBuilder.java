package partial.code.grapa.wala;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;

import com.ibm.wala.classLoader.IMethod;
import com.ibm.wala.classLoader.Language;
import com.ibm.wala.ipa.callgraph.AnalysisCacheImpl;
import com.ibm.wala.ipa.callgraph.AnalysisOptions;
import com.ibm.wala.ipa.callgraph.AnalysisScope;
import com.ibm.wala.ipa.callgraph.CallGraph;
import com.ibm.wala.ipa.callgraph.Entrypoint;
import com.ibm.wala.ipa.callgraph.IAnalysisCacheView;
import com.ibm.wala.ipa.callgraph.AnalysisOptions.ReflectionOptions;
import com.ibm.wala.ipa.callgraph.impl.DefaultEntrypoint;
import com.ibm.wala.ipa.callgraph.impl.Util;
import com.ibm.wala.ipa.callgraph.propagation.InstanceKey;
import com.ibm.wala.ipa.callgraph.propagation.PointerAnalysis;
import com.ibm.wala.ipa.cha.ClassHierarchy;
import com.ibm.wala.ipa.slicer.SDG;
import com.ibm.wala.ipa.slicer.Slicer;
import com.ibm.wala.ipa.slicer.Statement;
import com.ibm.wala.ipa.slicer.Slicer.ControlDependenceOptions;
import com.ibm.wala.ipa.slicer.Slicer.DataDependenceOptions;
import com.ibm.wala.ssa.IR;
import com.ibm.wala.types.ClassLoaderReference;
import com.ibm.wala.types.Descriptor;
import com.ibm.wala.types.MethodReference;
import com.ibm.wala.types.TypeName;
import com.ibm.wala.types.TypeReference;

import com.ibm.wala.util.collections.HashSetFactory;
import com.ibm.wala.util.strings.Atom;


import edu.uci.ics.jung.graph.DirectedSparseGraph;
import partial.code.grapa.delta.graph.DeltaEdge;
import partial.code.grapa.delta.graph.DeltaNode;
import partial.code.grapa.dependency.graph.Predicate;
import partial.code.grapa.dependency.graph.SDGwithPredicate;
import partial.code.grapa.tool.LabelUtil;


public class DependencyGraphBuilder {

	private AnalysisScope scope;
	private ClassHierarchy cha;
	private int maxNode;

	
	public DependencyGraphBuilder(AnalysisScope scope, ClassHierarchy cha) {
		this.scope = scope;
		this.cha = cha;
	}

	public DirectedSparseGraph<DeltaNode, DeltaEdge> build(MethodEntry point, Predicate predict) {
	
		AnalysisOptions options = new AnalysisOptions(scope, point.entryPoint);
		options.setReflectionOptions(ReflectionOptions.ONE_FLOW_TO_CASTS_APPLICATION_GET_METHOD);	
		options.setMaxNumberOfNodes(maxNode);
		options.setOnlyClientCode(true);
		com.ibm.wala.ipa.callgraph.CallGraphBuilder<InstanceKey> builder = Util.makeZeroCFABuilder(Language.JAVA, options, new AnalysisCacheImpl(), cha, scope);
		CallGraph cg = null;
		DirectedSparseGraph<DeltaNode, DeltaEdge> graph = null;
		try {
			cg = builder.makeCallGraph(options,null);
			DataDependenceOptions dOptions = DataDependenceOptions.FULL; 
			ControlDependenceOptions cOptions = ControlDependenceOptions.FULL;
			PointerAnalysis<InstanceKey> pointer = builder.getPointerAnalysis();
			SDG sdg = new SDG(cg, pointer, dOptions, cOptions);		
			IAnalysisCacheView cache = builder.getAnalysisCache();
			IR ir = cache.getIR(point.method);
			SDGwithPredicate g = new SDGwithPredicate(sdg, predict);
			graph = transformatSDG2Jung(g, ir);
		} catch (Exception | Error  e) {
			e.printStackTrace();
			System.out.println("Fail to build the SDG for "+point.getFullName());
		}
		return graph;
	}
	
	public ArrayList<DirectedSparseGraph<DeltaNode, DeltaEdge>> computeExceptionSubgraphs(MethodEntry point, Predicate predict) {
		
		AnalysisOptions options = new AnalysisOptions(scope, point.entryPoint);
		options.setReflectionOptions(ReflectionOptions.ONE_FLOW_TO_CASTS_APPLICATION_GET_METHOD);	
		options.setMaxNumberOfNodes(maxNode);
		options.setOnlyClientCode(true);
		com.ibm.wala.ipa.callgraph.CallGraphBuilder<InstanceKey> builder = Util.makeZeroCFABuilder(Language.JAVA, options, new AnalysisCacheImpl(), cha, scope);
		CallGraph cg = null;
		DirectedSparseGraph<DeltaNode, DeltaEdge> graph = null;
		ArrayList<DirectedSparseGraph<DeltaNode, DeltaEdge>> graphs = new ArrayList<DirectedSparseGraph<DeltaNode, DeltaEdge>>();
		try {
			cg = builder.makeCallGraph(options,null);
			DataDependenceOptions dOptions = DataDependenceOptions.FULL; 
			ControlDependenceOptions cOptions = ControlDependenceOptions.FULL;
			PointerAnalysis<InstanceKey> pointer = builder.getPointerAnalysis();
			SDG sdg = new SDG(cg, pointer, dOptions, cOptions);		
			IAnalysisCacheView cache = builder.getAnalysisCache();
			IR ir = cache.getIR(point.method);
			SDGwithPredicate g = new SDGwithPredicate(sdg, predict);
			graph = transformatSDG2Jung(g, ir);
			if(graph.getVertexCount()<200) {
				graphs.add(graph);
			}else {
				ArrayList<Statement> suspicousNodes = extractExceptions(sdg);
				
				for(Statement exception:suspicousNodes) {
					Collection<Statement> slice = Slicer.computeBackwardSlice(sdg, exception);
					slice.add(exception);
					DirectedSparseGraph<DeltaNode, DeltaEdge> subgraph = extractSubGraph(graph, slice);
					graphs.add(subgraph);
				}
			}
//			graphs.add(graph);
		} catch (Exception | Error  e) {
			e.printStackTrace();
			System.out.println("Fail to build the SDG for "+point.getFullName());
		}
		return graphs;
	}
	private ArrayList<Statement> extractExceptions(SDG sdg) {
		ArrayList<Statement> statements = new ArrayList<Statement>();
		for(int i=0; i<sdg.getNumberOfNodes(); i++) {
			Object node = sdg.getNode(i);
			String label = node.toString();
			if(label.indexOf(" = new ")>=0&&(label.indexOf("Exception>@")>0||label.indexOf("AssertionError>@")>0)) {
				Statement statement = (Statement)node;
				statements.add(statement);
			}
		}
		return statements;
	}
	

	private DirectedSparseGraph<DeltaNode, DeltaEdge> extractSubGraph(DirectedSparseGraph<DeltaNode, DeltaEdge> graph,
			Collection<Statement> slice) {
		DirectedSparseGraph<DeltaNode, DeltaEdge> g = new DirectedSparseGraph<DeltaNode, DeltaEdge>();
		for(Statement statement:slice) {
			for(DeltaNode node:graph.getVertices()) {
				if(statement.toString().compareTo(node.label)==0) {
					g.addVertex(node);
					break;
				}
			}			
		}
		for(DeltaNode node:graph.getVertices()) {
			if(node.label.indexOf("PARAM_CALLEE:")>=0) {
				g.addVertex(node);
			}
		}
		for(DeltaNode from:g.getVertices()) {
			for(DeltaNode to:g.getVertices()) {
				DeltaEdge edge = graph.findEdge(from, to);
				if(edge!=null) {
					g.addEdge(edge, from, to);
				}
			}
		}
		return g;
	}
	
	

	private DirectedSparseGraph<DeltaNode, DeltaEdge> transformatSDG2Jung(
			SDGwithPredicate flowGraph, IR ir) {
		LabelUtil lt = new LabelUtil();
		lt.setIR(ir);
		DirectedSparseGraph<DeltaNode, DeltaEdge> graph = new DirectedSparseGraph<DeltaNode, DeltaEdge>(); 
		Hashtable<String, DeltaNode> table = new Hashtable<String, DeltaNode>();
		
//		flowGraph.reConstruct(DataDependenceOptions.FULL, ControlDependenceOptions.NONE);
		Iterator<Statement> it = flowGraph.iterator();
		while(it.hasNext()){
			Statement s1 = it.next();
			Iterator<Statement> nodes = flowGraph.getSuccNodes(s1);
			while(nodes.hasNext()){
				Statement s2 = nodes.next();
				DeltaNode from = table.get(lt.getVisualLabel(s1));
				if(from==null) {
					from = new DeltaNode(lt.getVisualLabel(s1), lt.getLineNo(s1));
					table.put(lt.getVisualLabel(s1), from);
				}
				DeltaNode to = table.get(lt.getVisualLabel(s2));
				if(to==null) {
					to = new DeltaNode(lt.getVisualLabel(s2), lt.getLineNo(s2));
					table.put(lt.getVisualLabel(s2), to);
				}
				graph.addEdge(new DeltaEdge(from, to, DeltaEdge.DATA_FLOW), from, to);
			}
			if(graph.getVertexCount()>maxNode) {
				return null;
			}
		}
		
		
//		flowGraph.reConstruct(DataDependenceOptions.NONE, ControlDependenceOptions.FULL);
//		it = flowGraph.iterator();
//		while(it.hasNext()){
//			Statement s1 = it.next();
//			Iterator<Statement> nodes = flowGraph.getSuccNodes(s1);
//			while(nodes.hasNext()){
//				Statement s2 = nodes.next();
//				DeltaNode from = table.get(lt.getVisualLabel(s1));
//				if(from==null) {
//					from = new DeltaNode(lt.getVisualLabel(s1), lt.getLineNo(s1));
//					table.put(lt.getVisualLabel(s1), from);
//				}
//				DeltaNode to = table.get(lt.getVisualLabel(s2));
//				if(to==null) {
//					to = new DeltaNode(lt.getVisualLabel(s2), lt.getLineNo(s2));
//					table.put(lt.getVisualLabel(s2), to);
//				}
//				DeltaEdge edge = graph.findEdge(from, to);
//				if(edge==null){
//					graph.addEdge(new DeltaEdge(from, to, DeltaEdge.CONTROL_FLOW), from, to);
//				}
//			}
//			if(graph.getVertexCount()>maxNode) {
//				return null;
//			}
//		}
		
		//remove orphan
		ArrayList<DeltaNode> toDels = new ArrayList<DeltaNode>(); 
		for(DeltaNode node:graph.getVertices()) {
			if(graph.degree(node)==0) {
				toDels.add(node);
			}
		}
		for(DeltaNode node:toDels) {
			graph.removeVertex(node);
		}
		
		//update method call entry
		for(DeltaEdge edge:graph.getEdges()) {
			if(edge.from.label.startsWith("NORMAL")&&edge.to.label.startsWith("METHOD_ENTRY")) {
				edge.type = DeltaEdge.DATA_FLOW;
			}
		}
		
		//update method calls in conditions
		for(DeltaEdge edge:graph.getEdges()) {
			if(edge.from.label.indexOf("invokevirtual")>0&&edge.to.label.indexOf("conditional branch")>0) {
				edge.type = DeltaEdge.DATA_FLOW;
			}
		}
		
		//update string append
		for(DeltaEdge edge:graph.getEdges()) {
			if(edge.from.label.indexOf("Ljava/lang/StringBuilder, append(")>0
					&&(edge.to.label.indexOf("Ljava/lang/StringBuilder, append(")>0
					||edge.to.label.indexOf("Ljava/lang/StringBuilder, toString()")>0)) {
				edge.type = DeltaEdge.DATA_FLOW;
			}
		}
		return graph;
	}

	public void setMaxNode(int maxNode) {
		this.maxNode = maxNode;		
	}
	
	
	
}
