package partial.code.grapa.algorithm;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Stack;

import edu.uci.ics.jung.graph.DirectedSparseGraph;
import partial.code.grapa.delta.graph.DeltaEdge;
import partial.code.grapa.delta.graph.DeltaNode;

public class PathTool {

	protected DirectedSparseGraph<DeltaNode, DeltaEdge> graph;
	private Stack<DeltaNode> connectionPath = new Stack<DeltaNode> ();
	protected ArrayList<Stack<DeltaNode>> connectionPaths = new ArrayList<Stack<DeltaNode>>();

	public PathTool(DirectedSparseGraph<DeltaNode, DeltaEdge> g) {
		this.graph = g;
	}
	
	// Push to connectionsPath the object that would be passed as the parameter 'node' into the method below
	public void findAllPaths(DeltaNode from, DeltaNode to) {
		Collection<DeltaNode> nextNodes = graph.getSuccessors(from);	
	    for (DeltaNode nextNode : nextNodes) {
	       if (nextNode.equals(to)) {
	    	   Stack<DeltaNode> temp = new Stack<DeltaNode>();
	           for (DeltaNode node1 : connectionPath)
	               temp.add(node1);
	           connectionPaths.add(temp);	           
	       } else if (!connectionPath.contains(nextNode)&&isValid(connectionPath)) {
	           connectionPath.push(nextNode);
	           findAllPaths(nextNode, to);
	           connectionPath.pop(); 
	        }
	    }
	}

	protected boolean isValid(Stack<DeltaNode> path) {
		return true;
	}

	public ArrayList<Stack<DeltaNode>> getConnectionPaths() {
		return connectionPaths;
	}

	public void reset() {
		this.connectionPath.clear();
		this.connectionPaths.clear();
	}
	
	
}
