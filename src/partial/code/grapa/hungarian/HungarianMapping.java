package partial.code.grapa.hungarian;

import java.util.Hashtable;

import edu.uci.ics.jung.graph.DirectedSparseGraph;

import partial.code.grapa.delta.graph.DeltaNode;
import uk.ac.shef.wit.simmetrics.similaritymetrics.Levenshtein;

abstract public class HungarianMapping {

	protected double[][] costMatrix;
	public boolean bSwapSide;//true: does not swap left and right. false: does.
	protected Levenshtein stringComparator = new Levenshtein();
	protected abstract void calculateCostMatrix();
	protected abstract Object getLeftItem(int index);
	protected abstract Object getRightItem(int index);

	public Hashtable<Object, Object> extractNodeMappings() {
		// TODO Auto-generated method stub
		calculateCostMatrix();
		HungarianAlgorithm ha = new HungarianAlgorithm();
		Hashtable<Object, Object> vm = new Hashtable<Object, Object>();
		 
		if(costMatrix.length>0){
	        int[][] matching = ha.hgAlgorithm(costMatrix);
	        //mapped nodes
	        for(int i=0; i<matching.length; i++){
	        	Object v1 = getLeftItem(matching[i][0]);	
				Object v2 = getRightItem(matching[i][1]);
				if(bSwapSide){
					vm.put(v2, v1);
				}else{
					vm.put(v1, v2);
				}
	        }
		}
        return vm;
	}

	
	
	
	
}
