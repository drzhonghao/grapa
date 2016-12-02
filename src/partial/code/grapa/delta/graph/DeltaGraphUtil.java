package partial.code.grapa.delta.graph;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Hashtable;

import com.ibm.wala.examples.drivers.PDFTypeHierarchy;
import com.ibm.wala.ssa.IR;
import com.ibm.wala.util.WalaException;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.StaxDriver;

import edu.uci.ics.jung.graph.DirectedSparseGraph;
import partial.code.grapa.delta.graph.xml.XmlEdge;
import partial.code.grapa.delta.graph.xml.XmlNode;
import partial.code.grapa.dependency.graph.DeltaGraphDecorator;
import partial.code.grapa.dependency.graph.DependencyGraphUtil;
import partial.code.grapa.dependency.graph.StatementEdge;
import partial.code.grapa.dependency.graph.StatementNode;
import partial.code.grapa.tool.GraphUtil;

public class DeltaGraphUtil extends GraphUtil{
	
	public static void writeToXmlFile(
			DirectedSparseGraph<StatementNode, StatementEdge> graph,
			IR lir,
			IR rir,
			String filename) {
		String xmlFile = filename + ".xml";
		xmlFile = xmlFile.replaceAll("<", "");
		xmlFile = xmlFile.replaceAll(">", "");
		DirectedSparseGraph<XmlNode, XmlEdge> g = translateToXmlGraph(graph, lir, rir);
		XStream xstream = new XStream(new StaxDriver());
		 try{
			 File file = new File(xmlFile);
			 FileWriter writer=new FileWriter(file);
			 String content = xstream.toXML(g);
			 writer.write(content);
			 writer.close();
		 } catch (IOException e){
			 e.printStackTrace();
		 }
	}
	
	public static DirectedSparseGraph<XmlNode, XmlEdge> translateToXmlGraph(
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

	public static void writeToPdfFile(
			DirectedSparseGraph<StatementNode, StatementEdge> graph, IR lir,
			IR rir, String filename) {
		String psFile =  filename + ".pdf";
		psFile = psFile.replaceAll("<", "");
		psFile = psFile.replaceAll(">", "");
		
		DeltaGraphDecorator decorator = new DeltaGraphDecorator(lir,rir);
		try {
			DependencyGraphUtil.dotify(graph, decorator, PDFTypeHierarchy.DOT_FILE, psFile , dotExe);
		} catch (WalaException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
