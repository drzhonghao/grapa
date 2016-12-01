package partial.code.grapa.delta.xmlgraph.data;

import java.util.ArrayList;

public class LabelParser {

	private static ArrayList<String> labelTypes;
	
	static{
		
	}

	

	public static String parse(String label) {
		// TODO Auto-generated method stub
		labelTypes = new ArrayList<String>();
		labelTypes.add("conditional branch");
		labelTypes.add("getfield");
		labelTypes.add("invokeinterface");
		labelTypes.add("invokevirtual");
		labelTypes.add("goto");
		labelTypes.add("arraystore");
		labelTypes.add("putfield");
		labelTypes.add("PARAM_CALLEE");
		labelTypes.add("binaryop");
		labelTypes.add("getstatic");
		labelTypes.add("checkcast");
		labelTypes.add("new");
		labelTypes.add("enclosing");
		labelTypes.add("PARAM_CALLEE");
		labelTypes.add("phi");
		labelTypes.add("return");
		labelTypes.add("invokespecial");
		labelTypes.add("neg");
		labelTypes.add("invokestatic");
		labelTypes.add("throw");
		labelTypes.add("instanceof");
		labelTypes.add("arraylength");
		labelTypes.add("monitorexit");
		labelTypes.add("monitorenter");
		labelTypes.add("lexical:loader@");
		labelTypes.add("arrayload");
		labelTypes.add("lexical:name@");
		labelTypes.add("load_metadata");
		labelTypes.add("lexical:bundleContext@");
		labelTypes.add("conversion");
		labelTypes.add("lexical:id");
		labelTypes.add("switch");
		labelTypes.add("lexical:task");
		labelTypes.add("putstatic");
		labelTypes.add("lexical:toBeConverted@");
		labelTypes.add("lexical:b@");
		labelTypes.add("lexical:event@");
		labelTypes.add("assert");
		labelTypes.add("lexical:dispatcher@");
		labelTypes.add("lexical:toType@");
		labelTypes.add("lexical:fromValue@");
		labelTypes.add("lexical:type@");
		labelTypes.add("lexical:resName@");
		labelTypes.add("lexical:schemaMap@");
		labelTypes.add("lexical:container@");
		labelTypes.add("lexical:entry@");
		labelTypes.add("lexical:namingClass@");
		labelTypes.add("lexical:env@");
		labelTypes.add("bitnot");
		labelTypes.add("lexical:ctx@");
		labelTypes.add("lexical:pair@");
		labelTypes.add("lexical:filter@");
		labelTypes.add("lexical:rebind@");
		labelTypes.add("lexical:interface");
		labelTypes.add("lexical:value@");
		labelTypes.add("lexical:context@");
		labelTypes.add("lexical:urlScheme@");
		labelTypes.add("lexical:environment@");
		labelTypes.add("lexical:obj@");
		labelTypes.add("lexical:attrs@");
		labelTypes.add("lexical:nameCtx@");
		labelTypes.add("lexical:className@");
		labelTypes.add("lexical:cl2@");
		labelTypes.add("lexical:props@");
		labelTypes.add("lexical:m@");
		labelTypes.add("lexical:dataModel@");
		labelTypes.add("lexical:index@");
//		labelTypes.add("goto");
//		labelTypes.add("goto");
//		labelTypes.add("goto");
//		labelTypes.add("goto");
//		labelTypes.add("goto");
//		labelTypes.add("goto");
//		labelTypes.add("goto");
//		labelTypes.add("goto");
//		labelTypes.add("goto");
//		labelTypes.add("goto");
//		labelTypes.add("goto");
//		labelTypes.add("goto");
//		labelTypes.add("goto");
//		labelTypes.add("goto");
		String result = null;
		for(String type:labelTypes){
			if(label.indexOf(type)>0){
				result = type;
				break;
			}
		}
		if(result==null){
			System.err.println(label);
		};
		return result;
	}

}
