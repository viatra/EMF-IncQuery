package org.eclipse.viatra2.emf.incquery.queryexplorer.handlers;

import org.eclipse.core.resources.IFile;
import org.eclipse.viatra2.emf.incquery.queryexplorer.QueryExplorer;
import org.eclipse.viatra2.emf.incquery.queryexplorer.content.matcher.MatcherTreeViewerRoot;
import org.eclipse.viatra2.emf.incquery.queryexplorer.content.matcher.PatternMatcherRoot;
import org.eclipse.viatra2.emf.incquery.queryexplorer.util.PatternRegistry;

public class RuntimeMatcherUnRegistrator implements Runnable {

	private IFile file;
	
	public RuntimeMatcherUnRegistrator(IFile file) {
		this.file = file;
	}

	@Override
	public void run() {		
		MatcherTreeViewerRoot vr = QueryExplorer.getInstance().getMatcherTreeViewerRoot();

		for (PatternMatcherRoot root : vr.getRoots()) {
			root.unregisterPatternsFromFile(file);
		}
		
		PatternRegistry.getInstance().unregisterPatternModel(file);
	}

}