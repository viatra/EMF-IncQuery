package org.eclipse.viatra2.emf.incquery.matchsetviewer.handlers;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.viatra2.emf.incquery.matchsetviewer.MatchSetViewer;
import org.eclipse.viatra2.emf.incquery.matchsetviewer.observable.PatternMatcherRoot;
import org.eclipse.viatra2.emf.incquery.matchsetviewer.observable.ViewerRoot;
import org.eclipse.viatra2.emf.incquery.matchsetviewer.util.DatabindingUtil;

public class RuntimeMatcherUnRegistrationJob extends Job {

	private IFile file;

	public RuntimeMatcherUnRegistrationJob(String name, IFile file) {
		super(name);
		this.file = file;
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {
		ViewerRoot vr = MatchSetViewer.viewerRoot;

		for (PatternMatcherRoot root : vr.getRoots()) {
			root.unregisterPatternsFromFile(file);
		}
		DatabindingUtil.registeredPatterModels.remove(file);
		return Status.OK_STATUS;
	}

}