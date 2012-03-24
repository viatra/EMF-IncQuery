package org.eclipse.viatra2.emf.incquery.queryexplorer.handlers;

import org.eclipse.core.resources.IFile;
import org.eclipse.viatra2.emf.incquery.queryexplorer.QueryExplorer;
import org.eclipse.viatra2.emf.incquery.queryexplorer.observable.PatternMatcherRoot;
import org.eclipse.viatra2.emf.incquery.queryexplorer.observable.ViewerRoot;
import org.eclipse.viatra2.emf.incquery.queryexplorer.util.DatabindingUtil;

public class RuntimeMatcherUnRegistrator implements Runnable {

	private IFile file;

	public RuntimeMatcherUnRegistrator(IFile file) {
		this.file = file;
	}

	@Override
	public void run() {
		ViewerRoot vr = QueryExplorer.getViewerRoot();

		for (PatternMatcherRoot root : vr.getRoots()) {
			root.unregisterPatternsFromFile(file);
		}
		DatabindingUtil.registeredPatterModels.remove(file);
		
//		if (QueryExplorer.isViewOpen()) {
//			QueryExplorer.refreshTreeViewer();
//		}
	}

}