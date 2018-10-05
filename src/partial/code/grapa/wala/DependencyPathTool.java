package partial.code.grapa.wala;

import java.util.ArrayList;
import java.util.Stack;

import edu.uci.ics.jung.graph.DirectedSparseGraph;
import partial.code.grapa.algorithm.PathTool;
import partial.code.grapa.delta.graph.DeltaEdge;
import partial.code.grapa.delta.graph.DeltaNode;

public class DependencyPathTool extends PathTool{
	private DeltaNode from;
	private DeltaNode to;

	public DependencyPathTool(DirectedSparseGraph<DeltaNode, DeltaEdge> g, DeltaNode from, DeltaNode to) {
		super(g);
		this.from = from;
		this.to = to;
	}

	@Override
	protected boolean isValid(Stack<DeltaNode> path) {
		boolean bValid = true;
		for(int i=0; i<path.size()-1; i++) {
			DeltaNode fromNode = path.elementAt(i);
			DeltaNode toNode = path.elementAt(i+1);
			DeltaEdge edge = graph.findEdge(fromNode, toNode);
			if(edge.type!=DeltaEdge.DATA_FLOW) {
				if(!isCheckCondition(edge)) {
					bValid = false;
					break;
				}
			}
		}
		if(path.size()>0) {
			DeltaEdge edge = graph.findEdge(from, path.get(0));
			if(edge.type!=DeltaEdge.DATA_FLOW) {
				if(!isCheckCondition(edge)) {
					bValid = false;
				}
			}
//			edge = graph.findEdge(path.get(path.size()-1), to);			
//			if(edge!=null&&edge.type!=DeltaEdge.DATA_FLOW) {
//				if(isCheckCondition(edge)) {
//					bValid = false;
//				}
//			}
		}
		return bValid;
	}

	private boolean isCheckCondition(DeltaEdge edge) {
//		return isCheckCondition(edge.from)||isCheckCondition(edge.to);
		return isCheckCondition(edge.to);
	}

	private boolean isCheckCondition(DeltaNode node) {
		return node.label.indexOf("conditional branch")>0||node.label.indexOf("switch")>0;
	}
	
	public void printPath() {
		for(Stack<DeltaNode> path:this.connectionPaths) {
			System.out.println("---------p----------------------");
			DeltaNode t = path.get(0);
			DeltaEdge e = graph.findEdge(from, t);
			if(e.type == DeltaEdge.CONTROL_FLOW) {
				System.out.println("-c->");
			}else if(e.type == DeltaEdge.DATA_FLOW) {
				System.out.println("-d->");
			}
			System.out.println(from);
			for(int i=0; i<path.size()-1; i++) {
				DeltaNode from = path.get(i);
				DeltaNode to = path.get(i+1);
				DeltaEdge edge = graph.findEdge(from, to);
				System.out.println(from);
				if(edge.type == DeltaEdge.CONTROL_FLOW) {
					System.out.println("-c->");
				}else if(edge.type == DeltaEdge.DATA_FLOW) {
					System.out.println("-d->");
				}
			}
			System.out.println(path.get(path.size()-1));
			DeltaNode f = path.get(path.size()-1);
			e = graph.findEdge(f, to);
			if(e.type == DeltaEdge.CONTROL_FLOW) {
				System.out.println("-c->");
			}else if(e.type == DeltaEdge.DATA_FLOW) {
				System.out.println("-d->");
			}
			System.out.println(to);
		}
		
	} 
}
