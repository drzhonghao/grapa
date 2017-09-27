package partial.code.grapa.commit.method;

import java.util.ArrayList;

import partial.code.grapa.delta.HungarianComparator;

public class MethodComparator extends HungarianComparator{

	private ArrayList<ClientMethod> leftMethods;
	private ArrayList<ClientMethod> rightMethods;

	public MethodComparator(ArrayList<ClientMethod> leftMethods, ArrayList<ClientMethod> rightMethods) {
		// TODO Auto-generated constructor stub
	
		if(leftMethods.size()>rightMethods.size()){
			this.bSwapSide = true;
			this.leftMethods = rightMethods;
			this.rightMethods = leftMethods;
		}else{
			bSwapSide = false;
			this.leftMethods = leftMethods;
			this.rightMethods = rightMethods;
		}
	}

	@Override
	protected void calculateCostMatrix() {
		// TODO Auto-generated method stub
		for (int i = 0; i < leftMethods.size(); i++) {
			ClientMethod leftNode = leftMethods.get(i);
            for (int j = 0; j < rightMethods.size(); j++) {
            	ClientMethod rightNode = rightMethods.get(j);
            	String leftLine = leftNode.getTypeName()+"."+leftNode.getSignature();
            	String rightLine = rightNode.getTypeName()+"."+rightNode.getSignature();
                costMatrix[i][j] = stringComparator.getUnNormalisedSimilarity(leftLine, rightLine);;
            }
        }
	}

	@Override
	protected Object getLeftItem(int i) {
		// TODO Auto-generated method stub
		return leftMethods.get(i);
	}

	@Override
	protected Object getRightItem(int i) {
		// TODO Auto-generated method stub
		return rightMethods.get(i);
	}

}
