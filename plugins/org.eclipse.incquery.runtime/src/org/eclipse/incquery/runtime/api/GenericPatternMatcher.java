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

package org.eclipse.incquery.runtime.api;

import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.incquery.patternlanguage.helper.CorePatternLanguageHelper;
import org.eclipse.incquery.patternlanguage.patternLanguage.Pattern;
import org.eclipse.incquery.runtime.api.impl.BaseMatcher;
import org.eclipse.incquery.runtime.exception.IncQueryException;
import org.eclipse.incquery.runtime.rete.construction.RetePatternBuildException;
import org.eclipse.incquery.runtime.rete.matcher.RetePatternMatcher;
import org.eclipse.incquery.runtime.rete.tuple.Tuple;

/**
 * This is a generic pattern matcher for any EMF-IncQuery pattern, with "interpretative" query execution.
 * 
 * <p>When available, consider using the pattern-specific generated matcher API instead.
 * 
 * <p>Matches of the pattern will be represented as GenericPatternMatch. 
 * 
 * @author Bergmann Gábor
 * @see GenericPatternMatch
 * @see GenericMatcherFactory
 * @see GenericMatchProcessor
 */
public class GenericPatternMatcher extends BaseMatcher<GenericPatternMatch> implements IncQueryMatcher<GenericPatternMatch> {

	Pattern pattern;
	
	
	/**
	 * Initializes the pattern matcher over a given EMF model root (recommended: Resource or ResourceSet). 
	 * If a pattern matcher is already constructed with the same root, only a lightweight reference is created.
	 * 
	 * The scope of pattern matching will be the given EMF model root and below (see FAQ for more precise definition).
	 * The match set will be incrementally refreshed upon updates from this scope.
	 * 
	 * <p>The matcher will be created within the managed {@link IncQueryEngine} belonging to the EMF model root, so 
	 *   multiple matchers will reuse the same engine and benefit from increased performance and reduced memory footprint.
	 * 
	 * @param pattern the EMF-IncQuery pattern for which the matcher is to be constructed.
	 * @param emfRoot the root of the EMF containment hierarchy where the pattern matcher will operate. Recommended: Resource or ResourceSet.
	 * @throws IncQueryException if an error occurs during pattern matcher creation
	 */
	public GenericPatternMatcher(Pattern pattern, Notifier emfRoot) 
			throws IncQueryException 
	{
		this(pattern, EngineManager.getInstance().getIncQueryEngine(emfRoot));
	}
	
	/**
	 * Initializes the pattern matcher within an existing EMF-IncQuery engine. 
	 * If the pattern matcher is already constructed in the engine, only a lightweight reference is created.
	 * The match set will be incrementally refreshed upon updates.
	 * @param pattern the EMF-IncQuery pattern for which the matcher is to be constructed.
	 * @param engine the existing EMF-IncQuery engine in which this matcher will be created.
	 * @throws IncQueryException if an error occurs during pattern matcher creation
	 */
	public GenericPatternMatcher(Pattern pattern, IncQueryEngine engine) 
			throws IncQueryException 
	{
		super(engine, accessMatcher(pattern, engine), pattern);
		this.pattern = pattern;		
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.viatra2.emf.incquery.runtime.api.IncQueryMatcher#getPattern()
	 */
	@Override
	public Pattern getPattern() {
		return pattern;
	}
	
	private String fullyQualifiedName;
	/* (non-Javadoc)
	 * @see org.eclipse.viatra2.emf.incquery.runtime.api.IncQueryMatcher#getPatternName()
	 */
	@Override
	public String getPatternName() {
		if (fullyQualifiedName == null) 
			fullyQualifiedName = CorePatternLanguageHelper.getFullyQualifiedName(getPattern());
		return fullyQualifiedName;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.viatra2.emf.incquery.runtime.api.IncQueryMatcher#arrayToMatch(java.lang.Object[])
	 */
	@Override
	public GenericPatternMatch arrayToMatch(Object[] parameters) {
		return new GenericPatternMatch(this, parameters);
	}


	/* (non-Javadoc)
	 * @see org.eclipse.viatra2.emf.incquery.runtime.api.impl.BaseMatcher#tupleToMatch(org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.tuple.Tuple)
	 */
	@Override
	protected GenericPatternMatch tupleToMatch(Tuple t) {
		return new GenericPatternMatch(this, t.getElements());
	}

	private static RetePatternMatcher accessMatcher(Pattern pattern, IncQueryEngine engine) 
		throws IncQueryException 
	{
		checkPattern(engine, pattern);
		try {
			return engine.getReteEngine().accessMatcher(pattern);
		} catch (RetePatternBuildException e) {
			throw new IncQueryException(e);
		}
	}
}
