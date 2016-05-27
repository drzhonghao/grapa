package partial.code.grapa.commit;

import com.ibm.wala.ssa.IR;

import edu.uci.ics.jung.graph.DirectedSparseGraph;
import partial.code.grapa.dependency.graph.StatementEdge;
import partial.code.grapa.dependency.graph.StatementNode;
import partial.code.grapa.mapping.ClientMethod;

public class MethodDelta {	
	public String oldKey;
	public String oldName;
	public String oldSig;
	public String newKey;
	public String newName;
	public String newSig;
	public DirectedSparseGraph<StatementNode, StatementEdge> deltaGraph;
	public IR leftIR;
	public IR rightIR;
	public DirectedSparseGraph<StatementNode, StatementEdge> leftGraph;
	public DirectedSparseGraph<StatementNode, StatementEdge> rightGraph;
	
	public MethodDelta(ClientMethod oldMethod, ClientMethod newMethod,
			DirectedSparseGraph<StatementNode, StatementEdge> dg, IR leftIR, IR rightIR, DirectedSparseGraph<StatementNode, StatementEdge> leftGraph, DirectedSparseGraph<StatementNode, StatementEdge> rightGraph) {
		// TODO Auto-generated constructor stub
		oldKey = oldMethod.key;
		oldName = oldMethod.methodName;
		oldSig = oldMethod.sig;
		newKey = newMethod.key;
		newName = newMethod.methodName;
		newSig = newMethod.sig;
		deltaGraph = dg;
		this.leftIR = leftIR;
		this.rightIR = rightIR;
		this.leftGraph = leftGraph;
		this.rightGraph = rightGraph;
	}
}
