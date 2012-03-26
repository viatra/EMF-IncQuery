package org.eclipse.viatra2.emf.incquery.queryexplorer.handlers;

import org.eclipse.core.resources.IFile;
import org.eclipse.viatra2.emf.incquery.queryexplorer.QueryExplorer;
import org.eclipse.viatra2.emf.incquery.queryexplorer.observable.PatternMatcherRoot;
import org.eclipse.viatra2.emf.incquery.queryexplorer.observable.ViewerRoot;
import org.eclipse.viatra2.emf.incquery.queryexplorer.util.DatabindingUtil;
import org.eclipse.viatra2.patternlanguage.eMFPatternLanguage.PatternModel;

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
public class RuntimeMatcherRegistrator implements Runnable {

	private IFile file;
	
	public RuntimeMatcherRegistrator(IFile file) {
		this.file = file;
	}

	@Override
	public void run() {
		QueryExplorer.openView();
		ViewerRoot vr = QueryExplorer.getViewerRoot();

		PatternModel parsedEPM = DatabindingUtil.parseEPM(file);
			
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
