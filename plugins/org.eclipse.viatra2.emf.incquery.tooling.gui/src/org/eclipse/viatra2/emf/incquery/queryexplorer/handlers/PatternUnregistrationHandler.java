package org.eclipse.viatra2.emf.incquery.queryexplorer.handlers;

import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.viatra2.emf.incquery.queryexplorer.QueryExplorer;
import org.eclipse.viatra2.emf.incquery.queryexplorer.content.matcher.ObservablePatternMatcherRoot;
import org.eclipse.viatra2.emf.incquery.queryexplorer.content.patternsviewer.PatternComposite;
import org.eclipse.viatra2.emf.incquery.queryexplorer.content.patternsviewer.PatternLeaf;
import org.eclipse.viatra2.emf.incquery.queryexplorer.util.PatternRegistry;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Pattern;

public class PatternUnregistrationHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IStructuredSelection selection = (IStructuredSelection) 
				QueryExplorer.getInstance().getPatternsViewer().getSelection();
		Object element = selection.getFirstElement();
		
		if (element instanceof PatternLeaf) {
			PatternLeaf leaf = (PatternLeaf) element;
			PatternComposite composite = (PatternComposite) leaf.getParent();
			unregisterPattern(leaf.getFullPatternNamePrefix());
			QueryExplorer.getInstance().getPatternsViewer().refresh(composite);
		}
		else {
			PatternComposite composite = (PatternComposite) element;
			List<PatternLeaf> leaves = composite.getLeaves();
			for (PatternLeaf leaf : leaves) {
				unregisterPattern(((PatternLeaf) leaf).getFullPatternNamePrefix());
			}
			
			QueryExplorer.getInstance().getPatternsViewerInput().purge();
			QueryExplorer.getInstance().getPatternsViewer().refresh();
		}
		
		return null;
	}
	
	private void unregisterPattern(String patternFqn) {
		Pattern pattern = PatternRegistry.getInstance().getPatternByFqn(patternFqn);
		PatternRegistry.getInstance().unregisterPattern(patternFqn);
		
		//unregister patterns from observable roots
		for (ObservablePatternMatcherRoot root : QueryExplorer.getInstance().getMatcherTreeViewerRoot().getRoots()) {
			root.unregisterPattern(pattern);
		}
		
		//the pattern is not active anymore
		PatternRegistry.getInstance().removeActivePattern(pattern);
	}

}
