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

package org.eclipse.viatra2.compiled.emf.runtime.api;

/**
 * Generic interface for generated pattern signatures.
 * Each instance is a partial substitution of pattern parameters, usable e.g. to represent a match of the pattern.
 * @author Bergmann Gábor
 */
public interface IPatternSignature extends Cloneable {
	// CONVERSION
	/** Converts the signature to an array representation, with each pattern parameter at their respective position */
	public abstract Object[] toArray();
}
