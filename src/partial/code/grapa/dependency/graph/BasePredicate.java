package partial.code.grapa.dependency.graph;

import com.ibm.wala.classLoader.IMethod;
import com.ibm.wala.ipa.slicer.HeapStatement;
import com.ibm.wala.ipa.slicer.MethodEntryStatement;
import com.ibm.wala.ipa.slicer.MethodExitStatement;
import com.ibm.wala.ipa.slicer.NormalReturnCaller;
import com.ibm.wala.ipa.slicer.NormalStatement;
import com.ibm.wala.ipa.slicer.ParamCallee;
import com.ibm.wala.ipa.slicer.PhiStatement;
import com.ibm.wala.ipa.slicer.Statement;
import com.ibm.wala.ipa.slicer.StatementWithInstructionIndex;


public abstract class BasePredicate extends Predicate{
	abstract protected boolean isValidNode(IMethod method);
	@Override
	public boolean test(Object obj) {
		
		if(obj instanceof HeapStatement){
			return false;
	  	 }
//		else if(obj instanceof NormalReturnCaller){
//	  		NormalReturnCaller ns = (NormalReturnCaller)obj;
//	  		IMethod method = ns.getNode().getMethod();
//	  		return isValidNode(method);
//	  	 }else if(obj instanceof ParamCallee){
//	  		ParamCallee pc = (ParamCallee)obj;
//	  		IMethod method = pc.getNode().getMethod();
//	  		return isValidNode(method);
//	  	 }else if(obj instanceof PhiStatement){
//	  		 PhiStatement ns = (PhiStatement)obj;
//	  		 IMethod method = ns.getNode().getMethod();
//	  		 return isValidNode(method);
//	  	 }else if(obj instanceof MethodEntryStatement) {
//	  		 MethodEntryStatement ms = (MethodEntryStatement)obj;
//	  		 IMethod method = ms.getNode().getMethod();
//	  		 return isValidNode(method);
//	  	 }else if(obj instanceof MethodExitStatement) {
//	  		MethodExitStatement ms = (MethodExitStatement)obj;
//	  		IMethod method = ms.getNode().getMethod();
//	  		return isValidNode(method);
//	  	 }		
		Statement s = (Statement)obj;
		IMethod method = s.getNode().getMethod();
		return isValidNode(method);
	}
}
