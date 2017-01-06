package partial.code.grapa.commit;

import java.util.Hashtable;

import org.eclipse.jdt.core.dom.ASTNode;


import edu.uci.ics.jung.graph.DirectedSparseGraph;
import partial.code.grapa.delta.graph.xml.XmlEdge;
import partial.code.grapa.delta.graph.xml.XmlNode;
import partial.code.grapa.mapping.ClientMethod;

public class MethodDelta {	
	public ClientMethod oldMethod;
	public ClientMethod newMethod;
	public DirectedSparseGraph<XmlNode, XmlEdge> graph;
	public DirectedSparseGraph<XmlNode, XmlEdge> deltaGraph;
	public Hashtable<XmlNode, ASTNode> astTable;

	
	public MethodDelta(	DirectedSparseGraph<XmlNode, XmlEdge> g, DirectedSparseGraph<XmlNode, XmlEdge> dg, Hashtable<XmlNode, ASTNode> table) {
		// TODO Auto-generated constructor stub
		graph = g;
		deltaGraph = dg;
		astTable = table;
	}
}
