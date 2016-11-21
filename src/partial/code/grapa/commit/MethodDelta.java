package partial.code.grapa.commit;

import com.ibm.wala.ssa.IR;

import edu.uci.ics.jung.graph.DirectedSparseGraph;
import partial.code.grapa.dependency.graph.StatementEdge;
import partial.code.grapa.dependency.graph.StatementNode;
import partial.code.grapa.mapping.ClientMethod;

public class MethodDelta {	
	public ClientMethod oldMethod;
	public ClientMethod newMethod;
	public DirectedSparseGraph<StatementNode, StatementEdge> graph;
	public DirectedSparseGraph<StatementNode, StatementEdge> deltaGraph;
	
	public MethodDelta(ClientMethod om, ClientMethod nm,
			DirectedSparseGraph<StatementNode, StatementEdge> g, DirectedSparseGraph<StatementNode, StatementEdge> dg) {
		// TODO Auto-generated constructor stub
		oldMethod = om;
		newMethod = nm;
		graph = g;
		deltaGraph = dg;
	}
}
