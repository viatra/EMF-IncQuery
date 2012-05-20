package org.eclipse.viatra2.emf.incquery.queryexplorer.util;

import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.viatra2.emf.incquery.queryexplorer.QueryExplorer;
import org.eclipse.viatra2.emf.incquery.queryexplorer.content.matcher.MatcherTreeViewerRoot;
import org.eclipse.viatra2.emf.incquery.queryexplorer.content.matcher.ObservablePatternMatcherRoot;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Pattern;

public class CheckStateListener implements ICheckStateListener {

	@Override
	public void checkStateChanged(CheckStateChangedEvent event) {
		String patternFqn = event.getElement().toString();
		Pattern pattern = PatternRegistry.getInstance().getPatternByFqn(patternFqn);
		MatcherTreeViewerRoot vr = QueryExplorer.getInstance().getMatcherTreeViewerRoot();
		
		if (event.getChecked()) {
			for (ObservablePatternMatcherRoot root : vr.getRoots()) {
				root.registerPattern(pattern);
			}
			PatternRegistry.getInstance().addActivePattern(pattern);
		} else {
			for (ObservablePatternMatcherRoot root : vr.getRoots()) {
				root.unregisterPattern(patternFqn);
			}
			PatternRegistry.getInstance().removeActivePattern(pattern);
		}
	}

}
