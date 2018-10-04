package partial.code.grapa.dependency.graph;

import com.ibm.wala.classLoader.IMethod;
import com.ibm.wala.ipa.slicer.NormalReturnCaller;
import com.ibm.wala.ipa.slicer.NormalStatement;
import com.ibm.wala.ipa.slicer.ParamCallee;
import com.ibm.wala.ipa.slicer.PhiStatement;
import com.ibm.wala.ipa.slicer.Statement;
import com.ibm.wala.util.Predicate;

import partial.code.grapa.wala.MethodEntry;




public class ApplicationPredicate  extends BasePredicate{

	public ApplicationPredicate() {
	}

	
	protected boolean isValidNode(IMethod method) {
		String line = method.toString();
		if(line.indexOf("Application,")>0) {
			return true;
		}else {
			return false;
		}
	}
	
}
