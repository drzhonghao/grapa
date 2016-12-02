package partial.code.grapa.delta.graph.xml;

public class XmlNode {
	public XmlNode(String l) {
		// TODO Auto-generated constructor stub
		label = l;
	}
	public XmlNode(String l, int s, boolean b) {
		// TODO Auto-generated constructor stub
		label = l;
		side = s;
		bModified = b;
	}
	public static final int LEFT  = 1;
	public static final int RIGHT = 2;
	public int side = -1;
	public String label;	
	public boolean bModified;
	public String getKind() {
		// TODO Auto-generated method stub
		
		return LabelParser.parse(label);
	}
}
