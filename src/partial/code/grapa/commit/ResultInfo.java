package partial.code.grapa.commit;

import java.util.ArrayList;

import partial.code.grapa.dependency.graph.StatementNode;

public class ResultInfo {
	public static int MAX_DELTA_NODE = 10;
	public static int MAX_DELTA_LINE = 5;

	
	public DeltaInfo info = new DeltaInfo();
	public ArrayList<MethodDelta> methods = new ArrayList<MethodDelta>();
	public boolean isSingleModifiedMethod() {
		// TODO Auto-generated method stub
		return info.deltaMethod==0&&info.modifiedMethod==1;		
	}
	public boolean isSimpleModification() {
		// TODO Auto-generated method stub
		if(info.deltaGraphNode<10){
			return true;
		}else{
			ArrayList<Integer> lines = new ArrayList<Integer>();
			for(MethodDelta method:methods){
				for(StatementNode node:method.deltaGraph.getVertices()){
					if(!lines.contains(node.lineNumber)){
						lines.add(node.lineNumber);
					}
				}
				if(lines.size()<ResultInfo.MAX_DELTA_LINE){
					return true;
				}
			}
		}
		return false;
	}
	public boolean isValid() {
		// TODO Auto-generated method stub
		if(this.isSingleModifiedMethod()){
			if(isSimpleModification()){
				return true;
			}
		}
		return false;
	}	
}
