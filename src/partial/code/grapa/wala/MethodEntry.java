package partial.code.grapa.wala;

import java.util.HashSet;

import com.ibm.wala.classLoader.IMethod;
import com.ibm.wala.ipa.callgraph.AnalysisScope;
import com.ibm.wala.ipa.callgraph.Entrypoint;
import com.ibm.wala.ipa.callgraph.impl.DefaultEntrypoint;
import com.ibm.wala.ipa.cha.ClassHierarchy;
import com.ibm.wala.types.ClassLoaderReference;
import com.ibm.wala.types.Descriptor;
import com.ibm.wala.types.MethodReference;
import com.ibm.wala.types.TypeName;
import com.ibm.wala.types.TypeReference;
import com.ibm.wala.util.collections.HashSetFactory;
import com.ibm.wala.util.strings.Atom;

import sun.reflect.generics.tree.MethodTypeSignature;
import sun.reflect.generics.tree.TypeSignature;
import sun.reflect.generics.tree.*;
public class MethodEntry {
	public String typeName;
	public String name;
	public String sig;
	public Iterable<Entrypoint> entryPoint;
	public IMethod method;
	
	public MethodEntry(String methodName, String sig, String key, AnalysisScope scope, ClassHierarchy cha) {
		ClassLoaderReference loaderRef = scope.getApplicationLoader();
		Atom mainMethod = Atom.findOrCreateAsciiAtom(methodName);
		TypeReference type = TypeReference.findOrCreate(loaderRef, TypeName.string2TypeName(key));
		MethodReference mainRef = MethodReference.findOrCreate(type, mainMethod, Descriptor
                .findOrCreateUTF8(sig));
		HashSet<Entrypoint> eps = HashSetFactory.make();
		IMethod method = cha.resolveMethod(mainRef);
		if(method!=null) {
			DefaultEntrypoint p = new DefaultEntrypoint(method, cha);		
			eps.add(p);
			this.entryPoint = eps;
			this.name = methodName;
			this.typeName = key;
			this.sig = sig;
			this.method = method;
		}
	}
	
	public MethodEntry(HashSet<Entrypoint> eps, String name, String typeName, String desc, IMethod method) {
		this.entryPoint = eps;
		this.name = name;
		this.typeName = typeName;
		this.sig = desc;
		this.method = method;
	}

	public String getSignature() {
		String result = typeName.substring(1) + "." + name;
		result = result.replaceAll("/", ".");
		result += sig;
		return result;
	}
	public String getShortTypeName() {
		// TODO Auto-generated method stub
		int mark = typeName.lastIndexOf("/");
		return typeName.substring(mark+1);
	}
	public String getShortName() {
		// TODO Auto-generated method stub
		String result = name;
		result = result.replaceAll("<", "");
		result = result.replaceAll(">", "");
		return result;
	}
	public String getFileName() {
		// TODO Auto-generated method stub
		String line = getShortName()+getShortSig();
		return line;
	}
	private String getShortSig() {
		// TODO Auto-generated method stub
		String line = "(";
		SignatureParser parser = new SignatureParser();
		MethodTypeSignature msig = parser.parseMethodSig(sig);
		for(TypeSignature pt:msig.getParameterTypes()) {
			if(pt instanceof ArrayTypeSignature) {
				ArrayTypeSignature ats = (ArrayTypeSignature)pt;
				line += getType(ats.getComponentType())+",";
			}else {
				line += getType(pt)+",";
			}
		}
		if(line.endsWith(",")) {
			line = line.substring(0, line.length()-1);
		}
		line = line+")";
		return line;
	}
	private String getType(TypeSignature ts) {
		// TODO Auto-generated method stub
		String line = "";
		if(ts instanceof BooleanSignature) {
			line = "boolean";
		}else if(ts instanceof ByteSignature) {
			line = "byte";
		}else if(ts instanceof CharSignature) {
			line = "char";
		}else if(ts instanceof DoubleSignature) {
			line = "double";
		}else if(ts instanceof FloatSignature) {
			line = "float";
		}else if(ts instanceof IntSignature) {
			line = "int";
		}else if(ts instanceof LongSignature) {
			line = "long";
		}else if(ts instanceof ShortSignature) {
			line = "short";
		}else if(ts instanceof BottomSignature) {
			line = "bottom";
		}else if(ts instanceof ClassTypeSignature) {
			ClassTypeSignature cts = (ClassTypeSignature)ts;
			for(SimpleClassTypeSignature scts:cts.getPath()) {
				String name =  scts.getName();
				int mark = name.lastIndexOf(".");
				line += name.substring(mark+1)+",";
			}
			if(line.endsWith(",")) {
				line = line.substring(0, line.length()-1);
			}
		}else if(ts instanceof SimpleClassTypeSignature) {
			SimpleClassTypeSignature scts = (SimpleClassTypeSignature)ts;
			String name =  scts.getName();
			int mark = name.lastIndexOf(".");
			line += name.substring(mark+1);
		}else if(ts instanceof TypeVariableSignature) {
			line = "typevar";
		}
		return line;
	}
	public String getLongTypeName() {
		// TODO Auto-generated method stub
		String result = typeName.substring(1);
		result = result.replaceAll("/", ".");
		return result;
		
	}
	public String getFullName() {
		// TODO Auto-generated method stub
		return typeName+"#"+name+sig;
	}
}
