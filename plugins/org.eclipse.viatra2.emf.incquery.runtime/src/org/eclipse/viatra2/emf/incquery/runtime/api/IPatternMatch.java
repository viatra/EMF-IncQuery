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

import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Pattern;


/**
 * Generic interface for a single match of a pattern.
 * Each instance is a (partial) substitution of pattern parameters, essentially a parameter to value mapping.
 * 
 * Can also represent a partial match; unsubstituted parameters are assigned to null. 
 * Pattern matchers must never return a partial match, but they accept partial matches as method parameters. 
 * 
 * @author Bergmann Gábor
 */
public interface IPatternMatch extends Cloneable /*, Map<String, Object>*/ {
	/** @return the pattern for which this is a match. */
	public Pattern pattern();	
	
	/** Identifies the name of the pattern for which this is a match. */
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
	
	/** Converts the match to an array representation, with each pattern parameter at their respective position */
	public Object[] toArray();
	
	/** Prints the list of parameter-value pairs. */
	public String prettyPrint();
}
