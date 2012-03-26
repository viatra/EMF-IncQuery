package org.eclipse.viatra2.emf.incquery.queryexplorer.observable;

import org.eclipse.jface.databinding.viewers.TreeStructureAdvisor;
import org.eclipse.viatra2.emf.incquery.queryexplorer.QueryExplorer;

/**
 * Custom tree structure advisor implementation.
 * Note that every PatternMatcherRoot elements' parent will be the static viewerRoot of the MatchSetViewer class.
 * 
 * @author Tamas Szabo
 *
 */
public class TreeStructureAdvisorImpl extends TreeStructureAdvisor {

	@Override
	public Object getParent(Object element) {
		if (element instanceof PatternMatch) {
			return ((PatternMatch) element).getParent();
		}
		else if (element instanceof PatternMatcher) {
			return ((PatternMatcher) element).getParent();
		}
		else if (element instanceof PatternMatcherRoot) {
			return QueryExplorer.getViewerRoot();
		}
		else {
			return null;
		}
	}

	@Override
	public Boolean hasChildren(Object element) {
		if (element instanceof ViewerRoot) {
			ViewerRoot root = (ViewerRoot) element;
			return root.getRoots().size() > 0;
		} if (element instanceof PatternMatcherRoot) {
			/*
			 * Commented out as no listeners are registered
			 * if we return no known children.
			 */ 
//			PatternMatcherRoot root = (PatternMatcherRoot) element;
//			return root.getMatchers().size() > 0;
			return true;
		} else if (element instanceof PatternMatcher) {
			PatternMatcher matcher = (PatternMatcher) element;
			return matcher.getMatches().size() > 0;
		} else {
			return false;
		}
	}

}
