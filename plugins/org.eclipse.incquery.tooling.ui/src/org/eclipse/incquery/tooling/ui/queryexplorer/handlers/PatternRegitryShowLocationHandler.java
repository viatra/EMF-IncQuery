/*******************************************************************************
 * Copyright (c) 2010-2012, Zoltan Ujhelyi, Tamas Szabo, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Zoltan Ujhelyi, Tamas Szabo - initial API and implementation
 *******************************************************************************/

package org.eclipse.incquery.tooling.ui.queryexplorer.handlers;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.incquery.patternlanguage.patternLanguage.Pattern;
import org.eclipse.incquery.tooling.ui.queryexplorer.QueryExplorer;
import org.eclipse.incquery.tooling.ui.queryexplorer.content.patternsviewer.PatternLeaf;
import org.eclipse.incquery.tooling.ui.queryexplorer.util.PatternRegistry;
import org.eclipse.jface.viewers.TreeSelection;

/**
 * Show location handler for the pattern registry extension.
 * 
 * Note that the default handler (used in the middle viewer) cannot be used as the selection providers for the
 * QueryExplorer view should be set according to the focus. However, I could not manage to properly listen on focus
 * change events.
 * 
 * @author Tamas Szabo
 * 
 */
public class PatternRegitryShowLocationHandler extends ShowLocationHandler {

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        TreeSelection selection = (TreeSelection) QueryExplorer.getInstance().getPatternsViewer().getSelection();
        Object firstElement = selection.getFirstElement();

        if (firstElement instanceof PatternLeaf) {
            String patternFqn = ((PatternLeaf) firstElement).getFullPatternNamePrefix();
            Pattern pattern = PatternRegistry.getInstance().getPatternByFqn(patternFqn);
            setSelectionToXTextEditor(pattern);
        }

        return null;
    }

}