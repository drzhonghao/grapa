package partial.code.grapa.commit;

import java.util.Hashtable;

import org.eclipse.jdt.core.dom.ASTNode;


import edu.uci.ics.jung.graph.DirectedSparseGraph;
import partial.code.grapa.delta.graph.DeltaEdge;
import partial.code.grapa.delta.graph.DeltaNode;
import partial.code.grapa.mapping.ClientMethod;

public class MethodDelta {	
	public ClientMethod oldMethod;
	public ClientMethod newMethod;
	public DirectedSparseGraph<DeltaNode, DeltaEdge> graph;
	public DirectedSparseGraph<DeltaNode, DeltaEdge> deltaGraph;
	public Hashtable<DeltaNode, ASTNode> astTable;

	
	public MethodDelta(	DirectedSparseGraph<DeltaNode, DeltaEdge> g, DirectedSparseGraph<DeltaNode, DeltaEdge> dg, Hashtable<DeltaNode, ASTNode> table) {
		// TODO Auto-generated constructor stub
		graph = g;
		deltaGraph = dg;
		astTable = table;
	}
}
