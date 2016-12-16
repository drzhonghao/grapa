package partial.code.grapa.delta.graph.xml;

import java.util.ArrayList;

public class LabelParser {

	private static ArrayList<String> labelTypes;
	
	static{
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
		labelTypes.add("arrayload");	
		labelTypes.add("load_metadata");
		labelTypes.add("conversion");	
		labelTypes.add("switch");
		labelTypes.add("putstatic");	
		labelTypes.add("assert");	
		labelTypes.add("bitnot");
		labelTypes.add("lexical:");
	}

	public static int parseId(String label) {
		// TODO Auto-generated method stub
		
		int id = -1;
		for(int i=0; i<labelTypes.size(); i++){
			String type = labelTypes.get(i);
			if(label.indexOf(type)>0){
				id = i;
				break;
			}
		}		
		return id;
	}

	public static String parse(String label) {
		// TODO Auto-generated method stub
		
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



	public static String getCodeName(String label) {
		// TODO Auto-generated method stub
		String kind = parse(label);
		String codename = null;
		if(kind.compareTo("invokeinterface")==0||kind.compareTo("invokevirtual")==0||
				kind.compareTo("invokestatic")==0||kind.compareTo("invokespecial")==0){
			int mark = label.indexOf(", ");
			codename = label.substring(mark+2);
			mark = codename.indexOf(" > ");
			codename = codename.substring(0, mark);
			codename = codename.replace(", ", "#");			
		}else if(kind.compareTo("getfield")==0||
				kind.compareTo("putfield")==0||
				kind.compareTo("getstatic")==0||
				kind.compareTo("putstatic")==0){
			int mark = label.indexOf(", ");
			codename = label.substring(mark+2);
			codename = codename.replace(", <", "_f_");
			codename = codename.replace(", ", "#");
			mark = codename.indexOf(">");
			codename = codename.substring(0, mark);			
		}else if(kind.compareTo("checkcast")==0){
			int mark = label.indexOf(",");
			codename = label.substring(mark+1);
			mark = codename.indexOf(">");
			codename = codename.substring(0, mark);			
		}else if(kind.compareTo("enclosing")==0){
			int mark = label.indexOf("enclosing");
			codename = label.substring(mark+10);
		}else if(kind.compareTo("instanceof")==0){
			int mark = label.indexOf(",");
			codename = label.substring(mark+1);
			mark = codename.indexOf(">");
			codename = codename.substring(0, mark);			
		}else if(kind.compareTo("conversion")==0){
			int mark = label.indexOf("(");
			codename = label.substring(mark+1);
			mark = codename.indexOf(")");
			codename = codename.substring(0, mark);
		}
		return codename;
	}

}
