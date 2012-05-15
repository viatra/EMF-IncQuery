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

package org.eclipse.viatra2.emf.incquery.runtime.api.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.viatra2.emf.incquery.runtime.api.IMatchProcessor;
import org.eclipse.viatra2.emf.incquery.runtime.api.IPatternMatch;
import org.eclipse.viatra2.emf.incquery.runtime.api.IncQueryEngine;
import org.eclipse.viatra2.emf.incquery.runtime.api.IncQueryMatcher;
import org.eclipse.viatra2.emf.incquery.runtime.exception.IncQueryRuntimeException;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.matcher.ReteEngine;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.matcher.RetePatternMatcher;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.misc.DeltaMonitor;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.tuple.Tuple;
import org.eclipse.viatra2.patternlanguage.core.helper.CorePatternLanguageHelper;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Pattern;

/**
 * Base implementation of IncQueryMatcher.
 * @author Bergmann Gábor
 *
 * @param <Match>
 */
public abstract class BaseMatcher<Match extends IPatternMatch> implements IncQueryMatcher<Match> {

	// FIELDS AND CONSTRUCTOR
	
	protected IncQueryEngine engine;
	protected RetePatternMatcher patternMatcher;
	protected ReteEngine<Pattern> reteEngine;

	public BaseMatcher(IncQueryEngine engine, RetePatternMatcher patternMatcher) throws IncQueryRuntimeException {
		super();
		this.engine = engine;
		this.patternMatcher = patternMatcher;
		this.reteEngine = engine.getReteEngine();
	}


	// HELPERS
	
	protected abstract Match tupleToMatch(Tuple t);

	private static Object[] fEmptyArray;
	private Object[] emptyArray() {
		if (fEmptyArray == null) fEmptyArray = new Object[getPattern().getParameters().size()];
		return fEmptyArray;
	}

	private boolean[] notNull(Object[] parameters) {
		boolean[] notNull = new boolean[parameters.length];
		for (int i=0; i<parameters.length; ++i) notNull[i] = parameters[i] != null;
		return notNull;
	}
	
	// REFLECTION
	
	private Map<String, Integer> posMapping;
	protected Map<String, Integer> getPosMapping() {
		if (posMapping == null)
		{
			posMapping = CorePatternLanguageHelper.getParameterPositionsByName(getPattern());
		}
		return posMapping;
	}	
	/* (non-Javadoc)
	 * @see org.eclipse.viatra2.emf.incquery.runtime.api.IncQueryMatcher#getPositionOfParameter(java.lang.String)
	 */
	@Override
	public Integer getPositionOfParameter(String parameterName) {
		return getPosMapping().get(parameterName);
	}
	
	private String[] parameterNames;
	/* (non-Javadoc)
	 * @see org.eclipse.viatra2.emf.incquery.runtime.api.IncQueryMatcher#getParameterNames()
	 */
	@Override
	public String[] getParameterNames() {
		if (parameterNames == null) {
			Map<String, Integer> rawPosMapping = getPosMapping();
			parameterNames = new String[rawPosMapping.size()];
			for (Entry<String, Integer> entry : rawPosMapping.entrySet()) {
				parameterNames[entry.getValue()] = entry.getKey();
			}
		}
		return parameterNames;
	}

	// BASE IMPLEMENTATION

	/* (non-Javadoc)
	 * @see org.eclipse.viatra2.emf.incquery.runtime.api.IncQueryMatcher#getAllMatches()
	 */
	@Override
	public Collection<Match> getAllMatches() {
		return rawGetAllMatches(emptyArray());
	}

	/* (non-Javadoc)
	 * @see org.eclipse.viatra2.emf.incquery.runtime.api.IncQueryMatcher#rawGetAllMatches(java.lang.Object[])
	 */
	@Override
	public Collection<Match> rawGetAllMatches(Object[] parameters) {
		ArrayList<Tuple> m = patternMatcher.matchAll(parameters, notNull(parameters));
		ArrayList<Match> matches = new ArrayList<Match>();		
		//clones the tuples into a match object to protect the Tuples from modifications outside of the ReteMatcher 
		for(Tuple t: m) matches.add(tupleToMatch(t));
		return matches;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.viatra2.emf.incquery.runtime.api.IncQueryMatcher#getAllMatches(org.eclipse.viatra2.emf.incquery.runtime.api.IPatternMatch)
	 */
	@Override
	public Collection<Match> getAllMatches(Match partialMatch) {
		return rawGetAllMatches(partialMatch.toArray());
	}
	// with input binding as pattern-specific parameters: not declared in interface

	/* (non-Javadoc)
	 * @see org.eclipse.viatra2.emf.incquery.runtime.api.IncQueryMatcher#getOneArbitraryMatch()
	 */
	@Override
	public Match getOneArbitraryMatch() {
		return rawGetOneArbitraryMatch(emptyArray());
	}

	/* (non-Javadoc)
	 * @see org.eclipse.viatra2.emf.incquery.runtime.api.IncQueryMatcher#rawGetOneArbitraryMatch(java.lang.Object[])
	 */
	@Override
	public Match rawGetOneArbitraryMatch(Object[] parameters) {
		Tuple t = patternMatcher.matchOne(parameters, notNull(parameters));
		if(t != null) 
			return tupleToMatch(t);
		else
			return null; 	
	}

	/* (non-Javadoc)
	 * @see org.eclipse.viatra2.emf.incquery.runtime.api.IncQueryMatcher#getOneArbitraryMatch(org.eclipse.viatra2.emf.incquery.runtime.api.IPatternMatch)
	 */
	@Override
	public Match getOneArbitraryMatch(Match partialMatch) {
		return rawGetOneArbitraryMatch(partialMatch.toArray());
	}
	// with input binding as pattern-specific parameters: not declared in interface

	/* (non-Javadoc)
	 * @see org.eclipse.viatra2.emf.incquery.runtime.api.IncQueryMatcher#rawHasMatch(java.lang.Object[])
	 */
	@Override
	public boolean rawHasMatch(Object[] parameters) {
		return patternMatcher.count(parameters, notNull(parameters)) > 0;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.viatra2.emf.incquery.runtime.api.IncQueryMatcher#hasMatch(org.eclipse.viatra2.emf.incquery.runtime.api.IPatternMatch)
	 */
	@Override
	public boolean hasMatch(Match partialMatch) {
		return rawHasMatch(partialMatch.toArray());
	}
	// with input binding as pattern-specific parameters: not declared in interface

	/* (non-Javadoc)
	 * @see org.eclipse.viatra2.emf.incquery.runtime.api.IncQueryMatcher#countMatches()
	 */
	@Override
	public int countMatches() {
		return rawCountMatches(emptyArray());
	}

	/* (non-Javadoc)
	 * @see org.eclipse.viatra2.emf.incquery.runtime.api.IncQueryMatcher#rawCountMatches(java.lang.Object[])
	 */
	@Override
	public int rawCountMatches(Object[] parameters) {
		return patternMatcher.count(parameters, notNull(parameters));
	}

	/* (non-Javadoc)
	 * @see org.eclipse.viatra2.emf.incquery.runtime.api.IncQueryMatcher#countMatches(org.eclipse.viatra2.emf.incquery.runtime.api.IPatternMatch)
	 */
	@Override
	public int countMatches(Match partialMatch) {
		return rawCountMatches(partialMatch.toArray());
	}
	// with input binding as pattern-specific parameters: not declared in interface

	
	/* (non-Javadoc)
	 * @see org.eclipse.viatra2.emf.incquery.runtime.api.IncQueryMatcher#rawForEachMatch(java.lang.Object[], org.eclipse.viatra2.emf.incquery.runtime.api.IMatchProcessor)
	 */
	@Override
	public void rawForEachMatch(Object[] parameters, IMatchProcessor<? super Match> processor) {
		ArrayList<Tuple> m = patternMatcher.matchAll(parameters, notNull(parameters));
		//clones the tuples into match objects to protect the Tuples from modifications outside of the ReteMatcher 
		for(Tuple t: m) processor.process(tupleToMatch(t));
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.viatra2.emf.incquery.runtime.api.IncQueryMatcher#forEachMatch(org.eclipse.viatra2.emf.incquery.runtime.api.IMatchProcessor)
	 */
	@Override
	public void forEachMatch(IMatchProcessor<? super Match> processor) {
		rawForEachMatch(emptyArray(), processor);
	};
	
	/* (non-Javadoc)
	 * @see org.eclipse.viatra2.emf.incquery.runtime.api.IncQueryMatcher#forEachMatch(org.eclipse.viatra2.emf.incquery.runtime.api.IPatternMatch, org.eclipse.viatra2.emf.incquery.runtime.api.IMatchProcessor)
	 */
	@Override
	public void forEachMatch(Match match, IMatchProcessor<? super Match> processor) {
		rawForEachMatch(match.toArray(), processor);
	};
	// with input binding as pattern-specific parameters: not declared in interface
	
	/* (non-Javadoc)
	 * @see org.eclipse.viatra2.emf.incquery.runtime.api.IncQueryMatcher#forOneArbitraryMatch(org.eclipse.viatra2.emf.incquery.runtime.api.IMatchProcessor)
	 */
	@Override
	public boolean forOneArbitraryMatch(IMatchProcessor<? super Match> processor) {
		return rawForOneArbitraryMatch(emptyArray(), processor);
	}	
	
	/* (non-Javadoc)
	 * @see org.eclipse.viatra2.emf.incquery.runtime.api.IncQueryMatcher#forOneArbitraryMatch(org.eclipse.viatra2.emf.incquery.runtime.api.IPatternMatch, org.eclipse.viatra2.emf.incquery.runtime.api.IMatchProcessor)
	 */
	@Override
	public boolean forOneArbitraryMatch(Match partialMatch, IMatchProcessor<? super Match> processor) {
		return rawForOneArbitraryMatch(partialMatch.toArray(), processor);		
	};	
	
	/* (non-Javadoc)
	 * @see org.eclipse.viatra2.emf.incquery.runtime.api.IncQueryMatcher#rawForOneArbitraryMatch(java.lang.Object[], org.eclipse.viatra2.emf.incquery.runtime.api.IMatchProcessor)
	 */
	@Override
	public boolean rawForOneArbitraryMatch(Object[] parameters, IMatchProcessor<? super Match> processor) {
		Tuple t = patternMatcher.matchOne(parameters, notNull(parameters));
		if(t != null) { 
			processor.process(tupleToMatch(t));
			return true;
		} else {
			return false; 	
		}
	}
	// with input binding as pattern-specific parameters: not declared in interface
	
	/* (non-Javadoc)
	 * @see org.eclipse.viatra2.emf.incquery.runtime.api.IncQueryMatcher#newDeltaMonitor(boolean)
	 */
	@Override
	public DeltaMonitor<Match> newDeltaMonitor(boolean fillAtStart) {
		DeltaMonitor<Match> dm = new DeltaMonitor<Match>(reteEngine.getReteNet().getHeadContainer()) {
			@Override
			public Match statelessConvert(Tuple t) {
				return tupleToMatch(t);
			}
		};
		patternMatcher.connect(dm, fillAtStart);
		return dm;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.viatra2.emf.incquery.runtime.api.IncQueryMatcher#rawNewFilteredDeltaMonitor(boolean, java.lang.Object[])
	 */
	@Override
	public DeltaMonitor<Match> rawNewFilteredDeltaMonitor(boolean fillAtStart, final Object[] parameters) {
		final int length = parameters.length;
		DeltaMonitor<Match> dm = new DeltaMonitor<Match>(reteEngine.getReteNet().getHeadContainer()) {
			@Override
			public boolean statelessFilter(Tuple tuple) {
				for (int i=0; i<length; ++i) {
					final Object positionalFilter = parameters[i];
					if (positionalFilter != null && !positionalFilter.equals(tuple.get(i)))
						return false;
				}
				return true;
			}
			@Override
			public Match statelessConvert(Tuple t) {
				return tupleToMatch(t);
			}
		};
		patternMatcher.connect(dm, fillAtStart);
		return dm;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.viatra2.emf.incquery.runtime.api.IncQueryMatcher#newFilteredDeltaMonitor(boolean, org.eclipse.viatra2.emf.incquery.runtime.api.IPatternMatch)
	 */
	@Override
	public DeltaMonitor<Match> newFilteredDeltaMonitor(boolean fillAtStart, Match partialMatch) {
		return rawNewFilteredDeltaMonitor(fillAtStart, partialMatch.toArray());
	}

	/* (non-Javadoc)
	 * @see org.eclipse.viatra2.emf.incquery.runtime.api.IncQueryMatcher#addCallbackAfterUpdates(java.lang.Runnable)
	 */
	@Override
	public boolean addCallbackAfterUpdates(Runnable callback) {
		return reteEngine.getAfterUpdateCallbacks().add(callback);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.viatra2.emf.incquery.runtime.api.IncQueryMatcher#removeCallbackAfterUpdates(java.lang.Runnable)
	 */
	@Override
	public boolean removeCallbackAfterUpdates(Runnable callback) {
		return reteEngine.getAfterUpdateCallbacks().remove(callback);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.viatra2.emf.incquery.runtime.api.IncQueryMatcher#matchToArray(org.eclipse.viatra2.emf.incquery.runtime.api.IPatternMatch)
	 */
	@Override
	public Object[] matchToArray(Match partialMatch) {
		return partialMatch.toArray();
	}
}