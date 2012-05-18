package org.eclipse.viatra2.emf.incquery.queryexplorer.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.viatra2.emf.incquery.queryexplorer.QueryExplorer;
import org.eclipse.viatra2.emf.incquery.queryexplorer.content.matcher.ObservablePatternMatcherRoot;
import org.eclipse.viatra2.emf.incquery.queryexplorer.util.PatternRegistry;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Pattern;

public class PatternUnregistrationHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IStructuredSelection selection = (IStructuredSelection) QueryExplorer.getInstance().getPatternsViewer().getSelection();
		String patternFqn = selection.getFirstElement().toString();
		Pattern pattern = PatternRegistry.getInstance().getPatternByFqn(patternFqn);
		PatternRegistry.getInstance().unregisterPattern(patternFqn);
		
		//unregister patterns from observable roots
		for (ObservablePatternMatcherRoot root : QueryExplorer.getInstance().getMatcherTreeViewerRoot().getRoots()) {
			root.unregisterPattern(patternFqn);
		}
		
		//the pattern is not active anymore
		PatternRegistry.getInstance().removeActivePattern(pattern);
		return null;
	}

}
