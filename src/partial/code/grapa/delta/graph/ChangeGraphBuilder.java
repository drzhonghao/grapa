package partial.code.grapa.delta.graph;

import java.util.Hashtable;

import edu.uci.ics.jung.graph.DirectedSparseGraph;

public class ChangeGraphBuilder extends GraphComparator{

	
	public ChangeGraphBuilder(
			DirectedSparseGraph<DeltaNode, DeltaEdge> oldGraph,			
			DirectedSparseGraph<DeltaNode, DeltaEdge> newGraph,
			int mode) {
		super(oldGraph,  newGraph, mode);
		// TODO Auto-generated constructor stub
	}

	public DirectedSparseGraph<DeltaNode, DeltaEdge> extractChangeGraph() {
		// TODO Auto-generated method stub
		Hashtable<DeltaNode, DeltaNode> vm = this.extractNodeMappings();
		DirectedSparseGraph<DeltaNode, DeltaEdge> graph = new DirectedSparseGraph<DeltaNode, DeltaEdge>();
		//add left nodes
		for(DeltaNode s:leftGraph.getVertices()){
			s.side = DeltaNode.LEFT;
			graph.addVertex(s);
		}
		
		//add right nodes
		for(DeltaNode s:rightGraph.getVertices()){
			s.side = DeltaNode.RIGHT;
			graph.addVertex(s);
		}
		
		for(DeltaNode n1:graph.getVertices()){
			for(DeltaNode n2:graph.getVertices()){
				if(!n1.equals(n2)&&n1.side==n2.side){
					DeltaEdge edge;
					if(n1.side==DeltaNode.LEFT){
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
		
		for(DeltaNode n1:graph.getVertices()){
			DeltaNode n2 = vm.get(n1);
			if(n2!=null){
				if(calculateCost(n1,n2)==0){
					n1.bModified = false;
					n2.bModified = false;
				}else{
					DeltaEdge edge = graph.findEdge(n1, n2);
					if(edge==null){
						edge = new DeltaEdge(n1, n2, DeltaEdge.CHANGE);
						graph.addEdge(edge, n1, n2);
					}
				}
			}
		}
		return graph;
	}
	public double calculateNameDeltaValue() {
		// TODO Auto-generated method stub
		Hashtable<DeltaNode, DeltaNode> vm = this.extractNodeMappings();
		double sim = 0;
		for(DeltaNode leftNode:vm.keySet()){
			DeltaNode rightNode = vm.get(leftNode);
			sim += calculateNodeNameCost(leftNode, rightNode);
		}
		sim = sim/vm.size();
		return sim;
	}
	
}
 