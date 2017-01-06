package partial.code.grapa.delta.graph.xml;

import java.io.File;
import java.io.FileWriter;

import com.ibm.wala.util.WalaException;
import com.ibm.wala.viz.NodeDecorator;

import edu.uci.ics.jung.graph.DirectedSparseGraph;

import partial.code.grapa.tool.GraphUtil;

public class XmlGraphUtil extends GraphUtil{

	public XmlGraphUtil(String dotExe) {
		super( dotExe);
		// TODO Auto-generated constructor stub
	}

	public void writePdfXmlGraph(DirectedSparseGraph<XmlNode, XmlEdge> graph, String psFile) {
		// TODO Auto-generated method stub		
		fNodeDecorator = new XmlGraphDecorator();
		try {
			dotify(graph, psFile);
		} catch (WalaException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
