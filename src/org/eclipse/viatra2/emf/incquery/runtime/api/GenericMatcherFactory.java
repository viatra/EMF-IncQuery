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

import org.eclipse.viatra2.emf.incquery.runtime.api.impl.BaseMatcherFactory;
import org.eclipse.viatra2.emf.incquery.runtime.exception.IncQueryRuntimeException;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.construction.RetePatternBuildException;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.matcher.ReteEngine;

/**
 * This is a generic factory for IncQuery pattern matchers (for which code has been generated).
 * Instantiate the factory with the (fully qualified) name of a registered pattern, 
 *  and then use the factory to obtain an actual pattern matcher for the pattern. 
 *  
 * The created matcher will be of type GenericPatternMatcher. 
 * Matches of the pattern will be represented as GenericPatternSignature. 
 * See also the generated matcher and signature of the pattern, with pattern-specific API simplifications.
 * 
 * @author Bergmann Gábor
 */
@SuppressWarnings("unused")
public class GenericMatcherFactory extends BaseMatcherFactory<GenericPatternSignature, GenericPatternMatcher> 
	implements IMatcherFactory<GenericPatternSignature, GenericPatternMatcher>
{
	String patternName;
	
	/**
	 * Initializes a generic pattern factory for a given pattern.
	 * @param patternName the name of the pattern for which matchers are to be constructed.
	 */
	public GenericMatcherFactory(String patternName) {
		super();
		this.patternName = patternName;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.viatra2.emf.incquery.runtime.api.IMatcherFactory#getPatternName()
	 */
	@Override
	public String getPatternName() {
		return patternName;
	}

	@Override
	public GenericPatternMatcher instantiate(ReteEngine<String> reteEngine) throws IncQueryRuntimeException {
		try {
			return new GenericPatternMatcher(patternName, reteEngine, reteEngine.accessMatcher(getPatternName()));
		} catch (RetePatternBuildException e) {
			throw new IncQueryRuntimeException(e);
		}
	}

}
