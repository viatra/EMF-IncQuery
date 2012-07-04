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

public class PatternsViewerHierarchicalLabelProvider extends
		PatternsViewerFlatLabelProvider {

	public PatternsViewerHierarchicalLabelProvider(PatternsViewerInput input) {
		super(input);
	}

	@Override
	public String getText(Object element) {
		if (element instanceof PatternComponent) {
			return ((PatternComponent) element).getPatternNameFragment();
		}
		return null;
	}

}
