package partial.code.grapa.delta.graph;

import partial.code.grapa.tool.LabelUtil;

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
		LabelUtil tool = new LabelUtil();
		return tool.parse(label);
	}

	
}
