/*******************************************************************************
 * Copyright (c) 2010-2012, Zoltan Ujhelyi, Abel Hegedus, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Zoltan Ujhelyi, Abel Hegedus - initial API and implementation
 *******************************************************************************/

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
