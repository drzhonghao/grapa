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

public class ClassPredicate extends Predicate{
	private String className;

	public ClassPredicate(String className) {
		this.className = className;
	}

	@Override
	public boolean test(Object obj) {
		// TODO Auto-generated method stub
	
		if(obj instanceof NormalStatement){
	  		  NormalStatement ns = (NormalStatement)obj;
	  		  IMethod method = ns.getNode().getMethod();
//	  		  String name = ns.getInstruction().toString();
//	  		  return isIntraNode(method)&&name.indexOf("<init>")<0;
	  		  return isValidNode(method);
	  	 }else if(obj instanceof NormalReturnCaller){
	  		  NormalReturnCaller ns = (NormalReturnCaller)obj;
	  		  IMethod method = ns.getNode().getMethod();
	  		  return isValidNode(method);
	  	 }else if(obj instanceof ParamCallee){
	  		  ParamCallee pc = (ParamCallee)obj;
	  		  IMethod method = pc.getNode().getMethod();
	  		  return isValidNode(method);
//	  		  return isValidNode(method)&&pc.getValueNumber()!=1;
	  	 }else if(obj instanceof PhiStatement){
	  		  PhiStatement ns = (PhiStatement)obj;
	  		  IMethod method = ns.getNode().getMethod();
	  		  return isValidNode(method);
	  	 }else if(obj instanceof NormalReturnCaller){
	  		  NormalReturnCaller ns = (NormalReturnCaller)obj;
	  		  IMethod method = ns.getNode().getMethod();
	  		  return isValidNode(method);
	  	 }
	  	 return false;
	}

	private boolean isValidNode(IMethod method) {
		  boolean bIntraNode = false;
		  String n1 = method.getSignature();
		  if(n1.indexOf(className)>=0){
			  bIntraNode = true;
		  }
//		  return  true;
		  return bIntraNode;
	}

}
