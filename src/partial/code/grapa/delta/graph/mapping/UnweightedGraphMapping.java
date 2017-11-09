package partial.code.grapa.delta.graph.mapping;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;

import uk.ac.shef.wit.simmetrics.similaritymetrics.Levenshtein;
import util.EditDistance;

import com.ibm.wala.cast.java.ssa.AstJavaInvokeInstruction;
import com.ibm.wala.ipa.slicer.HeapStatement;
import com.ibm.wala.ipa.slicer.NormalReturnCaller;
import com.ibm.wala.ipa.slicer.NormalStatement;
import com.ibm.wala.ipa.slicer.ParamCallee;
import com.ibm.wala.ipa.slicer.ParamCaller;
import com.ibm.wala.ipa.slicer.PhiStatement;
import com.ibm.wala.ipa.slicer.Statement;
import com.ibm.wala.ssa.IR;
import com.ibm.wala.ssa.SSAGetInstruction;
import com.ibm.wala.ssa.SSAInstruction;
import com.ibm.wala.ssa.SSANewInstruction;
import com.ibm.wala.ssa.SymbolTable;
import com.ibm.wala.types.FieldReference;

import edu.uci.ics.jung.graph.DirectedSparseGraph;
import partial.code.grapa.delta.graph.AbstractEdge;
import partial.code.grapa.delta.graph.DeltaEdge;
import partial.code.grapa.delta.graph.DeltaNode;
import partial.code.grapa.hungarian.HungarianAlgorithm;
import partial.code.grapa.hungarian.HungarianMapping;
import partial.code.grapa.tool.LabelUtil;

public class UnweightedGraphMapping extends GraphMapping{
	public UnweightedGraphMapping(DirectedSparseGraph<DeltaNode, DeltaEdge> oldGraph,
			DirectedSparseGraph<DeltaNode, DeltaEdge> newGraph) {
		super(oldGraph, newGraph);
		// TODO Auto-generated constructor stub
	}
	protected Hashtable<String, Integer> extractKinds(DirectedSparseGraph<DeltaNode, DeltaEdge> graph, DeltaNode node,
			int type, boolean bOut) {
		// TODO Auto-generated method stub
		Hashtable<String, Integer> table = new Hashtable<String, Integer>();
		Collection<DeltaEdge> edges = null;
		if(bOut){
			edges = graph.getOutEdges(node);
		}else{
			edges = graph.getInEdges(node);
		}
		if(edges!=null) {
			for(DeltaEdge edge:edges){
				if(edge.type == type){
					DeltaNode match = null;
					if(bOut){
						match = (DeltaNode)edge.to;
					}else{
						match = (DeltaNode)edge.from;
					}
					String key = match.getKind();
					Integer no = table.get(key);
					if(no==null){
						no = 1;
					}else{
						no++;
					}
					table.put(key, no);
				}
			}
		}
		return table;
	}
	
}

