package partial.code.grapa.delta.graph.mapping;

import edu.uci.ics.jung.graph.DirectedSparseGraph;
import partial.code.grapa.delta.graph.DeltaEdge;
import partial.code.grapa.delta.graph.DeltaNode;
import partial.code.grapa.hungarian.HungarianMapping;

public abstract class GraphMapping extends HungarianMapping{
	protected DirectedSparseGraph<DeltaNode, DeltaEdge> leftGraph;
	protected DirectedSparseGraph<DeltaNode, DeltaEdge> rightGraph;
	
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
		// TODO Auto-generated method stub
		return leftGraph.getVertices().toArray()[i];
	}
	
	protected Object getRightItem(int i) {
		// TODO Auto-generated method stub
		return rightGraph.getVertices().toArray()[i];
	}
}
