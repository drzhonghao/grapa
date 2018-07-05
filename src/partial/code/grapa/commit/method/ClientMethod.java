package partial.code.grapa.commit.method;

import java.util.ArrayList;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;

import partial.code.grapa.tool.JdtUtil;



public class ClientMethod {
	public String key;
	public String methodName;
	public String sig;
	public ASTNode ast;
	
	public MethodDeclaration methodbody;
	
	public ClientMethod(String key, String methodName, String sig) {
		// TODO Auto-generated constructor stub
		this.key = key;
		this.methodName = methodName;
		this.sig = sig;
	}

	public ClientMethod(String key, String methodName, MethodDeclaration node) {
		// TODO Auto-generated constructor stub
		this.key = key;
		this.methodName = methodName;
		methodbody = node;
	}

	public String getSignature() {
		// TODO Auto-generated method stub
		String line = getTypeName();
		return line+"."+methodName+sig;
	}

	public String getTypeName() {
		// TODO Auto-generated method stub
		String line = key.substring(1);
		line = line.replaceAll("/", ".");
		return line;
	}

	public void resolveSig() {
		// TODO Auto-generated method stub
		IMethodBinding mdb = methodbody.resolveBinding();
		IJavaElement element = mdb.getJavaElement();
		this.sig = JdtUtil.getMethodSignature(element);
		if(sig!=null){
			int mark = sig.indexOf("(");
			sig = sig.substring(mark);
		}
	}

	public String getMethodName() {
		String out = methodName.replaceAll("<", "");
		out = out.replaceAll(">", "");
		return out;
	}
	
}
