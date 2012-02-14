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
 * A "lambda" action that can be executed on each match of a pattern.
 * 
 * Clients should derive an (anonymous) class that implements process(). 
 * Alternatively, extend GenericMatchProcessor or the user-friendly generated match processor classes.
 * 
 * @author Bergmann Gábor
 *
 */
public interface IMatchProcessor<Match extends IPatternMatch> {	
	/**
	 * Defines the action that is to be executed on each match. 
	 * @param match a single match of the pattern that must be processed by the implementation of this method
	 */
	public void process(Match match);
}
