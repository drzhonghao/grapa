package partial.code.grapa.delta.graph.xml;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import com.ibm.wala.ssa.IR;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.StaxDriver;

import edu.uci.ics.jung.graph.DirectedSparseGraph;
import partial.code.grapa.dependency.graph.StatementEdge;
import partial.code.grapa.dependency.graph.StatementNode;
import partial.code.grapa.tool.GraphTranslateTool;

public class XmlGraphFileTool {
	public static void writeToXmlFile(
			DirectedSparseGraph<StatementNode, StatementEdge> graph,
			IR lir,
			IR rir,
			String filename) {
		String xmlFile = filename + ".xml";
		xmlFile = xmlFile.replaceAll("<", "");
		xmlFile = xmlFile.replaceAll(">", "");
		DirectedSparseGraph<XmlNode, XmlEdge> g = GraphTranslateTool.translateDeltaToXmlGraph(graph, lir, rir);
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
}
