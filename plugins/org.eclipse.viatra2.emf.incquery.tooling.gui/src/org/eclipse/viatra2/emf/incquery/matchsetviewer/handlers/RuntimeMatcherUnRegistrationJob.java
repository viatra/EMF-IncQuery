package org.eclipse.viatra2.emf.incquery.matchsetviewer.handlers;

import org.eclipse.core.resources.IFile;
import org.eclipse.viatra2.emf.incquery.matchsetviewer.MatchSetView;
import org.eclipse.viatra2.emf.incquery.matchsetviewer.observable.PatternMatcherRoot;
import org.eclipse.viatra2.emf.incquery.matchsetviewer.observable.ViewerRoot;
import org.eclipse.viatra2.emf.incquery.matchsetviewer.util.DatabindingUtil;

public class RuntimeMatcherUnRegistrationJob implements Runnable {

	private IFile file;

	public RuntimeMatcherUnRegistrationJob(IFile file) {
		this.file = file;
	}

	@Override
	public void run() {
		ViewerRoot vr = MatchSetView.viewerRoot;

		for (PatternMatcherRoot root : vr.getRoots()) {
			root.unregisterPatternsFromFile(file);
		}
		DatabindingUtil.registeredPatterModels.remove(file);
	}

}