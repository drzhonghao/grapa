package partial.code.grapa.delta.graph;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import partial.code.grapa.tool.LabelUtil;

public class DeltaNode {
	public static final int LEFT  = 1;
	public static final int RIGHT = 2;
	public boolean bModified = true;
	public int side = 1;
	public int lineNo;
	
	@Override
	public String toString() {
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
	

	public DeltaNode(String l, int no, int s, boolean b) {
		label = l;
		lineNo = no;
		side = s;
		bModified = b;
	}
	
	public DeltaNode(String l, int no) {
		label = l;
		lineNo = no;
	}
	
	

	public DeltaNode(DeltaNode node) {
		this.bModified = node.bModified;
		this.label = node.label;
		this.side = node.side;
		this.lineNo = node.lineNo;
	}

	public DeltaNode(String visualLabel) {
		label = visualLabel;
	}

	public String getKind() {
		LabelUtil tool = new LabelUtil();
		return tool.parse(label);
	}

	public String getComparedLabel() {
		Pattern p = Pattern.compile("[\\d]");
		Matcher matcher = p.matcher(label);
		String result = matcher.replaceAll("");
		int mark = result.indexOf(":");
		result = result.substring(mark+1);
		return result;
	}

	public boolean isVar() {
		String type = getKind();
		if(type.compareTo("getfield")==0) {
			return true;
		}else if(type.compareTo("putfield")==0) {
			return true;
		}else if(type.compareTo("getstatic")==0) {
			return true;
		}else if(type.compareTo("putstatic")==0) {
			return true;
		}
		return false;
	}

	public boolean isMethod() {
		String type = getKind();
		if(type.compareTo("invokestatic")==0) {
			return true;
		}else if(type.compareTo("invokespecial")==0) {
			return true;
		}else if(type.compareTo("invokeinterface")==0) {
			return true;
		}else if(type.compareTo("invokevirtual")==0) {
			return true;
		}
		return false;
	}
}
