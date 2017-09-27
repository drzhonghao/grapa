package partial.code.grapa.commit.method;

import java.io.File;
import java.util.ArrayList;
import java.util.Hashtable;
import org.eclipse.jdt.core.dom.ASTNode;

import partial.code.grapa.commit.Comparator;
import partial.code.grapa.delta.graph.SDGComparator;
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
		// TODO Auto-generated method stub
		System.out.println(oldMethod.methodName);
		SDGwithPredicate lfg = leftEngine.buildSystemDependencyGraph(oldMethod);
		IR lir = leftEngine.getCurrentIR();
		
		SDGwithPredicate rfg = rightEngine.buildSystemDependencyGraph(newMethod);
		IR rir = rightEngine.getCurrentIR();
		
		SDGComparator gt = new SDGComparator(lir, rir, bResolveAst,oldMethod, newMethod);
		MethodDelta md = gt.compare(lfg, rfg);	
		return md;
	}


	@Override
	protected void doCompare(ArrayList<ASTNode> leftTrees, ArrayList<ASTNode> rightTrees) {
		// TODO Auto-generated method stub
		M2MComparator comparator = new M2MComparator(leftTrees, rightTrees);
		comparator.extractMappings();
		Hashtable<ClientMethod, ClientMethod> mps = comparator.getResults();
		for(ClientMethod oldMethod:mps.keySet()){
			ClientMethod newMethod = mps.get(oldMethod);					
			MethodDelta md = compareMethodPair(oldMethod, newMethod, bResolveAst);				
			methods.add(md);
		}
	}
	
	
	
	

	

}
