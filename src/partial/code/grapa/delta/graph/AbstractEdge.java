package partial.code.grapa.delta.graph;

public class AbstractEdge implements Comparable{
	public int type;
	public AbstractNode from;
	public AbstractNode to;
	public static final int DATA_FLOW = 0;
	public static final int CONTROL_FLOW = 1;
	public static final int CHANGE = 2;
	
	public AbstractEdge(AbstractNode from, AbstractNode to, int m) {
		// TODO Auto-generated constructor stub
		this.from = from;
		this.to = to;
		type = m;
	}

	@Override
	public int compareTo(Object obj) {
		// TODO Auto-generated method stub
		AbstractEdge edge = (AbstractEdge)obj;		
		return (type-edge.type)*(edge.from.side-edge.to.side);
	}
}
