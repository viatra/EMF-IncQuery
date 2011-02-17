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

package org.eclipse.viatra2.emf.incquery.runtime.api;

/**
 * Interface for generated pattern signatures.
 * Each instance is a partial substitution of pattern parameters, usable e.g. to represent a match of the pattern.
 * Unsubstituted elements are represented by null.
 * 
 * @author Bergmann Gábor
 */
public interface IPatternSignature extends Cloneable {
	/** Identifies the name of the pattern for which this is a signature. */
	public String patternName();

	/** Returns the list of symbolic parameter names. */
	public String[] parameterNames();
	
	/** Returns the value of the parameter with the given name, or null if name is invalid. */
	public Object get(String parameterName);

	/** Returns the value of the parameter at the given position, or null if position is invalid. */
	public Object get(int position);
	
	/** 
	 * Sets the parameter with the given name to the given value. 
	 * @returns true if successful, false if parameter name is invalid. May also fail and return false if the value type is incompatible. 
	 */
	public boolean set(String parameterName, Object newValue);

	/** 
	 * Sets the parameter at the given position to the given value. 
	 * @returns true if successful, false if position is invalid. May also fail and return false if the value type is incompatible. 
	 */
	public boolean set(int position, Object newValue);	
	
	/** Converts the signature to an array representation, with each pattern parameter at their respective position */
	public abstract Object[] toArray();
}
