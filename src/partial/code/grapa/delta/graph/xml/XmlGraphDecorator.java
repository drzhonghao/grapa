package partial.code.grapa.delta.graph.xml;

import com.ibm.wala.util.WalaException;
import com.ibm.wala.viz.NodeDecorator;


public class XmlGraphDecorator implements NodeDecorator<XmlNode>{

	@Override
	public String getLabel(XmlNode n) throws WalaException {
		// TODO Auto-generated method stub
		return n.label;
	}

}
