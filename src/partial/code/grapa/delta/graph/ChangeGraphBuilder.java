package partial.code.grapa.delta.graph;

import java.util.Hashtable;

import partial.code.grapa.delta.graph.xml.XmlEdge;
import partial.code.grapa.delta.graph.xml.XmlNode;


import edu.uci.ics.jung.graph.DirectedSparseGraph;

public class ChangeGraphBuilder extends GraphComparator{

	public ChangeGraphBuilder(
			DirectedSparseGraph<XmlNode, XmlEdge> oldGraph,			
			DirectedSparseGraph<XmlNode, XmlEdge> newGraph) {
		super(oldGraph,  newGraph);
		// TODO Auto-generated constructor stub
	}

	public DirectedSparseGraph<XmlNode, XmlEdge> extractChangeGraph() {
		// TODO Auto-generated method stub
		Hashtable<XmlNode, XmlNode> vm = this.extractNodeMappings();
		DirectedSparseGraph<XmlNode, XmlEdge> graph = new DirectedSparseGraph<XmlNode, XmlEdge>();
		//add left nodes
		for(XmlNode s:leftGraph.getVertices()){
			s.side = XmlNode.LEFT;
			graph.addVertex(s);
		}
		
		//add right nodes
		for(XmlNode s:rightGraph.getVertices()){
			s.side = XmlNode.RIGHT;
			graph.addVertex(s);
		}
		
		for(XmlNode n1:graph.getVertices()){
			for(XmlNode n2:graph.getVertices()){
				if(!n1.equals(n2)&&n1.side==n2.side){
					XmlEdge edge;
					if(n1.side==XmlNode.LEFT){
						edge = leftGraph.findEdge(n1, n2);
					}else{
						edge = rightGraph.findEdge(n1, n2);
					}
					if(edge != null){
						graph.addEdge(edge, n1, n2);
					}
				}			
			}
		}
		
		for(XmlNode n1:graph.getVertices()){
			XmlNode n2 = vm.get(n1);
			if(n2!=null){
				if(calculateCost(n1,n2)==0){
					n1.bModified = false;
					n2.bModified = false;
				}else{
					XmlEdge edge = graph.findEdge(n1, n2);
					if(edge==null){
						edge = new XmlEdge(n1, n2, XmlEdge.CHANGE);
						graph.addEdge(edge, n1, n2);
					}
				}
			}
		}
		return graph;
	}
}
 