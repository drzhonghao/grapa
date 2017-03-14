package partial.code.grapa.delta.graph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Set;

import edu.uci.ics.jung.graph.DirectedSparseGraph;
import partial.code.grapa.tool.LabelUtil;

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


	public double calculateCosts(Hashtable<DeltaNode, DeltaNode> vm, boolean bOnlyModified) {
		// TODO Auto-generated method stub
		double cost = 0;
		double total = 0;
		for(DeltaNode leftNode:vm.keySet()){
			DeltaNode rightNode = vm.get(leftNode);
			if(bOnlyModified){
				if(leftNode.bModified){
					cost += calculateCost(leftNode, rightNode);
					total += 1;
				}
			}else{
				cost += calculateCost(leftNode, rightNode);
				total += 1;	
			}
		}
		if(total>0){
			cost = cost/total;
		}else{
			cost = 1;
		}
		return cost;
	}



//	public double calculateAbstractCost(Hashtable<DeltaNode, DeltaNode> vm, boolean bOnlyModified, String prefix) {
//		// TODO Auto-generated method stub
//		double cost = 0;
//		double total = 0;
//		for(DeltaNode leftNode:vm.keySet()){
//			DeltaNode rightNode = vm.get(leftNode);
//			if(bOnlyModified){
//				if(leftNode.bModified){
//					cost = calculateAbstractCost(leftNode, rightNode, prefix);
//					total++;
//				}
//			}else{
//				cost = calculateAbstractCost(leftNode, rightNode, prefix);
//				total++;
//			}	
//		}
//		if(total>0){
//			cost = cost/total;
//		}else{
//			cost = 1;
//		}
//		return cost;
//	}
//
//	private double calculateAbstractCost(DeltaNode leftNode, DeltaNode rightNode, String prefix) {
//		// TODO Auto-generated method stub
//		LabelUtil lt = new LabelUtil();
//		ArrayList<String> leftNames = lt.getCodeNames(leftNode.label);
//		ArrayList<String> rightNames = lt.getCodeNames(rightNode.label);
//		double cost = 0;
//		if(leftNames.size()>0&&rightNames.size()>0){
//			String leftName = leftNames.get(0);
//			String rightName = rightNames.get(0);
//			if(leftName.startsWith(prefix)&&rightName.startsWith(prefix)){
//				cost += calculateAbstractNodeNameCost(leftNode, rightNode);
//			}else if(leftName.startsWith(prefix)||rightName.startsWith(prefix)){
//				cost = 1;
//			}else{
//				cost +=   1 - stringComparator.getSimilarity(leftName,rightName);;
//			}
//		}else if(leftNames.size()==0&&rightNames.size()==0){
//			cost += calculateAbstractNodeNameCost(leftNode, rightNode);
//		}else{
//			cost += 1;
//		}
//		cost += calculateIndegreeCost(leftNode, rightNode);
//    	cost += calculateOutDegreeCost(leftNode, rightNode);
//    	cost = cost/3;
//    	return cost;
//	}
	
}
 