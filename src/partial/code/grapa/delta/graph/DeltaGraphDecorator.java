package partial.code.grapa.delta.graph;

import com.ibm.wala.ipa.slicer.HeapStatement;
import com.ibm.wala.ipa.slicer.NormalStatement;
import com.ibm.wala.ipa.slicer.ParamCallee;
import com.ibm.wala.ipa.slicer.ParamCaller;
import com.ibm.wala.ipa.slicer.Statement;
import com.ibm.wala.ssa.IR;
import com.ibm.wala.util.WalaException;
import com.ibm.wala.viz.NodeDecorator;

import partial.code.grapa.delta.graph.xml.LabelTool;

public class DeltaGraphDecorator implements NodeDecorator<StatementNode>{


	private LabelTool leftTool;
	private LabelTool rightTool;

	public DeltaGraphDecorator(IR lir, IR rir) {
		// TODO Auto-generated constructor stub
		leftTool = new LabelTool(lir);
		rightTool = new LabelTool(rir);
	}

	@Override
	public String getLabel(StatementNode sn) {
		// TODO Auto-generated method stub
		Statement s = sn.statement;
		
		if(sn.side == StatementNode.LEFT){
			return "l: "+leftTool.getVisualLabel(s);
		}else if(sn.side == StatementNode.RIGHT){
			return "r: "+rightTool.getVisualLabel(s);
		}else{
			return null;
		}
		
	}


}
