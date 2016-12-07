package partial.code.grapa.tool;

import java.util.Hashtable;

import com.ibm.wala.ssa.IR;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.StaxDriver;

import edu.uci.ics.jung.graph.DirectedSparseGraph;
import partial.code.grapa.delta.graph.GraphComparator;
import partial.code.grapa.delta.graph.StatementEdge;
import partial.code.grapa.delta.graph.StatementNode;
import partial.code.grapa.delta.graph.xml.XmlEdge;
import partial.code.grapa.delta.graph.xml.XmlNode;

public class GraphTranslateTool {
	
	
	public static DirectedSparseGraph<XmlNode, XmlEdge> translateDeltaToXmlGraph(
			DirectedSparseGraph<StatementNode, StatementEdge> graph, IR lir, IR rir) {
		// TODO Auto-generated method stub
		
		DirectedSparseGraph<XmlNode, XmlEdge> g = new DirectedSparseGraph<XmlNode, XmlEdge>(); 
		Hashtable<StatementNode, XmlNode> table = new Hashtable<StatementNode, XmlNode>();
		for(StatementNode sn:graph.getVertices()){		
			String label = null;
			if(sn.side == StatementNode.LEFT){
				label = "l: "+GraphComparator.getVisualLabel(lir, sn.statement);
			}else if(sn.side == StatementNode.RIGHT){
				label = "r: "+GraphComparator.getVisualLabel(rir, sn.statement);
			}
			XmlNode node = new XmlNode(label, sn.side, sn.bModified);
			g.addVertex(node);
			table.put(sn, node);
		}

		for(StatementEdge edge:graph.getEdges()){
			XmlNode from = table.get(edge.from);
			XmlNode to = table.get(edge.to);
			XmlEdge e = new XmlEdge(from, to, edge.type);
			g.addEdge(e, from, to);
		}
		return g;
	}
}
