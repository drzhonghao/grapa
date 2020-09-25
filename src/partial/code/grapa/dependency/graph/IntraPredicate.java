package partial.code.grapa.dependency.graph;



import partial.code.grapa.commit.method.ClientMethod;

import com.ibm.wala.cast.ipa.callgraph.AstCallGraph.AstFakeRoot;
import com.ibm.wala.cast.loader.AstMethod;
import com.ibm.wala.classLoader.IMethod;
import com.ibm.wala.ipa.slicer.ExceptionalReturnCallee;
import com.ibm.wala.ipa.slicer.ExceptionalReturnCaller;
import com.ibm.wala.ipa.slicer.HeapStatement;
import com.ibm.wala.ipa.slicer.MethodEntryStatement;
import com.ibm.wala.ipa.slicer.MethodExitStatement;
import com.ibm.wala.ipa.slicer.NormalReturnCallee;
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

	@Override
	public boolean test(Object obj) {
		if(obj instanceof MethodEntryStatement
				||obj instanceof MethodExitStatement
				||obj instanceof ParamCaller
				||obj instanceof NormalReturnCaller
				||obj instanceof NormalReturnCallee
				||obj instanceof ExceptionalReturnCallee
				||obj instanceof ExceptionalReturnCaller){
			return false;
		}else {
			return super.test(obj);
		}
	}

}
