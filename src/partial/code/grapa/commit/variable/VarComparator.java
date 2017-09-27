package partial.code.grapa.commit.variable;

import java.util.ArrayList;

import org.eclipse.jdt.core.dom.ASTNode;


import partial.code.grapa.commit.Comparator;

public class VarComparator extends Comparator{
	private ArrayList<VarDelta> vars;
	@Override
	protected void doCompare(ArrayList<ASTNode> leftTrees, ArrayList<ASTNode> rightTrees) {
		// TODO Auto-generated method stub
		
	}

	public ArrayList<VarDelta> getResults() {
		// TODO Auto-generated method stub
		return vars;
	}

}
