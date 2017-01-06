package partial.code.grapa.tool;

import java.util.Hashtable;
import java.util.Iterator;

import com.ibm.wala.ipa.slicer.Statement;
import com.ibm.wala.ipa.slicer.Slicer.ControlDependenceOptions;
import com.ibm.wala.ipa.slicer.Slicer.DataDependenceOptions;
import com.ibm.wala.ssa.IR;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.StaxDriver;

import edu.uci.ics.jung.graph.DirectedSparseGraph;
import partial.code.grapa.delta.graph.GraphComparator;
import partial.code.grapa.delta.graph.StatementEdge;
import partial.code.grapa.delta.graph.StatementNode;
import partial.code.grapa.delta.graph.xml.LabelTool;
import partial.code.grapa.delta.graph.xml.XmlEdge;
import partial.code.grapa.delta.graph.xml.XmlNode;
import partial.code.grapa.dependency.graph.SDGwithPredicate;

public class GraphTranslateTool {
	
	public static DirectedSparseGraph<StatementNode, StatementEdge> translateSDG(
			SDGwithPredicate flowGraph) {
		// TODO Auto-generated method stub
		if(flowGraph == null){
			return null;
		}
		DirectedSparseGraph<StatementNode, StatementEdge> graph = new DirectedSparseGraph<StatementNode, StatementEdge>(); 
		Hashtable<Statement, StatementNode> table = new Hashtable<Statement, StatementNode>();
		Iterator<Statement> it = flowGraph.iterator();
		while(it.hasNext()){
			Statement statement = it.next();
			StatementNode node = new StatementNode(statement);
			table.put(statement, node);
			graph.addVertex(node);
		}
		
		flowGraph.reConstruct(DataDependenceOptions.NO_BASE_NO_EXCEPTIONS, ControlDependenceOptions.NONE);
		it = flowGraph.iterator();
		while(it.hasNext()){
			Statement s1 = it.next();
		
			Iterator<Statement> nodes = flowGraph.getSuccNodes(s1);
			while(nodes.hasNext()){
				Statement s2 = nodes.next();
				StatementNode from = table.get(s1);
				StatementNode to = table.get(s2);
				graph.addEdge(new StatementEdge(from, to, StatementEdge.DATA_FLOW), from, to);
			}
		}
		
		flowGraph.reConstruct(DataDependenceOptions.NONE, ControlDependenceOptions.FULL);
		it = flowGraph.iterator();
		while(it.hasNext()){
			Statement s1 = it.next();
			Iterator<Statement> nodes = flowGraph.getSuccNodes(s1);
			while(nodes.hasNext()){
				Statement s2 = nodes.next();
				StatementNode from = table.get(s1);
				StatementNode to = table.get(s2);
				StatementEdge edge = graph.findEdge(from, to);
				if(edge==null){
					graph.addEdge(new StatementEdge(from, to, StatementEdge.CONTROL_FLOW), from, to);
				}
			}
		}
		return graph;
	}
	
	public static DirectedSparseGraph<XmlNode, XmlEdge> translateDeltaToXmlGraph(
			DirectedSparseGraph<StatementNode, StatementEdge> graph, IR lir, IR rir) {
		// TODO Auto-generated method stub
		LabelTool lt = new LabelTool();
		lt.setIR(lir);
		LabelTool rt = new LabelTool();
		rt.setIR(rir);
		DirectedSparseGraph<XmlNode, XmlEdge> g = new DirectedSparseGraph<XmlNode, XmlEdge>(); 
		Hashtable<StatementNode, XmlNode> table = new Hashtable<StatementNode, XmlNode>();
		for(StatementNode sn:graph.getVertices()){		
			String label = null;
			if(sn.side == StatementNode.LEFT){
				label = "l: "+lt.getVisualLabel(sn.statement);
			}else if(sn.side == StatementNode.RIGHT){
				label = "r: "+rt.getVisualLabel(sn.statement);
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
