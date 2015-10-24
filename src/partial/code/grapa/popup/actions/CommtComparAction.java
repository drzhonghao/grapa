package partial.code.grapa.popup.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

import partial.code.grapa.commit.CommitComparator;

public class CommtComparAction implements IObjectActionDelegate {

	private Shell shell;
	
	/**
	 * Constructor for Action1.
	 */
	public CommtComparAction() {
		super();
	}

	/**
	 * @see IObjectActionDelegate#setActivePart(IAction, IWorkbenchPart)
	 */
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		shell = targetPart.getSite().getShell();
	}

	/**
	 * @see IActionDelegate#run(IAction)
	 */
	public void run(IAction action) {
		CommitComparator comparator = new CommitComparator();
		comparator.setDotExe("E:/Program Files (x86)/Graphviz2.38/bin/dot.exe");
		comparator.setProject("derby");
		comparator.setElementListDir("D:/project/empirical/bugfix_largecommit/data/elementlist/");
		comparator.setCommitDir("D:/project/empirical/bugfix_icse2015/data/repository/");
		comparator.setLibDir("D:/project/empirical/bugfix_largecommit/data/lib/");
		comparator.setJ2seDir("C:/Program Files/Java/jre1.8.0_60/lib/");
		comparator.setOtherLibDir("D:/project/empirical/bugfix_largecommit/data/otherlib/");
		comparator.setResultDir("D:/project/empirical/bugfix_largecommit/data/result/grapa/");
		comparator.setExclusionFile("D:/project/empirical/bugfix_largecommit/tool/grapa/Java60RegressionExclusions.txt");
		comparator.run();
	}

	/**
	 * @see IActionDelegate#selectionChanged(IAction, ISelection)
	 */
	public void selectionChanged(IAction action, ISelection selection) {
	}

}
