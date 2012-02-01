package org.eclipse.viatra2.emf.incquery.databinding.ui.handlers;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.viatra2.emf.incquery.databinding.ui.MatchSetViewer;
import org.eclipse.viatra2.emf.incquery.databinding.ui.observable.PatternMatcherRoot;
import org.eclipse.viatra2.emf.incquery.databinding.ui.observable.ViewerRoot;
import org.eclipse.viatra2.emf.incquery.databinding.ui.util.DatabindingUtil;
import org.eclipse.viatra2.patternlanguage.eMFPatternLanguage.PatternModel;
import org.eclipse.viatra2.patternlanguage.emf.matcherbuilder.runtime.PatternRegistry;

public class RuntimeMatcherRegistrationJob extends Job {

	private IFile file;
	
	public RuntimeMatcherRegistrationJob(String name, IFile file) {
		super(name);
		this.file = file;
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {
		ViewerRoot vr = MatchSetViewer.viewerRoot;

		PatternModel parsedEPM = DatabindingUtil.parseEPM(file);
		PatternRegistry.INSTANCE.registerAllInModel(parsedEPM);
			
		DatabindingUtil.registeredPatterModels.remove(file);
			
		for (PatternMatcherRoot root : vr.getRoots()) {
			root.unregisterPatternsFromFile(file);
		}

		DatabindingUtil.registeredPatterModels.put(file, parsedEPM);
			
		for (PatternMatcherRoot root : vr.getRoots()) {
			root.registerPatternsFromFile(file, parsedEPM);
		}

		return Status.OK_STATUS;
	}

}
