package partial.code.grapa.commit.method;

import java.lang.reflect.Modifier;
import java.util.ArrayList;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;

import partial.code.grapa.tool.JdtUtil;

public class MethodVisitor extends ASTVisitor {

	public ArrayList<ClientMethod> methods = new ArrayList<ClientMethod>();

	@Override
	public boolean visit(MethodDeclaration node) {
		// TODO Auto-generated method stub
		IMethodBinding mdb = node.resolveBinding();
		if(mdb!=null&&!Modifier.isAbstract(mdb.getModifiers())&&!Modifier.isInterface(mdb.getModifiers())&&!mdb.getDeclaringClass().isAnonymous()){
			
			String key = mdb.getDeclaringClass().getKey();
			key = key.substring(0, key.length()-1);
			int mark = key.indexOf("~");
			if(mark>0){
				String shortname = key.substring(mark+1);
				mark = key.lastIndexOf("/");
				String longname = key.substring(0, mark+1);
				key = longname+shortname;					
			}
			key = key.replaceAll("<[a-z;A-z]+>", "");
			key = key.replace(".", "$");
			
			String methodName;
			if(mdb.isConstructor()){
				methodName = "<init>";
			}else{
				methodName = node.getName().getFullyQualifiedName();
			}
			
			ClientMethod cm = new ClientMethod(key, methodName, node);
			cm.methodbody = node;
			methods.add(cm);
		}
		return super.visit(node);
	}

	

	public void clear() {
		// TODO Auto-generated method stub
		this.methods.clear();
	}

}
