package partial.code.grapa.delta.graph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;

import uk.ac.shef.wit.simmetrics.similaritymetrics.Levenshtein;
import util.EditDistance;

import com.ibm.wala.cast.java.ssa.AstJavaInvokeInstruction;
import com.ibm.wala.classLoader.CallSiteReference;
import com.ibm.wala.classLoader.NewSiteReference;
import com.ibm.wala.ipa.slicer.HeapStatement;
import com.ibm.wala.ipa.slicer.NormalReturnCaller;
import com.ibm.wala.ipa.slicer.NormalStatement;
import com.ibm.wala.ipa.slicer.ParamCallee;
import com.ibm.wala.ipa.slicer.ParamCaller;
import com.ibm.wala.ipa.slicer.PhiStatement;
import com.ibm.wala.ipa.slicer.Statement;
import com.ibm.wala.ssa.IR;
import com.ibm.wala.ssa.SSAGetInstruction;
import com.ibm.wala.ssa.SSAInstruction;
import com.ibm.wala.ssa.SSANewInstruction;
import com.ibm.wala.ssa.SymbolTable;
import com.ibm.wala.types.FieldReference;

import edu.uci.ics.jung.graph.DirectedSparseGraph;
import partial.code.grapa.tool.LabelUtil;

public class GraphComparator {
	
	private double[][] costMatrix;
	protected DirectedSparseGraph<DeltaNode, DeltaEdge> leftGraph;
	protected DirectedSparseGraph<DeltaNode, DeltaEdge> rightGraph;

	protected boolean bSwapSide;//true: does not swap left and right. false: does.
	
	protected Levenshtein stringComparator;
	
	protected Hashtable<Integer, String> leftValueTable;
	protected Hashtable<Integer, String> rightValueTable;
	
	protected Hashtable<Integer, String> leftIndexTable;
	protected Hashtable<Integer, String> rightIndexTable;

	
	
	public GraphComparator(
			DirectedSparseGraph<DeltaNode, DeltaEdge> oldGraph,			
			DirectedSparseGraph<DeltaNode, DeltaEdge> newGraph) {
		// TODO Auto-generated constructor stub
		if (oldGraph.getVertexCount()>newGraph.getVertexCount()){
			leftGraph = newGraph;
			rightGraph = oldGraph;
			bSwapSide = true;
		}else{
			leftGraph = oldGraph;
			rightGraph = newGraph;
			bSwapSide = false;
		}
		stringComparator = new Levenshtein();
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
	

	public Hashtable<DeltaNode, DeltaNode> extractNodeMappings() {
		// TODO Auto-generated method stub
		calculateCostMatrix();
		HungarianAlgorithm ha = new HungarianAlgorithm();
		Hashtable<DeltaNode, DeltaNode> vm = new Hashtable<DeltaNode, DeltaNode>();
		 
		if(costMatrix.length>0){
	        int[][] matching = ha.hgAlgorithm(costMatrix);
	        //mapped nodes
	        for(int i=0; i<matching.length; i++){
				DeltaNode v1 = (DeltaNode)leftGraph.getVertices().toArray()[matching[i][0]];
				DeltaNode v2 = (DeltaNode)rightGraph.getVertices().toArray()[matching[i][1]];
				if(bSwapSide){
					vm.put(v2, v1);
				}else{
					vm.put(v1, v2);
				}
	        }
		}
        return vm;
	}
	
	
	
	private void calculateCostMatrix() {
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


	

	protected double calculateNodeNameCost(DeltaNode leftNode,
			DeltaNode rightNode) {
		// TODO Auto-generated method stub
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
	
	protected double calculateNodeKindCost(DeltaNode leftNode,
			DeltaNode rightNode) {
		// TODO Auto-generated method stub
		double cost =  1 - stringComparator.getSimilarity(leftNode.getKind(), rightNode.getKind());
		return cost;
	}


	public double calculateCost(DeltaNode leftNode, DeltaNode rightNode) {
		// TODO Auto-generated method stub
		double inDataNodeCost = calculateInDataCost(leftNode, rightNode);
    	double outDataNodeCost = calculateOutDataCost(leftNode, rightNode);
    	double inControlNodeCost = calculateInControlCost(leftNode, rightNode);
    	double outControlNodeCost = calculateOutControlCost(leftNode, rightNode);
        double nodeNameCost = calculateNodeNameCost(leftNode, rightNode);
        return (inDataNodeCost+outDataNodeCost+inControlNodeCost+outControlNodeCost+nodeNameCost)/5;
	}



	private double calculateOutControlCost(DeltaNode leftNode, DeltaNode rightNode) {
		// TODO Auto-generated method stub
		Hashtable<String, Integer> leftKinds = extractKinds(leftGraph, leftNode, AbstractEdge.CONTROL_FLOW, true);
		Hashtable<String, Integer> rightKinds = extractKinds(rightGraph, rightNode, AbstractEdge.CONTROL_FLOW, true);
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



	private Hashtable<String, Integer> extractKinds(DirectedSparseGraph<DeltaNode, DeltaEdge> graph, DeltaNode node,
			int type, boolean bOut) {
		// TODO Auto-generated method stub
		Hashtable<String, Integer> table = new Hashtable<String, Integer>();
		Collection<DeltaEdge> edges = null;
		if(bOut){
			edges = graph.getOutEdges(node);
		}else{
			edges = graph.getInEdges(node);
		}
		for(DeltaEdge edge:edges){
			if(edge.type == type){
				DeltaNode match = null;
				if(bOut){
					match = (DeltaNode)edge.to;
				}else{
					match = (DeltaNode)edge.from;
				}
				String key = match.getKind();
				Integer no = table.get(key);
				if(no==null){
					no = 1;
				}else{
					no++;
				}
				table.put(key, no);
			}
		}
		return table;
	}



	private double calculateInControlCost(DeltaNode leftNode, DeltaNode rightNode) {
		// TODO Auto-generated method stub
		Hashtable<String, Integer> leftKinds = extractKinds(leftGraph, leftNode, AbstractEdge.CONTROL_FLOW, false);
		Hashtable<String, Integer> rightKinds = extractKinds(rightGraph, rightNode, AbstractEdge.CONTROL_FLOW, false);
		return calculateCost(leftKinds, rightKinds);
	}



	private double calculateOutDataCost(DeltaNode leftNode, DeltaNode rightNode) {
		// TODO Auto-generated method stub
		Hashtable<String, Integer> leftKinds = extractKinds(leftGraph, leftNode, AbstractEdge.DATA_FLOW, true);
		Hashtable<String, Integer> rightKinds = extractKinds(rightGraph, rightNode, AbstractEdge.DATA_FLOW, true);
		return calculateCost(leftKinds, rightKinds);
	}



	private double calculateInDataCost(DeltaNode leftNode, DeltaNode rightNode) {
		// TODO Auto-generated method stub
		Hashtable<String, Integer> leftKinds = extractKinds(leftGraph, leftNode, AbstractEdge.DATA_FLOW, false);
		Hashtable<String, Integer> rightKinds = extractKinds(rightGraph, rightNode, AbstractEdge.DATA_FLOW, false);
		return calculateCost(leftKinds, rightKinds);
	}
	
	

	
	
}

