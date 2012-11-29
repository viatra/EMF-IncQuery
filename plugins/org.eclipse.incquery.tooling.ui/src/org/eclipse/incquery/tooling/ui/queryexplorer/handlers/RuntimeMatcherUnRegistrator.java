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

import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.incquery.patternlanguage.helper.CorePatternLanguageHelper;
import org.eclipse.incquery.patternlanguage.patternLanguage.Pattern;
import org.eclipse.incquery.tooling.ui.queryexplorer.QueryExplorer;
import org.eclipse.incquery.tooling.ui.queryexplorer.content.matcher.MatcherTreeViewerRoot;
import org.eclipse.incquery.tooling.ui.queryexplorer.content.matcher.ObservablePatternMatcherRoot;
import org.eclipse.incquery.tooling.ui.queryexplorer.util.PatternRegistry;

public class RuntimeMatcherUnRegistrator implements Runnable {

	private final IFile file;
	
	public RuntimeMatcherUnRegistrator(IFile file) {
		this.file = file;
	}

	@Override
	public void run() {		
		MatcherTreeViewerRoot vr = QueryExplorer.getInstance().getMatcherTreeViewerRoot();
		List<Pattern> removedPatterns = PatternRegistry.getInstance().unregisterPatternModel(file);
		for (Pattern pattern : removedPatterns) {
			for (ObservablePatternMatcherRoot root : vr.getRoots()) {
				root.unregisterPattern(pattern);
			}
			QueryExplorer.getInstance().getPatternsViewerInput().getGenericPatternsRoot().removeComponent(CorePatternLanguageHelper.getFullyQualifiedName(pattern));
		}
		
		QueryExplorer.getInstance().getPatternsViewer().refresh();
	}

}