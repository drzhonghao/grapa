package partial.code.grapa.delta.graph;

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

public class GraphComparator {
	public static final int CONCRETE = 0;
	public static final int ABSTRACT = 0;
	
	private double[][] costMatrix;
	protected DirectedSparseGraph<DeltaNode, DeltaEdge> leftGraph;
	protected DirectedSparseGraph<DeltaNode, DeltaEdge> rightGraph;

	protected boolean bSwapSide;//true: does not swap left and right. false: does.
	
	protected Levenshtein stringComparator;
	
	protected Hashtable<Integer, String> leftValueTable;
	protected Hashtable<Integer, String> rightValueTable;
	
	protected Hashtable<Integer, String> leftIndexTable;
	protected Hashtable<Integer, String> rightIndexTable;
	private int mode;
	
	
	public GraphComparator(
			DirectedSparseGraph<DeltaNode, DeltaEdge> oldGraph,			
			DirectedSparseGraph<DeltaNode, DeltaEdge> newGraph, int mode) {
		// TODO Auto-generated constructor stub
		if (oldGraph.getVertexCount()>newGraph.getVertexCount()){
			leftGraph = newGraph;
			rightGraph = oldGraph;
			bSwapSide = false;
		}else{
			leftGraph = oldGraph;
			rightGraph = newGraph;
			bSwapSide = true;
		}
		this.mode = mode;
		stringComparator = new Levenshtein();
	}
	


	

	public Hashtable<DeltaNode, DeltaNode> extractNodeMappings() {
		// TODO Auto-generated method stub
		calculateCostMatrix();
		HungarianAlgorithm ha = new HungarianAlgorithm();
        int[][] matching = ha.hgAlgorithm(costMatrix);
        
        //mapped nodes
        Hashtable<DeltaNode, DeltaNode> vm = new Hashtable<DeltaNode, DeltaNode>();
        for(int i=0; i<matching.length; i++){
			DeltaNode v1 = (DeltaNode)leftGraph.getVertices().toArray()[matching[i][0]];
			DeltaNode v2 = (DeltaNode)rightGraph.getVertices().toArray()[matching[i][1]];
			vm.put(v1, v2);
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


	private double calculateOutDegreeCost(DeltaNode leftNode,
			DeltaNode rightNode) {
		double outNodeCost = 0;
		try{
			if((leftGraph.outDegree(leftNode) + rightGraph.outDegree(rightNode))!=0){
				outNodeCost = (double)Math.abs(leftGraph.outDegree(leftNode) -  rightGraph.outDegree(rightNode))/(leftGraph.outDegree(leftNode) + rightGraph.outDegree(rightNode));
			}else{
				outNodeCost = 0;
			}
		}catch(Exception e){
//			System.out.println("jung internal error at: "+leftNode.toString());
		}
		return outNodeCost;
	}

	private double calculateIndegreeCost(DeltaNode leftNode,
			DeltaNode rightNode) {
		double inNodeCost = 0;
		try{
			if((leftGraph.inDegree(leftNode) + rightGraph.inDegree(rightNode))!=0){
				inNodeCost = (double)Math.abs(leftGraph.inDegree(leftNode) - rightGraph.inDegree(rightNode))/(leftGraph.inDegree(leftNode) + rightGraph.inDegree(rightNode));
			}else{
				inNodeCost = 0;
			}
		}catch(Exception e){
//			System.out.println("jung internal error at "+leftNode.toString());
		}
		return inNodeCost;
	}

	protected double calculateNodeNameCost(DeltaNode leftNode,
			DeltaNode rightNode) {
		// TODO Auto-generated method stub
		double distance = 0;
		if(mode == GraphComparator.CONCRETE){
			stringComparator.getUnNormalisedSimilarity(leftNode.getComparedLabel(), rightNode.getComparedLabel());
		}else if(mode == GraphComparator.ABSTRACT){
			stringComparator.getUnNormalisedSimilarity(leftNode.getKind(), rightNode.getKind());
		}
		return distance;
	}
	
	


	protected double calculateCost(DeltaNode v1, DeltaNode v2) {
		// TODO Auto-generated method stub
		double inNodeCost = calculateIndegreeCost(v1, v2);
    	double outNodeCost = calculateOutDegreeCost(v1, v2);
        double nodeNameCost = calculateNodeNameCost(v1, v2);
        return inNodeCost+outNodeCost+nodeNameCost;
	}
	
	

	
	
}

