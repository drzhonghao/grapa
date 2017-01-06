package partial.code.grapa.change;

public class AbstractNodeEdit extends AbstractEdit{
	private String nodeName;

	public AbstractNodeEdit(String nodeName) {
		super();
		this.nodeName = nodeName;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return nodeName;
	}
	
}
