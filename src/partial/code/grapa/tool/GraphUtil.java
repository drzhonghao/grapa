package partial.code.grapa.tool;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Iterator;

import partial.code.grapa.delta.xmlgraph.data.XmlNode;
import partial.code.grapa.delta.graph.GraphComparator;

import partial.code.grapa.delta.xmlgraph.data.XmlEdge;

import partial.code.grapa.dependency.graph.DeltaGraphDecorator;
import partial.code.grapa.dependency.graph.DependencyGraphDotUtil;
import partial.code.grapa.dependency.graph.DfgNodeDecorator;
import partial.code.grapa.dependency.graph.SDGwithPredicate;
import partial.code.grapa.dependency.graph.StatementEdge;
import partial.code.grapa.dependency.graph.StatementNode;

import com.ibm.wala.cast.ir.ssa.AstAssertInstruction;
import com.ibm.wala.cast.java.ssa.AstJavaInvokeInstruction;
import com.ibm.wala.examples.drivers.PDFTypeHierarchy;
import com.ibm.wala.ipa.slicer.HeapStatement;
import com.ibm.wala.ipa.slicer.NormalReturnCaller;
import com.ibm.wala.ipa.slicer.NormalStatement;
import com.ibm.wala.ipa.slicer.ParamCallee;
import com.ibm.wala.ipa.slicer.ParamCaller;
import com.ibm.wala.ipa.slicer.PhiStatement;
import com.ibm.wala.ipa.slicer.Statement;
import com.ibm.wala.ipa.slicer.Slicer.ControlDependenceOptions;
import com.ibm.wala.ipa.slicer.Slicer.DataDependenceOptions;
import com.ibm.wala.ssa.IR;
import com.ibm.wala.ssa.SSAArrayStoreInstruction;
import com.ibm.wala.ssa.SSABinaryOpInstruction;
import com.ibm.wala.ssa.SSACheckCastInstruction;
import com.ibm.wala.ssa.SSAConditionalBranchInstruction;
import com.ibm.wala.ssa.SSAGetInstruction;
import com.ibm.wala.ssa.SSAGotoInstruction;
import com.ibm.wala.ssa.SSAInstanceofInstruction;
import com.ibm.wala.ssa.SSAInstruction;
import com.ibm.wala.ssa.SSANewInstruction;
import com.ibm.wala.ssa.SSAPutInstruction;
import com.ibm.wala.ssa.SSAUnaryOpInstruction;
import com.ibm.wala.types.TypeReference;
import com.ibm.wala.util.WalaException;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.StaxDriver;

import edu.uci.ics.jung.graph.DirectedSparseGraph;

public class GraphUtil {
	public static String dotExe = "e:/Program Files (x86)/Graphviz2.38/bin/dot.exe";

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
	
	public static void writeGraphXMLFile(
			DirectedSparseGraph<StatementNode, StatementEdge> graph,
			IR ir, String filename) {
		// TODO Auto-generated method stub
		String xmlFile = filename + ".xml";
		xmlFile = xmlFile.replaceAll("<", "");
		xmlFile = xmlFile.replaceAll(">", "");
		DirectedSparseGraph<XmlNode, XmlEdge> g = translateGraphToXMLGraph(graph, ir);
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
	
	public static DirectedSparseGraph<XmlNode, XmlEdge> translateGraphToXMLGraph(
			DirectedSparseGraph<StatementNode, StatementEdge> graph, IR ir) {
		// TODO Auto-generated method stub
		DirectedSparseGraph<XmlNode, XmlEdge> g = new DirectedSparseGraph<XmlNode, XmlEdge>(); 
		Hashtable<StatementNode, XmlNode> table = new Hashtable<StatementNode, XmlNode>();
		for(StatementNode sn:graph.getVertices()){
			String label = GraphComparator.getVisualLabel(ir, sn.statement);
			XmlNode node = new XmlNode(label);
			g.addVertex(node);
		}

		for(StatementEdge edge:graph.getEdges()){
			XmlNode from = table.get(edge.from);
			XmlNode to = table.get(edge.to);
			XmlEdge e = new XmlEdge(from, to, edge.type);
			g.addEdge(e, from, to);
		}
		return g;
	}
	
	public static void writePdfSDGraph(
			DirectedSparseGraph<StatementNode, StatementEdge> graph,IR ir, 
			String filename) {
		// TODO Auto-generated method stub
		String psFile = filename + ".pdf";
		psFile = psFile.replaceAll("<", "");
		psFile = psFile.replaceAll(">", "");

		DfgNodeDecorator decorator = new DfgNodeDecorator(ir);
		try {
			SdgDotUtil.dotify(graph, decorator, PDFTypeHierarchy.DOT_FILE, psFile , dotExe );
		} catch (WalaException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void writePdfDeltaGraph(
			DirectedSparseGraph<StatementNode, StatementEdge> graph, IR lir,
			IR rir, String filename) {
		String psFile =  filename + ".pdf";
		psFile = psFile.replaceAll("<", "");
		psFile = psFile.replaceAll(">", "");
		
		DeltaGraphDecorator decorator = new DeltaGraphDecorator(lir,rir);
		try {
			DependencyGraphDotUtil.dotify(graph, decorator, PDFTypeHierarchy.DOT_FILE, psFile , dotExe);
		} catch (WalaException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void writeDeltaGraphXMLFile(
			DirectedSparseGraph<StatementNode, StatementEdge> graph,
			IR lir,
			IR rir,
			String filename) {
		String xmlFile = filename + ".xml";
		xmlFile = xmlFile.replaceAll("<", "");
		xmlFile = xmlFile.replaceAll(">", "");
		DirectedSparseGraph<XmlNode, XmlEdge> g = translateDeltaGraphToXMLGraph(graph, lir, rir);
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
	
	public static DirectedSparseGraph<XmlNode, XmlEdge> translateDeltaGraphToXMLGraph(
			DirectedSparseGraph<StatementNode, StatementEdge> graph, IR lir, IR rir) {
		// TODO Auto-generated method stub
		
		DirectedSparseGraph<XmlNode, XmlEdge> g = new DirectedSparseGraph<XmlNode, XmlEdge>(); 
		Hashtable<StatementNode, XmlNode> table = new Hashtable<StatementNode, XmlNode>();
		for(StatementNode sn:graph.getVertices()){
			NormalStatement ns = (NormalStatement)sn.statement;
			String label = null;
			if(sn.side == StatementNode.LEFT){
				label = "l: "+GraphComparator.getVisualLabel(lir, sn.statement);
			}else if(sn.side == StatementNode.RIGHT){
				label = "r: "+GraphComparator.getVisualLabel(rir, sn.statement);
			}
			XmlNode node = new XmlNode(label, sn.side, sn.bModified);
			g.addVertex(node);
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
