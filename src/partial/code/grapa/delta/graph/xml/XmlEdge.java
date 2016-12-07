package partial.code.grapa.delta.graph.xml;

import partial.code.grapa.delta.graph.AbstractEdge;


public class XmlEdge extends AbstractEdge{

	public XmlEdge(XmlNode from, XmlNode to, int m) {
		// TODO Auto-generated constructor stub
		super(from, to, m);
	}


	public XmlEdge(XmlNode f, XmlNode t, String label) {
		// TODO Auto-generated constructor stub
		super(f,t,-1);

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
