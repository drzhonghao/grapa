package partial.code.grapa.delta.graph.xml;

import partial.code.grapa.delta.graph.AbstractEdge;


public class XmlEdge extends AbstractEdge{

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return ((XmlNode)from).label+"->"+((XmlNode)to).label;
	}


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

	@Override
	public int compareTo(Object obj) {
		// TODO Auto-generated method stub
		XmlEdge edge = (XmlEdge)obj;		
		return (type-edge.type)*(edge.from.side-edge.to.side)
				*((XmlNode)edge.from).label.compareTo(((XmlNode)from).label)
				*((XmlNode)edge.to).label.compareTo(((XmlNode)to).label);
	}
}
