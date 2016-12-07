package partial.code.grapa.delta.script;

import java.util.ArrayList;





import java.util.Collection;
import java.util.Hashtable;

import partial.code.grapa.delta.graph.GraphComparator;
import partial.code.grapa.delta.graph.StatementEdge;
import partial.code.grapa.delta.graph.StatementNode;

import com.ibm.wala.cast.ir.ssa.AstAssertInstruction;
import com.ibm.wala.cast.java.ssa.AstJavaInvokeInstruction;
import com.ibm.wala.classLoader.CallSiteReference;
import com.ibm.wala.classLoader.JavaLanguage;
import com.ibm.wala.classLoader.JavaLanguage.JavaInstructionFactory;
import com.ibm.wala.classLoader.NewSiteReference;
import com.ibm.wala.ipa.slicer.HeapStatement;
import com.ibm.wala.ipa.slicer.NormalStatement;
import com.ibm.wala.ipa.slicer.ParamCallee;
import com.ibm.wala.ipa.slicer.ParamCaller;
import com.ibm.wala.ipa.slicer.Statement;
import com.ibm.wala.ssa.IR;
import com.ibm.wala.ssa.SSAAbstractInvokeInstruction;
import com.ibm.wala.ssa.SSAArrayStoreInstruction;
import com.ibm.wala.ssa.SSABinaryOpInstruction;
import com.ibm.wala.ssa.SSACheckCastInstruction;
import com.ibm.wala.ssa.SSAConditionalBranchInstruction;
import com.ibm.wala.ssa.SSAFieldAccessInstruction;
import com.ibm.wala.ssa.SSAGetInstruction;
import com.ibm.wala.ssa.SSAGotoInstruction;
import com.ibm.wala.ssa.SSAInstanceofInstruction;
import com.ibm.wala.ssa.SSAInstruction;
import com.ibm.wala.ssa.SSANewInstruction;
import com.ibm.wala.ssa.SSAPutInstruction;
import com.ibm.wala.ssa.SSAUnaryOpInstruction;
import com.ibm.wala.types.FieldReference;
import com.ibm.wala.types.TypeReference;























import uk.ac.shef.wit.simmetrics.similaritymetrics.Levenshtein;
import util.CostFunction;
import util.EditDistance;
import util.Graph;
import util.MatrixGenerator;
import edu.uci.ics.jung.graph.DirectedSparseGraph;

public class GraphEditScript extends GraphComparator{
	
	
	public GraphEditScript(
			DirectedSparseGraph<StatementNode, StatementEdge> oldGraph,
			IR oldIr, DirectedSparseGraph<StatementNode, StatementEdge> newGraph, IR newIr) {
		// TODO Auto-generated constructor stub
			super(oldGraph, oldIr, newGraph, newIr);
	}

	

	public ArrayList<AbstractEdit> extractChanges() {
		// TODO Auto-generated method stub
		ArrayList<AbstractEdit> changes = new ArrayList<AbstractEdit>();
	
        Hashtable<StatementNode, StatementNode> vm = extractNodeMappings();
        for(StatementNode v1:vm.keySet()){
        	StatementNode v2 = vm.get(v1);
    		if(calculateNodeCost(v1,v2)!=0){
    			if(mode){
	    			UpdateNode un = new UpdateNode(getComparedLabel(v1),
	        					getComparedLabel(v2));
	       			changes.add(un);
    			}else{
    				UpdateNode un = new UpdateNode(getComparedLabel(v2),
    						getComparedLabel(v1));
    				changes.add(un);
    			}
    		}
        }
        
        for(StatementNode node:this.rightGraph.getVertices()){
        	if(!vm.containsValue(node)){
        		if(mode){
        			InsertNode in = new InsertNode(getComparedLabel(node));
        			changes.add(in);
        		}else{
        			DeleteNode dn = new DeleteNode(getComparedLabel(node));
        			changes.add(dn);
        		}
        	}
        }      
		return changes;
	}
}
