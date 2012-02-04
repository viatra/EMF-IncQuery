package org.eclipse.viatra2.emf.incquery.matchsetviewer.handlers;

import org.eclipse.core.resources.IFile;
import org.eclipse.viatra2.emf.incquery.matchsetviewer.MatchSetViewer;
import org.eclipse.viatra2.emf.incquery.matchsetviewer.observable.PatternMatcherRoot;
import org.eclipse.viatra2.emf.incquery.matchsetviewer.observable.ViewerRoot;
import org.eclipse.viatra2.emf.incquery.matchsetviewer.util.DatabindingUtil;
import org.eclipse.viatra2.patternlanguage.eMFPatternLanguage.PatternModel;
import org.eclipse.viatra2.patternlanguage.emf.matcherbuilder.runtime.PatternRegistry;

/**
 * Runnable unit of registering patterns in given file.
 * 
 * Note that if the work is implemented as a job, 
 * NullPointerException will occur when creating observables as the default realm will be null 
 * (because of non-ui thread).
 * 
 * @author Tamas Szabo
 *
 */
public class RuntimeMatcherRegistrationJob implements Runnable {

	private IFile file;
	
	public RuntimeMatcherRegistrationJob(IFile file) {
		this.file = file;
	}

	@Override
	public void run() {
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
	}

}
