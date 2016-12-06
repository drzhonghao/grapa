package partial.code.grapa.delta.graph.xml;

import java.io.File;
import java.io.FileWriter;

import com.ibm.wala.examples.drivers.PDFTypeHierarchy;
import com.ibm.wala.util.WalaException;
import com.ibm.wala.viz.NodeDecorator;

import edu.uci.ics.jung.graph.DirectedSparseGraph;
import partial.code.grapa.delta.graph.DeltaGraphDecorator;
import partial.code.grapa.delta.graph.DeltaGraphUtil;
import partial.code.grapa.tool.GraphUtil;

public class XmlGraphUtil extends GraphUtil{

	public XmlGraphUtil( String dotExe, String dotFile) {
		super( dotExe);
		// TODO Auto-generated constructor stub
	}



//	public static void writePdfXmlGraph(DirectedSparseGraph<XmlNode, XmlEdge> graph, String psFile) {
//		// TODO Auto-generated method stub		
//		XmlGraphDecorator decorator = new XmlGraphDecorator();
//		try {
//			XmlGraphUtil.dotify(graph, decorator, PDFTypeHierarchy.DOT_FILE, psFile, dotExe);
//		} catch (WalaException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}

	

	private static StringBuffer dotOutput(DirectedSparseGraph<XmlNode, XmlEdge> graph, XmlGraphDecorator labels) {
		// TODO Auto-generated method stub
		return null;
	}



	@Override
	protected StringBuffer outputLeftNodes(DirectedSparseGraph graph) {
		// TODO Auto-generated method stub
		return null;
	}



	@Override
	protected StringBuffer outputLeftEdges(DirectedSparseGraph graph) {
		// TODO Auto-generated method stub
		return null;
	}



	@Override
	protected StringBuffer outputRightNodes(DirectedSparseGraph graph) {
		// TODO Auto-generated method stub
		return null;
	}



	@Override
	protected StringBuffer outputRightEdges(DirectedSparseGraph graph) {
		// TODO Auto-generated method stub
		return null;
	}



	@Override
	protected StringBuffer outputBetweenClusterEdges(DirectedSparseGraph graph) {
		// TODO Auto-generated method stub
		return null;
	}



	@Override
	protected StringBuffer decorateNode(Object n) {
		// TODO Auto-generated method stub
		return null;
	}

	

}
