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

package org.eclipse.incquery.runtime.api.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.incquery.patternlanguage.helper.CorePatternLanguageHelper;
import org.eclipse.incquery.patternlanguage.patternLanguage.Pattern;
import org.eclipse.incquery.runtime.api.IMatchProcessor;
import org.eclipse.incquery.runtime.api.IMatchUpdateListener;
import org.eclipse.incquery.runtime.api.IPatternMatch;
import org.eclipse.incquery.runtime.api.IncQueryEngine;
import org.eclipse.incquery.runtime.api.IncQueryMatcher;
import org.eclipse.incquery.runtime.base.api.NavigationHelper;
import org.eclipse.incquery.runtime.exception.IncQueryException;
import org.eclipse.incquery.runtime.internal.boundary.CallbackNode;
import org.eclipse.incquery.runtime.rete.matcher.ReteEngine;
import org.eclipse.incquery.runtime.rete.matcher.RetePatternMatcher;
import org.eclipse.incquery.runtime.rete.misc.DeltaMonitor;
import org.eclipse.incquery.runtime.rete.tuple.Tuple;

/**
 * Base implementation of IncQueryMatcher.
 * 
 * @author Bergmann Gábor
 * 
 * @param <Match>
 */
public abstract class BaseMatcher<Match extends IPatternMatch> implements IncQueryMatcher<Match> {

    // FIELDS AND CONSTRUCTOR

    protected IncQueryEngine engine;
    protected RetePatternMatcher patternMatcher;
    protected ReteEngine<Pattern> reteEngine;
    protected NavigationHelper baseIndex;

    public BaseMatcher(IncQueryEngine engine, RetePatternMatcher patternMatcher, Pattern pattern)
            throws IncQueryException {
        super();
        this.engine = engine;
        this.patternMatcher = patternMatcher;
        this.reteEngine = engine.getReteEngine();
        this.baseIndex = engine.getBaseIndex();
    }

    // HELPERS

    /**
     * Call this to sanitize the pattern before usage.
     * 
     * @throws IncQueryException
     *             if the pattern has errors
     */
    protected static void checkPattern(IncQueryEngine engine, Pattern pattern) throws IncQueryException {
        final boolean admissible = engine.getSanitizer().admit(pattern);
        if (!admissible)
            throw new IncQueryException(
                    String.format(
                            "Could not initialize matcher for pattern %s because sanity check failed; see Error Log for details.",
                            CorePatternLanguageHelper.getFullyQualifiedName(pattern)), "Pattern contains errors");
    }

    protected abstract Match tupleToMatch(Tuple t);

    private static Object[] fEmptyArray;

    protected Object[] emptyArray() {
        if (fEmptyArray == null)
            fEmptyArray = new Object[getPattern().getParameters().size()];
        return fEmptyArray;
    }

    private boolean[] notNull(Object[] parameters) {
        boolean[] notNull = new boolean[parameters.length];
        for (int i = 0; i < parameters.length; ++i)
            notNull[i] = parameters[i] != null;
        return notNull;
    }

    // REFLECTION

    private Map<String, Integer> posMapping;

    protected Map<String, Integer> getPosMapping() {
        if (posMapping == null) {
            posMapping = CorePatternLanguageHelper.getParameterPositionsByName(getPattern());
        }
        return posMapping;
    }

    @Override
    public Integer getPositionOfParameter(String parameterName) {
        return getPosMapping().get(parameterName);
    }

    private String[] parameterNames;

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

    @Override
    public Collection<Match> getAllMatches() {
        return rawGetAllMatches(emptyArray());
    }

    @Override
    public Collection<Match> rawGetAllMatches(Object[] parameters) {
        ArrayList<Tuple> m = patternMatcher.matchAll(parameters, notNull(parameters));
        ArrayList<Match> matches = new ArrayList<Match>();
        // clones the tuples into a match object to protect the Tuples from modifications outside of the ReteMatcher
        for (Tuple t : m)
            matches.add(tupleToMatch(t));
        return matches;
    }

    @Override
    public Collection<Match> getAllMatches(Match partialMatch) {
        return rawGetAllMatches(partialMatch.toArray());
    }

    // with input binding as pattern-specific parameters: not declared in interface

    @Override
    public Match getOneArbitraryMatch() {
        return rawGetOneArbitraryMatch(emptyArray());
    }

    @Override
    public Match rawGetOneArbitraryMatch(Object[] parameters) {
        Tuple t = patternMatcher.matchOne(parameters, notNull(parameters));
        if (t != null)
            return tupleToMatch(t);
        else
            return null;
    }

    @Override
    public Match getOneArbitraryMatch(Match partialMatch) {
        return rawGetOneArbitraryMatch(partialMatch.toArray());
    }

    // with input binding as pattern-specific parameters: not declared in interface

    @Override
    public boolean rawHasMatch(Object[] parameters) {
        return patternMatcher.count(parameters, notNull(parameters)) > 0;
    }

    @Override
    public boolean hasMatch(Match partialMatch) {
        return rawHasMatch(partialMatch.toArray());
    }

    // with input binding as pattern-specific parameters: not declared in interface

    @Override
    public int countMatches() {
        return rawCountMatches(emptyArray());
    }

    @Override
    public int rawCountMatches(Object[] parameters) {
        return patternMatcher.count(parameters, notNull(parameters));
    }

    @Override
    public int countMatches(Match partialMatch) {
        return rawCountMatches(partialMatch.toArray());
    }

    // with input binding as pattern-specific parameters: not declared in interface

    @Override
    public void rawForEachMatch(Object[] parameters, IMatchProcessor<? super Match> processor) {
        ArrayList<Tuple> m = patternMatcher.matchAll(parameters, notNull(parameters));
        // clones the tuples into match objects to protect the Tuples from modifications outside of the ReteMatcher
        for (Tuple t : m)
            processor.process(tupleToMatch(t));
    }

    @Override
    public void forEachMatch(IMatchProcessor<? super Match> processor) {
        rawForEachMatch(emptyArray(), processor);
    };

    @Override
    public void forEachMatch(Match match, IMatchProcessor<? super Match> processor) {
        rawForEachMatch(match.toArray(), processor);
    };

    // with input binding as pattern-specific parameters: not declared in interface

    @Override
    public boolean forOneArbitraryMatch(IMatchProcessor<? super Match> processor) {
        return rawForOneArbitraryMatch(emptyArray(), processor);
    }

    @Override
    public boolean forOneArbitraryMatch(Match partialMatch, IMatchProcessor<? super Match> processor) {
        return rawForOneArbitraryMatch(partialMatch.toArray(), processor);
    };

    @Override
    public boolean rawForOneArbitraryMatch(Object[] parameters, IMatchProcessor<? super Match> processor) {
        Tuple t = patternMatcher.matchOne(parameters, notNull(parameters));
        if (t != null) {
            processor.process(tupleToMatch(t));
            return true;
        } else {
            return false;
        }
    }

    // with input binding as pattern-specific parameters: not declared in interface

    @Override
    public void addCallbackOnMatchUpdate(IMatchUpdateListener<Match> listener, boolean fireNow) {
        final CallbackNode<Match> callbackNode = new CallbackNode<Match>(reteEngine.getReteNet().getHeadContainer(),
                engine, listener) {
            @Override
            public Match statelessConvert(Tuple t) {
                return tupleToMatch(t);
            }
        };
        patternMatcher.connect(callbackNode, listener, fireNow);
    }

    @Override
    public void removeCallbackOnMatchUpdate(IMatchUpdateListener<Match> listener) {
        patternMatcher.disconnectByTag(listener);
    }

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

    @Override
    public DeltaMonitor<Match> rawNewFilteredDeltaMonitor(boolean fillAtStart, final Object[] parameters) {
        final int length = parameters.length;
        DeltaMonitor<Match> dm = new DeltaMonitor<Match>(reteEngine.getReteNet().getHeadContainer()) {
            @Override
            public boolean statelessFilter(Tuple tuple) {
                for (int i = 0; i < length; ++i) {
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

    @Override
    public DeltaMonitor<Match> newFilteredDeltaMonitor(boolean fillAtStart, Match partialMatch) {
        return rawNewFilteredDeltaMonitor(fillAtStart, partialMatch.toArray());
    }

    @Override
    public boolean addCallbackAfterUpdates(Runnable callback) {
        return baseIndex.getAfterUpdateCallbacks().add(callback);
    }

    @Override
    public boolean removeCallbackAfterUpdates(Runnable callback) {
        return baseIndex.getAfterUpdateCallbacks().remove(callback);
    }

    @Override
    public boolean addCallbackAfterWipes(Runnable callback) {
        return engine.getAfterWipeCallbacks().add(callback);
    }

    @Override
    public boolean removeCallbackAfterWipes(Runnable callback) {
        return engine.getAfterWipeCallbacks().remove(callback);
    }

    @Override
    public Object[] matchToArray(Match partialMatch) {
        return partialMatch.toArray();
    }

    @Override
    public Match newEmptyMatch() {
        return arrayToMatch(new Object[getParameterNames().length]);
    }

    @Override
    public Match newMatch(Object... parameters) {
        return arrayToMatch(parameters);
    }

    @Override
    public Set<Object> getAllValues(final String parameterName) {
        return rawGetAllValues(getPositionOfParameter(parameterName), emptyArray());
    }

    @Override
    public Set<Object> getAllValues(final String parameterName, Match partialMatch) {
        return rawGetAllValues(getPositionOfParameter(parameterName), partialMatch.toArray());
    };

    @Override
    public Set<Object> rawGetAllValues(final int position, Object[] parameters) {
        if (position >= 0 && position < getParameterNames().length) {
            if (parameters.length == getParameterNames().length) {
                if (parameters[position] == null) {
                    final Set<Object> results = new HashSet<Object>();
                    rawAccumulateAllValues(position, parameters, results);
                    return results;
                }
            }
        }
        return null;
    }

    /**
     * Uses an existing set to accumulate all values of the parameter with the given name. Since it is a protected
     * method, no error checking or input validation is performed!
     * 
     * @param position
     *            position of the parameter for which values are returned
     * @param parameters
     *            a parameter array corresponding to a partial match of the pattern where each non-null field binds the
     *            corresponding pattern parameter to a fixed value.
     * @param accumulator
     *            the existing set to fill with the values
     */
    protected <T> void rawAccumulateAllValues(final int position, Object[] parameters, final Set<T> accumulator) {
        rawForEachMatch(parameters, new IMatchProcessor<Match>() {

            @SuppressWarnings("unchecked")
            @Override
            public void process(Match match) {
                accumulator.add((T) match.get(position));
            }
        });
    }

    @Override
    public IncQueryEngine getEngine() {
        return engine;
    }
}
