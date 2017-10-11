package partial.code.grapa.delta.graph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Set;

import edu.uci.ics.jung.graph.DirectedSparseGraph;
import partial.code.grapa.tool.LabelUtil;

public class ChangeGraphBuilder extends GraphMapping{

	
	public ChangeGraphBuilder(
			DirectedSparseGraph<DeltaNode, DeltaEdge> oldGraph,			
			DirectedSparseGraph<DeltaNode, DeltaEdge> newGraph) {
		super(oldGraph,  newGraph);
		// TODO Auto-generated constructor stub
	}

	public DirectedSparseGraph<DeltaNode, DeltaEdge> extractChangeGraph() {
		// TODO Auto-generated method stub
		Hashtable<Object, Object> vm = this.extractNodeMappings();
		DirectedSparseGraph<DeltaNode, DeltaEdge> graph = new DirectedSparseGraph<DeltaNode, DeltaEdge>();
		//add left nodes
		for(DeltaNode s:leftGraph.getVertices()){
			if(!this.bSwapSide){
				s.side = DeltaNode.LEFT;
				s.label = "l:"+s.label;
			}else{
				s.side = DeltaNode.RIGHT;
				s.label = "r:"+s.label;
			}
			graph.addVertex(s);
		}
		
		//add right nodes
		for(DeltaNode s:rightGraph.getVertices()){
			if(!this.bSwapSide){
				s.side = DeltaNode.RIGHT;
				s.label = "r:"+s.label;
			}else{
				s.side = DeltaNode.LEFT;
				s.label = "l:"+s.label;
			}
			graph.addVertex(s);
		}
		
		//add left edges;
		for(DeltaEdge e:leftGraph.getEdges()){
			graph.addEdge(e, (DeltaNode)e.from, (DeltaNode)e.to);
		}
		//add right edges
		for(DeltaEdge e:rightGraph.getEdges()){
			graph.addEdge(e, (DeltaNode)e.from, (DeltaNode)e.to);
		}
		
		//add modification edges
		for(DeltaNode n1:graph.getVertices()){
			DeltaNode n2 = (DeltaNode) vm.get(n1);
			if(n2!=null){
				if(calculateCost(n1,n2)==0){
					n1.bModified = false;
					n2.bModified = false;
				}else{
					n1.bModified = true;
					n2.bModified = true;
					DeltaEdge edge = null;
					if(!this.bSwapSide){
						edge = new DeltaEdge(n1, n2, DeltaEdge.CHANGE);
						graph.addEdge(edge, n1, n2);
					}else{
						edge = new DeltaEdge(n2, n1, DeltaEdge.CHANGE);
						graph.addEdge(edge, n2, n1);
					}
					
					
				}
			}
		}
		return graph;
	}


	public double calculateCosts(Hashtable<DeltaNode, DeltaNode> vm, boolean bOnlyModified) {
		// TODO Auto-generated method stub
		double cost = 0;
		cost = calculateNameCosts(vm, bOnlyModified);
		cost += calcluateStructureCosts(vm, bOnlyModified);
		cost = cost/2;
		return cost;
	}

	public double calcluateStructureCosts(Hashtable<DeltaNode, DeltaNode> vm, boolean bOnlyModified) {
		// TODO Auto-generated method stub
		double cost = 0;
		if(vm.size()==0){
			cost = 1;
		}else{
			int count = 0;
			int total = 0;
			for(DeltaNode l1:vm.keySet()){
				for(DeltaNode l2:vm.keySet()){
					DeltaEdge le = leftGraph.findEdge(l1, l2);
					if(le!=null){
						DeltaNode r1 = vm.get(l1);
						DeltaNode r2 = vm.get(l2);
						DeltaEdge re = leftGraph.findEdge(r1, r2);
						if(re!=null&&re.type==le.type){
							count++;
						}
						total++;
					}
				}
			}
			if(total!=0){
				cost = 1 - ((double)count)/total;
			}else{
				cost = 0;
			}
		}
		return cost;
	}

	public double calculateNameCosts(Hashtable<DeltaNode, DeltaNode> vm, boolean bOnlyModified) {
		double total = 0;
		double cost = 0;
		for(DeltaNode leftNode:vm.keySet()){
			DeltaNode rightNode = vm.get(leftNode);
			if(bOnlyModified){
				if(leftNode.bModified){
					cost += calculateNodeNameCost(leftNode, rightNode);
					total += 1;
				}
			}else{
				cost += calculateNodeNameCost(leftNode, rightNode);
				total += 1;	
			}
		}
		if(total>0){
			cost = cost/total;
		}else{
			cost = 1;
		}
		return cost;
	}

	



	
}
 