package partial.code.grapa.commit.method;

import java.io.File;
import java.util.ArrayList;
import java.util.Hashtable;
import org.eclipse.jdt.core.dom.ASTNode;

import partial.code.grapa.commit.Comparator;
import partial.code.grapa.delta.graph.mapping.SDGComparator;
import partial.code.grapa.dependency.graph.DataFlowAnalysisEngine;
import partial.code.grapa.dependency.graph.SDGwithPredicate;
import partial.code.grapa.version.detect.VersionDetector;
import partial.code.grapa.version.detect.VersionPair;

import com.ibm.wala.ssa.IR;

public class MethodComparator extends Comparator{
	private ArrayList<MethodDelta> methods = new ArrayList<MethodDelta>();
	private boolean bResolveAst;

	public ArrayList<MethodDelta> getResults(){
		return methods;
	}
	
	public void analyzeCommit(File d, boolean bResolveAst) {
		methods.clear();
		this.bResolveAst = bResolveAst;
		analyzeCommit(d);
	}

	
	private MethodDelta compareMethodPair(ClientMethod oldMethod,
			ClientMethod newMethod, boolean bResolveAst) {
		System.out.println(oldMethod.methodName);
		SDGwithPredicate lfg = leftEngine.buildSystemDependencyGraph(oldMethod);
		IR lir = leftEngine.getCurrentIR();
		
		SDGwithPredicate rfg = rightEngine.buildSystemDependencyGraph(newMethod);
		IR rir = rightEngine.getCurrentIR();
		
		SDGComparator gt = new SDGComparator(lir, rir, bResolveAst,oldMethod, newMethod);
		MethodDelta md = gt.compare(lfg, rfg);	
		return md;
	}


	
	private Hashtable<Object, Object> extractMethodMapping(
			ASTNode leftTree, ASTNode rightTree) {
		// TODO Auto-generated method stub	
		MethodVisitor visitor = new MethodVisitor();
		leftTree.accept(visitor);
		ArrayList<ClientMethod> leftMethods = new ArrayList<ClientMethod>();
		leftMethods.addAll(visitor.methods);
		
		visitor.clear();
		rightTree.accept(visitor);
		ArrayList<ClientMethod> rightMethods = new ArrayList<ClientMethod>();
		rightMethods.addAll(visitor.methods);
		
		if(leftMethods.size()==0||rightMethods.size()==0){
			return null;
		}		
		
		MethodMapping comparator = new MethodMapping(leftMethods, rightMethods);
		return comparator.extractItemMappings();
	}

	@Override
	protected void extractFinerMapping(ASTNode leftTree, ASTNode rightTree) {
		Hashtable<Object, Object> mm = extractMethodMapping(leftTree, rightTree);
		Hashtable<ClientMethod, ClientMethod> mms = new Hashtable<ClientMethod, ClientMethod>();
		if(mm!=null){
			for(Object o1:mm.keySet()){
				ClientMethod m1 = (ClientMethod)o1;
				ClientMethod m2 = (ClientMethod) mm.get(m1);
				if(m1.methodbody.toString().compareTo(m2.methodbody.toString())!=0){
					m1.ast = leftTree;
					m2.ast = rightTree;
					mms .put(m1, m2);
				}
			}
		}
		for(ClientMethod m1:mms.keySet()){
			ClientMethod m2 = mms.get(m1);
			m1.resolveSig();
			m2.resolveSig();
		}
		for(ClientMethod oldMethod:mms.keySet()){
			ClientMethod newMethod = mms.get(oldMethod);					
			MethodDelta md = compareMethodPair(oldMethod, newMethod, bResolveAst);				
			methods.add(md);
		}
	}
	
	
	
	

	

}
