package partial.code.grapa.wala;

import com.ibm.wala.classLoader.IMethod;
import com.ibm.wala.ipa.slicer.NormalReturnCaller;
import com.ibm.wala.ipa.slicer.NormalStatement;
import com.ibm.wala.ipa.slicer.ParamCallee;
import com.ibm.wala.ipa.slicer.PhiStatement;
import com.ibm.wala.ipa.slicer.Statement;
import com.ibm.wala.util.Predicate;




public class IntraPredicate  extends Predicate{
	private  MethodEntry currentMethod;

	public IntraPredicate(MethodEntry method) {
		// TODO Auto-generated constructor stub
		currentMethod = method;
	}

	@Override
	public boolean test(Object obj) {

		
		if(obj instanceof NormalStatement){
	  		  NormalStatement ns = (NormalStatement)obj;
	  		  IMethod method = ns.getNode().getMethod();
	  		  return isIntraNode(method);
	  	 }else if(obj instanceof NormalReturnCaller){
	  		  NormalReturnCaller ns = (NormalReturnCaller)obj;
	  		  IMethod method = ns.getNode().getMethod();
	  		  return isIntraNode(method);
	  	 }else if(obj instanceof ParamCallee){
	  		  ParamCallee pc = (ParamCallee)obj;
	  		  IMethod method = pc.getNode().getMethod();
	  		  return isIntraNode(method);
	  	 }else if(obj instanceof PhiStatement){
	  		  PhiStatement ns = (PhiStatement)obj;
	  		  IMethod method = ns.getNode().getMethod();
	  		  return isIntraNode(method);
	  	 }else if(obj instanceof NormalReturnCaller){
	  		  NormalReturnCaller ns = (NormalReturnCaller)obj;
	  		  IMethod method = ns.getNode().getMethod();
	  		  return isIntraNode(method);
	  	 }
	  	 return false;
	}

	private boolean isIntraNode(IMethod method) {
		 boolean bIntraNode = false;
		  String n1 = method.getSignature();
		  String n2 = currentMethod.getSignature();
		  
		  if(n1.compareTo(n2)==0){
			  bIntraNode = true;
		  }
		  return bIntraNode;
	}

	
}
