package partial.code.grapa.delta.xmlgraph.data;

import java.util.ArrayList;

public class LabelParser {

	private static ArrayList<String> labelTypes;
	
	static{
		labelTypes = new ArrayList<String>();
		labelTypes.add("conditional branch");
		labelTypes.add("= getfield");
		labelTypes.add("= invokeinterface");
		labelTypes.add("= invokevirtual");
		labelTypes.add("goto");
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
		assert result!=null;
		return result;
	}

}
