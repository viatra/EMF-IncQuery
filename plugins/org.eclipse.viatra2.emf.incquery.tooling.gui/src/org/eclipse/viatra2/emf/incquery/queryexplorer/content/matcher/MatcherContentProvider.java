package org.eclipse.viatra2.emf.incquery.queryexplorer.content.matcher;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

public class MatcherContentProvider implements ITreeContentProvider {

	private MatcherTreeViewerRoot input;
	
	public MatcherContentProvider() {

	}

	@Override
	public void dispose() {
		input = null;
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		if (newInput != null && newInput instanceof MatcherTreeViewerRoot) {
			input = (MatcherTreeViewerRoot) newInput;		
		}
	}

	@Override
	public Object[] getElements(Object inputElement) {
		if (inputElement instanceof MatcherTreeViewerRoot) {
			return input.getRoots().toArray();
		}
		return null;
	}

	@Override
	public Object[] getChildren(Object parentElement) {
		if (parentElement instanceof MatcherTreeViewerRoot) {
			return input.getRoots().toArray();
		}
		else if (parentElement instanceof PatternMatcherRoot) {
			return ((PatternMatcherRoot) parentElement).getMatchers().toArray();
		}
		else if (parentElement instanceof PatternMatcher) {
			return ((PatternMatcher) parentElement).getMatches().toArray();
		}
		return null;
	}

	@Override
	public Object getParent(Object element) {
		if (element instanceof PatternMatcherRoot) {
			return input;
		}
		else if (element instanceof PatternMatcher) {
			return ((PatternMatcher) element).getParent();
		}
		else if (element instanceof PatternMatch) {
			return ((PatternMatch) element).getParent();
		}
		return null;
	}

	@Override
	public boolean hasChildren(Object element) {
		if (element != null) {
			if (element instanceof MatcherTreeViewerRoot) {
				return (!input.getRoots().isEmpty());
			}
			else if (element instanceof PatternMatcherRoot) {
				return (!((PatternMatcherRoot) element).getMatchers().isEmpty());
			}
			else if (element instanceof PatternMatcher) {
				return (!(((PatternMatcher) element).getMatches().isEmpty()));
			}
		}
		return false;
	}


}
