package partial.code.grapa.dependency.graph;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
























import com.ibm.wala.examples.drivers.PDFTypeHierarchy;
import com.ibm.wala.ipa.slicer.HeapStatement;
import com.ibm.wala.ipa.slicer.NormalStatement;
import com.ibm.wala.ipa.slicer.ParamCallee;
import com.ibm.wala.ipa.slicer.ParamCaller;
import com.ibm.wala.ipa.slicer.SDG;
import com.ibm.wala.ipa.slicer.Slicer.ControlDependenceOptions;
import com.ibm.wala.ipa.slicer.Slicer.DataDependenceOptions;
import com.ibm.wala.ipa.slicer.Statement;
import com.ibm.wala.ssa.IR;
import com.ibm.wala.util.WalaException;
import com.ibm.wala.util.collections.Iterator2Collection;
import com.ibm.wala.util.debug.Assertions;
import com.ibm.wala.util.graph.Graph;
import com.ibm.wala.viz.DotUtil;
import com.ibm.wala.viz.NodeDecorator;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.StaxDriver;

import edu.uci.ics.jung.graph.DirectedSparseGraph;
import partial.code.grapa.delta.graph.GraphComparator;
import partial.code.grapa.delta.graph.xml.XmlEdge;
import partial.code.grapa.delta.graph.xml.XmlGraphDecorator;
import partial.code.grapa.delta.graph.xml.XmlNode;
import partial.code.grapa.tool.GraphUtil;

/**
 * utilities for interfacing with DOT
 */
public class DependencyGraphUtil extends GraphUtil {
  public static String dotExe = "e:/Program Files (x86)/Graphviz2.38/bin/dot.exe";
  public static void dotify(
			DirectedSparseGraph<StatementNode, StatementEdge> graph, NodeDecorator decorator,
			String dotFile, String outputFile, String dotExe) throws WalaException {
	// TODO Auto-generated method stub
	if (graph == null) {
      throw new IllegalArgumentException("g is null");
    }
	
    File f = DependencyGraphUtil.writeDotFile(graph, decorator, dotFile);
    if (dotExe != null) {
      spawnDot(dotExe, outputFile, f);
    }
  }

	private static File writeDotFile(
			DirectedSparseGraph<StatementNode, StatementEdge> graph, NodeDecorator labels, String dotfile) throws WalaException {
		// TODO Auto-generated method stub
		 if (graph == null) {
		      throw new IllegalArgumentException("g is null");
		    }
		    StringBuffer dotStringBuffer = dotOutput(graph, labels);
		  
		    // retrieve the filename parameter to this component, a String
		    if (dotfile == null) {
		      throw new WalaException("internal error: null filename parameter");
		    }
		    try {
		      File f = new File(dotfile);
		      FileWriter fw = new FileWriter(f);
		      fw.write(dotStringBuffer.toString());
		      fw.close();
		      return f;

		    } catch (Exception e) {
		      throw new WalaException("Error writing dot file " + dotfile);
		    }
	}

	private static StringBuffer dotOutput(
			DirectedSparseGraph<StatementNode, StatementEdge> graph,
			NodeDecorator labels) throws WalaException {
		// TODO Auto-generated method stub
		StringBuffer result = new StringBuffer("digraph \"DirectedSDG\" {\n");

		  
		result.append("graph [concentrate = true];");
		   
		    
	    String rankdir = getRankDir();
	    if (rankdir != null) {
	      result.append("rankdir=" + rankdir + ";");
	    }
	    String fontsizeStr = "fontsize=" + fontSize;
	    String fontcolorStr = (fontColor != null) ? ",fontcolor="+fontColor : "";
	    String fontnameStr = (fontName != null) ? ",fontname="+fontName : "";
	         
	    result.append("center=true;");
	    result.append(fontsizeStr);
	    result.append(";node [ color=blue,shape=\"box\"");
	    result.append(fontsizeStr);
	    result.append(fontcolorStr);
	    result.append(fontnameStr);
	    result.append("];edge [ color=black,");
	    result.append(fontsizeStr);
	    result.append(fontcolorStr);
	    result.append(fontnameStr);
	    result.append("]; \n");

		
	    result.append("  subgraph cluster0{\n");
	    outputNodes(labels, result, graph.getVertices(), StatementNode.LEFT);
		    
    	for (StatementNode fn:graph.getVertices()) {
    	      for (StatementNode tn:graph.getSuccessors(fn)) {
    	    	if(fn.side == StatementNode.LEFT&&tn.side == StatementNode.LEFT){  
	    	        result.append(" ");
	    	        result.append(getPort(fn, labels));
	    	        result.append(" -> ");
	    	        result.append(getPort(tn, labels));
	    	        StatementEdge edge = graph.findEdge(fn,tn);
	    	        if(edge.type==StatementEdge.DATA_FLOW){
	    	        	result.append(" [color=red]\n");
	    	        }else if(edge.type==StatementEdge.CONTROL_FLOW){
	    	        	result.append(" [color=blue]\n");
	    	        }else if(edge.type==StatementEdge.CHANGE){
	    	        	result.append(" [color=green]\n");
	    	        }else{
	    	        	result.append(" \n");
	    	        }
    	    	}
    	      }
    	 }
		 result.append("  \n}\n");
		 
		 result.append("  subgraph cluster1{\n");
		 outputNodes(labels, result, graph.getVertices(), StatementNode.RIGHT);
		 for (StatementNode fn:graph.getVertices()) {
	   	      for (StatementNode tn:graph.getSuccessors(fn)) {
		   	    	if(fn.side == StatementNode.RIGHT&&tn.side == StatementNode.RIGHT){  
			    	        result.append(" ");
			    	        result.append(getPort(fn, labels));
			    	        result.append(" -> ");
			    	        result.append(getPort(tn, labels));
			    	        StatementEdge edge = graph.findEdge(fn,tn);
			    	        if(edge.type==StatementEdge.DATA_FLOW){
			    	        	result.append(" [color=red]\n");
			    	        }else if(edge.type==StatementEdge.CONTROL_FLOW){
			    	        	result.append(" [color=blue]\n");
			    	        }else if(edge.type==StatementEdge.CHANGE){
			    	        	result.append(" [color=green]\n");
			    	        }else{
			    	        	result.append(" \n");
			    	        }
		   	    	}
	   	      }
	   	 }
		 result.append("  \n}\n");
		 for (StatementNode fn:graph.getVertices()) {
	   	      for (StatementNode tn:graph.getSuccessors(fn)) {
		   	    	if(fn.side == StatementNode.LEFT&&tn.side == StatementNode.RIGHT){  
			    	        result.append(" ");
			    	        result.append(getPort(fn, labels));
			    	        result.append(" -> ");
			    	        result.append(getPort(tn, labels));
			    	        StatementEdge edge = graph.findEdge(fn,tn);
			    	        if(edge.type==StatementEdge.DATA_FLOW){
			    	        	result.append(" [color=red]\n");
			    	        }else if(edge.type==StatementEdge.CONTROL_FLOW){
			    	        	result.append(" [color=blue]\n");
			    	        }else if(edge.type==StatementEdge.CHANGE){
			    	        	result.append(" [color=green]\n");
			    	        }else{
			    	        	result.append(" \n");
			    	        }
		   	    	}
	   	      }
	   	 }
		 result.append("\n}");
		 return result;
	}
	

	private static void outputNodes(NodeDecorator<StatementNode> labels, StringBuffer result, Collection dotNodes, int side) throws WalaException {
	    for (Iterator<StatementNode> it = dotNodes.iterator(); it.hasNext();) {
	      outputNode(labels, result, it.next(), side);
	    }
    }

	private static void outputNode(NodeDecorator<StatementNode> labels, StringBuffer result, StatementNode n, int side) throws WalaException {
	    if(n.side == side){
			result.append("   ");
		    result.append("\"");
		    result.append(getLabel(n, labels));
		    result.append("\"");
		    result.append(decorateNode(n, labels));
	    }
	}

  
  private static Object decorateNode(StatementNode n,
			NodeDecorator<StatementNode> labels) {
		// TODO Auto-generated method stub
	  StringBuffer result = new StringBuffer();
	  if(n.bModified){
		  result.append(" [color=red]\n");
	  }else if(n.side == StatementNode.LEFT){
		  result.append(" [color=limegreen]\n");
	  }else if(n.side == StatementNode.RIGHT){
		  result.append(" [color=dodgerblue]\n");
	  }
	  return result.toString();
  }


  private static  String getLabel(StatementNode n, NodeDecorator d) throws WalaException {
    String result = null;
    if (d == null) {
      result = n.toString();
    } else {
      result = d.getLabel(n);
      result = result == null ? n.toString() : result;
    }
    if (result.length() >= MAX_LABEL_LENGTH) {
      result = result.substring(0, MAX_LABEL_LENGTH - 3) + "...";
    }
    return result;
  }

  private static  String getPort(StatementNode n, NodeDecorator d) throws WalaException {
    return "\"" + getLabel(n, d) + "\"";

  }

  public static DirectedSparseGraph<StatementNode, StatementEdge> translateToJungGraph(
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

  	public static DirectedSparseGraph<XmlNode, XmlEdge> translatehToXml(
			DirectedSparseGraph<StatementNode, StatementEdge> graph, IR ir) {
		// TODO Auto-generated method stub
		DirectedSparseGraph<XmlNode, XmlEdge> g = new DirectedSparseGraph<XmlNode, XmlEdge>(); 
		Hashtable<StatementNode, XmlNode> table = new Hashtable<StatementNode, XmlNode>();
		for(StatementNode sn:graph.getVertices()){
			String label = GraphComparator.getVisualLabel(ir, sn.statement);
			XmlNode node = new XmlNode(label);
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
  	
  	public static void writeToXmlFile(
			DirectedSparseGraph<StatementNode, StatementEdge> graph,
			IR ir, String filename) {
		// TODO Auto-generated method stub
		String xmlFile = filename + ".xml";
		xmlFile = xmlFile.replaceAll("<", "");
		xmlFile = xmlFile.replaceAll(">", "");
		DirectedSparseGraph<XmlNode, XmlEdge> g = translatehToXml(graph, ir);
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
  	
//  	public static void writePdfSDGraph(
//			DirectedSparseGraph<StatementNode, StatementEdge> graph,IR ir, 
//			String filename) {
//		// TODO Auto-generated method stub
//		String psFile = filename + ".pdf";
//		psFile = psFile.replaceAll("<", "");
//		psFile = psFile.replaceAll(">", "");
//
//		DfgNodeDecorator decorator = new DfgNodeDecorator(ir);
//		try {
//			SdgDotUtils.dotify(graph, decorator, PDFTypeHierarchy.DOT_FILE, psFile , dotExe );
//		} catch (WalaException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
}
