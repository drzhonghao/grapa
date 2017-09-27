package partial.code.grapa.commit.method;

import java.util.ArrayList;
import java.util.Hashtable;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;

import partial.code.grapa.commit.AstComparator;
import partial.code.grapa.delta.graph.HungarianAlgorithm;
import uk.ac.shef.wit.simmetrics.similaritymetrics.Levenshtein;

public class M2MComparator extends AstComparator{

	private ArrayList<ClientMethod> leftMethods;
	private ArrayList<ClientMethod> rightMethods;
	private Hashtable<ClientMethod, ClientMethod> mms = new Hashtable<ClientMethod, ClientMethod> ();
	public M2MComparator(ArrayList<ASTNode> leftTrees,
			ArrayList<ASTNode> rightTrees) {
		// TODO Auto-generated constructor stub
		super(leftTrees, rightTrees);
	}

	

	private Hashtable<Object, Object> extractMethodMapping(
			ASTNode leftTree, ASTNode rightTree) {
		// TODO Auto-generated method stub
		
		mms.clear();
		ClientMethodVisitor visitor = new ClientMethodVisitor();
		leftTree.accept(visitor);
		leftMethods = new ArrayList<ClientMethod>();
		leftMethods.addAll(visitor.methods);
		
		visitor.clear();
		rightTree.accept(visitor);
		rightMethods = new ArrayList<ClientMethod>();
		rightMethods.addAll(visitor.methods);
		
		if(leftMethods.size()==0||rightMethods.size()==0){
			return null;
		}		
		
		MethodMapping comparator = new MethodMapping(leftMethods, rightMethods);
		return comparator.extractNodeMappings();
	}

	public int getDeltaMethod() {
		// TODO Auto-generated method stub
		return Math.abs(this.leftMethods.size()-this.rightMethods.size());
	}



	@Override
	protected void extractFinerMapping(ASTNode leftTree, ASTNode rightTree) {
		// TODO Auto-generated method stub
		Hashtable<Object, Object> mm = extractMethodMapping(leftTree, rightTree);
		if(mm!=null){
			for(Object o1:mm.keySet()){
				ClientMethod m1 = (ClientMethod)o1;
				ClientMethod m2 = (ClientMethod) mm.get(m1);
				if(m1.methodbody.toString().compareTo(m2.methodbody.toString())!=0){
					m1.ast = leftTree;
					m2.ast = rightTree;
					mms.put(m1, m2);
				}
			}
		}
		for(ClientMethod m1:mms.keySet()){
			ClientMethod m2 = mms.get(m1);
			m1.resolveSig();
			m2.resolveSig();
		}	
	}



	public Hashtable<ClientMethod, ClientMethod> getResults() {
		// TODO Auto-generated method stub
		return mms;
	}

}
