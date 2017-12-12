package partial.code.grapa.tool;

import java.util.ArrayList;
import java.util.Hashtable;

import com.ibm.wala.cast.ir.ssa.AstAssertInstruction;
import com.ibm.wala.cast.ir.ssa.AstLexicalAccess.Access;
import com.ibm.wala.cast.ir.ssa.AstLexicalRead;
import com.ibm.wala.cast.ir.ssa.AstLexicalWrite;
import com.ibm.wala.cast.java.loader.JavaSourceLoaderImpl.ConcreteJavaMethod;
import com.ibm.wala.cast.java.ssa.AstJavaInvokeInstruction;
import com.ibm.wala.classLoader.CallSiteReference;
import com.ibm.wala.classLoader.IMethod;
import com.ibm.wala.classLoader.NewSiteReference;
import com.ibm.wala.classLoader.ShrikeBTMethod;
import com.ibm.wala.ipa.callgraph.CGNode;
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
import com.ibm.wala.ssa.SSAMonitorInstruction;
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
//import partial.code.grapa.delta.graph.StatementEdge;
//import partial.code.grapa.delta.graph.StatementNode;

public class LabelUtil {

	private ArrayList<String> labelTypes;
	private IR ir;
	
	public LabelUtil(){
		
		labelTypes = new ArrayList<String>();
		labelTypes.add("PARAM_CALLEE");
		labelTypes.add("PARAM_CALLEE");
		labelTypes.add("NORMAL_RET_CALLER");
		labelTypes.add("invokestatic");
		labelTypes.add("invokespecial");
		labelTypes.add("invokeinterface");
		labelTypes.add("invokevirtual");
		labelTypes.add("conditional branch");
		labelTypes.add("getfield");
		labelTypes.add("goto");
		labelTypes.add("arraystore");
		labelTypes.add("putfield");		
		labelTypes.add("binaryop");
		labelTypes.add("getstatic");
		labelTypes.add("checkcast");
		labelTypes.add("new");
		labelTypes.add("enclosing");	
		labelTypes.add("phi");
		labelTypes.add("return");
		labelTypes.add("neg");
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

	public void setIR(IR ir){
		this.ir = ir;
	}
	public int parseId(String label) {
		// TODO Auto-generated method stub
		
		int id = -1;
		for(int i=0; i<labelTypes.size(); i++){
			String type = labelTypes.get(i);
			if(label.indexOf(type)>=0){
				id = i;
				break;
			}
		}		
		return id;
	}

	public String parse(String label) {
		// TODO Auto-generated method stub
		
		String result = null;
		for(String type:labelTypes){
			if(label.indexOf(type)>=0){
				result = type;
				break;
			}
		}
		if(result==null){
			System.err.println(label);
		};
		return result;
	}



	public ArrayList<String> getCodeNames(String label) {
		// TODO Auto-generated method stub
		String kind = parse(label);
		ArrayList<String> codenames = new ArrayList<String>();	
		String codename;
		if(kind.compareTo("invokeinterface")==0||kind.compareTo("invokevirtual")==0||
				kind.compareTo("invokestatic")==0||kind.compareTo("invokespecial")==0){
			int mark = label.indexOf(", ");
			codename = label.substring(mark+2);
			mark = codename.indexOf(" > ");
			codename = codename.substring(0, mark);
			codename = codename.replace(", ", "#");
			
			mark = codename.indexOf("(");
			codenames.add(codename.substring(0,  mark));
			codename = codename.substring(mark+1);
			mark = codename.indexOf(")");
			codenames.add(codename.substring(mark+1));
			codename = codename.substring(0, mark);
			mark = codename.indexOf("(");
			codename = codename.substring(mark+1);
			mark = codename.indexOf(";");
			while(mark>0){
				codenames.add(codename.substring(0, mark));
				codename = codename.substring(mark+1);
				mark = codename.indexOf(";");
			}
		}else if(kind.compareTo("getfield")==0||
				kind.compareTo("putfield")==0||
				kind.compareTo("getstatic")==0||
				kind.compareTo("putstatic")==0){
			int mark = label.indexOf(", ");
			codename = label.substring(mark+2);
		
			mark = codename.indexOf(", <");
			codenames.add(codename.substring(0, mark).replace(", ", "#"));
			
			codename = codename.substring(mark+3);
			mark = codename.indexOf(">");
			codename = codename.substring(0, mark);	
			mark = codename.indexOf(",");
			
			codenames.add(codename.substring(mark+2));
		}else if(kind.compareTo("checkcast")==0){
			int mark = label.indexOf(",");
			codename = label.substring(mark+1);
			mark = codename.indexOf(">");
			codename = codename.substring(0, mark);		
			codenames.add(codename);
		}else if(kind.compareTo("enclosing")==0){
			int mark = label.indexOf("enclosing");
			codename = label.substring(mark+10);
			codenames.add(codename);
		}else if(kind.compareTo("instanceof")==0){
			int mark = label.indexOf(",");
			codename = label.substring(mark+1);
			mark = codename.indexOf(">");
			codename = codename.substring(0, mark);	
			codenames.add(codename);
		}else if(kind.compareTo("conversion")==0){
			int mark = label.indexOf("(");
			codename = label.substring(mark+1);
			mark = codename.indexOf(")");
			codename = codename.substring(0, mark);
			codenames.add(codename);
		}
		return codenames;
	}

//	public String getComparedLabel(StatementNode node) {
//		// TODO Auto-generated method stub
//		Hashtable<Integer, String> valueTable = null;
//		Hashtable<Integer, String> indexTable = null;
//		
//		
//		Statement s = node.statement;
//		String line = "";
//		switch (s.getKind()) {
//	        case HEAP_PARAM_CALLEE:
//	        case HEAP_PARAM_CALLER:
//	        case HEAP_RET_CALLEE:
//	        case HEAP_RET_CALLER:
//	          HeapStatement h = (HeapStatement) s;	          
//	          line = s.getKind() + "\\n" + h.getNode();
//	          break;
//	        case NORMAL:
//	          NormalStatement n = (NormalStatement) s;
//	          SSAInstruction ins = n.getInstruction();
//	          line = getComparedInstructionString(valueTable, indexTable, ir, ins);
//	          break;
//	        case PARAM_CALLEE:
//	          ParamCallee paramCallee = (ParamCallee) s;
//	          line = s.getKind() + " " + paramCallee.getValueNumber();
//	          break;
//	        case PARAM_CALLER:
//	          ParamCaller paramCaller = (ParamCaller) s;
//	          line = s.getKind() + " " + paramCaller.getValueNumber();
//	          break;	      
//	        case EXC_RET_CALLER:
//	        	line = s.toString();
//	        	break;
//	        case PHI:
//	        	PhiStatement phi = (PhiStatement)s;
//	        	SSAPhiInstruction pins = phi.getPhi();
//	        	line = valueTable.get(pins.getDef())+"=phi ";
//	        	for(int i=0;i<pins.getNumberOfUses(); i++){
//	        		line += valueTable.get(pins.getUse(i));
//	        	}
//	        	break;	
//	        case NORMAL_RET_CALLER:
//	        	NormalReturnCaller caller = (NormalReturnCaller)s;
//	        	line = "NORMAL_RET_CALLER:" + getComparedInstructionString(valueTable, indexTable, ir, caller.getInstruction());
//	        	break;	
//	        case EXC_RET_CALLEE:
//	        case NORMAL_RET_CALLEE:
//	        default:
//	          line =  s.toString();
//	     }
//	
//		 return line;
//	}
//
//	public  String getComparedInstructionString(Hashtable<Integer, String> valueTable, Hashtable<Integer, String> indexTable, IR ir, SSAInstruction ins) {
//			String line;
//			if(ins instanceof AstJavaInvokeInstruction){
//				  AstJavaInvokeInstruction aji = (AstJavaInvokeInstruction)ins;
//				  CallSiteReference site = aji.getCallSite();
//				  line = "invoke "+site.getInvocationString() + " ";
//				  line = line + site.getDeclaredTarget().toString();
//			  }else if (ins instanceof SSANewInstruction){
//				  SSANewInstruction nis = (SSANewInstruction)ins;
//				  NewSiteReference site = nis.getNewSite();
//				  line = "new " + site.getDeclaredType();
//			  }else if(ins instanceof SSAPutInstruction){
//				  SSAPutInstruction pis = (SSAPutInstruction)ins;
//				  if (pis.isStatic()) {
//				      line = "putstatic " + pis.getDeclaredField() + " = " + pis.getValueString(null, pis.getVal());
//				  } else {
//				      line =  "putfield " + pis.getDeclaredField();
//				  }
//			  }else if(ins instanceof SSAGetInstruction){
//				  SSAGetInstruction gis = (SSAGetInstruction)ins;
//				  if (gis.isStatic()) {
//				      line = "getstatic " + gis.getDeclaredField();
//				  } else {
//				      line = "getfield " + gis.getDeclaredField();
//				  }			 
//			  }else if(ins instanceof SSABinaryOpInstruction){
//				  SSABinaryOpInstruction ois = (SSABinaryOpInstruction)ins;
//				  line = valueTable.get(ois.getDef())+"=binaryop(" + ois.getOperator() + ") "+valueTable.get(ois.getVal1())+","+valueTable.get(ois.getVal2());
//			  }else if(ins instanceof SSAArrayStoreInstruction){
//				  SSAArrayStoreInstruction sas = (SSAArrayStoreInstruction)ins;
//				  line = "arraystore " + valueTable.get(sas.getArrayRef()) + "[" + valueTable.get(sas.getIndex())
//			        + "] = " + valueTable.get(sas.getValue());
//			  }else if(ins instanceof SSAArrayLoadInstruction){
//				  SSAArrayLoadInstruction sas = (SSAArrayLoadInstruction)ins;
//				  line = valueTable.get(sas.getDef()) + " = arrayload " + valueTable.get(sas.getArrayRef()) + "["
//					        + valueTable.get(sas.getIndex()) + "]";
//			  }else if(ins instanceof SSAArrayLengthInstruction){
//				  SSAArrayLengthInstruction sas = (SSAArrayLengthInstruction)ins;
//				  line = valueTable.get(sas.getDef()) + " = arraylength " + valueTable.get(sas.getArrayRef());
//			  }else if(ins instanceof SSAUnaryOpInstruction){
//				  SSAUnaryOpInstruction uoi = (SSAUnaryOpInstruction)ins;
//				  line = uoi.getOpcode().toString();
//			  }else if(ins instanceof SSAGotoInstruction){
//				  SSAGotoInstruction gti = (SSAGotoInstruction)ins;		
//				  if(indexTable!=null){
//					  line = "goto " + indexTable.get(gti.getTarget());
//				  }else{
//					  line = "goto";
//				  }
//			  }else if(ins instanceof AstAssertInstruction){
//				  line = "assert"; 
//			  }else if(ins instanceof SSAConditionalBranchInstruction){
//				  SSAConditionalBranchInstruction cbi = (SSAConditionalBranchInstruction)ins;
//				  line = "conditional branch(" + cbi.getOperator() + ") "+valueTable.get(cbi.getUse(0)) + "," + valueTable.get(cbi.getUse(1));
//			  }else if(ins instanceof SSAInstanceofInstruction){
//				  SSAInstanceofInstruction iis = (SSAInstanceofInstruction)ins;
//				  line = "instanceof " + iis.getCheckedType();
//			  }else if(ins instanceof SSACheckCastInstruction){
//				  SSACheckCastInstruction cci = (SSACheckCastInstruction)ins;
//				  line = "checkcast";
//			      for (TypeReference t : cci.getDeclaredResultTypes()) {
//			          line = line + " " + t;
//			      }
//			  }else if(ins instanceof SSAAbstractThrowInstruction){
//				  line = "throw";
//			  }else if(ins instanceof SSAReturnInstruction){
//				  line = "return";
//			  }else if(ins instanceof SSALoadMetadataInstruction){
//				  line = "load_metadata";
//			  }else if(ins instanceof SSASwitchInstruction){
//				  line = "switch";
//			  }else if(ins instanceof SSAConversionInstruction){
//				  SSAConversionInstruction ci = (SSAConversionInstruction)ins;
//				  line = valueTable.get(ci.getDef())+" = conversion(" + ci.getToType().getName() + ") " + valueTable.get(ci.getUse(0));
//			  }else if(ins instanceof SSAMonitorInstruction){
//				  SSAMonitorInstruction mi = (SSAMonitorInstruction)ins;
//				  line = "monitor" + (mi.isMonitorEnter() ? "enter " : "exit ") + valueTable.get(mi.getRef());
//			  }else if(ins instanceof AstLexicalWrite){
//				  AstLexicalWrite ast = (AstLexicalWrite)ins;
//				  line = "";
//				  for (int i = 0; i < ast.getAccessCount(); i++) {
//				      Access A = ast.getAccess(i);
//				      if (i != 0)
//				        line += ", ";
//				      line += "lexical:";
//				      line += A.variableName;
//				      line += "@";
//				      line += A.variableDefiner;
//				      line += " = ";
//				      line += valueTable.get(A.valueNumber);
//				    }			 
//			  }else if(ins instanceof AstLexicalRead){
//				  AstLexicalRead ast = (AstLexicalRead)ins;
//				  line = "";
//				  for (int i = 0; i < ast.getAccessCount(); i++) {
//				      Access A = ast.getAccess(i);
//				      if (i != 0)
//				      line += ", ";
//				      line += valueTable.get(A.valueNumber);
//				      line += " = lexical:";
//				      line += "@";
//				      line += A.variableDefiner;
//				   }		 
//			  }else{
//				  line = ins.toString(ir.getSymbolTable());
//			  }
//			return line;
//		}
	
	public String getVisualLabel(Statement s) {
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
	          line = getInstructionVisualString(ins);
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
	        	line = "NORMAL_RET_CALLER:" + getInstructionVisualString(caller.getInstruction());
	        	break;	
	        case EXC_RET_CALLEE:
	        case NORMAL_RET_CALLEE:
	        default:
	          line =  s.toString();
	        }
		 return line;
	}

	public String getInstructionVisualString(SSAInstruction ins) {
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
//	
//	private Hashtable<Integer, String> extractValues(
//			DirectedSparseGraph<StatementNode, StatementEdge> graph, IR ir) {
//		// TODO Auto-generated method stub
//		Hashtable<Integer, String> valueTable = new Hashtable<Integer, String>();
//		
//		SymbolTable table = ir.getSymbolTable();
//		for(int i=0; i<table.getMaxValueNumber(); i++){
//			String line = table.getValueString(i);
//			int mark = line.indexOf(":");
//			if(mark>0){
//				line = line.substring(mark+1);
//				valueTable.put(i, line);
//			}
//		}
//		
//		for(StatementNode node:graph.getVertices()){
//			if(node.statement instanceof NormalStatement){
//				NormalStatement ns = (NormalStatement)node.statement;
//				SSAInstruction ins = ns.getInstruction();
//				if(ins instanceof SSAGetInstruction){
//					SSAGetInstruction gis = (SSAGetInstruction)ins;
//					FieldReference field = gis.getDeclaredField();
//					String sig = field.getSignature();
//					valueTable.put(gis.getDef(), sig);
//				}else if(ins instanceof AstJavaInvokeInstruction){
//					AstJavaInvokeInstruction inv = (AstJavaInvokeInstruction)ins;
//					if(inv.hasDef()){
//						CallSiteReference site = inv.getCallSite();
//						String  line = "invoke "+site.getInvocationString() + " ";
//						line = line + site.getDeclaredTarget().toString();
//						valueTable.put(inv.getDef(), line);
//					}
//				}else if (ins instanceof SSANewInstruction){
//					  SSANewInstruction nis = (SSANewInstruction)ins;
//					  NewSiteReference site = nis.getNewSite();
//					  String line = "new " + site.getDeclaredType();
//					  valueTable.put(nis.getDef(), line);
//				}
//			}
//		}
//		
//		return valueTable;
//	}

	public int getLineNo(Statement statement) {
		int lineNo = -1;
		if (statement.getKind() == Statement.Kind.NORMAL) {			
			IMethod method = statement.getNode().getMethod();
			if(method instanceof ShrikeBTMethod) {
				lineNo = getBytecodeLine((ShrikeBTMethod)method, (NormalStatement) statement);
			}else if(method instanceof ConcreteJavaMethod) {
				lineNo = getSourceLine((ConcreteJavaMethod)method, (NormalStatement) statement);
			}			  
		}
		return lineNo;
	}

	private int getSourceLine(ConcreteJavaMethod method, NormalStatement statement) {
		int instructionIndex = statement.getInstructionIndex();
		int lineNum = method.getLineNumber(instructionIndex);
		return lineNum;
	}

	private int getBytecodeLine(ShrikeBTMethod method, NormalStatement statement) {
		int bcIndex, instructionIndex = ((NormalStatement) statement).getInstructionIndex();
		int lineNo = -1;
		try {
		    bcIndex = method.getBytecodeIndex(instructionIndex);
		    try {
		    	lineNo = statement.getNode().getMethod().getLineNumber(bcIndex);
		    } catch (Exception e) {
		      System.err.println("Bytecode index no good");
		      System.err.println(e.getMessage());
		    }
		} catch (Exception e ) {
		    System.err.println("it's probably not a BT method (e.g. it's a fakeroot method)");
		    System.err.println(e.getMessage());
		}
		return lineNo;
	}
	
//	private Hashtable<Integer, String> extractIndexTable(
//			DirectedSparseGraph<StatementNode, StatementEdge> graph) {
//		// TODO Auto-generated method stub
//		Hashtable<Integer, String> table = new Hashtable<Integer, String>();
//		for(StatementNode node:graph.getVertices()){			
//			if(node.statement instanceof NormalStatement){
//				NormalStatement ns = (NormalStatement)node.statement;
//				SSAInstruction ins = ns.getInstruction();
//				String label = getComparedLabel(node);
//				int index = ins.iindex;
//				table.put(index, label);
//			}
//		}
//		return table;
//	}
}
