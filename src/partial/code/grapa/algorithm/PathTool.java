package partial.code.grapa.algorithm;

import java.util.ArrayList;
import java.util.Stack;

import edu.uci.ics.jung.graph.DirectedSparseGraph;
import partial.code.grapa.delta.graph.DeltaEdge;
import partial.code.grapa.delta.graph.DeltaNode;

public class PathTool {

	private DirectedSparseGraph<DeltaNode, DeltaEdge> graph;
	private Stack<DeltaNode> connectionPath = new Stack<DeltaNode> ();
	private ArrayList<Stack<DeltaNode>> connectionPaths = new ArrayList<Stack<DeltaNode>>();

	public PathTool(DirectedSparseGraph<DeltaNode, DeltaEdge> g) {
		this.graph = g;
	}
	
	// Push to connectionsPath the object that would be passed as the parameter 'node' into the method below
	public void findAllPaths(DeltaNode from, DeltaNode to) {
		ArrayList<DeltaNode> nextNodes = new ArrayList<DeltaNode>();
		for(DeltaEdge edge:graph.getOutEdges(from)) {
			nextNodes.add(edge.to);
		}		
	    for (DeltaNode nextNode : nextNodes) {
	       if (nextNode.equals(to)) {
	    	   Stack<DeltaNode> temp = new Stack<DeltaNode>();
	           for (DeltaNode node1 : connectionPath)
	               temp.add(node1);
	           connectionPaths.add(temp);
	       } else if (!connectionPath.contains(nextNode)) {
	           connectionPath.push(nextNode);
	           findAllPaths(nextNode, to);
	           connectionPath.pop();
	        }
	    }
	}

	public ArrayList<Stack<DeltaNode>> getConnectionPaths() {
		return connectionPaths;
	}
	
	
}
