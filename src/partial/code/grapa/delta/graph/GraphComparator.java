package partial.code.grapa.delta.graph;

import java.util.Hashtable;

import partial.code.grapa.dependency.graph.StatementEdge;
import partial.code.grapa.dependency.graph.StatementNode;
import uk.ac.shef.wit.simmetrics.similaritymetrics.Levenshtein;
import util.EditDistance;

import com.ibm.wala.cast.ir.ssa.AstAssertInstruction;
import com.ibm.wala.cast.ir.ssa.AstLexicalRead;
import com.ibm.wala.cast.ir.ssa.AstLexicalWrite;
import com.ibm.wala.cast.ir.ssa.AstLexicalAccess.Access;
import com.ibm.wala.cast.java.ssa.AstJavaInvokeInstruction;
import com.ibm.wala.classLoader.CallSiteReference;
import com.ibm.wala.classLoader.NewSiteReference;
import com.ibm.wala.ipa.slicer.HeapStatement;
import com.ibm.wala.ipa.slicer.NormalReturnCaller;
import com.ibm.wala.ipa.slicer.NormalStatement;
import com.ibm.wala.ipa.slicer.ParamCallee;
import com.ibm.wala.ipa.slicer.ParamCaller;
import com.ibm.wala.ipa.slicer.PhiStatement;
import com.ibm.wala.ipa.slicer.Statement;
import com.ibm.wala.ssa.IR;
import com.ibm.wala.ssa.SSAAbstractThrowInstruction;
import com.ibm.wala.ssa.SSAArrayLengthInstruction;
import com.ibm.wala.ssa.SSAArrayLoadInstruction;
import com.ibm.wala.ssa.SSAArrayStoreInstruction;
import com.ibm.wala.ssa.SSABinaryOpInstruction;
import com.ibm.wala.ssa.SSACheckCastInstruction;
import com.ibm.wala.ssa.SSAConditionalBranchInstruction;
import com.ibm.wala.ssa.SSAConversionInstruction;
import com.ibm.wala.ssa.SSAGetInstruction;
import com.ibm.wala.ssa.SSAGotoInstruction;
import com.ibm.wala.ssa.SSAInstanceofInstruction;
import com.ibm.wala.ssa.SSAInstruction;
import com.ibm.wala.ssa.SSALoadMetadataInstruction;
import com.ibm.wala.ssa.SSANewInstruction;
import com.ibm.wala.ssa.SSAPhiInstruction;
import com.ibm.wala.ssa.SSAPutInstruction;
import com.ibm.wala.ssa.SSAReturnInstruction;
import com.ibm.wala.ssa.SSASwitchInstruction;
import com.ibm.wala.ssa.SSAUnaryOpInstruction;
import com.ibm.wala.ssa.SymbolTable;
import com.ibm.wala.types.FieldReference;
import com.ibm.wala.types.TypeReference;

import edu.uci.ics.jung.graph.DirectedSparseGraph;

public class GraphComparator {
	private double[][] costMatrix;
	protected DirectedSparseGraph<StatementNode, StatementEdge> leftGraph;
	protected DirectedSparseGraph<StatementNode, StatementEdge> rightGraph;
	private IR leftIr;
	private IR rightIr;
	protected boolean mode;//true: does not swap left and right. false: does.
	
	protected Levenshtein stringComparator;
	
	protected Hashtable<Integer, String> leftValueTable;
	protected Hashtable<Integer, String> rightValueTable;
	
	
	public GraphComparator(
			DirectedSparseGraph<StatementNode, StatementEdge> oldGraph,
			IR oldIr,
			DirectedSparseGraph<StatementNode, StatementEdge> newGraph, IR newIr) {
		// TODO Auto-generated constructor stub
		if (oldGraph.getVertexCount()>newGraph.getVertexCount()){
			leftGraph = newGraph;
			rightGraph = oldGraph;
			leftIr = newIr;
			rightIr = oldIr;
			mode = false;
		}else{
			leftGraph = oldGraph;
			rightGraph = newGraph;
			leftIr = oldIr;
			rightIr = newIr;
			mode = true;
		}

		stringComparator = new Levenshtein();		
		leftValueTable = extractValues(leftGraph, leftIr);
		rightValueTable = extractValues(rightGraph, rightIr);
	}
	
	private Hashtable<Integer, String> extractValues(
			DirectedSparseGraph<StatementNode, StatementEdge> graph, IR ir) {
		// TODO Auto-generated method stub
		Hashtable<Integer, String> valueTable = new Hashtable<Integer, String>();
		
		SymbolTable table = ir.getSymbolTable();
		for(int i=0; i<table.getMaxValueNumber(); i++){
			String line = table.getValueString(i);
			int mark = line.indexOf(":");
			if(mark>0){
				line = line.substring(mark+1);
				valueTable.put(i, line);
			}
		}
		
		for(StatementNode node:graph.getVertices()){
			if(node.statement instanceof NormalStatement){
				NormalStatement ns = (NormalStatement)node.statement;
				SSAInstruction ins = ns.getInstruction();
				if(ins instanceof SSAGetInstruction){
					SSAGetInstruction gis = (SSAGetInstruction)ins;
					FieldReference field = gis.getDeclaredField();
					String sig = field.getSignature();
					valueTable.put(gis.getDef(), sig);
				}else if(ins instanceof AstJavaInvokeInstruction){
					AstJavaInvokeInstruction inv = (AstJavaInvokeInstruction)ins;
					if(inv.hasDef()){
						CallSiteReference site = inv.getCallSite();
						String  line = "invoke "+site.getInvocationString() + " ";
						line = line + site.getDeclaredTarget().toString();
						valueTable.put(inv.getDef(), line);
					}
				}
			}
		}
		
		return valueTable;
	}

	public Hashtable<StatementNode, StatementNode> extractNodeMappings() {
		// TODO Auto-generated method stub
		calculateCostMatrix();
		HungarianAlgorithm ha = new HungarianAlgorithm();
        int[][] matching = ha.hgAlgorithm(costMatrix);
        
        //mapped nodes
        Hashtable<StatementNode, StatementNode> vm = new Hashtable<StatementNode, StatementNode>();
        for(int i=0; i<matching.length; i++){
			StatementNode v1 = (StatementNode)leftGraph.getVertices().toArray()[matching[i][0]];
			StatementNode v2 = (StatementNode)rightGraph.getVertices().toArray()[matching[i][1]];
			vm.put(v1, v2);
        }
        return vm;
	}
	
	private void calculateCostMatrix() {
		// TODO Auto-generated method stub
		costMatrix =  new double[leftGraph.getVertexCount()][rightGraph.getVertexCount()];
		double inNodeCost;
		double outNodeCost;
		double nodeCost;

		for (int i = 0; i < leftGraph.getVertexCount(); i++) {
			StatementNode leftNode = (StatementNode)leftGraph.getVertices().toArray()[i];

            for (int j = 0; j < rightGraph.getVertexCount(); j++) {
            	StatementNode rightNode = (StatementNode)rightGraph.getVertices().toArray()[j];
            	inNodeCost = calculateIndegreeSimilarity(leftNode, rightNode);
            	outNodeCost = calculateOutDegreeSimilarity(leftNode, rightNode);
                nodeCost = calculateNodeCost(leftNode, rightNode);
                costMatrix[i][j] = inNodeCost+outNodeCost+nodeCost;
            }
        }
	}

	private double calculateOutDegreeSimilarity(StatementNode leftNode,
			StatementNode rightNode) {
		double outNodeCost = 0;
		try{
			if((leftGraph.outDegree(leftNode) + rightGraph.outDegree(rightNode))!=0){
				outNodeCost = (double)Math.abs(leftGraph.outDegree(leftNode) -  rightGraph.outDegree(rightNode))/(leftGraph.outDegree(leftNode) + rightGraph.outDegree(rightNode));
			}else{
				outNodeCost = 0;
			}
		}catch(Exception e){
//			System.out.println("jung internal error at: "+leftNode.toString());
		}
		return outNodeCost;
	}

	private double calculateIndegreeSimilarity(StatementNode leftNode,
			StatementNode rightNode) {
		double inNodeCost = 0;
		try{
			if((leftGraph.inDegree(leftNode) + rightGraph.inDegree(rightNode))!=0){
				inNodeCost = (double)Math.abs(leftGraph.inDegree(leftNode) - rightGraph.inDegree(rightNode))/(leftGraph.inDegree(leftNode) + rightGraph.inDegree(rightNode));
			}else{
				inNodeCost = 0;
			}
		}catch(Exception e){
//			System.out.println("jung internal error at "+leftNode.toString());
		}
		return inNodeCost;
	}

	protected double calculateNodeCost(StatementNode leftNode,
			StatementNode rightNode) {
		// TODO Auto-generated method stub
		String leftLine;
		String rightLine;
		
		leftLine = getComparedLabel(leftNode);
		rightLine = getComparedLabel(rightNode);
	
		double distance = stringComparator.getUnNormalisedSimilarity(leftLine, rightLine);
//		int length = leftLine.length()>rightLine.length()?leftLine.length():rightLine.length();
//		return distance/length;
		return distance;
	}
	
	


	protected double calculateCost(StatementNode v1, StatementNode v2) {
		// TODO Auto-generated method stub
		double inNodeCost = calculateIndegreeSimilarity(v1, v2);
    	double outNodeCost = calculateOutDegreeSimilarity(v1, v2);
        double nodeCost = calculateNodeCost(v1, v2);
        return inNodeCost+outNodeCost+nodeCost;
	}
	
	

	
	public  String getComparedLabel(StatementNode node) {
		// TODO Auto-generated method stub
		Hashtable<Integer, String> valueTable = null;
		IR ir = null;
		if(leftGraph.containsVertex(node)){
			valueTable = this.leftValueTable;
			ir = this.leftIr;
		}else{
			valueTable = this.rightValueTable;
			ir = this.rightIr;
		}
		Statement s = node.statement;
		String line = "";
		switch (s.getKind()) {
	        case HEAP_PARAM_CALLEE:
	        case HEAP_PARAM_CALLER:
	        case HEAP_RET_CALLEE:
	        case HEAP_RET_CALLER:
	          HeapStatement h = (HeapStatement) s;
	          line = s.getKind() + "\\n" + h.getNode();
	          break;
	        case NORMAL:
	          NormalStatement n = (NormalStatement) s;
	          SSAInstruction ins = n.getInstruction();
	          line = getComparedInstructionString(valueTable, ir, ins);
	          break;
	        case PARAM_CALLEE:
	          ParamCallee paramCallee = (ParamCallee) s;
	          line = s.getKind() + " " + paramCallee.getValueNumber();
	          break;
	        case PARAM_CALLER:
	          ParamCaller paramCaller = (ParamCaller) s;
	          line = s.getKind() + " " + paramCaller.getValueNumber();
	          break;	      
	        case EXC_RET_CALLER:
	        	line = s.toString();
	        	break;
	        case PHI:
	        	PhiStatement phi = (PhiStatement)s;
	        	SSAPhiInstruction pins = phi.getPhi();
	        	line = valueTable.get(pins.getDef())+"=phi ";
	        	for(int i=0;i<pins.getNumberOfUses(); i++){
	        		line += valueTable.get(pins.getUse(i));
	        	}

	        	break;	
	        case NORMAL_RET_CALLER:
	        	NormalReturnCaller caller = (NormalReturnCaller)s;
	        	line = "NORMAL_RET_CALLER:" + getComparedInstructionString(valueTable, ir, caller.getInstruction());
	        	break;	
	        case EXC_RET_CALLEE:
	        case NORMAL_RET_CALLEE:
	        default:
	          line =  s.toString();
	     }
      
		 return line;
	}
	

	
	public  String getComparedInstructionString(Hashtable<Integer, String> valueTable, IR ir, SSAInstruction ins) {
		String line;
		if(ins instanceof AstJavaInvokeInstruction){
			  AstJavaInvokeInstruction aji = (AstJavaInvokeInstruction)ins;
			  CallSiteReference site = aji.getCallSite();
			  line = "invoke "+site.getInvocationString() + " ";
			  line = line + site.getDeclaredTarget().toString();
		  }else if (ins instanceof SSANewInstruction){
			  SSANewInstruction nis = (SSANewInstruction)ins;
			  NewSiteReference site = nis.getNewSite();
			  line = "new " + site.getDeclaredType();
		  }else if(ins instanceof SSAPutInstruction){
			  SSAPutInstruction pis = (SSAPutInstruction)ins;
			  if (pis.isStatic()) {
			      line = "putstatic " + pis.getDeclaredField() + " = " + pis.getValueString(null, pis.getVal());
			  } else {
			      line =  "putfield " + pis.getDeclaredField();
			  }
		  }else if(ins instanceof SSAGetInstruction){
			  SSAGetInstruction gis = (SSAGetInstruction)ins;
			  if (gis.isStatic()) {
			      line = "getstatic " + gis.getDeclaredField();
			  } else {
			      line = "getfield " + gis.getDeclaredField();
			  }			 
		  }else if(ins instanceof SSABinaryOpInstruction){
			  SSABinaryOpInstruction ois = (SSABinaryOpInstruction)ins;
			  line = valueTable.get(ois.getDef())+"=binaryop(" + ois.getOperator() + ") "+valueTable.get(ois.getVal1())+","+valueTable.get(ois.getVal2());
		  }else if(ins instanceof SSAArrayStoreInstruction){
			  SSAArrayStoreInstruction sas = (SSAArrayStoreInstruction)ins;
			  line = "arraystore " + valueTable.get(sas.getArrayRef()) + "[" + valueTable.get(sas.getIndex())
		        + "] = " + valueTable.get(sas.getValue());
		  }else if(ins instanceof SSAArrayLoadInstruction){
			  SSAArrayLoadInstruction sas = (SSAArrayLoadInstruction)ins;
			  line = valueTable.get(sas.getDef()) + " = arrayload " + valueTable.get(sas.getArrayRef()) + "["
				        + valueTable.get(sas.getIndex()) + "]";
		  }else if(ins instanceof SSAArrayLengthInstruction){
			  SSAArrayLengthInstruction sas = (SSAArrayLengthInstruction)ins;
			  line = valueTable.get(sas.getDef()) + " = arraylength " + valueTable.get(sas.getArrayRef());
		  }else if(ins instanceof SSAUnaryOpInstruction){
			  SSAUnaryOpInstruction uoi = (SSAUnaryOpInstruction)ins;
			  line = uoi.getOpcode().toString();
		  }else if(ins instanceof SSAGotoInstruction){
			  line = "goto";
		  }else if(ins instanceof AstAssertInstruction){
			  line = "assert"; 
		  }else if(ins instanceof SSAConditionalBranchInstruction){
			  SSAConditionalBranchInstruction cbi = (SSAConditionalBranchInstruction)ins;
			  line = "conditional branch(" + cbi.getOperator() + ") "+valueTable.get(cbi.getUse(0)) + "," + valueTable.get(cbi.getUse(1));
		  }else if(ins instanceof SSAInstanceofInstruction){
			  SSAInstanceofInstruction iis = (SSAInstanceofInstruction)ins;
			  line = "instanceof " + iis.getCheckedType();
		  }else if(ins instanceof SSACheckCastInstruction){
			  SSACheckCastInstruction cci = (SSACheckCastInstruction)ins;
			  line = "checkcast";
		      for (TypeReference t : cci.getDeclaredResultTypes()) {
		          line = line + " " + t;
		      }
		  }else if(ins instanceof SSAAbstractThrowInstruction){
			  line = "throw";
		  }else if(ins instanceof SSAReturnInstruction){
			  line = "return";
		  }else if(ins instanceof SSALoadMetadataInstruction){
			  line = "load_metadata";
		  }else if(ins instanceof SSASwitchInstruction){
			  line = "switch";
		  }else if(ins instanceof SSAConversionInstruction){
			  SSAConversionInstruction ci = (SSAConversionInstruction)ins;
			  line = valueTable.get(ci.getDef())+" = conversion(" + ci.getToType().getName() + ") " + valueTable.get(ci.getUse(0));
		  }else if(ins instanceof AstLexicalWrite){
			  AstLexicalWrite ast = (AstLexicalWrite)ins;
			  line = "";
			  for (int i = 0; i < ast.getAccessCount(); i++) {
			      Access A = ast.getAccess(i);
			      if (i != 0)
			        line += ", ";
			      line += "lexical:";
			      line += A.variableName;
			      line += "@";
			      line += A.variableDefiner;
			      line += " = ";
			      line += valueTable.get(A.valueNumber);
			    }			 
		  }else if(ins instanceof AstLexicalRead){
			  AstLexicalRead ast = (AstLexicalRead)ins;
			  line = "";
			  for (int i = 0; i < ast.getAccessCount(); i++) {
			      Access A = ast.getAccess(i);
			      if (i != 0)
			      line += ", ";
			      line += valueTable.get(A.valueNumber);
			      line += " = lexical:";
			      line += "@";
			      line += A.variableDefiner;
			   }		 
		  }else{
			  line = ins.toString(ir.getSymbolTable());
		  }
//		if(line.indexOf("load_metadata")>=0){
//			ins.toString(ir.getSymbolTable());
//			System.out.println("here!");
//		}
		return line;
	}
	
	public static String getVisualLabel(IR ir, Statement s) {
		// TODO Auto-generated method stub
		String line = "";
		 switch (s.getKind()) {
	        case HEAP_PARAM_CALLEE:
	        case HEAP_PARAM_CALLER:
	        case HEAP_RET_CALLEE:
	        case HEAP_RET_CALLER:
	          HeapStatement h = (HeapStatement) s;
	          line = s.getKind() + "\\n" + h.getNode();
	          break;
	        case NORMAL:
	          NormalStatement n = (NormalStatement) s;
	          SSAInstruction ins = n.getInstruction();
	          line = getInstructionVisualString(ir, ins);
	          break;
	        case PARAM_CALLEE:
	          ParamCallee paramCallee = (ParamCallee) s;
	          line = s.getKind() + " " + paramCallee.getValueNumber();
	          break;
	        case PARAM_CALLER:
	          ParamCaller paramCaller = (ParamCaller) s;
	          line = s.getKind() + " " + paramCaller.getValueNumber();
	          break;	      
	        case EXC_RET_CALLER:
	        	line = s.toString();
	        	break;
	        case PHI:
	        	PhiStatement phi = (PhiStatement)s;
	        	line = phi.getPhi().toString();
	        	break;	
	        case NORMAL_RET_CALLER:
	        	NormalReturnCaller caller = (NormalReturnCaller)s;
	        	line = "NORMAL_RET_CALLER:" + getInstructionVisualString(ir, caller.getInstruction());
	        	break;	
	        case EXC_RET_CALLEE:
	        case NORMAL_RET_CALLEE:
	        default:
	          line =  s.toString();
	        }
		 return line;
	}

	public static String getInstructionVisualString(IR ir, SSAInstruction ins) {
		String line = ins.toString(ir.getSymbolTable());
//		if(ins instanceof AstJavaInvokeInstruction){
//			  AstJavaInvokeInstruction aji = (AstJavaInvokeInstruction)ins;
//			  CallSiteReference site = aji.getCallSite();
//			  line = "invoke "+site.getInvocationString() + " ";
//			  line += site.getDeclaredTarget().toString();
//			  line += " @" + site.getProgramCounter();
//		  }else if (ins instanceof SSANewInstruction){
//			  SSANewInstruction nis = (SSANewInstruction)ins;
//			  NewSiteReference site = nis.getNewSite();
//			  line = "new " + site.getDeclaredType();
//			  line += " @" + site.getProgramCounter();
//		  }else if(ins instanceof SSAPutInstruction){
//			  line = ins.toString(ir.getSymbolTable());			  
//		  }else if(ins instanceof SSAGetInstruction){
//			  line = ins.toString(ir.getSymbolTable());
//		  }else if(ins instanceof SSABinaryOpInstruction){
//			  SSABinaryOpInstruction ois = (SSABinaryOpInstruction)ins;
//			  line = ois.toString(ir.getSymbolTable());
//		  }else if(ins instanceof SSAArrayStoreInstruction){
//			  line = ins.toString(ir.getSymbolTable());
//		  }else if(ins instanceof SSAUnaryOpInstruction){
//			  SSAUnaryOpInstruction uoi = (SSAUnaryOpInstruction)ins;
//			  line = uoi.getOpcode().toString();
//		  }else if(ins instanceof SSAGotoInstruction){
//			  SSAGotoInstruction goi = (SSAGotoInstruction)ins;
//			  line = goi.toString(ir.getSymbolTable());
//		  }else if(ins instanceof AstAssertInstruction){
//			  line = ins.toString(ir.getSymbolTable());
//		  }else if(ins instanceof SSAConditionalBranchInstruction){
//			  SSAConditionalBranchInstruction cbi = (SSAConditionalBranchInstruction)ins;
//			  line = cbi.toString(ir.getSymbolTable());
//		  }else if(ins instanceof SSAInstanceofInstruction){
//			  line = ins.toString(ir.getSymbolTable());
//		  }else if(ins instanceof SSACheckCastInstruction){
//			  line = ins.toString(ir.getSymbolTable());
//		  }else{
//			  line = ins.toString();
//		  }
		return line;
	}
}

