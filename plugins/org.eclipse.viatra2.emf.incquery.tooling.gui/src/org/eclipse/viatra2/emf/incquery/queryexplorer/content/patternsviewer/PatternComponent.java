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
 * A component inside the pattern hierarchy.
 * 
 * @author Tamas Szabo
 *
 */
public abstract class PatternComponent {

	protected String patternNameFragment;
	protected boolean selected;
	protected PatternComposite parent;
	
	public PatternComponent() {
		selected = false;
	}
	
	/**
	 * Returns the parent element of the component. 
	 * The root component will should return null.
	 * 
	 * @return the parent of the component
	 */
	public PatternComposite getParent() {
		return this.parent;
	}
	
	/**
	 * Sets the selected state of the {@link PatternComponent}.
	 * 
	 * @param selected the selected state
	 */
	public void setSelected(boolean selected) {
		this.selected = selected;
	}
	
	/**
	 * Updates the checked state of the {@link PatternComponent} in the given {@link CheckboxTreeViewer} instance. 
	 * 
	 * @param treeViewer the {@link CheckboxTreeViewer} instance
	 * @return true if all children elements of the {@link PatternComponent} are checked, false otherwise
	 */
	public abstract boolean updateSelection(CheckboxTreeViewer treeViewer);
	
	/**
	 * Returns the prefix of the fully qualified pattern name for the given component. 
	 * 
	 * @return the prefix of the pattern fqn
	 */
	public abstract String getFullPatternNamePrefix();
	
	/**
	 * Returns the fragment inside the fully qualified pattern name for the given component. 
	 * 
	 * @return the pattern fqn fragment
	 */
	public String getPatternNameFragment() {
		return patternNameFragment;
	}
	
	@Override
	public String toString() {
		return patternNameFragment;
	}
}
