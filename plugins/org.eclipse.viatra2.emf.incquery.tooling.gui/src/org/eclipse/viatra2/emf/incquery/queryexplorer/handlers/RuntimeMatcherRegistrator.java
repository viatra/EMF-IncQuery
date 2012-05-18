package org.eclipse.viatra2.emf.incquery.queryexplorer.handlers;

import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.viatra2.emf.incquery.queryexplorer.QueryExplorer;
import org.eclipse.viatra2.emf.incquery.queryexplorer.content.matcher.MatcherTreeViewerRoot;
import org.eclipse.viatra2.emf.incquery.queryexplorer.content.matcher.ObservablePatternMatcherRoot;
import org.eclipse.viatra2.emf.incquery.queryexplorer.util.DatabindingUtil;
import org.eclipse.viatra2.emf.incquery.queryexplorer.util.PatternRegistry;
import org.eclipse.viatra2.patternlanguage.core.helper.CorePatternLanguageHelper;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Pattern;
import org.eclipse.viatra2.patternlanguage.eMFPatternLanguage.PatternModel;

import com.google.inject.Inject;
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
	
	@Inject
	DatabindingUtil dbUtil;
	
	public RuntimeMatcherRegistrator(IFile file, Injector injector) {
		this.file = file;
		this.injector = injector;
	}

	@Override
	public void run() {
			
		MatcherTreeViewerRoot vr = QueryExplorer.getInstance().getMatcherTreeViewerRoot();

		PatternModel parsedEPM = dbUtil.parseEPM(file);
			
		Set<Pattern> removedPatterns = PatternRegistry.getInstance().unregisterPatternModel(file);
		for (ObservablePatternMatcherRoot root : vr.getRoots()) {
			for (Pattern pattern : removedPatterns) {
				root.unregisterPattern(pattern);
			}
		}

		Set<Pattern> newPatterns = PatternRegistry.getInstance().registerPatternModel(file, parsedEPM);
		for (ObservablePatternMatcherRoot root : vr.getRoots()) {
			for (Pattern pattern : newPatterns) {
				root.registerPattern(pattern);
			}
		}

		CheckboxTableViewer patternsViewer = QueryExplorer.getInstance().getPatternsViewer();
		for (Pattern pattern : newPatterns) {
			patternsViewer.setChecked(CorePatternLanguageHelper.getFullyQualifiedName(pattern), true);
		}
	}

}
