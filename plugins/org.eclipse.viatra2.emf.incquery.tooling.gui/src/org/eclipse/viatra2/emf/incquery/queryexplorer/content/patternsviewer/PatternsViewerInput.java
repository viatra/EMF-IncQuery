/*******************************************************************************
 * Copyright (c) 2010-2012, Tamas Szabo, Zoltan Ujhelyi, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Tamas Szabo, Zoltan Ujhelyi - initial API and implementation
 *******************************************************************************/

package org.eclipse.viatra2.emf.incquery.queryexplorer.content.patternsviewer;

public class PatternsViewerInput {

	private PatternComposite generatedPatternsRoot;
	private PatternComposite genericPatternsRoot;
	
	public PatternsViewerInput() {
		this.generatedPatternsRoot = new PatternComposite("Plug-in", null);
		this.genericPatternsRoot = new PatternComposite("Runtime", null);
	}
	
	public PatternComposite getGeneratedPatternsRoot() {
		return generatedPatternsRoot;
	}
	
	public PatternComposite getGenericPatternsRoot() {
		return genericPatternsRoot;
	}
	
	public Object[] getChildren() {
		Object[] children = new Object[2];
		children[0] = generatedPatternsRoot;
		children[1] = genericPatternsRoot;
		return children;
	}
	
	@Override
	public String toString() {
		return this.getClass().getSimpleName();
	}
}
