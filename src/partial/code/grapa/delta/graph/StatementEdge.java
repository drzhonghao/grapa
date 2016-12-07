package partial.code.grapa.delta.graph;

public class StatementEdge extends AbstractEdge{

	public StatementEdge(StatementNode from, StatementNode to, int m) {
		// TODO Auto-generated constructor stub
		super(from, to, m);
	}

	@Override
	public String toString() {
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
