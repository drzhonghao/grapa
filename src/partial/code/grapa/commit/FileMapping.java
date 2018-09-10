package partial.code.grapa.commit;

import java.io.File;
import java.util.ArrayList;
import partial.code.grapa.hungarian.HungarianMapping;

public class FileMapping extends HungarianMapping{
	private ArrayList<File> leftFiles;
	private ArrayList<File> rightFiles;
	
	public FileMapping(ArrayList<File> leftFiles, ArrayList<File> rightFiles) {
		if(leftFiles.size()>rightFiles.size()){
			bSwapSide = true;
			this.leftFiles = rightFiles;
			this.rightFiles = leftFiles;
		}else{
			bSwapSide = false;
			this.leftFiles = leftFiles;
			this.rightFiles = rightFiles;
		}
	}
	@Override
	protected void calculateCostMatrix() {
		costMatrix = new double[leftFiles.size()][rightFiles.size()];
		for (int i = 0; i < leftFiles.size(); i++) {
			File leftNode = leftFiles.get(i);
            for (int j = 0; j < rightFiles.size(); j++) {
            	File rightNode = rightFiles.get(j);
            	costMatrix[i][j] = calculateDistance(leftNode, rightNode);
            	
            }
        }
	}

	public double calculateDistance(File leftNode, File rightNode) {
		double distance = Double.MAX_VALUE;
		String leftLine = leftNode.getName();
    	String rightLine = rightNode.getName();
    	if(leftLine!=null&&rightLine!=null) {
    		distance = stringComparator.getUnNormalisedSimilarity(leftLine, rightLine);
    	}
		return distance;
	}
	
	@Override
	protected Object getLeftItem(int index) {
		return leftFiles.get(index);
	}

	@Override
	protected Object getRightItem(int index) {
		return rightFiles.get(index);
	}

}
