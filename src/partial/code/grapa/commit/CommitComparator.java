package partial.code.grapa.commit;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import org.eclipse.jdt.core.dom.ASTNode;
import partial.code.grapa.dependency.graph.DataFlowAnalysisEngine;
import partial.code.grapa.dependency.graph.SDGwithPredicate;
import partial.code.grapa.mapping.AstTreeComparator;
import partial.code.grapa.mapping.ClientMethod;
import partial.code.grapa.tool.SDGComparator;
import partial.code.grapa.version.detect.VersionDetector;
import partial.code.grapa.version.detect.VersionPair;

import com.ibm.wala.ssa.IR;

public class CommitComparator {

	private String pName;
	private String elementListDir;
	
	private String libDir;
	private String j2seDir;

	private String resultDir;
	
	private DataFlowAnalysisEngine leftEngine;
	private DataFlowAnalysisEngine rightEngine;
	private String otherLibDir;
	private String exclusionsFile;
	private String bugName;


	
	
	
	public void setBugName(String bugName) {
		this.bugName = bugName;
	}


	
	public ArrayList<MethodDelta> analyzeCommit(File d, boolean bResolveAst) {
		// TODO Auto-generated method stub
		
		VersionDetector detector = new VersionDetector();
		detector.setProject(pName);
		detector.readElementList(elementListDir);
		File fd = new File(d.getAbsolutePath()+"/from");
		ArrayList<File> oldfiles = new ArrayList<File>();
		for(File f:fd.listFiles()){
			if(f.getName().endsWith(".java")&&f.getName().indexOf("Test")<0){
				oldfiles.add(f);
			}
		}
		fd = new File(d.getAbsolutePath()+"/to");
		ArrayList<File> newfiles = new ArrayList<File>();
		for(File f:fd.listFiles()){
			if(f.getName().endsWith(".java")&&f.getName().indexOf("Test")<0){
				newfiles.add(f);
			}
		}
		VersionPair pair = detector.run(oldfiles, newfiles);
		
		for(int i=oldfiles.size()-1; i>=0; i--){
			File file = oldfiles.get(i);
			if(pair.left.testFiles.contains(file)){
				oldfiles.remove(i);
			}
		}
		
		for(int i=newfiles.size()-1; i>=0; i--){
			File file = newfiles.get(i);
			if(pair.right.testFiles.contains(file)){
				newfiles.remove(i);
			}
		}		
		
		ArrayList<MethodDelta> info = null;
		if(pair.left.versions.size()!=0&&pair.right.versions.size()!=0){
			info = compareVersions(pair, oldfiles, newfiles, bResolveAst);
		}
		return info;
	}



	public ArrayList<MethodDelta> compareVersions(VersionPair pair, ArrayList<File> oldfiles,
			ArrayList<File> newfiles, boolean bResolveAst) {
		// TODO Auto-generated method stub\
		ArrayList<MethodDelta> methods = new ArrayList<MethodDelta>();
		boolean bLeftSuccess = false;
		ArrayList<ASTNode> leftTrees = null; 
		for(String oldVersion:pair.left.versions){
			System.out.print(oldVersion+",");
			try{
				leftEngine = new DataFlowAnalysisEngine();
				leftEngine.setExclusionsFile(this.exclusionsFile);
				leftTrees = leftEngine.addtoScope("left", pair.left.pTable, j2seDir, libDir, otherLibDir, oldVersion, oldfiles);
				leftEngine.initClassHierarchy();
				bLeftSuccess = true;
			}catch(Exception e){
				e.printStackTrace();
				bLeftSuccess = false;
			}
			if(bLeftSuccess){
				break;
			}
		}
		
		if(!bLeftSuccess){
			System.out.println("Fail to parse the left side project");
		}
		
		boolean bRightSuccess = false;
		System.out.print("->");
		
		ArrayList<ASTNode> rightTrees = null;
		for(String newVersion:pair.right.versions){
			System.out.print(newVersion+",");
			try{
				rightEngine = new DataFlowAnalysisEngine();
				rightEngine.setExclusionsFile(this.exclusionsFile);	
				rightTrees = rightEngine.addtoScope("right", pair.right.pTable, j2seDir, libDir, otherLibDir,  newVersion, newfiles);
				rightEngine.initClassHierarchy();
				bRightSuccess = true;
			}catch(Exception e){
				e.printStackTrace();
				bRightSuccess = false;
			}
			if(bRightSuccess){
				break;
			}
		}
		
		if(!bRightSuccess){
			System.out.println("Fail to parse the right side project");
		}
//		try {
//			Thread.sleep(200);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
		if(bLeftSuccess&&bRightSuccess){
			AstTreeComparator comparator = new AstTreeComparator(leftTrees, rightTrees);
			Hashtable<ClientMethod, ClientMethod> mps = comparator.extractMappings();
			
			for(ClientMethod oldMethod:mps.keySet()){
				ClientMethod newMethod = mps.get(oldMethod);					
				MethodDelta md = compareMethodPair(oldMethod, newMethod, bResolveAst);				
				methods.add(md);
			}
		}else{
			System.out.println("Error:"+bugName);
		}
		return methods;
	}
	
	private MethodDelta compareMethodPair(ClientMethod oldMethod,
			ClientMethod newMethod, boolean bResolveAst) {
		// TODO Auto-generated method stub
		System.out.println(oldMethod.methodName);
		SDGwithPredicate lfg = leftEngine.buildSystemDependencyGraph(oldMethod);
		IR lir = leftEngine.getCurrentIR();
		
		SDGwithPredicate rfg = rightEngine.buildSystemDependencyGraph(newMethod);
		IR rir = rightEngine.getCurrentIR();
		
		SDGComparator gt = new SDGComparator(lir, rir, bResolveAst,oldMethod, newMethod);
		MethodDelta md = gt.compare(lfg, rfg);
	
		return md;
	}
	
	
	
	

	public String getResultDir() {
		return resultDir;
	}


	public String getBugName() {
		return bugName;
	}

	public void setProject(String name) {
		// TODO Auto-generated method stub
		pName = name;	
	}

	public void setElementListDir(String eld) {
		// TODO Auto-generated method stub
		elementListDir = eld+pName+"/";
	}

	

	public void setLibDir(String dir) {
		// TODO Auto-generated method stub
		libDir = dir+pName+"/";
	}

	public void setJ2seDir(String dir) {
		// TODO Auto-generated method stub
		j2seDir = dir;
	}

	public void setResultDir(String dir) {
		// TODO Auto-generated method stub
		resultDir = dir+pName+"/";
	}

	public void setOtherLibDir(String dir) {
		// TODO Auto-generated method stub
		otherLibDir = dir+pName+"/";
	}

	public void setExclusionFile(String file) {
		// TODO Auto-generated method stub
		exclusionsFile = file;
	}

}
