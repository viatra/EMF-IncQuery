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

import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.viatra2.emf.incquery.runtime.api.impl.BaseMatcher;
import org.eclipse.viatra2.emf.incquery.runtime.exception.IncQueryRuntimeException;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.construction.RetePatternBuildException;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.matcher.RetePatternMatcher;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.tuple.Tuple;
import org.eclipse.viatra2.patternlanguage.core.helper.CorePatternLanguageHelper;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Pattern;

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
@SuppressWarnings("unused")
public class GenericPatternMatcher extends BaseMatcher<GenericPatternMatch> implements IncQueryMatcher<GenericPatternMatch> {

	Pattern pattern;
	
	
	/**
	 * Initializes the pattern matcher over a given EMF model root (recommended: Resource or ResourceSet). 
	 * If a pattern matcher is already constructed with the same root, only a lightweight reference is created.
	 * The match set will be incrementally refreshed upon updates from the given EMF root and below.
	 * 
	 * <p>Note: if emfRoot is a resourceSet, the scope will include even those resources that are not part of the resourceSet but are referenced. 
	 * 	This is mainly to support nsURI-based instance-level references to registered EPackages.
	 * 
	 * @param pattern the EMF-IncQuery pattern for which the matcher is to be constructed.
	 * @param emfRoot the root of the EMF tree where the pattern matcher will operate. Recommended: Resource or ResourceSet.
	 * @throws IncQueryRuntimeException if an error occurs during pattern matcher creation
	 */
	public GenericPatternMatcher(Pattern pattern, Notifier emfRoot) 
			throws IncQueryRuntimeException 
	{
		this(pattern, EngineManager.getInstance().getIncQueryEngine(emfRoot));
	}
	
	/**
	 * Initializes the pattern matcher within an existing EMF-IncQuery engine. 
	 * If the pattern matcher is already constructed in the engine, only a lightweight reference is created.
	 * The match set will be incrementally refreshed upon updates.
	 * @param pattern the EMF-IncQuery pattern for which the matcher is to be constructed.
	 * @param engine the existing EMF-IncQuery engine in which this matcher will be created.
	 * @throws IncQueryRuntimeException if an error occurs during pattern matcher creation
	 */
	public GenericPatternMatcher(Pattern pattern, IncQueryEngine engine) 
			throws IncQueryRuntimeException 
	{
		super(engine, accessMatcher(pattern, engine));
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
		return new GenericPatternMatch(getPatternName(), getParameterNames(), getPosMapping(), parameters);
	}


	/* (non-Javadoc)
	 * @see org.eclipse.viatra2.emf.incquery.runtime.api.impl.BaseMatcher#tupleToMatch(org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.tuple.Tuple)
	 */
	@Override
	protected GenericPatternMatch tupleToMatch(Tuple t) {
		return new GenericPatternMatch(getPatternName(), getParameterNames(), getPosMapping(), t.getElements());
	}

	private static RetePatternMatcher accessMatcher(Pattern pattern, IncQueryEngine engine) 
		throws IncQueryRuntimeException 
	{
		try {
			return engine.getReteEngine().accessMatcher(pattern);
		} catch (RetePatternBuildException e) {
			throw new IncQueryRuntimeException(e);
		}
	}
}
