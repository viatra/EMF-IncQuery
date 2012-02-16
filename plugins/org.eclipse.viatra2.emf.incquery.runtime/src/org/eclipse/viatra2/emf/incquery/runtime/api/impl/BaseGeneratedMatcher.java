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

import org.eclipse.viatra2.emf.incquery.runtime.api.IMatcherFactory;
import org.eclipse.viatra2.emf.incquery.runtime.api.IPatternMatch;
import org.eclipse.viatra2.emf.incquery.runtime.api.IncQueryEngine;
import org.eclipse.viatra2.emf.incquery.runtime.exception.IncQueryRuntimeException;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.construction.RetePatternBuildException;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.matcher.RetePatternMatcher;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Pattern;

/**
 * Provides common functionality of pattern-specific generated matchers.
 * @author Bergmann Gábor
 * @param <Signature>
 *
 */
public abstract class BaseGeneratedMatcher<Signature extends IPatternMatch> extends BaseMatcher<Signature> {
	
	protected IMatcherFactory<Signature, ? extends BaseGeneratedMatcher<Signature>> factory;
	
	public BaseGeneratedMatcher(
			IncQueryEngine engine, 
			IMatcherFactory<Signature, ? extends BaseGeneratedMatcher<Signature>> factory) 
			throws IncQueryRuntimeException 
	{
		super(engine, accessMatcher(engine, factory.getPattern()));
		this.factory = factory;
	}
	
	static RetePatternMatcher accessMatcher(IncQueryEngine engine, Pattern pattern) throws IncQueryRuntimeException {
		try {
			return engine.getReteEngine().accessMatcher(pattern);
		} catch (RetePatternBuildException e) {
			throw new IncQueryRuntimeException(e);
		}
	}
	
	
	/* (non-Javadoc)
	 * @see org.eclipse.viatra2.emf.incquery.runtime.api.IncQueryMatcher#getPattern()
	 */
	@Override
	public Pattern getPattern() {
		return factory.getPattern();
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.viatra2.emf.incquery.runtime.api.IncQueryMatcher#getPatternName()
	 */
	@Override
	public String getPatternName() {
		return factory.getPatternFullyQualifiedName();
	}
	
}
