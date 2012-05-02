package org.eclipse.viatra2.emf.incquery.queryexplorer.handlers;

import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.viatra2.emf.incquery.queryexplorer.QueryExplorer;
import org.eclipse.viatra2.emf.incquery.queryexplorer.content.matcher.MatcherTreeViewerRoot;
import org.eclipse.viatra2.emf.incquery.queryexplorer.content.matcher.PatternMatcherRoot;
import org.eclipse.viatra2.emf.incquery.queryexplorer.util.PatternRegistry;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Pattern;

public class RuntimeMatcherUnRegistrator implements Runnable {

	private IFile file;
	
	public RuntimeMatcherUnRegistrator(IFile file) {
		this.file = file;
	}

	@Override
	public void run() {		
		MatcherTreeViewerRoot vr = QueryExplorer.getInstance().getMatcherTreeViewerRoot();
		Set<Pattern> removedPatterns = PatternRegistry.getInstance().unregisterPatternModel(file);
		for (PatternMatcherRoot root : vr.getRoots()) {
			for (Pattern pattern : removedPatterns) {
				root.unregisterPattern(pattern);
			}
		}
	}

}