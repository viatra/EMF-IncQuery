package org.eclipse.viatra2.emf.incquery.queryexplorer.handlers;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.viatra2.emf.incquery.queryexplorer.QueryExplorer;
import org.eclipse.viatra2.emf.incquery.queryexplorer.content.matcher.MatcherTreeViewerRoot;
import org.eclipse.viatra2.emf.incquery.queryexplorer.content.matcher.ObservablePatternMatcherRoot;
import org.eclipse.viatra2.emf.incquery.queryexplorer.content.patternsviewer.PatternComponent;
import org.eclipse.viatra2.emf.incquery.queryexplorer.util.DatabindingUtil;
import org.eclipse.viatra2.emf.incquery.queryexplorer.util.PatternRegistry;
import org.eclipse.viatra2.patternlanguage.core.helper.CorePatternLanguageHelper;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Pattern;
import org.eclipse.viatra2.patternlanguage.eMFPatternLanguage.PatternModel;

import com.google.inject.Inject;

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
	
	@Inject
	DatabindingUtil dbUtil;
	
	public RuntimeMatcherRegistrator(IFile file) {
		this.file = file;
	}

	@Override
	public void run() {
			
		MatcherTreeViewerRoot vr = QueryExplorer.getInstance().getMatcherTreeViewerRoot();
		PatternModel parsedEPM = dbUtil.parseEPM(file);
			
		//unregistering patterns
		Set<Pattern> removedPatterns = PatternRegistry.getInstance().unregisterPatternModel(file);
		for (Pattern pattern : removedPatterns) {
			for (ObservablePatternMatcherRoot root : vr.getRoots()) {
				root.unregisterPattern(pattern);
			}
			QueryExplorer.getInstance().getPatternsViewerInput().removeComponent(CorePatternLanguageHelper.getFullyQualifiedName(pattern));
		}
		
		QueryExplorer.getInstance().getPatternsViewer().refresh();

		//registering patterns
		Set<Pattern> newPatterns = PatternRegistry.getInstance().registerPatternModel(file, parsedEPM);
		for (Pattern pattern : newPatterns) {
			for (ObservablePatternMatcherRoot root : vr.getRoots()) {
				root.registerPattern(pattern);
			}
		}
		
		//setting check states
		List<PatternComponent> components = new ArrayList<PatternComponent>();
		for (Pattern pattern : newPatterns) {
			PatternComponent component = QueryExplorer.getInstance().getPatternsViewerInput().addComponent(CorePatternLanguageHelper.getFullyQualifiedName(pattern));
			components.add(component);
		}
		//note that after insertion a refresh is necessary otherwise setting check state will not work
		QueryExplorer.getInstance().getPatternsViewer().refresh();
		
		for (PatternComponent component : components) {
			QueryExplorer.getInstance().getPatternsViewer().setChecked(component, true);
		}
		
		if (components.size() > 0) {
			QueryExplorer.getInstance().getPatternsViewerInput().propagateSelectionToBottom();
		}
	}
}
