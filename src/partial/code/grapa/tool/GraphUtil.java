package partial.code.grapa.tool;

import java.io.File;
import java.io.FileWriter;

import com.ibm.wala.examples.drivers.PDFTypeHierarchy;
import com.ibm.wala.util.WalaException;
import com.ibm.wala.viz.DotUtil;
import com.ibm.wala.viz.NodeDecorator;

import edu.uci.ics.jung.graph.DirectedSparseGraph;
import partial.code.grapa.delta.graph.AbstractEdge;
import partial.code.grapa.delta.graph.AbstractNode;





@SuppressWarnings("rawtypes")
public abstract class GraphUtil extends DotUtil{

	protected String fDotExe;
	protected NodeDecorator fNodeDecorator;

	
	public GraphUtil( String dotExe){
		fDotExe = dotExe;
	}
	protected void dotify( DirectedSparseGraph graph,
			String outputFile) throws WalaException {
		// TODO Auto-generated method stub
		if (graph == null) {
	      throw new IllegalArgumentException("g is null");
	    }
		String dotFile = outputFile.replace(".pdf", ".dt");		
	    File f = writeDotFile(graph,dotFile);
	    if (fDotExe != null) {
	      spawnDot(fDotExe, outputFile, f);
	    }
	}

	protected File writeDotFile(DirectedSparseGraph graph, String dotFile) throws WalaException {
		// TODO Auto-generated method stub
		if (graph == null) {
	      throw new IllegalArgumentException("g is null");
	    }
	    StringBuffer dotStringBuffer = dotOutput(graph);
	  
	    // retrieve the filename parameter to this component, a String
	
	    try {
	      File f = new File(dotFile);
	      FileWriter fw = new FileWriter(f);
	      fw.write(dotStringBuffer.toString());
	      fw.close();
	      return f;

	    } catch (Exception e) {
	      throw new WalaException("Error writing dot file.");
	    }
	}

	private StringBuffer dotOutput(DirectedSparseGraph<AbstractNode, AbstractEdge> graph) throws WalaException {
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
	    result.append("   label = \"left side\";\n");
		for(Object obj: graph.getVertices()){
			AbstractNode node = (AbstractNode)obj;
			if(node.side == AbstractNode.LEFT){
				result.append("   ");
			    result.append("\"");
			    result.append(getLabel(node));
			    result.append("\"");
			    if(node.bModified){
					  result.append(" [color=red]\n");
				}else if(node.side == AbstractNode.LEFT){
					  result.append(" [color=limegreen]\n");
				}else if(node.side == AbstractNode.RIGHT){
					  result.append(" [color=dodgerblue]\n");
				}
			}			
		}	
		
		for (AbstractNode fn:graph.getVertices()) {
	  	      for (AbstractNode tn:graph.getSuccessors(fn)) {
	  	    	if(fn.side == AbstractNode.LEFT&&tn.side == AbstractNode.LEFT){  
		    	        result.append(" ");
		    	        result.append(getPort(fn));
		    	        result.append(" -> ");
		    	        result.append(getPort(tn));
		    	        AbstractEdge edge = graph.findEdge(fn,tn);
		    	        
		    	        if(edge.type==AbstractEdge.DATA_FLOW){
		    	        	result.append(" [color=red]\n");
		    	        }else if(edge.type==AbstractEdge.CONTROL_FLOW){
		    	        	result.append(" [color=blue]\n");
		    	        }else if(edge.type==AbstractEdge.CHANGE){
		    	        	result.append(" [color=green]\n");
		    	        }else{
		    	        	result.append(" \n");
		    	        }
	  	    	}
	  	      }
	  	 }
    	result.append("  \n}\n");
		 
		result.append("  subgraph cluster1{\n");
		result.append("   label = \"right side\";\n");
		for(Object obj: graph.getVertices()){
			AbstractNode node = (AbstractNode)obj;
			if(node.side == AbstractNode.RIGHT){
				result.append("   ");
			    result.append("\"");
			    result.append(getLabel(node));
			    result.append("\"");
			    if(node.bModified){
					  result.append(" [color=red]\n");
				}else if(node.side == AbstractNode.LEFT){
					  result.append(" [color=limegreen]\n");
				}else if(node.side == AbstractNode.RIGHT){
					  result.append(" [color=dodgerblue]\n");
				}
			}			
		}
		for (AbstractNode fn:graph.getVertices()) {
	  	      for (AbstractNode tn:graph.getSuccessors(fn)) {
	  	    	if(fn.side == AbstractNode.RIGHT&&tn.side == AbstractNode.RIGHT){  
		    	        result.append(" ");
		    	        result.append(getPort(fn));
		    	        result.append(" -> ");
		    	        result.append(getPort(tn));
		    	        AbstractEdge edge = graph.findEdge(fn,tn);
		    	        if(edge.type==AbstractEdge.DATA_FLOW){
		    	        	result.append(" [color=red]\n");
		    	        }else if(edge.type==AbstractEdge.CONTROL_FLOW){
		    	        	result.append(" [color=blue]\n");
		    	        }else if(edge.type==AbstractEdge.CHANGE){
		    	        	result.append(" [color=green]\n");
		    	        }else{
		    	        	result.append(" \n");
		    	        }
	  	    	}
	  	      }
	  	 }
		result.append("  \n}\n");
		 for (AbstractNode fn:graph.getVertices()) {
	   	      for (AbstractNode tn:graph.getSuccessors(fn)) {
		   	    	if(fn.side == AbstractNode.LEFT&&tn.side == AbstractNode.RIGHT){  
			    	        result.append(" ");
			    	        result.append(getPort(fn));
			    	        result.append(" -> ");
			    	        result.append(getPort(tn));
			    	        AbstractEdge edge = graph.findEdge(fn,tn);
			    	        if(edge.type==AbstractEdge.DATA_FLOW){
			    	        	result.append(" [color=red]\n");
			    	        }else if(edge.type==AbstractEdge.CONTROL_FLOW){
			    	        	result.append(" [color=blue]\n");
			    	        }else if(edge.type==AbstractEdge.CHANGE){
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

	
	protected  String getLabel(Object n)  {
	    String result = null;
	    if (this.fNodeDecorator == null) {
	      result = n.toString();
	    } else {
	    	try {
	    	  result = fNodeDecorator.getLabel(n);
	    	  result = result == null ? n.toString() : result;
			} catch (WalaException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	     
	    }
	    if (result.length() >= MAX_LABEL_LENGTH) {
	      result = result.substring(0, MAX_LABEL_LENGTH - 3) + "...";
	    }
	    return result;
	  }

	 protected  String getPort(Object n) {
	    return "\"" + getLabel(n) + "\"";
	 }
}
