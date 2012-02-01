package org.eclipse.viatra2.emf.incquery.databinding.ui.observable;

import org.eclipse.jface.databinding.viewers.TreeStructureAdvisor;
import org.eclipse.viatra2.emf.incquery.databinding.ui.MatchSetViewer;

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
			return MatchSetViewer.viewerRoot;
		}
		else {
			return null;
		}
	}

	@Override
	public Boolean hasChildren(Object element) {
		if (element instanceof ViewerRoot) {
			ViewerRoot root = (ViewerRoot) element;
			if (root.getRoots().size() > 0) {
				return true;
			}
			else {
				return false;
			}
		}
		if (element instanceof PatternMatcherRoot) {
			PatternMatcherRoot root = (PatternMatcherRoot) element;
			if (root.getMatchers().size() > 0) {
				return true;
			}
			else {
				return false;
			}
		}
		else if (element instanceof PatternMatcher) {
			PatternMatcher matcher = (PatternMatcher) element;
			if (matcher.getMatches().size() > 0) {
				return true;
			}
			else {
				return false;
			}
		}
		else {
			return false;
		}
	}

}
