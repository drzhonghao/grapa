package partial.code.grapa.delta.script;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import partial.code.grapa.delta.graph.GraphComparator;
import partial.code.grapa.delta.graph.StatementEdge;
import partial.code.grapa.delta.graph.StatementNode;

import com.ibm.wala.ipa.slicer.NormalStatement;
import com.ibm.wala.ssa.IR;
import com.ibm.wala.ssa.SSAGotoInstruction;
import com.ibm.wala.ssa.SSAInstruction;

import edu.uci.ics.jung.algorithms.cluster.WeakComponentClusterer;
import edu.uci.ics.jung.algorithms.filters.FilterUtils;
import edu.uci.ics.jung.algorithms.shortestpath.DijkstraShortestPath;
import edu.uci.ics.jung.graph.DirectedSparseGraph;

public class ChangeGraphBuilder extends GraphComparator{

	public ChangeGraphBuilder(
			DirectedSparseGraph<StatementNode, StatementEdge> oldGraph,
			IR oldIr,
			DirectedSparseGraph<StatementNode, StatementEdge> newGraph, IR newIr) {
		super(oldGraph, oldIr, newGraph, newIr);
		// TODO Auto-generated constructor stub
	}

	public DirectedSparseGraph<StatementNode, StatementEdge> extractChangeGraph() {
		// TODO Auto-generated method stub
		Hashtable<StatementNode, StatementNode> vm = this.extractNodeMappings();
		DirectedSparseGraph<StatementNode, StatementEdge> graph = new DirectedSparseGraph<StatementNode, StatementEdge>();
		//add left nodes
		for(StatementNode s:leftGraph.getVertices()){
			s.side = StatementNode.LEFT;
			graph.addVertex(s);
		}
		
		//add right nodes
		for(StatementNode s:rightGraph.getVertices()){
			s.side = StatementNode.RIGHT;
			graph.addVertex(s);
		}
		
		for(StatementNode n1:graph.getVertices()){
			for(StatementNode n2:graph.getVertices()){
				if(!n1.equals(n2)&&n1.side==n2.side){
					StatementEdge edge;
					if(n1.side==StatementNode.LEFT){
						edge = leftGraph.findEdge(n1, n2);
					}else{
						edge = rightGraph.findEdge(n1, n2);
					}
					if(edge != null){
						graph.addEdge(edge, n1, n2);
					}
				}			
			}
		}
		
		for(StatementNode n1:graph.getVertices()){
			StatementNode n2 = vm.get(n1);
			if(n2!=null){
				if(calculateCost(n1,n2)==0){
					n1.bModified = false;
					n2.bModified = false;
				}else{
					StatementEdge edge = graph.findEdge(n1, n2);
					if(edge==null){
						edge = new StatementEdge(n1, n2, StatementEdge.CHANGE);
						graph.addEdge(edge, n1, n2);
					}
				}
			}
		}
		return graph;
	}

	private ArrayList<DirectedSparseGraph<StatementNode, StatementEdge>> generateClusters(
			DirectedSparseGraph<StatementNode, StatementEdge> graph) {
		// TODO Auto-generated method stub
		WeakComponentClusterer<StatementNode, StatementEdge> wcc = new WeakComponentClusterer<StatementNode, StatementEdge>();
		Set<Set<StatementNode>> set = wcc.transform(graph);
		ArrayList<DirectedSparseGraph<StatementNode,StatementEdge>> result = new  ArrayList<DirectedSparseGraph<StatementNode,StatementEdge>> ();
		Collection<DirectedSparseGraph<StatementNode, StatementEdge>> ccs = FilterUtils.createAllInducedSubgraphs(set, graph);
		Iterator<DirectedSparseGraph<StatementNode, StatementEdge>> it = ccs.iterator();
		while(it.hasNext()){
			DirectedSparseGraph<StatementNode, StatementEdge> g = it.next();
//			if(isInteresting(g)){
				result.add(g);
//			}
		}
		return result;
	}

	

}
 