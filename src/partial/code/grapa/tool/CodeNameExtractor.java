package partial.code.grapa.tool;

import java.util.ArrayList;
import java.util.Collection;

import edu.uci.ics.jung.graph.DirectedSparseGraph;
import partial.code.grapa.delta.graph.DeltaEdge;
import partial.code.grapa.delta.graph.DeltaNode;
import partial.code.grapa.tool.LabelUtil;


public class CodeNameExtractor {
	private ArrayList<String> fClientcodeNames = new ArrayList<String>();
	private ArrayList<String> fAPIcodeNames = new ArrayList<String>();
	private ArrayList<String> fPrefixes;
	private ArrayList<String> fCodenames = new ArrayList<String>();
	private LabelUtil lt = new LabelUtil();
	
	public CodeNameExtractor(ArrayList<String> fPrefixes) {
		// TODO Auto-generated constructor stub
		this.fPrefixes = fPrefixes;
	}

	public void analyze(DirectedSparseGraph<DeltaNode, DeltaEdge> graph) {
		// TODO Auto-generated method stub
		for(DeltaNode node:graph.getVertices()){			
			analyzeNode(node);
		}
	}

	public void analyzeNode(DeltaNode node) {		
		ArrayList<String> codenames = extractCodeNames(node.label);
		for(String codename:codenames){
			boolean bClient = false;
			for(String prefix:this.fPrefixes){
				if(codename.indexOf(prefix)>=0){
					bClient = true;
					break;
				}
			}
			if(bClient){
				this.fClientcodeNames.add(codename);
			}else{
				this.fAPIcodeNames.add(codename);
			}
		}
		this.fCodenames.addAll(codenames);
	}

	private ArrayList<String> extractCodeNames(String label) {
		// TODO Auto-generated method stub
		return lt.getCodeNames(label);		
		
	}
	
	public ArrayList<String> getCodeNames() {
		return fCodenames;
	}

	public ArrayList<String> getClientcodeNames() {
		return fClientcodeNames;
	}

	public ArrayList<String> getAPIcodeNames() {
		return fAPIcodeNames;
	}

	

	public void analyzeNodes(ArrayList<DeltaNode> nodes) {
		// TODO Auto-generated method stub
		for(DeltaNode node:nodes){
			analyzeNode(node);
		}
	}

	public void reset() {
		// TODO Auto-generated method stub
		this.fAPIcodeNames.clear();
		this.fClientcodeNames.clear();
		this.fCodenames.clear();
	}

}
