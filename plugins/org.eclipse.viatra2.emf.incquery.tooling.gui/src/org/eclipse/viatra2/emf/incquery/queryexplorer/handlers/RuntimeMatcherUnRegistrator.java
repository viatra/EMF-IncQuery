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

package org.eclipse.viatra2.emf.incquery.queryexplorer.handlers;

import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.viatra2.emf.incquery.queryexplorer.QueryExplorer;
import org.eclipse.viatra2.emf.incquery.queryexplorer.content.matcher.MatcherTreeViewerRoot;
import org.eclipse.viatra2.emf.incquery.queryexplorer.content.matcher.ObservablePatternMatcherRoot;
import org.eclipse.viatra2.emf.incquery.queryexplorer.util.PatternRegistry;
import org.eclipse.viatra2.patternlanguage.core.helper.CorePatternLanguageHelper;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Pattern;

public class RuntimeMatcherUnRegistrator implements Runnable {

	private IFile file;
	
	public RuntimeMatcherUnRegistrator(IFile file) {
		this.file = file;
	}

	@Override
	public void run() {		
		MatcherTreeViewerRoot vr = QueryExplorer.getInstance().getMatcherTreeViewerRoot();
		Set<Pattern> removedPatterns = PatternRegistry.getInstance().unregisterPatternModel(file);
		for (Pattern pattern : removedPatterns) {
			for (ObservablePatternMatcherRoot root : vr.getRoots()) {
				root.unregisterPattern(pattern);
			}
			QueryExplorer.getInstance().getPatternsViewerInput().removeComponent(CorePatternLanguageHelper.getFullyQualifiedName(pattern), false);
		}
		
		QueryExplorer.getInstance().getPatternsViewer().refresh();
	}

}