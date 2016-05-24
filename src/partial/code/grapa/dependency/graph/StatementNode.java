package partial.code.grapa.dependency.graph;

import partial.code.grapa.delta.graph.GraphComparator;

import org.eclipse.jdt.core.dom.ASTNode;

import com.ibm.wala.ipa.slicer.NormalStatement;
import com.ibm.wala.ipa.slicer.Statement;
import com.ibm.wala.ssa.IR;

public class StatementNode {
	public int side = 0;
	public Statement statement;
	public ASTNode node;
	public static final int LEFT  = 1;
	public static final int RIGHT = 2;
	
	public StatementNode(Statement statement) {
		// TODO Auto-generated constructor stub
		this.statement = statement;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return statement.toString();
	}

	public boolean isSameType(StatementNode sn) {
		// TODO Auto-generated method stub
		String t1;
		String t2;
		if(statement instanceof NormalStatement&&sn.statement instanceof NormalStatement){
			NormalStatement ns1 = (NormalStatement)statement;
			NormalStatement ns2 = (NormalStatement)sn.statement;
			t1 = ns1.getInstruction().getClass().getTypeName();
			t2 = ns2.getInstruction().getClass().getTypeName();
		}else{
			t1 = statement.getClass().getTypeName();
			t2 = sn.statement.getClass().getTypeName();
		}
		return t1.compareTo(t2)==0;
	}

	

}
