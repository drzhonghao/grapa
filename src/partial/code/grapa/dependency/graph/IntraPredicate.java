package partial.code.grapa.dependency.graph;

import com.ibm.wala.util.Predicate;

import partial.code.grapa.commit.method.ClientMethod;

import com.ibm.wala.cast.ipa.callgraph.AstCallGraph.AstFakeRoot;
import com.ibm.wala.cast.loader.AstMethod;
import com.ibm.wala.classLoader.IMethod;
import com.ibm.wala.ipa.slicer.HeapStatement;
import com.ibm.wala.ipa.slicer.NormalReturnCaller;
import com.ibm.wala.ipa.slicer.NormalStatement;
import com.ibm.wala.ipa.slicer.ParamCallee;
import com.ibm.wala.ipa.slicer.ParamCaller;
import com.ibm.wala.ipa.slicer.PhiStatement;
import com.ibm.wala.ssa.SSAAbstractInvokeInstruction;
import com.ibm.wala.ssa.SSAFieldAccessInstruction;
import com.ibm.wala.ssa.SSAInstruction;
import com.ibm.wala.ssa.SSANewInstruction;
import com.ibm.wala.types.FieldReference;
import com.ibm.wala.types.MethodReference;
import com.ibm.wala.types.TypeReference;
import com.ibm.wala.util.Predicate;

public class IntraPredicate extends BasePredicate{
	private String sig;

	public IntraPredicate(String sig) {
		this.sig = sig;
	}
	
	protected boolean isValidNode(IMethod method) {
		  boolean bIntraNode = false;
		  String n1 = method.getSignature();
		  if(n1.compareTo(sig)==0){
			  bIntraNode = true;
		  }
//		  return  true;
		  return bIntraNode;
	}

}
