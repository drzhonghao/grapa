package partial.code.grapa.delta.graph.data;

public class AbstractNode {
	public static final int LEFT  = 1;
	public static final int RIGHT = 2;
	public int side;
	public String label;
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		String shortLabel = label;
		int mark = label.lastIndexOf(".");
		if(mark>0){
			shortLabel = label.substring(mark+1);
		}
		return shortLabel;
	}	
	public String getAbstractLabel() {
		// TODO Auto-generated method stub
		String label = this.getClass().getSimpleName();
		
		return label;
	}
}
