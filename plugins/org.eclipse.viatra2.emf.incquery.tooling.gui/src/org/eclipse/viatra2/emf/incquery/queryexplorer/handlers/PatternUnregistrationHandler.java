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

import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.viatra2.emf.incquery.queryexplorer.QueryExplorer;
import org.eclipse.viatra2.emf.incquery.queryexplorer.content.matcher.ObservablePatternMatcherRoot;
import org.eclipse.viatra2.emf.incquery.queryexplorer.content.patternsviewer.PatternComposite;
import org.eclipse.viatra2.emf.incquery.queryexplorer.content.patternsviewer.PatternLeaf;
import org.eclipse.viatra2.emf.incquery.queryexplorer.util.PatternRegistry;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Pattern;

/**
 * Handler used for pattern unregistration (called from Pattern Registry). 
 * 
 * @author Tamas Szabo
 *
 */
public class PatternUnregistrationHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		TreeSelection selection = (TreeSelection) QueryExplorer.getInstance().getPatternsViewer().getSelection();
		
		for (Object element : selection.toArray()) {	
			if (element instanceof PatternLeaf) {
				PatternLeaf leaf = (PatternLeaf) element;
				PatternComposite composite = (PatternComposite) leaf.getParent();
				unregisterPattern(leaf.getFullPatternNamePrefix());
				QueryExplorer.getInstance().getPatternsViewer().refresh(composite);
			}
			else {
				PatternComposite composite = (PatternComposite) element;
				List<PatternLeaf> leaves = composite.getLeaves();
				for (PatternLeaf leaf : leaves) {
					unregisterPattern(((PatternLeaf) leaf).getFullPatternNamePrefix());
				}
				
				QueryExplorer.getInstance().getPatternsViewerModel().purge();
				QueryExplorer.getInstance().getPatternsViewer().refresh();
			}
		}
		return null;
	}
	
	/**
	 * Unregisters the given pattern both from the QueryExplorer and the Pattern Registry.
	 * 
	 * @param patternFqn the fully qualified name of the pattern
	 */
	private void unregisterPattern(String patternFqn) {
		Pattern pattern = PatternRegistry.getInstance().getPatternByFqn(patternFqn);
		PatternRegistry.getInstance().unregisterPattern(patternFqn);
		QueryExplorer.getInstance().getPatternsViewerModel().removeComponent(patternFqn, false);
		
		//unregister patterns from observable roots
		for (ObservablePatternMatcherRoot root : QueryExplorer.getInstance().getMatcherTreeViewerRoot().getRoots()) {
			root.unregisterPattern(pattern);
		}
		
		//the pattern is not active anymore
		PatternRegistry.getInstance().removeActivePattern(pattern);
	}
}
