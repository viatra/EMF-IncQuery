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

import org.eclipse.incquery.emf.incquery.runtime.exception.IncQueryException;
import org.eclipse.incquery.gtasm.patternmatcher.incremental.rete.construction.RetePatternBuildException;
import org.eclipse.incquery.gtasm.patternmatcher.incremental.rete.matcher.RetePatternMatcher;
import org.eclipse.incquery.patternlanguage.emf.core.patternLanguage.Pattern;
import org.eclipse.viatra2.emf.incquery.runtime.api.IMatcherFactory;
import org.eclipse.viatra2.emf.incquery.runtime.api.IPatternMatch;
import org.eclipse.viatra2.emf.incquery.runtime.api.IncQueryEngine;

/**
 * Provides common functionality of pattern-specific generated matchers.
 * @author Bergmann Gábor
 * @param <Signature>
 *
 */
public abstract class BaseGeneratedMatcher<Signature extends IPatternMatch> extends BaseMatcher<Signature> {
	
	protected IMatcherFactory<? extends BaseGeneratedMatcher<Signature>> factory;
	
	public BaseGeneratedMatcher(
			IncQueryEngine engine, 
			IMatcherFactory<? extends BaseGeneratedMatcher<Signature>> factory) 
			throws IncQueryException 
	{
		super(engine, accessMatcher(engine, factory.getPattern()), factory.getPattern());
		this.factory = factory;
	}
	
	static RetePatternMatcher accessMatcher(IncQueryEngine engine, Pattern pattern) throws IncQueryException {
		checkPattern(engine, pattern);
		try {
			return engine.getReteEngine().accessMatcher(pattern);
		} catch (RetePatternBuildException e) {
			throw new IncQueryException(e);
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
