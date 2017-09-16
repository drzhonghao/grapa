package partial.code.grapa.delta.graph;

import com.ibm.wala.util.WalaException;
import com.ibm.wala.viz.NodeDecorator;


public class DeltaGraphDecorator implements NodeDecorator<DeltaNode>{

	@Override
	public String getLabel(DeltaNode n) throws WalaException {
		// TODO Auto-generated method stub
		return n.toString();
	}

}
