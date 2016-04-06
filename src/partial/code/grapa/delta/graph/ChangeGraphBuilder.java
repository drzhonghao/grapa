package partial.code.grapa.delta.graph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;










import partial.code.grapa.dependency.graph.StatementEdge;
import partial.code.grapa.dependency.graph.StatementNode;

import com.ibm.wala.ipa.slicer.NormalStatement;
import com.ibm.wala.ssa.IR;

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
		
		for(StatementNode m:vm.keySet()){
			StatementNode n = vm.get(m);		
			if(calculateNodeCost(m,n)!=0){
//				if(n.toString().indexOf("conditional branch(eq)")>=0){
//					System.out.println("Here");
//				}
				graph.addVertex(m);
				graph.addVertex(n);
				if(mode){
					m.side = StatementNode.LEFT;
					n.side = StatementNode.RIGHT;
					StatementEdge edge = new StatementEdge(m, n, StatementEdge.CHANGE);
					graph.addEdge(edge, m, n);
				}else{
					m.side = StatementNode.RIGHT;
					n.side = StatementNode.LEFT;
					StatementEdge edge = new StatementEdge(n, m, StatementEdge.CHANGE);
					graph.addEdge(edge, n, m);
				}
			}
		}
		
		 for(StatementNode node:this.rightGraph.getVertices()){
        	if(!vm.containsValue(node)){
        		if(mode){
        			node.side = StatementNode.RIGHT;
        			graph.addVertex(node);
        		}else{
        			node.side = StatementNode.LEFT;
        			graph.addVertex(node);
        		}
        	}
        }     
		
		for(StatementNode n1:graph.getVertices()){
			for(StatementNode n2:graph.getVertices()){
				StatementEdge edge = this.leftGraph.findEdge(n1, n2);
				if(edge==null){
					edge = this.rightGraph.findEdge(n1, n2);
				}
				if(edge != null){
					graph.addEdge(edge, n1, n2);
				}			
			}
		}
//		ArrayList<DirectedSparseGraph<StatementNode,StatementEdge>> ccs = generateClusters(graph);
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
 