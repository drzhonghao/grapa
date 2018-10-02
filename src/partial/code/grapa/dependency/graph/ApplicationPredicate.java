package partial.code.grapa.dependency.graph;

import com.ibm.wala.classLoader.IMethod;
import com.ibm.wala.ipa.slicer.NormalReturnCaller;
import com.ibm.wala.ipa.slicer.NormalStatement;
import com.ibm.wala.ipa.slicer.ParamCallee;
import com.ibm.wala.ipa.slicer.PhiStatement;
import com.ibm.wala.ipa.slicer.Statement;
import com.ibm.wala.util.Predicate;

import partial.code.grapa.wala.MethodEntry;




public class ApplicationPredicate  extends Predicate{

	public ApplicationPredicate() {
	}

	@Override
	public boolean test(Object obj) {

		
		if(obj instanceof NormalStatement){
	  		  NormalStatement ns = (NormalStatement)obj;
	  		  IMethod method = ns.getNode().getMethod();
	  		  return isValidNode(method);
	  	 }else if(obj instanceof NormalReturnCaller){
	  		  NormalReturnCaller ns = (NormalReturnCaller)obj;
	  		  IMethod method = ns.getNode().getMethod();
	  		  return isValidNode(method);
	  	 }else if(obj instanceof ParamCallee){
	  		  ParamCallee pc = (ParamCallee)obj;
	  		  IMethod method = pc.getNode().getMethod();
	  		  return isValidNode(method);
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
		String line = method.toString();
		if(line.indexOf("Application,")>0) {
			return true;
		}else {
			return false;
		}
	}
	
}
