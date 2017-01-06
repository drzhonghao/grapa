package partial.code.grapa.delta.graph.xml;

import partial.code.grapa.delta.graph.AbstractNode;

public class XmlNode extends AbstractNode{
	public String label;
	

	public XmlNode(String l, int s, boolean b) {
		// TODO Auto-generated constructor stub
		label = l;
		side = s;
		bModified = b;
	}
	
	public XmlNode(String l) {
		// TODO Auto-generated constructor stub
		label = l;
	}

	public String getKind() {
		// TODO Auto-generated method stub
		LabelTool tool = new LabelTool();
		return tool.parse(label);
	}

	
}
