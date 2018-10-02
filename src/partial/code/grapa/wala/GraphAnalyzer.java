package partial.code.grapa.wala;

import java.io.File;
import java.util.ArrayList;

import edu.uci.ics.jung.graph.DirectedSparseGraph;
import partial.code.grapa.algorithm.PathTool;
import partial.code.grapa.delta.graph.DeltaEdge;
import partial.code.grapa.delta.graph.DeltaGraphUtil;
import partial.code.grapa.delta.graph.DeltaNode;

public class GraphAnalyzer {

	private DirectedSparseGraph<DeltaNode, DeltaEdge> graph;
	private PathTool pt;
	private MethodEntry point;

	
	
	public GraphAnalyzer(DirectedSparseGraph<DeltaNode, DeltaEdge> graph, MethodEntry point) {
		this.graph = graph;	
		pt = new PathTool(graph);
		this.point = point;
	}

	public void saveGraph(String resultDir) {
		File d = new File(resultDir+"/"+point.getShortTypeName());
		d.mkdirs();
		DeltaGraphUtil gu =  new DeltaGraphUtil("c:/Program Files (x86)/Graphviz2.38/bin/dot.exe");
		gu.writeToPdfGraph(graph, d.getAbsolutePath()+"/"+point.getShortName()+".pdf");

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
			if(n.label.compareTo(label)==0) {
				match = n;
				break;
			}
		}
		return match;
	}
}
