package org.eclipse.viatra2.emf.incquery.validation.runtime.ui.gmf.util;

import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.gmf.runtime.diagram.ui.resources.editor.parts.DiagramDocumentEditor;
import org.eclipse.viatra2.emf.incquery.queryexplorer.content.matcher.ObservablePatternMatch;

public class MatchEditorTester extends PropertyTester {

	@Override
	public boolean test(Object receiver, String property, Object[] args, Object expectedValue) {
		if (receiver instanceof ObservablePatternMatch) {
			ObservablePatternMatch match = (ObservablePatternMatch) receiver;
			return match.getParent().getParent().getEditorPart() instanceof DiagramDocumentEditor;
		}
		return false;
	}

}
