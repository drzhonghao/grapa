package partial.code.grapa.dependency.graph;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;
import java.util.jar.JarFile;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.PackageDeclaration;
import org.eclipse.jdt.launching.JavaRuntime;

import partial.code.grapa.commit.method.ClientMethod;
import partial.code.grapa.tool.FileUtils;




import com.ibm.wala.cast.java.client.JavaSourceAnalysisEngine;
import com.ibm.wala.cast.java.ipa.callgraph.AstJavaZeroXCFABuilder;
import com.ibm.wala.cast.java.ipa.callgraph.JavaSourceAnalysisScope;
import com.ibm.wala.cast.java.ipa.modref.AstJavaModRef;
import com.ibm.wala.cast.java.translator.jdt.JDTClassLoaderFactory;
import com.ibm.wala.cast.java.translator.jdt.JDTSourceLoaderImpl;
import com.ibm.wala.classLoader.BinaryDirectoryTreeModule;
import com.ibm.wala.classLoader.ClassLoaderFactory;
import com.ibm.wala.classLoader.ClassLoaderFactoryImpl;
import com.ibm.wala.classLoader.IClass;
import com.ibm.wala.classLoader.IClassLoader;
import com.ibm.wala.classLoader.IMethod;
import com.ibm.wala.classLoader.JarFileModule;
import com.ibm.wala.classLoader.Module;
import com.ibm.wala.ide.classloader.EclipseSourceFileModule;
import com.ibm.wala.ipa.callgraph.AnalysisCache;
import com.ibm.wala.ipa.callgraph.AnalysisOptions;
import com.ibm.wala.ipa.callgraph.Entrypoint;
import com.ibm.wala.ipa.callgraph.IAnalysisCacheView;
import com.ibm.wala.ipa.callgraph.impl.DefaultEntrypoint;
import com.ibm.wala.ipa.callgraph.impl.Everywhere;
import com.ibm.wala.ipa.callgraph.propagation.InstanceKey;
import com.ibm.wala.ipa.callgraph.propagation.PointerAnalysis;
import com.ibm.wala.ipa.cha.IClassHierarchy;
import com.ibm.wala.ipa.slicer.SDG;
import com.ibm.wala.ipa.slicer.Slicer.ControlDependenceOptions;
import com.ibm.wala.ipa.slicer.Slicer.DataDependenceOptions;
import com.ibm.wala.ssa.IR;
import com.ibm.wala.types.ClassLoaderReference;
import com.ibm.wala.types.Descriptor;
import com.ibm.wala.types.MethodReference;
import com.ibm.wala.types.TypeName;
import com.ibm.wala.util.CancelException;
import com.ibm.wala.util.collections.HashSetFactory;
import com.ibm.wala.util.config.SetOfClasses;
import com.ibm.wala.util.strings.Atom;

import ca.mcgill.cs.swevo.ppa.PPAOptions;
import ca.mcgill.cs.swevo.ppa.ui.PPAUtil;

public class DataFlowAnalysisEngine extends JavaSourceAnalysisEngine{
	private IR ir;

	public DataFlowAnalysisEngine() {
		
	}

	public void initClassHierarchy() {
		IClassHierarchy cha = buildClassHierarchyWithPPA();
		setClassHierarchy(cha);
	}



	public void clearAnalysisScope() throws JavaModelException {
		if(scope!=null){
			JavaSourceAnalysisScope js = (JavaSourceAnalysisScope)scope;
			ClassLoaderReference loader = js.getSourceLoader();
			List<Module> s = js.getModules(loader);
			s.clear();
		}
	}

	
	
	public AstJavaZeroXCFABuilder getCFGBuilder(ClientMethod method) {
		IClassHierarchy cha = this.getClassHierarchy();
		HashSet<Entrypoint> eps = HashSetFactory.make();
		final Atom mainMethod = Atom.findOrCreateAsciiAtom(method.methodName);
		IMethod m = null; 
		for(IClassLoader loader: cha.getLoaders()){
			 if(loader instanceof JDTSourceLoaderImpl){
				 JDTSourceLoaderImpl jdtloader = (JDTSourceLoaderImpl)loader;
				 TypeName name = TypeName.findOrCreate(method.key);
				 
				 IClass type = jdtloader.lookupClass(name);
				 if(type!=null){
					 MethodReference mainRef = MethodReference.findOrCreate(type.getReference(), mainMethod, Descriptor
					            .findOrCreateUTF8(method.sig));
					 m = type.getMethod(mainRef.getSelector());
					 if (m != null) {
						 eps.add(new DefaultEntrypoint(m, cha));
					 } 
				 }				 
			 }			
		}
		AstJavaZeroXCFABuilder builder = null;
		if(eps.size()>0){
			AnalysisOptions options = getDefaultOptions(eps);
			options.setOnlyClientCode(true);
			try {
				builder = (AstJavaZeroXCFABuilder)buildCallGraph(cha, options, true, null);
				IAnalysisCacheView ce = builder.getAnalysisCache();
				ir = ce.getIR(m);
//	        	ir = ce.getSSACache().findOrCreateIR(m, Everywhere.EVERYWHERE, options.getSSAOptions() );
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (CancelException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else{
//		  System.out.println("Here!");
//		  System.out.println("Here!");
		}
		
		return builder;
	}

	
	public SDGwithPredicate buildSystemDependencyGraph(ClientMethod method)  {
		// TODO Auto-generated method stub
		AstJavaZeroXCFABuilder  builder =  getCFGBuilder(method);
		SDGwithPredicate g = null;
		if(builder!=null){
			DataDependenceOptions dOptions = DataDependenceOptions.FULL; 
			ControlDependenceOptions cOptions = ControlDependenceOptions.FULL;
			PointerAnalysis<InstanceKey> pointer = builder.getPointerAnalysis();
			SDG sdg = new SDG(cg, pointer, new AstJavaModRef(), dOptions, cOptions);
			g = pruneSDG(sdg, method);
		}
		return g;
	}
	
	private SDGwithPredicate pruneSDG(SDG g, ClientMethod method) {
		// TODO Auto-generated method stub
//		typeName = "L"+methodname.replace(".", "/");
		IntraPredicate p = new IntraPredicate(method.getSignature());
		if (g == null) {
		     throw new IllegalArgumentException("g is null");
		}
		SDGwithPredicate graph = new SDGwithPredicate(g, p);
		return graph;
	}
	
	

	@Override
	protected ClassLoaderFactory getClassLoaderFactory(SetOfClasses exclusions) {
	
		return new JDTClassLoaderFactory(exclusions);	
	}

	public ArrayList<ASTNode> parse(String prn, Hashtable<File, String> packageTable, String j2seDir, String libDir, String otherLibDir, String version,
			ArrayList<File> files) {
		ArrayList<ASTNode> trees = null;
		try {
			super.buildAnalysisScope();
			resolveJ2sePathEntry(j2seDir);			
			String clibDir = libDir+version;
			resolveLibraryPathEntry(clibDir);
			resolveLibraryPathEntry(otherLibDir);
			trees = resolveSourcePathyEntry(prn,  packageTable, files,  clibDir, otherLibDir);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return trees;
	}
	
	public ArrayList<ASTNode> parse(String pn, String j2seDir, String libDir, String otherLib, ArrayList<File> files) {
		Hashtable<File, String> table = resolvePackageTable(files);
		ArrayList<ASTNode> trees = null;
		try {
			super.buildAnalysisScope();
			resolveJ2sePathEntry(j2seDir);
			resolveLibraryPathEntry(libDir);
			resolveLibraryPathEntry(otherLib);
			trees = resolveSourcePathyEntry(pn,  table, files,  libDir, otherLib);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return trees;
	}

	

	private Hashtable<File, String> resolvePackageTable(ArrayList<File> files) {
		PPAOptions option = new PPAOptions();
		option.setAllowMemberInference(true);
		option.setAllowCollectiveMode(true);
		option.setAllowTypeInferenceMode(true);
		option.setAllowMethodBindingMode(true);
		
		Hashtable<File, String> table = new Hashtable<File, String>();
		for(File file:files) {
			String sourcecode = FileUtils.getContent(file);
			String[] lines = sourcecode.split("\n");
			for(String line:lines) {
				line = line.trim();
				if(line.startsWith("package")&&line.endsWith(";")) {
					String paName = line.substring(7, line.length()-1).trim();
					table.put(file, paName);
					break;
				}
			}
		}
		return table;
	}

	public ArrayList<ASTNode> addtoScope(String prn, Hashtable<File, String> pTable,
			String j2seDir, String jarPath, String otherLibDir, ArrayList<File> files) {
		ArrayList<ASTNode> trees = null;
		try {
			super.buildAnalysisScope();
			resolveJ2sePathEntry(j2seDir);	
			
			resolveSourcePathyEntry(prn,  pTable, files,  jarPath, "");
			
			resolveJarFileName(jarPath);
			File d = new File(otherLibDir);
			for(File f:d.listFiles()){
				resolveJarFileName(f.getAbsolutePath());				
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return trees;
	}

	private void resolveJarFileName(String jarFileName) throws IOException {
		File f = new File(jarFileName);
		if(f.isFile()) {
			JarFile j = new JarFile(f);
			Module module = (f.isDirectory() ? (Module) new BinaryDirectoryTreeModule(f) : (Module) new JarFileModule(j));
			scope.addToScope(scope.getApplicationLoader(), module);
		}else {
			for(File jf:f.listFiles()) {
				JarFile j = new JarFile(jf);
				Module module = (f.isDirectory() ? (Module) new BinaryDirectoryTreeModule(f) : (Module) new JarFileModule(j));
				scope.addToScope(scope.getApplicationLoader(), module);
			}
		}
	}

	
	public void addtoScope(String j2seDir, String jarFileName){
	    try {
			super.buildAnalysisScope();
			resolveJ2sePathEntry(j2seDir);	
			resolveJarFileName(jarFileName);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private ArrayList<ASTNode> resolveSourcePathyEntry(String prn,
			Hashtable<File, String> pTable, ArrayList<File> files,
			String clibDir, String otherLibDir) throws CoreException {
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IWorkspaceRoot root= workspace.getRoot();
		IProject project= root.getProject(prn);
		if(!project.exists()){
			project.create(null);
			project.open(null);
			//set the Java nature
			IProjectDescription description = project.getDescription();
			description.setNatureIds(new String[] { JavaCore.NATURE_ID });
			 
			//create the project
			project.setDescription(description, null);
		}
		IJavaProject javaProject = JavaCore.create(project);
		IClasspathEntry[] buildPath = {
				JavaCore.newSourceEntry(project.getFullPath().append("src")),
						JavaRuntime.getDefaultJREContainerEntry() };
		 
		javaProject.setRawClasspath(buildPath, project.getFullPath().append(
						"bin"), null);
		
		//create folder by using resources package
		IFolder folder = project.getFolder("src");
		if(folder.exists()){
			folder.delete(true, null);
		}
		folder.create(true, true, null);
		
		Set<IClasspathEntry> entries = new HashSet<IClasspathEntry>();
		entries.addAll(Arrays.asList(javaProject.getRawClasspath()));
		File d = new File(clibDir);
		if(d.isDirectory()){
			for(File f:d.listFiles()){
				if(f.getName().endsWith(".jar")){
					 IClasspathEntry libEntry = JavaCore.newLibraryEntry(
							    new Path(f.getAbsolutePath()), // library location
							    null, // source archive location
							    null, // source archive root path
							    true); // exported
					 entries.add(libEntry);
				}
			}
		}else if(d.isFile()){
			 IClasspathEntry libEntry = JavaCore.newLibraryEntry(
					    new Path(d.getAbsolutePath()), // library location
					    null, // source archive location
					    null, // source archive root path
					    true); // exported
			 entries.add(libEntry);
		}
		d = new File(otherLibDir);
		if(d.exists()){
			for(File f:d.listFiles()){
				if(f.getName().endsWith(".jar")){
					 IClasspathEntry libEntry = JavaCore.newLibraryEntry(
							    new Path(f.getAbsolutePath()), // library location
							    null, // source archive location
							    null, // source archive root path
							    true); // exported
					 entries.add(libEntry);
				}
			}
		}
		javaProject.setRawClasspath(entries.toArray(new IClasspathEntry[entries.size()]), null);

		//Add folder to Java element
		IPackageFragmentRoot srcFolder = javaProject
						.getPackageFragmentRoot(folder);
		for(File file:files){
			String pn = pTable.get(file);
			//create package fragment
			IPackageFragment pf = srcFolder.getPackageFragment(pn);
			if(!pf.exists()){
				pf = srcFolder.createPackageFragment(
						pn, true, null);
			}
			String source = FileUtils.getContent(file);
			String unitname = file.getName();
			int mark = unitname.lastIndexOf("_");
			unitname = unitname.substring(mark+1);
			ICompilationUnit cu = pf.createCompilationUnit(unitname, source, false, null);
			IResource rs = cu.getResource();
			IFile ifile= (IFile)rs;
			EclipseSourceFileModule module = EclipseSourceFileModule.createEclipseSourceFileModule(ifile);
			JavaSourceAnalysisScope s = (JavaSourceAnalysisScope)scope;
			this.scope.addToScope(s.getSourceLoader(), module);
		}
		
		ArrayList<ASTNode> trees = new  ArrayList<ASTNode>();
		for(IPackageFragment pf:javaProject.getPackageFragments()){
			if(pf.getKind() == IPackageFragmentRoot.K_SOURCE){
				for(ICompilationUnit cu:pf.getCompilationUnits()){
//					CompilerOptions options = new CompilerOptions();
//				    options.docCommentSupport = true;
//				    options.complianceLevel = ClassFileConstants.JDK1_8;
//				    options.sourceLevel = ClassFileConstants.JDK1_8;
//				    options.targetJDK = ClassFileConstants.JDK1_8;
//				    
//				    CommentRecorderParser commentParser =  new CommentRecorderParser(new ProblemReporter(
//				                DefaultErrorHandlingPolicies.proceedWithAllProblems(),
//				                options,
//				                new DefaultProblemFactory()), false);
//				    CompilationResult compilationResult =  new CompilationResult((org.eclipse.jdt.internal.compiler.env.ICompilationUnit) cu, 0, 0, options.maxProblemsPerUnit);
//				    CompilationUnitDeclaration ast = commentParser.parse((org.eclipse.jdt.internal.compiler.env.ICompilationUnit) cu, compilationResult);
//				    JavaCompilation jcu = new JavaCompilation(ast, commentParser.scanner);
				    
				    ASTParser parser = ASTParser.newParser(AST.JLS8);
					parser.setKind(ASTParser.K_COMPILATION_UNIT);
					parser.setProject(javaProject);
					parser.setSource(cu);
					parser.setResolveBindings(true);
					ASTNode tree = parser.createAST(null);
				    trees.add(tree);
				}
			}
		}
		return trees;
	}



	private void resolveLibraryPathEntry(String dir) throws IOException {
		// TODO Auto-generated method stub
		File d = new File(dir);
		for(File f:d.listFiles()){
			if(f.getName().endsWith(".jar")){
				JarFile j = new JarFile(f);
				Module module = (f.isDirectory() ? (Module) new BinaryDirectoryTreeModule(f) : (Module) new JarFileModule(j));
				scope.addToScope(scope.getApplicationLoader(), module);
			}
		}
	}

	private void resolveJ2sePathEntry(String j2seDir) throws IOException {
		// TODO Auto-generated method stub
		File file = new File(j2seDir+"rt.jar");
		JarFile j = new JarFile(file);
		Module module = (file.isDirectory() ? (Module) new BinaryDirectoryTreeModule(file) : (Module) new JarFileModule(j));
		scope.addToScope(scope.getPrimordialLoader(), module);
		
//		File d = new File(j2seDir+"/ext/");
//		for(File f:d.listFiles()){
//			if(f.getName().endsWith(".jar")){
//				j = new JarFile(f);
//				module = (f.isDirectory() ? (Module) new BinaryDirectoryTreeModule(f) : (Module) new JarFileModule(j));
//				scope.addToScope(scope.getExtensionLoader(), module);
//			}
//		}
	}

	

	public IR getCurrentIR() {
		return ir;
	}

	

	
	
}
