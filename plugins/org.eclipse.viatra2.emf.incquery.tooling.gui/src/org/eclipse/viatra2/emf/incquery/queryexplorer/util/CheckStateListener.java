package org.eclipse.viatra2.emf.incquery.queryexplorer.util;

import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.viatra2.emf.incquery.queryexplorer.QueryExplorer;
import org.eclipse.viatra2.emf.incquery.queryexplorer.content.matcher.ObservablePatternMatcherRoot;
import org.eclipse.viatra2.emf.incquery.queryexplorer.content.patternsviewer.PatternComponent;
import org.eclipse.viatra2.emf.incquery.queryexplorer.content.patternsviewer.PatternComposite;
import org.eclipse.viatra2.emf.incquery.queryexplorer.content.patternsviewer.PatternLeaf;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Pattern;

public class CheckStateListener implements ICheckStateListener {

	@Override
	public void checkStateChanged(CheckStateChangedEvent event) {
		Object element = event.getElement();
		
		if (element instanceof PatternLeaf) {
			processLeaf((PatternLeaf) element, event);
		}
		else if (element instanceof PatternComposite){
			PatternComposite composite = (PatternComposite) element;
			for (PatternLeaf leaf : composite.getLeaves()) {
				processLeaf(leaf, event);
			}
			
			if (event.getChecked()) {
				for (PatternComponent component : composite.getAllChildren()) {
					QueryExplorer.getInstance().getPatternsViewer().setChecked(component, true);
				}
			}
			else {
				for (PatternComponent component : composite.getAllChildren()) {
					QueryExplorer.getInstance().getPatternsViewer().setChecked(component, false);
				}
			}
		}		
	}
	
	private void processLeaf(PatternLeaf leaf, CheckStateChangedEvent event) {
		String patternFqn = leaf.getFullPatternNamePrefix();
		Pattern pattern = PatternRegistry.getInstance().getPatternByFqn(patternFqn);
		if (event.getChecked()) {
			for (ObservablePatternMatcherRoot root : QueryExplorer.getInstance().getMatcherTreeViewerRoot().getRoots()) {
				root.registerPattern(pattern);
			}
			PatternRegistry.getInstance().addActivePattern(pattern);
		} else {
			for (ObservablePatternMatcherRoot root : QueryExplorer.getInstance().getMatcherTreeViewerRoot().getRoots()) {
				root.unregisterPattern(pattern);
			}
			PatternRegistry.getInstance().removeActivePattern(pattern);
		}
	}
}
