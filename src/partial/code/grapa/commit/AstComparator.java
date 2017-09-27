package partial.code.grapa.commit;

import java.util.ArrayList;
import java.util.Hashtable;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;

import partial.code.grapa.delta.HungarianMapping;
import partial.code.grapa.delta.graph.HungarianAlgorithm;
import uk.ac.shef.wit.simmetrics.similaritymetrics.Levenshtein;

abstract public class AstComparator{
	
	protected ArrayList<ASTNode> leftTrees;
	protected ArrayList<ASTNode> rightTrees;

	protected abstract void extractFinerMapping(ASTNode leftTree, ASTNode rightTree);
	
	public AstComparator(ArrayList<ASTNode> leftTrees, ArrayList<ASTNode> rightTrees) {
		super();
		this.leftTrees = leftTrees;
		this.rightTrees = rightTrees;
	}
	
	public void extractMappings() {
		// TODO Auto-generated method stub
		ClassMapping comparator = new ClassMapping(leftTrees, rightTrees);
		Hashtable<Object, Object> nm = comparator.extractNodeMappings();
		
		for(ASTNode leftTree:leftTrees){
			ASTNode rightTree = (ASTNode) nm.get(leftTree);
			if(rightTree!=null){
				extractFinerMapping((ASTNode) leftTree, rightTree);				
			}
		}
	
	}

	
	
	
}
