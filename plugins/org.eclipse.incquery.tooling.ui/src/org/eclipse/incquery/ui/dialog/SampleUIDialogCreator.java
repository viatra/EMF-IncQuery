/*******************************************************************************
 * Copyright (c) 2010-2012, Mark Czotter, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Mark Czotter - initial API and implementation
 *******************************************************************************/
package org.eclipse.incquery.ui.dialog;

import java.util.Collection;

import org.eclipse.incquery.runtime.api.IPatternMatch;
import org.eclipse.incquery.runtime.api.IncQueryMatcher;
import org.eclipse.incquery.ui.queryexplorer.util.DatabindingUtil;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;

/**
 * @author Mark Czotter
 *
 */
public class SampleUIDialogCreator {

	/**
	 * Creates a dialog that shows the current matches of the matcher.
	 * @param parent
	 * @return
	 */
	public static final Dialog createDialog(IncQueryMatcher<? extends IPatternMatch> matcher) {
		final String patternFqn = matcher.getPatternName();
		final Collection<? extends IPatternMatch> matches = matcher.getAllMatches();
		final Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
		final ElementTreeSelectionDialog dialog = new ElementTreeSelectionDialog(shell, new PatternMatchDialogLabelProvider(), new PatternMatchDialogContentProvider(matcher, matches));
		dialog.setTitle(String.format("Matchset of the pattern %s", patternFqn));
		dialog.setMessage(DatabindingUtil.getMessage(matcher, matches.size(), patternFqn));
		dialog.setEmptyListMessage("No matches!");
		dialog.setAllowMultiple(false);
		dialog.setDoubleClickSelects(false);
		dialog.setInput(matcher);
		return dialog;
	}
	
}
