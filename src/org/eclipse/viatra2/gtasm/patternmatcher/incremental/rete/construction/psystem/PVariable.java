/*******************************************************************************
 * Copyright (c) 2004-2010 Gabor Bergmann and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Gabor Bergmann - initial API and implementation
 *******************************************************************************/

package org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.construction.psystem;

/**
 * @author Bergmann Gábor
 *
 */
public class PVariable {
	/**
	 * The name of the pattern variable. This is the unique key of the pattern node.
	 */
	private String name;
	/**
	 * virtual pVariables are nodes that do not correspond to actual pattern
	 * variables; they represent constants or Term substitutes
	 */
	private boolean virtual;
	
	
	public PVariable(String name) {
		this(name, false);
	}

	public PVariable(String name, boolean virtual) {
		super();
		this.name = name;
		this.virtual = virtual;
	}
	
	/**
	 * @return the name of the pattern variable. This is the unique key of the pattern node.
	 */
	public String getName() {
		return name;
	}
	/**
	 * @return the virtual
	 */
	public boolean isVirtual() {
		return virtual;
	}

	@Override
	public String toString() {
		return name;// + ":PatternNode";
	}
	
	
}
