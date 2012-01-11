/*******************************************************************************
 * Copyright (c) 2004-2010 Gabor Bergmann and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *  Gabor Bergmann - initial API and implementation
 *******************************************************************************/

package org.eclipse.viatra2.emf.incquery.runtime.api.impl;

import org.eclipse.viatra2.emf.incquery.runtime.api.IPatternSignature;
import org.eclipse.viatra2.emf.incquery.runtime.exception.IncQueryRuntimeException;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.construction.RetePatternBuildException;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.matcher.ReteEngine;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.matcher.RetePatternMatcher;

/**
 * Performs the initialization of a BaseMatcher so that it is adapted to the EMF-IncQuery runtime component.
 * @author Bergmann Gábor
 * @param <Signature>
 *
 */
public abstract class BaseGeneratedMatcher<Signature extends IPatternSignature> extends BaseMatcher<Signature> {
	
	static RetePatternMatcher accessMatcher(ReteEngine<String> engine, String patternName) throws IncQueryRuntimeException {
		try {
			return engine.accessMatcher(patternName);
		} catch (RetePatternBuildException e) {
			throw new IncQueryRuntimeException(e);
		}
	}
	
	/**
	 * @param engine
	 * @param patternMatcher
	 * @throws RetePatternBuildException 
	 */
	public BaseGeneratedMatcher(ReteEngine<String> engine, String patternName) throws IncQueryRuntimeException {
		super(engine, accessMatcher(engine, patternName));
	}
	
}
