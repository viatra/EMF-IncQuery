package org.eclipse.viatra2.emf.incquery.queryexplorer.handlers;

import org.eclipse.core.resources.IFile;
import org.eclipse.viatra2.emf.incquery.queryexplorer.QueryExplorer;
import org.eclipse.viatra2.emf.incquery.queryexplorer.content.matcher.MatcherTreeViewerRoot;
import org.eclipse.viatra2.emf.incquery.queryexplorer.content.matcher.PatternMatcherRoot;
import org.eclipse.viatra2.emf.incquery.queryexplorer.util.DatabindingUtil;
import org.eclipse.viatra2.emf.incquery.queryexplorer.util.PatternRegistry;
import org.eclipse.viatra2.patternlanguage.eMFPatternLanguage.PatternModel;

import com.google.inject.Injector;

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
	private Injector injector;
	
	public RuntimeMatcherRegistrator(IFile file, Injector injector) {
		this.file = file;
		this.injector = injector;
	}

	@Override
	public void run() {
			
		MatcherTreeViewerRoot vr = QueryExplorer.getInstance().getMatcherTreeViewerRoot();

		PatternModel parsedEPM = DatabindingUtil.parseEPM(file, injector);
			
		PatternRegistry.getInstance().unregisterPatternModel(file);
			
		for (PatternMatcherRoot root : vr.getRoots()) {
			root.unregisterPatternsFromFile(file);
		}

		PatternRegistry.getInstance().registerPatternModel(file, parsedEPM);
			
		for (PatternMatcherRoot root : vr.getRoots()) {
			root.registerPatternsFromFile(file, parsedEPM);
		}

	}

}
