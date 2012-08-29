package org.eclipse.viatra2.emf.incquery.runtime.graphiti.util;

import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.graphiti.ui.editor.DiagramEditor;
import org.eclipse.viatra2.emf.incquery.queryexplorer.content.matcher.ObservablePatternMatch;

public class MatchEditorTester extends PropertyTester {

	@Override
	public boolean test(Object receiver, String property, Object[] args, Object expectedValue) {
		if (receiver instanceof ObservablePatternMatch) {
			ObservablePatternMatch match = (ObservablePatternMatch) receiver;
			return match.getParent().getParent().getEditorPart() instanceof DiagramEditor;
		}
		return false;
	}

}
