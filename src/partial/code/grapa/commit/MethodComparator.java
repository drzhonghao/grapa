package partial.code.grapa.commit;

import java.io.File;
import java.util.ArrayList;
import java.util.Hashtable;
import org.eclipse.jdt.core.dom.ASTNode;

import partial.code.grapa.delta.graph.SDGComparator;
import partial.code.grapa.dependency.graph.DataFlowAnalysisEngine;
import partial.code.grapa.dependency.graph.SDGwithPredicate;
import partial.code.grapa.mapping.AstTreeComparator;
import partial.code.grapa.mapping.ClientMethod;
import partial.code.grapa.version.detect.VersionDetector;
import partial.code.grapa.version.detect.VersionPair;

import com.ibm.wala.ssa.IR;

public class MethodComparator extends Comparator{
	private ArrayList<MethodDelta> methods = new ArrayList<MethodDelta>();
	private boolean bResolveAst;

	
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
		AstTreeComparator comparator = new AstTreeComparator(leftTrees, rightTrees);
		Hashtable<ClientMethod, ClientMethod> mps = comparator.extractMappings();
		
		for(ClientMethod oldMethod:mps.keySet()){
			ClientMethod newMethod = mps.get(oldMethod);					
			MethodDelta md = compareMethodPair(oldMethod, newMethod, bResolveAst);				
			methods.add(md);
		}
	}
	
	
	
	

	

}
