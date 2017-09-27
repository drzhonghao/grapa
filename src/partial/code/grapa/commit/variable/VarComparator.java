package partial.code.grapa.commit.variable;

import java.util.ArrayList;

import org.eclipse.jdt.core.dom.ASTNode;


import partial.code.grapa.commit.Comparator;

public class VarComparator extends Comparator{
	private ArrayList<VarDelta> vars;


	public ArrayList<VarDelta> getResults() {
		// TODO Auto-generated method stub
		return vars;
	}

	@Override
	protected void extractFinerMapping(ASTNode leftTree, ASTNode rightTree) {
		// TODO Auto-generated method stub
		
	}

}
