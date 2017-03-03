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
	public double calculateNameCosts(Hashtable<DeltaNode, DeltaNode> vm, double weight) {
		// TODO Auto-generated method stub
		double cost = 0;
		double total = 0;
		for(DeltaNode leftNode:vm.keySet()){
			DeltaNode rightNode = vm.get(leftNode);
			if(leftNode.bModified){
				cost += calculateNodeNameCost(leftNode, rightNode)*weight;
				total += weight;
			}else{
				cost += calculateNodeNameCost(leftNode, rightNode);
				total+=1;
			}
		}
		cost = cost/total;
		return cost;
	}

	
	public double calculateAbstactNameCosts(Hashtable<DeltaNode, DeltaNode> vm, double weight) {
		// TODO Auto-generated method stub
		double cost = 0;
		double total = 0;
		for(DeltaNode leftNode:vm.keySet()){
			DeltaNode rightNode = vm.get(leftNode);
			if(leftNode.bModified){
				cost += calculateAbstractNodeNameCost(leftNode, rightNode)*weight;
				total += weight;
			}else{
				cost += calculateAbstractNodeNameCost(leftNode, rightNode);
				total+=1;
			}
		}
		cost = cost/total;
		return cost;
	}
	
	public double calculateCodeNameCosts(Hashtable<DeltaNode, DeltaNode> vm, double weight) {
		// TODO Auto-generated method stub
		double cost = 0;
		double total = 0;
		for(DeltaNode leftNode:vm.keySet()){
			DeltaNode rightNode = vm.get(leftNode);
			LabelUtil lt = new LabelUtil();
			ArrayList<String> leftNames = lt.getCodeNames(leftNode.label);
			ArrayList<String> rightNames = lt.getCodeNames(rightNode.label);
			if(leftNames.size()>0&&rightNames.size()>0){
				if(leftNode.bModified){
					cost +=  (1 - stringComparator.getSimilarity(leftNames.get(0), rightNames.get(0)))*weight;	
					total += weight;
				}else{
					cost +=  1 - stringComparator.getSimilarity(leftNames.get(0), rightNames.get(0));
					total++;
				}
			}else if(leftNames.size()==0&&rightNames.size()==0){
				cost += 0;	
				total++;
			}else{
				cost += 1;
				total++;
			}
		}
		cost = cost/total;
		return cost;
	}
	
	public double calculateDataFlowCosts(Hashtable<DeltaNode, DeltaNode> vm, double weight) {
		// TODO Auto-generated method stub
		double cost = 0;
		int commonEdges = calculateCommonEdges(vm, DeltaEdge.DATA_FLOW, weight);
		int leftEdges = calculateEdges(vm.keySet(), leftGraph,DeltaEdge.DATA_FLOW, weight);
		int rightEdges = calculateEdges(vm.values(), rightGraph,DeltaEdge.DATA_FLOW, weight);
		if((leftEdges+rightEdges-commonEdges)!=0){
			cost = 1 - commonEdges/(double)(leftEdges+rightEdges-commonEdges);
		}
		return cost;
	}

	private int calculateEdges(Collection<DeltaNode> nodes, DirectedSparseGraph<DeltaNode, DeltaEdge> graph, int type, double weight) {
		// TODO Auto-generated method stub
		int edges = 0;
		for(DeltaNode n1:nodes){
			for(DeltaNode n2:nodes){
				DeltaEdge edge = graph.findEdge(n1, n2);
				if(edge!=null&&edge.type==type){
					if(n1.bModified||n2.bModified){
						edges += weight;
					}else{
						edges++;
					}
				}
			}
		}
		return edges;
	}

	private int calculateCommonEdges(Hashtable<DeltaNode, DeltaNode> vm, int type, double weight) {
		// TODO Auto-generated method stub
		int commonEdges = 0;
		for(DeltaNode l1:vm.keySet()){
			for(DeltaNode l2:vm.keySet()){				
				DeltaEdge leftEdge = leftGraph.findEdge(l1, l2);
				if(leftEdge!=null&&leftEdge.type==type){
					DeltaNode r1 = vm.get(l1);
					DeltaNode r2 = vm.get(l2);
					DeltaEdge rightEdge = rightGraph.findEdge(r1, r2);
					if(rightEdge!=null&&rightEdge.type==type){
						if(l1.bModified||l2.bModified){
							commonEdges += weight;
						}else{
							commonEdges++;
						}
					}				
				}
			}
		}
		return commonEdges;
	}

	public double calculateControlFlowCosts(Hashtable<DeltaNode, DeltaNode> vm, double weight) {
		// TODO Auto-generated method stub
		double cost = 0;
		int commonEdges = calculateCommonEdges(vm, DeltaEdge.CONTROL_FLOW, weight);
		int leftEdges = calculateEdges(vm.keySet(), leftGraph,DeltaEdge.CONTROL_FLOW, weight);
		int rightEdges = calculateEdges(vm.values(), rightGraph,DeltaEdge.CONTROL_FLOW, weight);
		if((leftEdges+rightEdges-commonEdges)!=0){
			cost = 1 - commonEdges/(double)(leftEdges+rightEdges-commonEdges);
		}
		return cost;
	}


	
}
 