package partial.code.grapa.delta.graph;

public class DeltaEdge implements Comparable{
	public int type;
	public DeltaNode from;
	public DeltaNode to;
	public static final int DATA_FLOW = 0;
	public static final int CONTROL_FLOW = 1;
	public static final int CHANGE = 2;
	public double weight = 0;
	
	public DeltaEdge(DeltaNode from, DeltaNode to, int m) {
		// TODO Auto-generated constructor stub
		this.from = from;
		this.to = to;
		type = m;
	}

	public DeltaEdge(DeltaNode f, DeltaNode t, String label) {
		// TODO Auto-generated constructor stub
		if(label.compareTo("df")==0){
			type = DATA_FLOW;
		}else if(label.compareTo("cf")==0){
			type = CONTROL_FLOW;
		}else if(label.compareTo("m")==0){
			type = CHANGE;
		}
		
	}	
	
	public DeltaEdge(DeltaNode from, DeltaNode to, int type, int size) {
		// TODO Auto-generated constructor stub
		this(from, to, type);
		weight = size;
	}

	

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return ((DeltaNode)from).label+"->"+((DeltaNode)to).label +":"+weight;
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
		DeltaEdge edge = (DeltaEdge)obj;		
		return (type-edge.type)*(edge.from.side-edge.to.side)
				*((DeltaNode)edge.from).label.compareTo(((DeltaNode)from).label)
				*((DeltaNode)edge.to).label.compareTo(((DeltaNode)to).label);
	}
}
