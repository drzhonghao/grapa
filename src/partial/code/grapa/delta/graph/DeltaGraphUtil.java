package partial.code.grapa.delta.graph;

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
import partial.code.grapa.delta.graph.xml.XmlEdge;
import partial.code.grapa.delta.graph.xml.XmlGraphDecorator;
import partial.code.grapa.delta.graph.xml.XmlNode;
import partial.code.grapa.dependency.graph.SDGwithPredicate;
import partial.code.grapa.tool.GraphUtil;


/**
 * utilities for interfacing with DOT
 */
public class DeltaGraphUtil extends GraphUtil {
  

	public DeltaGraphUtil(String dotExe) {
		super(dotExe);
		// TODO Auto-generated constructor stub
	}
	public void writeToPdfFile(
			DirectedSparseGraph<StatementNode, StatementEdge> graph, IR lir,
			IR rir, String filename) {
		
		this.fNodeDecorator = new DeltaGraphDecorator(lir,rir);
		try {
			dotify(graph,filename);
		} catch (WalaException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	
}
