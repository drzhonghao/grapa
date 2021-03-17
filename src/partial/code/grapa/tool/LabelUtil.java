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
		labelTypes.add("Node:");
	}
	
	public static void main(String args[]) {
		LabelUtil lu = new LabelUtil();
		String r = lu.parse("invokeinterface < Source, Lorg/osgi/framework/ServiceReference, getProperty(Ljava/lang/String;)Ljava/lang/Object; > v2,r");
		System.out.println(r);
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

	
	public String getVisualLabel(Statement s) {
		String line = "";
//		 switch (s.getKind()) {
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
//	          line = getInstructionVisualString(ins);
//	          break;
//	        case PARAM_CALLEE:
//	          ParamCallee paramCallee = (ParamCallee) s;
//	          IMethod method = paramCallee.getNode().getMethod();
//	          int index = paramCallee.getValueNumber();
//	          if(method.isStatic()) {
//	        	  index = index - 1;
//	          }else {
//	        	  index = index - 2;
//	          }
//	          String type;
//	          if(index>=0) {
//	        	  type = method.getParameterType(index).toString();
//	          }else {
//	        	  type = "this";
//	          }
//	          line = s.getKind() + " " + paramCallee.getValueNumber()+":"+type+":"+method.getSignature();
//	          break;
//	        case PARAM_CALLER:
//	          ParamCaller paramCaller = (ParamCaller) s;
//	          method = paramCaller.getNode().getMethod();
//	          line = s.getKind() + " " + paramCaller.getValueNumber()+":"+method.getSignature();
//	          break;	      
//	        case EXC_RET_CALLER:
//	        	line = s.toString();
//	        	break;
//	        case PHI:
//	        	PhiStatement phi = (PhiStatement)s;
//	        	line = phi.getPhi().toString();
//	        	break;	
//	        case NORMAL_RET_CALLER:
//	        	NormalReturnCaller caller = (NormalReturnCaller)s;
//	        	line = "NORMAL_RET_CALLER:" + getInstructionVisualString(caller.getInstruction());
//	        	break;	
//	        case EXC_RET_CALLEE:
//	        case NORMAL_RET_CALLEE:
//	        default:
//	          line =  s.toString();
//	     }
		 line =  s.toString();
		 return line;
	}

	public String getInstructionVisualString(SSAInstruction ins) {
		String line = ins.toString(ir.getSymbolTable());
		return line;
	}


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

}
