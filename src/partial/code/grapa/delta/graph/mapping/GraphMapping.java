package partial.code.grapa.delta.graph.mapping;

import java.util.ArrayList;
import java.util.Hashtable;

import edu.uci.ics.jung.graph.DirectedSparseGraph;
import partial.code.grapa.delta.graph.DeltaEdge;

import partial.code.grapa.delta.graph.DeltaNode;
import partial.code.grapa.hungarian.HungarianAlgorithm;
import partial.code.grapa.hungarian.HungarianMapping;
import partial.code.grapa.tool.LabelUtil;

public abstract class GraphMapping extends HungarianMapping{
	protected DirectedSparseGraph<DeltaNode, DeltaEdge> leftGraph;
	protected DirectedSparseGraph<DeltaNode, DeltaEdge> rightGraph;
	protected abstract Hashtable<String, Integer> extractKinds(DirectedSparseGraph<DeltaNode, DeltaEdge> graph,
			DeltaNode node, int controlFlow, boolean b);

	
	public GraphMapping(DirectedSparseGraph<DeltaNode, DeltaEdge> oldGraph,
			DirectedSparseGraph<DeltaNode, DeltaEdge> newGraph) {
		super();
		if (oldGraph.getVertexCount()>newGraph.getVertexCount()){
			leftGraph = newGraph;
			rightGraph = oldGraph;
			bSwapSide = true;
		}else{
			leftGraph = oldGraph;
			rightGraph = newGraph;
			bSwapSide = false;
		}
	}
	
	protected Object getLeftItem(int i) {
		return leftGraph.getVertices().toArray()[i];
	}
	
	protected Object getRightItem(int i) {
		return rightGraph.getVertices().toArray()[i];
	}
	
	public double calculateNodeCost(DeltaNode from, DeltaNode to) {
		if(this.bSwapSide) {
			return calculateCost(to, from);
		}else {
			return calculateCost(from, to);
		}
	}
	
	private double calculateCost(DeltaNode leftNode, DeltaNode rightNode) {
		double edgeCost = 1;
		double nameCost = 2;
		double inDataNodeCost = calculateInDataCost(leftNode, rightNode)*edgeCost;
    	double outDataNodeCost = calculateOutDataCost(leftNode, rightNode)*edgeCost;
    	double inControlNodeCost = calculateInControlCost(leftNode, rightNode)*edgeCost;
    	double outControlNodeCost = calculateOutControlCost(leftNode, rightNode)*edgeCost;
        double nodeNameCost = calculateNodeNameCost(leftNode, rightNode)*nameCost;
        return (inDataNodeCost+outDataNodeCost+inControlNodeCost+outControlNodeCost+nodeNameCost)/(4*edgeCost+nameCost);
	}
	
	public double calculateNodeNameCost(DeltaNode leftNode,
			DeltaNode rightNode) {
		LabelUtil lt = new LabelUtil();
		ArrayList<String> leftNames = lt.getCodeNames(leftNode.label);
		ArrayList<String> rightNames = lt.getCodeNames(rightNode.label);
		double cost = 0;
		if(leftNames.size()>0&&rightNames.size()>0){
			String leftName = leftNames.get(0);
			String rightName = rightNames.get(0);
			cost =  1 - stringComparator.getSimilarity(leftName, rightName);
		}else if(leftNames.size()==0&&rightNames.size()==0){
			cost = calculateNodeKindCost(leftNode, rightNode);
		}else{
			cost = 1;
		}
		return cost;
	}
	
	private double calculateNodeKindCost(DeltaNode leftNode,
			DeltaNode rightNode) {
		double cost =  1 - stringComparator.getSimilarity(leftNode.getKind(), rightNode.getKind());
		return cost;
	}


	public double calculateCosts() {
		// TODO Auto-generated method stub
		calculateCostMatrix();
		HungarianAlgorithm ha = new HungarianAlgorithm();
        int[][] matching = ha.hgAlgorithm(costMatrix);
        
        double cost = 0;
		double total = 0;
        for(int i=0; i<matching.length; i++){
			DeltaNode v1 = (DeltaNode)leftGraph.getVertices().toArray()[matching[i][0]];
			DeltaNode v2 = (DeltaNode)rightGraph.getVertices().toArray()[matching[i][1]];
			cost += calculateCost(v1, v2);
			total += 1;
        }
	
		if(total>0){
			cost = cost/total;
		}else{
			cost = 1;
		}
		return cost;
	}	
	
	
	protected void calculateCostMatrix() {
		// TODO Auto-generated method stub
		costMatrix =  new double[leftGraph.getVertexCount()][rightGraph.getVertexCount()];
		for (int i = 0; i < leftGraph.getVertexCount(); i++) {
			DeltaNode leftNode = (DeltaNode)leftGraph.getVertices().toArray()[i];
			
            for (int j = 0; j < rightGraph.getVertexCount(); j++) {
            	DeltaNode rightNode = (DeltaNode)rightGraph.getVertices().toArray()[j];
            	costMatrix[i][j] =  calculateCost(leftNode, rightNode);
            }
        }
	}
	
	private double calculateInControlCost(DeltaNode leftNode, DeltaNode rightNode) {
		// TODO Auto-generated method stub
		Hashtable<String, Integer> leftKinds = extractKinds(leftGraph, leftNode, DeltaEdge.CONTROL_FLOW, false);
		Hashtable<String, Integer> rightKinds = extractKinds(rightGraph, rightNode, DeltaEdge.CONTROL_FLOW, false);
		return calculateCost(leftKinds, rightKinds);
	}





	private double calculateOutDataCost(DeltaNode leftNode, DeltaNode rightNode) {
		// TODO Auto-generated method stub
		Hashtable<String, Integer> leftKinds = extractKinds(leftGraph, leftNode, DeltaEdge.DATA_FLOW, true);
		Hashtable<String, Integer> rightKinds = extractKinds(rightGraph, rightNode, DeltaEdge.DATA_FLOW, true);
		return calculateCost(leftKinds, rightKinds);
	}



	private double calculateInDataCost(DeltaNode leftNode, DeltaNode rightNode) {
		// TODO Auto-generated method stub
		Hashtable<String, Integer> leftKinds = extractKinds(leftGraph, leftNode, DeltaEdge.DATA_FLOW, false);
		Hashtable<String, Integer> rightKinds = extractKinds(rightGraph, rightNode, DeltaEdge.DATA_FLOW, false);
		return calculateCost(leftKinds, rightKinds);
	}
	
	private double calculateOutControlCost(DeltaNode leftNode, DeltaNode rightNode) {
		// TODO Auto-generated method stub
		Hashtable<String, Integer> leftKinds = extractKinds(leftGraph, leftNode, DeltaEdge.CONTROL_FLOW, true);
		Hashtable<String, Integer> rightKinds = extractKinds(rightGraph, rightNode, DeltaEdge.CONTROL_FLOW, true);
		return calculateCost(leftKinds, rightKinds);
	}



	private double calculateCost(Hashtable<String, Integer> leftTable, Hashtable<String, Integer> rightTable) {
		// TODO Auto-generated method stub
		double cost = 0;
		if(leftTable.size()==0&&rightTable.size()==0){
			cost = 0;
		}else if(leftTable.size()>0&&rightTable.size()>0){
			ArrayList<String> keys = new ArrayList<String>();
			keys.addAll(leftTable.keySet());
			for(String key:rightTable.keySet()){
				if(!keys.contains(key)){
					keys.add(key);
				}
			}
			for(String key:keys){
				Integer leftValue = leftTable.get(key);
				if(leftValue == null){
					leftValue = 0;
				}
				Integer rightValue = rightTable.get(key);
				if(rightValue == null){
					rightValue = 0;
				}
				cost += Math.abs(leftValue-rightValue)/Math.max(leftValue,rightValue);
			}			
			cost = ((double)cost)/keys.size();
		}else{
			cost = 1;
		}
		return cost;
	}
	
	
	public DirectedSparseGraph<DeltaNode, DeltaEdge> extractChangeGraph() {
		// TODO Auto-generated method stub
		Hashtable<Object, Object> vm = this.extractItemMappings();
		DirectedSparseGraph<DeltaNode, DeltaEdge> graph = new DirectedSparseGraph<DeltaNode, DeltaEdge>();
		//add left nodes
		for(DeltaNode s:leftGraph.getVertices()){
			if(!this.bSwapSide){
				s.side = DeltaNode.LEFT;
				s.label = "l:"+s.label;
			}else{
				s.side = DeltaNode.RIGHT;
				s.label = "r:"+s.label;
			}
			graph.addVertex(s);
		}
		
		//add right nodes
		for(DeltaNode s:rightGraph.getVertices()){
			if(!this.bSwapSide){
				s.side = DeltaNode.RIGHT;
				s.label = "r:"+s.label;
			}else{
				s.side = DeltaNode.LEFT;
				s.label = "l:"+s.label;
			}
			graph.addVertex(s);
		}
		
		//add left edges;
		for(DeltaEdge e:leftGraph.getEdges()){
			graph.addEdge(e, (DeltaNode)e.from, (DeltaNode)e.to);
		}
		//add right edges
		for(DeltaEdge e:rightGraph.getEdges()){
			graph.addEdge(e, (DeltaNode)e.from, (DeltaNode)e.to);
		}
		
		//add modification edges
		for(DeltaNode n1:graph.getVertices()){
			DeltaNode n2 = (DeltaNode) vm.get(n1);
			if(n2!=null){
				if(calculateCost(n1,n2)==0){
					n1.bModified = false;
					n2.bModified = false;
				}else{
					n1.bModified = true;
					n2.bModified = true;
					DeltaEdge edge = null;
					if(!this.bSwapSide){
						edge = new DeltaEdge(n1, n2, DeltaEdge.CHANGE);
						graph.addEdge(edge, n1, n2);
					}else{
						edge = new DeltaEdge(n2, n1, DeltaEdge.CHANGE);
						graph.addEdge(edge, n2, n1);
					}
					
					
				}
			}
		}
		return graph;
	}


	public double calculateCosts(Hashtable<DeltaNode, DeltaNode> vm, boolean bOnlyModified) {
		// TODO Auto-generated method stub
		double cost = 0;
		cost = calculateNameCosts(vm, bOnlyModified);
		cost += calcluateStructureCosts(vm, bOnlyModified);
		cost = cost/2;
		return cost;
	}

	public double calcluateStructureCosts(Hashtable<DeltaNode, DeltaNode> vm, boolean bOnlyModified) {
		// TODO Auto-generated method stub
		double cost = 0;
		if(vm.size()==0){
			cost = 1;
		}else{
			int count = 0;
			int total = 0;
			for(DeltaNode l1:vm.keySet()){
				for(DeltaNode l2:vm.keySet()){
					DeltaEdge le = leftGraph.findEdge(l1, l2);
					if(le!=null){
						DeltaNode r1 = vm.get(l1);
						DeltaNode r2 = vm.get(l2);
						DeltaEdge re = leftGraph.findEdge(r1, r2);
						if(re!=null&&re.type==le.type){
							count++;
						}
						total++;
					}
				}
			}
			if(total!=0){
				cost = 1 - ((double)count)/total;
			}else{
				cost = 0;
			}
		}
		return cost;
	}

	public double calculateNameCosts(Hashtable<DeltaNode, DeltaNode> vm, boolean bOnlyModified) {
		double total = 0;
		double cost = 0;
		for(DeltaNode leftNode:vm.keySet()){
			DeltaNode rightNode = vm.get(leftNode);
			if(bOnlyModified){
				if(leftNode.bModified){
					cost += calculateNodeNameCost(leftNode, rightNode);
					total += 1;
				}
			}else{
				cost += calculateNodeNameCost(leftNode, rightNode);
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
}
