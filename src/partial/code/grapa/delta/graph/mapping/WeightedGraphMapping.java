package partial.code.grapa.delta.graph.mapping;

import java.util.Collection;
import java.util.Hashtable;

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
	protected Hashtable<String, Integer> extractKinds(DirectedSparseGraph<DeltaNode, DeltaEdge> graph, DeltaNode node,
			int type, boolean bOut) {
		// TODO Auto-generated method stub
		Hashtable<String, Integer> table = new Hashtable<String, Integer>();
		Collection<DeltaEdge> edges = null;
		if(bOut){
			edges = graph.getOutEdges(node);
		}else{
			edges = graph.getInEdges(node);
		}
		if(edges!=null) {
			for(DeltaEdge edge:edges){
				if(edge.type == type){
					String key = edge.weight+"";
					Integer no = table.get(key);
					if(no==null){
						no = 1;
					}else{
						no++;
					}
					table.put(key, no);
					DeltaNode match;
					if(bOut){
						match = (DeltaNode)edge.to;
					}else{
						match = (DeltaNode)edge.from;
					}
					key = match.getKind();
					no = table.get(key);
					if(no==null){
						no = 1;
					}else{
						no++;
					}
					table.put(key, no);
				}
			}
		}
		return table;
	}

	
}
