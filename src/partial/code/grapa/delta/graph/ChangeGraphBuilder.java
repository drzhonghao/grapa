package partial.code.grapa.delta.graph;

import java.util.Collection;
import java.util.Hashtable;

import edu.uci.ics.jung.graph.DirectedSparseGraph;

public class ChangeGraphBuilder extends GraphComparator{

	
	public ChangeGraphBuilder(
			DirectedSparseGraph<DeltaNode, DeltaEdge> oldGraph,			
			DirectedSparseGraph<DeltaNode, DeltaEdge> newGraph) {
		super(oldGraph,  newGraph);
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
	public double calculateNameCosts(Hashtable<DeltaNode, DeltaNode> vm) {
		// TODO Auto-generated method stub
		double cost = 0;
		for(DeltaNode leftNode:vm.keySet()){
			DeltaNode rightNode = vm.get(leftNode);
			cost += calculateNodeNameCost(leftNode, rightNode);
		}
		cost = cost/vm.size();
		return cost;
	}

	public double calculateInEdgeCosts(Hashtable<DeltaNode, DeltaNode> vm) {
		// TODO Auto-generated method stub
		double cost = 0;
		for(DeltaNode leftNode:vm.keySet()){
			DeltaNode rightNode = vm.get(leftNode);
			cost += calculateIndegreeCost(leftNode, rightNode);
		}
		cost = cost/vm.size();
		return cost;
	}

	public double calculateOutEdgeCosts(Hashtable<DeltaNode, DeltaNode> vm) {
		// TODO Auto-generated method stub
		double cost = 0;
		for(DeltaNode leftNode:vm.keySet()){
			DeltaNode rightNode = vm.get(leftNode);
			cost += calculateOutDegreeCost(leftNode, rightNode);
		}
		cost = cost/vm.size();
		return cost;
	}
	
	public double calculateAbstactNameCosts(Hashtable<DeltaNode, DeltaNode> vm) {
		// TODO Auto-generated method stub
		double cost = 0;
		for(DeltaNode leftNode:vm.keySet()){
			DeltaNode rightNode = vm.get(leftNode);
			cost += calculateAbstractNodeNameCost(leftNode, rightNode);
		}
		cost = cost/vm.size();
		return cost;
	}

	public double calculateDataFlowCosts(Hashtable<DeltaNode, DeltaNode> vm) {
		// TODO Auto-generated method stub
		double cost = 0;
		for(DeltaNode leftNode:vm.keySet()){
			DeltaNode rightNode = vm.get(leftNode);
			int leftIn = 0;
			int leftOut = 0;
			int rightIn = 0;
			int rightOut = 0;
			Collection<DeltaEdge> edges = this.leftGraph.getInEdges(leftNode);
			for(DeltaEdge edge:edges){
				if(edge.type == DeltaEdge.DATA_FLOW){
					if(edge.from.equals(leftNode)){
						leftOut++;
					}else{
						leftIn++;
					}
				}
			}
			edges = this.rightGraph.getInEdges(rightNode);
			for(DeltaEdge edge:edges){
				if(edge.type == DeltaEdge.DATA_FLOW){
					if(edge.from.equals(rightNode)){
						rightOut++;
					}else{
						rightIn++;
					}
				}
			}
			if((leftOut+rightOut)>0){
				cost = Math.abs(leftOut-rightOut)/(leftOut+rightOut);
			}
			if((leftIn+rightIn)>0){
				cost += Math.abs(leftIn-rightIn)/(leftIn+rightIn);
			}
			cost = cost/2;
		}
		cost = cost/vm.size();
		return cost;
	}

	public double calculateControlFlowCosts(Hashtable<DeltaNode, DeltaNode> vm) {
		// TODO Auto-generated method stub
		double cost = 0;
		for(DeltaNode leftNode:vm.keySet()){
			DeltaNode rightNode = vm.get(leftNode);
			int leftIn = 0;
			int leftOut = 0;
			int rightIn = 0;
			int rightOut = 0;
			Collection<DeltaEdge> edges = this.leftGraph.getInEdges(leftNode);
			for(DeltaEdge edge:edges){
				if(edge.type == DeltaEdge.CONTROL_FLOW){
					if(edge.from.equals(leftNode)){
						leftOut++;
					}else{
						leftIn++;
					}
				}
			}
			edges = this.rightGraph.getInEdges(rightNode);
			for(DeltaEdge edge:edges){
				if(edge.type == DeltaEdge.CONTROL_FLOW){
					if(edge.from.equals(rightNode)){
						rightOut++;
					}else{
						rightIn++;
					}
				}
			}
			if((leftOut+rightOut)>0){
				cost = Math.abs(leftOut-rightOut)/(leftOut+rightOut);
			}
			if((leftIn+rightIn)>0){
				cost += Math.abs(leftIn-rightIn)/(leftIn+rightIn);
			}
			cost = cost/2;
		}
		cost = cost/vm.size();
		return cost;
	}
	
}
 