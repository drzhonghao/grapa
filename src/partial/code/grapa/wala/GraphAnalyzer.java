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

	public ArrayList<Stack<DeltaNode>> findAllPaths(DeltaNode from, DeltaNode to) {
		pt.reset();
		pt.findAllPaths(from, to);
		ArrayList<Stack<DeltaNode>> paths = pt.getConnectionPaths();
		return paths;		
	}

	public ArrayList<Stack<DeltaNode>> findValidPath(DeltaNode from, DeltaNode to) {
		ArrayList<Stack<DeltaNode>> paths = findAllPaths(from, to);
		ArrayList<Stack<DeltaNode>> validPaths = new ArrayList<Stack<DeltaNode>>();
		for(Stack<DeltaNode> path:paths) {
			boolean bValid = true;
			for(int i=0; i<path.size()-1; i++) {
				DeltaNode fromNode = path.elementAt(i);
				DeltaNode toNode = path.elementAt(i+1);
				DeltaEdge edge = graph.findEdge(fromNode, toNode);
				if(edge.type!=DeltaEdge.DATA_FLOW) {
					if(isCheckCondition(edge)) {
						bValid = false;
						break;
					}
				}
			}
			DeltaEdge edge = graph.findEdge(from, path.get(0));
			if(edge.type!=DeltaEdge.DATA_FLOW) {
				if(isCheckCondition(edge)) {
					bValid = false;
				}
			}
			edge = graph.findEdge(path.get(path.size()-1), to);
			if(edge.type!=DeltaEdge.DATA_FLOW) {
				if(isCheckCondition(edge)) {
					bValid = false;
				}
			}
			
			if(bValid) {
				validPaths.add(path);
			}
		}
		return validPaths;
	}
	

	private boolean isCheckCondition(DeltaEdge edge) {
		return isCheckCondition(edge.from)||isCheckCondition(edge.to);
	}

	private boolean isCheckCondition(DeltaNode node) {
		return !(node.label.indexOf("conditional branch")<0&&node.label.indexOf("switch")<0);
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
