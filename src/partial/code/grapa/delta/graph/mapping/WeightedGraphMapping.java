package partial.code.grapa.delta.graph.mapping;

import edu.uci.ics.jung.graph.DirectedSparseGraph;
import partial.code.grapa.delta.graph.DeltaEdge;
import partial.code.grapa.delta.graph.DeltaNode;
import partial.code.grapa.hungarian.HungarianMapping;

public class WeightedGraphMapping extends GraphMapping{


	public WeightedGraphMapping(DirectedSparseGraph<DeltaNode, DeltaEdge> oldGraph,
			DirectedSparseGraph<DeltaNode, DeltaEdge> newGraph) {
		super(oldGraph, newGraph);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void calculateCostMatrix() {
		// TODO Auto-generated method stub
		
	}

}
