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

package org.eclipse.viatra2.emf.incquery.queryexplorer.content.patternsviewer;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.viatra2.emf.incquery.queryexplorer.QueryExplorer;
import org.eclipse.viatra2.emf.incquery.queryexplorer.util.PatternRegistry;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Pattern;

public class PatternsViewerFlatLabelProvider implements ILabelProvider {

	@Override
	public void addListener(ILabelProviderListener listener) {
		
	}

	@Override
	public void dispose() {
		
	}

	@Override
	public boolean isLabelProperty(Object element, String property) {
		return false;
	}

	@Override
	public void removeListener(ILabelProviderListener listener) {
		
	}

	@Override
	public Image getImage(Object element) {
		return null;
	}

	@Override
	public String getText(Object element) {
		PatternsViewerInput input = QueryExplorer.getInstance().getPatternsViewerInput();
		if (element instanceof PatternComposite) {
			PatternComposite composite = (PatternComposite) element;
			
			if (composite.equals(input.getGeneratedPatternsRoot())) {
				return "Plug-in";
			}
			else if (composite.equals(input.getGenericPatternsRoot())) {
				return "Runtime";
			}
			else {
				return composite.getFullPatternNamePrefix();
			}
		}
		else if (element instanceof PatternLeaf) {
			String name = ((PatternComponent) element).getPatternNameFragment();
			String gen = "";
			String patternFqn = ((PatternComponent) element).getFullPatternNamePrefix();
			Pattern pattern = PatternRegistry.getInstance().getPatternByFqn(patternFqn);
			if (PatternRegistry.getInstance().isGenerated(pattern)) {
				gen += " (plugin)";
			}
			
			return String.format("%s %s", name, gen);
		}
		
		return null;
	}

}
