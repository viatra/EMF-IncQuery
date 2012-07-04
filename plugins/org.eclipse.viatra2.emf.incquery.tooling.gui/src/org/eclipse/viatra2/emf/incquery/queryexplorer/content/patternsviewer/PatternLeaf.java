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

import org.eclipse.jface.viewers.CheckboxTreeViewer;

/**
 * This class represents a leaf element inside a pattern hierarchy.
 * 
 * @author Tamas Szabo
 *
 */
public class PatternLeaf extends PatternComponent {
	
	public PatternLeaf(String patternNameFragment, PatternComposite parent) {
		super();
		this.patternNameFragment = patternNameFragment;
		this.parent = parent;
	}
	
	@Override
	public String getFullPatternNamePrefix() {
		StringBuilder sb = new StringBuilder(patternNameFragment);
		if (parent != null) {
			sb.insert(0, parent.getFullPatternNamePrefix()+".");
		}
		return sb.toString();
	}

	@Override
	public boolean updateSelection(CheckboxTreeViewer treeViewer) {
		treeViewer.setChecked(this, selected);
		return this.selected;
	}
	
	@Override
	public int hashCode() {
		return patternNameFragment.hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		if(this == obj) {
			return true;
		}
		if((obj == null) || (obj.getClass() != this.getClass())) {
			return false;
		}
		
		PatternLeaf composite = (PatternLeaf) obj;
		
		if ((this.patternNameFragment == composite.patternNameFragment) &&
				(this.parent == composite.parent)) {
			return true;
		}
		
		return false;
	}
}
