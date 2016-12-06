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
import partial.code.grapa.dependency.graph.StatementEdge;
import partial.code.grapa.dependency.graph.StatementNode;
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


	@Override
	protected StringBuffer outputLeftNodes(DirectedSparseGraph graph) {
		// TODO Auto-generated method stub
		StringBuffer result = new StringBuffer();
		for(Object obj: graph.getVertices()){
			StatementNode node = (StatementNode)obj;
			if(node.side == StatementNode.LEFT){
				result.append("   ");
			    result.append("\"");
			    result.append(getLabel(node));
			    result.append("\"");
			    result.append(decorateNode(node));
			}			
		}		
		return result;
	}
	
	@Override
	protected StringBuffer decorateNode(Object obj) {
			// TODO Auto-generated method stub
		  StatementNode n = (StatementNode)obj;
		  StringBuffer result = new StringBuffer();
		  if(n.bModified){
			  result.append(" [color=red]\n");
		  }else if(n.side == StatementNode.LEFT){
			  result.append(" [color=limegreen]\n");
		  }else if(n.side == StatementNode.RIGHT){
			  result.append(" [color=dodgerblue]\n");
		  }
		  return result;
	 }


	@Override
	protected StringBuffer outputLeftEdges(DirectedSparseGraph g) {
		// TODO Auto-generated method stub
		StringBuffer result = new StringBuffer();
		DirectedSparseGraph<StatementNode, StatementEdge> graph = (DirectedSparseGraph<StatementNode, StatementEdge>)g;
		for (StatementNode fn:graph.getVertices()) {
	  	      for (StatementNode tn:graph.getSuccessors(fn)) {
	  	    	if(fn.side == StatementNode.LEFT&&tn.side == StatementNode.LEFT){  
		    	        result.append(" ");
		    	        result.append(getPort(fn));
		    	        result.append(" -> ");
		    	        result.append(getPort(tn));
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
		return null;
	}


	@Override
	protected StringBuffer outputRightNodes(DirectedSparseGraph graph) {
		// TODO Auto-generated method stub
		StringBuffer result = new StringBuffer();
		for(Object obj: graph.getVertices()){
			StatementNode node = (StatementNode)obj;
			if(node.side == StatementNode.RIGHT){
				result.append("   ");
			    result.append("\"");
			    result.append(getLabel(node));
			    result.append("\"");
			    result.append(decorateNode(node));
			}			
		}
		
		return result;
	}


	@Override
	protected StringBuffer outputRightEdges(DirectedSparseGraph g) {
		// TODO Auto-generated method stub
		StringBuffer result = new StringBuffer();
		DirectedSparseGraph<StatementNode, StatementEdge> graph = (DirectedSparseGraph<StatementNode, StatementEdge>)g;
		for (StatementNode fn:graph.getVertices()) {
	  	      for (StatementNode tn:graph.getSuccessors(fn)) {
	  	    	if(fn.side == StatementNode.RIGHT&&tn.side == StatementNode.RIGHT){  
		    	        result.append(" ");
		    	        result.append(getPort(fn));
		    	        result.append(" -> ");
		    	        result.append(getPort(tn));
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
		return result;
	}


	@Override
	protected StringBuffer outputBetweenClusterEdges(DirectedSparseGraph g) {
		// TODO Auto-generated method stub
		StringBuffer result = new StringBuffer();
		DirectedSparseGraph<StatementNode, StatementEdge> graph = (DirectedSparseGraph<StatementNode, StatementEdge>)g;
		
		 for (StatementNode fn:graph.getVertices()) {
	   	      for (StatementNode tn:graph.getSuccessors(fn)) {
		   	    	if(fn.side == StatementNode.LEFT&&tn.side == StatementNode.RIGHT){  
			    	        result.append(" ");
			    	        result.append(getPort(fn));
			    	        result.append(" -> ");
			    	        result.append(getPort(tn));
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


	
  	
}
