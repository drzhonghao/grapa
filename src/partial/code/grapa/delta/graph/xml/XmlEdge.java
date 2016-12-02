package partial.code.grapa.delta.graph.xml;

public class XmlEdge {
	public int type;
	public XmlNode from;
	public XmlNode to;
	public static final int DATA_FLOW = 0;
	public static final int CONTROL_FLOW = 1;
	public static final int CHANGE = 2;

	public XmlEdge(XmlNode from, XmlNode to, int m) {
		// TODO Auto-generated constructor stub
		this.from = from;
		this.to = to;
		type = m;
	}


	public XmlEdge(XmlNode f, XmlNode t, String label) {
		// TODO Auto-generated constructor stub
		from = f;
		to = t;
		if(label.compareTo("df")==0){
			type = DATA_FLOW;
		}else if(label.compareTo("cf")==0){
			type = CONTROL_FLOW;
		}else if(label.compareTo("m")==0){
			type = CHANGE;
		}
	}


	public String getKind() {
		// TODO Auto-generated method stub
		String line = "";
		if(type == DATA_FLOW){
			line = "df";
		}else if(type == CONTROL_FLOW){
			line = "cf";
		}else if(type == CHANGE){
			line = "m";
		}
		return line;
	}

}
