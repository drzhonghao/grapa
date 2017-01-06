package partial.code.grapa.delta.graph;

public class DeltaNode extends AbstractNode{
	public String label;
	

	public DeltaNode(String l, int s, boolean b) {
		// TODO Auto-generated constructor stub
		label = l;
		side = s;
		bModified = b;
	}
	
	public DeltaNode(String l) {
		// TODO Auto-generated constructor stub
		label = l;
	}

	public String getKind() {
		// TODO Auto-generated method stub
		LabelTool tool = new LabelTool();
		return tool.parse(label);
	}

	
}
