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
import partial.code.grapa.delta.graph.xml.XmlEdge;
import partial.code.grapa.delta.graph.xml.XmlNode;

public class GraphComparator {
	private double[][] costMatrix;
	protected DirectedSparseGraph<XmlNode, XmlEdge> leftGraph;
	protected DirectedSparseGraph<XmlNode, XmlEdge> rightGraph;

	protected boolean mode;//true: does not swap left and right. false: does.
	
	protected Levenshtein stringComparator;
	
	protected Hashtable<Integer, String> leftValueTable;
	protected Hashtable<Integer, String> rightValueTable;
	
	protected Hashtable<Integer, String> leftIndexTable;
	protected Hashtable<Integer, String> rightIndexTable;
	
	
	public GraphComparator(
			DirectedSparseGraph<XmlNode, XmlEdge> oldGraph,			
			DirectedSparseGraph<XmlNode, XmlEdge> newGraph) {
		// TODO Auto-generated constructor stub
		if (oldGraph.getVertexCount()>newGraph.getVertexCount()){
			leftGraph = newGraph;
			rightGraph = oldGraph;
			mode = false;
		}else{
			leftGraph = oldGraph;
			rightGraph = newGraph;
			mode = true;
		}

		stringComparator = new Levenshtein();
	}
	


	

	public Hashtable<XmlNode, XmlNode> extractNodeMappings() {
		// TODO Auto-generated method stub
		calculateCostMatrix();
		HungarianAlgorithm ha = new HungarianAlgorithm();
        int[][] matching = ha.hgAlgorithm(costMatrix);
        
        //mapped nodes
        Hashtable<XmlNode, XmlNode> vm = new Hashtable<XmlNode, XmlNode>();
        for(int i=0; i<matching.length; i++){
			XmlNode v1 = (XmlNode)leftGraph.getVertices().toArray()[matching[i][0]];
			XmlNode v2 = (XmlNode)rightGraph.getVertices().toArray()[matching[i][1]];
			vm.put(v1, v2);
        }
        return vm;
	}
	
	private void calculateCostMatrix() {
		// TODO Auto-generated method stub
		costMatrix =  new double[leftGraph.getVertexCount()][rightGraph.getVertexCount()];
		double inNodeCost;
		double outNodeCost;
		double nodeCost;
//		double lineCost;

		for (int i = 0; i < leftGraph.getVertexCount(); i++) {
			XmlNode leftNode = (XmlNode)leftGraph.getVertices().toArray()[i];

            for (int j = 0; j < rightGraph.getVertexCount(); j++) {
            	XmlNode rightNode = (XmlNode)rightGraph.getVertices().toArray()[j];
            	inNodeCost = calculateIndegreeCost(leftNode, rightNode);
            	outNodeCost = calculateOutDegreeCost(leftNode, rightNode);
                nodeCost = calculateNodeCost(leftNode, rightNode);
//                lineCost = calculateLineCost(leftGraph.getVertexCount(), leftNode, rightGraph.getVertexCount(), rightNode);
                costMatrix[i][j] = inNodeCost+outNodeCost+nodeCost;
            }
        }
	}

//	private double calculateLineCost(int leftSize, XmlNode leftNode,
//			int rightSize, XmlNode rightNode) {
//		// TODO Auto-generated method stub
//		double cost = 0;
//		if(leftNode.statement instanceof NormalStatement && rightNode.statement instanceof NormalStatement){
//			NormalStatement leftstatement = (NormalStatement)leftNode.statement;
//			NormalStatement rightstatement = (NormalStatement)rightNode.statement;
//			SSAInstruction leftins = leftstatement.getInstruction();
//			SSAInstruction rightins = rightstatement.getInstruction();
//			cost = Math.abs(leftins.iindex-rightins.iindex);
//			int size = leftSize>rightSize?leftSize:rightSize;
//			cost = cost/size;
//		}
//		return cost;
//	}

	private double calculateOutDegreeCost(XmlNode leftNode,
			XmlNode rightNode) {
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

	private double calculateIndegreeCost(XmlNode leftNode,
			XmlNode rightNode) {
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

	protected double calculateNodeCost(XmlNode leftNode,
			XmlNode rightNode) {
		// TODO Auto-generated method stub
//		String leftLine;
//		String rightLine;
//		
//		leftLine = getComparedLabel(leftNode);
//		rightLine = getComparedLabel(rightNode);
	
		double distance = stringComparator.getUnNormalisedSimilarity(leftNode.label, rightNode.label);
//		int length = leftLine.length()>rightLine.length()?leftLine.length():rightLine.length();
//		return distance/length;
		return distance;
	}
	
	


	protected double calculateCost(XmlNode v1, XmlNode v2) {
		// TODO Auto-generated method stub
		double inNodeCost = calculateIndegreeCost(v1, v2);
    	double outNodeCost = calculateOutDegreeCost(v1, v2);
        double nodeCost = calculateNodeCost(v1, v2);
        return inNodeCost+outNodeCost+nodeCost;
	}
	
	

	
	
}

