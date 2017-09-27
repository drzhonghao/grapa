package partial.code.grapa.commit.variable;

import java.util.ArrayList;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;



public class VarVisitor  extends ASTVisitor {
	public ArrayList<Var> vars = new ArrayList<Var>();

	@Override
	public boolean visit(VariableDeclarationFragment node) {
		// TODO Auto-generated method stub
		IVariableBinding nodeB = node.resolveBinding();
		if(nodeB!=null) {
			Var var = new Var();
			vars.add(var);
			var.name = node.getName().getIdentifier();
			var.type = nodeB.getName();
			var.location = resolveLocation(node);
		}
		return super.visit(node);
	}

	private String resolveLocation(VariableDeclarationFragment node) {
		// TODO Auto-generated method stub
		ASTNode parent = node.getParent();
		while(parent!=null) {
			if(parent instanceof MethodDeclaration) {
				break;
			}
			parent = parent.getParent();
		}
		String name = null;
		if(parent!=null) {
			MethodDeclaration method = (MethodDeclaration)parent;
			name = method.getName().getIdentifier();
		}
		return name;
	}

}
