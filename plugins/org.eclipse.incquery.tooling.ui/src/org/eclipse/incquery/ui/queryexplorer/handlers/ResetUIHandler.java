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

package org.eclipse.incquery.ui.queryexplorer.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.incquery.patternlanguage.helper.CorePatternLanguageHelper;
import org.eclipse.incquery.patternlanguage.patternLanguage.Pattern;
import org.eclipse.incquery.ui.queryexplorer.QueryExplorer;
import org.eclipse.incquery.ui.queryexplorer.handlers.util.ModelConnector;
import org.eclipse.incquery.ui.queryexplorer.util.PatternRegistry;

public class ResetUIHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		QueryExplorer explorer = QueryExplorer.getInstance();
		
		if (explorer != null) {
			for (ModelConnector connector : explorer.getModelConnectorMap().values()) {
				connector.unloadModel();
			}
			for (Pattern pattern : PatternRegistry.getInstance().getActivePatterns()) {
				String patternFqn = CorePatternLanguageHelper.getFullyQualifiedName(pattern);
				PatternRegistry.getInstance().unregisterPattern(pattern);
				PatternRegistry.getInstance().removeActivePattern(pattern);
				explorer.getPatternsViewerInput().getGenericPatternsRoot().removeComponent(patternFqn);
			}
		
			//refresh selection
			explorer.getPatternsViewerInput().getGenericPatternsRoot().updateSelection(explorer.getPatternsViewer());
			explorer.getPatternsViewer().refresh();
		}
		return null;
	}
}
