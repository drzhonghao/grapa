package partial.code.grapa.delta.graph;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import partial.code.grapa.tool.LabelUtil;

public class DeltaNode extends AbstractNode{
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		String visualLabel;
		if(side==DeltaNode.LEFT){
			visualLabel = "l:"+label;
		}else if(side==DeltaNode.RIGHT){
			visualLabel = "r:"+label;
		}else{
			visualLabel = label;
		}
		return visualLabel;
//		return label;
	}

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
	
	

	public DeltaNode(DeltaNode node) {
		// TODO Auto-generated constructor stub
		this.bModified = node.bModified;
		this.label = node.label;
		this.side = node.side;
	}

	public String getKind() {
		// TODO Auto-generated method stub
		LabelUtil tool = new LabelUtil();
		return tool.parse(label);
	}

	public String getComparedLabel() {
		// TODO Auto-generated method stub
		Pattern p = Pattern.compile("[\\d]");
		Matcher matcher = p.matcher(label);
		String result = matcher.replaceAll("");
		int mark = result.indexOf(":");
		result = result.substring(mark+1);
		return result;
	}

	
}
