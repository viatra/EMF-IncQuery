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

/**
 * This class represents a leaf element inside a pattern hierarchy.
 * 
 * @author Tamas Szabo
 *
 */
public class PatternLeaf implements PatternComponent {

	private String patternNameFragment;
	private PatternComposite parent;
	
	public PatternLeaf(String patternNameFragment, PatternComposite parent) {
		this.patternNameFragment = patternNameFragment;
		this.parent = parent;
	}
	
	@Override
	public String getPatternNameFragment() {
		return patternNameFragment;
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
	public PatternComposite getParent() {
		return parent;
	}
}
