package partial.code.grapa.delta.graph;

public class AbstractEdge {
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
}
