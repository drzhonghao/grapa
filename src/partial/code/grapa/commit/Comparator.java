package partial.code.grapa.commit;

import java.io.File;
import java.util.ArrayList;
import java.util.Hashtable;

import org.eclipse.jdt.core.dom.ASTNode;

import partial.code.grapa.commit.method.ClientMethod;
import partial.code.grapa.dependency.graph.DataFlowAnalysisEngine;
import partial.code.grapa.version.detect.VersionDetector;
import partial.code.grapa.version.detect.VersionPair;

abstract public class Comparator {
	protected String pName;
	protected String elementListDir;
	
	protected String libDir;
	protected String j2seDir;

		
	protected DataFlowAnalysisEngine leftEngine;
	protected DataFlowAnalysisEngine rightEngine;
	protected String otherLibDir;
	protected String exclusionsFile = "./Java60RegressionExclusions.txt";
	protected String bugName;
	
	public void setBugName(String bugName) {
		this.bugName = bugName;
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

	

	public void setOtherLibDir(String dir) {
		// TODO Auto-generated method stub
		otherLibDir = dir+pName+"/";
	}

	public void setExclusionFile(String file) {
		// TODO Auto-generated method stub
		exclusionsFile = file;
	}
	
	public void analyzeCommit(File d) {
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
		
		if(pair.left.versions.size()!=0&&pair.right.versions.size()!=0){
			compareVersions(pair, oldfiles, newfiles);
		}	
	}
	
	public void compareVersions(VersionPair pair, ArrayList<File> oldfiles,
			ArrayList<File> newfiles) {
		boolean bLeftSuccess = false;
		ArrayList<ASTNode> leftTrees = null; 
		for(String oldVersion:pair.left.versions){
			System.out.print(oldVersion+",");
			try{
				leftEngine = new DataFlowAnalysisEngine();
				leftEngine.setExclusionsFile(this.exclusionsFile);
				leftTrees = leftEngine.parse("left", pair.left.pTable, j2seDir, libDir, otherLibDir, oldVersion, oldfiles);
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
				rightTrees = rightEngine.parse("right", pair.right.pTable, j2seDir, libDir, otherLibDir,  newVersion, newfiles);
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
			compareAstTrees(leftTrees, rightTrees);			
		}else{
			System.out.println("Error:"+bugName);
		}
	}

	protected void compareAstTrees(ArrayList<ASTNode> leftTrees, ArrayList<ASTNode> rightTrees) {
		ClassMapping comparator = new ClassMapping(leftTrees, rightTrees);
		Hashtable<Object, Object> nm = comparator.extractItemMappings();
		
		for(ASTNode leftTree:leftTrees){
			ASTNode rightTree = (ASTNode) nm.get(leftTree);
			if(rightTree!=null){
				extractFinerMapping((ASTNode) leftTree, rightTree);				
			}
		}
	}




	protected abstract void extractFinerMapping(ASTNode leftTree, ASTNode rightTree);
}
