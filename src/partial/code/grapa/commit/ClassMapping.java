package partial.code.grapa.commit;

import java.util.ArrayList;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;

import partial.code.grapa.hungarian.HungarianMapping;

public class ClassMapping extends HungarianMapping{

	private ArrayList<ASTNode> leftTrees;
	private ArrayList<ASTNode> rightTrees;

	public ClassMapping(ArrayList<ASTNode> leftTrees, ArrayList<ASTNode> rightTrees) {
		// TODO Auto-generated constructor stub
		if(leftTrees.size()>rightTrees.size()){
			bSwapSide = true;
			this.leftTrees = rightTrees;
			this.rightTrees = leftTrees;
		}else{
			bSwapSide = false;
			this.leftTrees = leftTrees;
			this.rightTrees = rightTrees;
		}
	}

	@Override
	protected void calculateCostMatrix() {
		costMatrix = new double[leftTrees.size()][rightTrees.size()];
		for (int i = 0; i < leftTrees.size(); i++) {
			ASTNode leftNode = leftTrees.get(i);
            for (int j = 0; j < rightTrees.size(); j++) {
            	ASTNode rightNode = rightTrees.get(j);
            	String leftLine = ((CompilationUnit)leftNode).getTypeRoot().getElementName();
            	String rightLine = ((CompilationUnit)rightNode).getTypeRoot().getElementName();
            	if(leftLine!=null&&rightLine!=null) {
            		costMatrix[i][j] = stringComparator.getUnNormalisedSimilarity(leftLine, rightLine);
            	}
            }
        }
	}

	

	@Override
	protected Object getLeftItem(int i) {
		// TODO Auto-generated method stub
		return leftTrees.get(i);
	}

	@Override
	protected Object getRightItem(int i) {
		// TODO Auto-generated method stub
		return rightTrees.get(i);
	}

}
