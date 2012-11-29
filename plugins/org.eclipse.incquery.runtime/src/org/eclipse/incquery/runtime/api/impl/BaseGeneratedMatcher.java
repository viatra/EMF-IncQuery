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

package org.eclipse.incquery.runtime.api.impl;

import org.eclipse.incquery.patternlanguage.patternLanguage.Pattern;
import org.eclipse.incquery.runtime.api.IMatcherFactory;
import org.eclipse.incquery.runtime.api.IPatternMatch;
import org.eclipse.incquery.runtime.api.IncQueryEngine;
import org.eclipse.incquery.runtime.exception.IncQueryException;
import org.eclipse.incquery.runtime.rete.construction.RetePatternBuildException;
import org.eclipse.incquery.runtime.rete.matcher.RetePatternMatcher;

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
	
	
	@Override
	public Pattern getPattern() {
		return factory.getPattern();
	}
	
	@Override
	public String getPatternName() {
		return factory.getPatternFullyQualifiedName();
	}
	
}
