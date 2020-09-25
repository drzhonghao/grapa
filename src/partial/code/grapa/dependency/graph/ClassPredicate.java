package partial.code.grapa.dependency.graph;



import partial.code.grapa.commit.method.ClientMethod;

import com.ibm.wala.cast.ipa.callgraph.AstCallGraph.AstFakeRoot;
import com.ibm.wala.cast.loader.AstMethod;
import com.ibm.wala.classLoader.IMethod;
import com.ibm.wala.ipa.slicer.HeapStatement;
import com.ibm.wala.ipa.slicer.MethodEntryStatement;
import com.ibm.wala.ipa.slicer.MethodExitStatement;
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

public class ClassPredicate extends BasePredicate{
	private String className;

	public ClassPredicate(String className) {
		this.className = className;
	}
	

	protected boolean isValidNode(IMethod method) {
		  boolean bIntraNode = false;
		  String n1 = method.getSignature();
		  if(n1.indexOf(className)>=0){
			  bIntraNode = true;
		  }
		  return bIntraNode;
	}

}
