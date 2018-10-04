package partial.code.grapa.wala;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Stack;

import edu.uci.ics.jung.graph.DirectedSparseGraph;
import partial.code.grapa.algorithm.PathTool;
import partial.code.grapa.delta.graph.DeltaEdge;
import partial.code.grapa.delta.graph.DeltaGraphUtil;
import partial.code.grapa.delta.graph.DeltaNode;

public class GraphAnalyzer {

	private DirectedSparseGraph<DeltaNode, DeltaEdge> graph;
	private MethodEntry point;

	
	
	public GraphAnalyzer(DirectedSparseGraph<DeltaNode, DeltaEdge> graph, MethodEntry point) {
		this.graph = graph;			
		this.point = point;
	}

	public void saveGraph(String resultDir) {
		File d = new File(resultDir+"/"+point.getShortTypeName());
		d.mkdirs();
		DeltaGraphUtil gu =  new DeltaGraphUtil("c:/Program Files (x86)/Graphviz2.38/bin/dot.exe");
		gu.writeToPdfGraph(graph, d.getAbsolutePath()+"/"+point.getFileName()+".pdf");

	}
	

	public ArrayList<DeltaNode> extractExceptionNodes() {
		ArrayList<DeltaNode> nodes = new ArrayList<DeltaNode>();
		for(DeltaNode node:graph.getVertices()) {
			String label = node.label;
			if(label.indexOf("throw ")>=0) {
				nodes.add(node);
			}
		}
		return nodes;
	}
	
	public DeltaNode extractParaNode(int i, boolean isStatic) {
		int index;
		if(isStatic) {
			index = i+1;//no this
		}else {
			index = i+2;//param1 is this
		}		
		String label = "PARAM_CALLEE "+index;
		DeltaNode match = null;
		for(DeltaNode n:graph.getVertices()) {
			if(n.label.startsWith(label)) {
				match = n;
				break;
			}
		}
		return match;
	}


	public ArrayList<Stack<DeltaNode>> findValidPath(DeltaNode from, DeltaNode to) {
		DependencyPathTool pt = new DependencyPathTool(graph, from, to);
		pt.findAllPaths(from, to);
		return pt.getConnectionPaths();
	}
	

	
	
	public String extractExceptionName(DeltaNode exceptionNode) {
		Collection<DeltaEdge> edges = graph.getInEdges(exceptionNode);
		String name = null;
		for(DeltaEdge edge:edges) {
			if(edge.from.label.indexOf("new <Application")>=0) {
				int mark = edge.from.label.indexOf(",");
				name = edge.from.label.substring(mark+1);
				mark = name.indexOf(">");
				name = name.substring(0, mark);
				break;
			}
		}
		return name;
	}

}
