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
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.incquery.emf.incquery.runtime.exception.IncQueryException;
import org.eclipse.incquery.gtasm.patternmatcher.incremental.rete.matcher.ReteEngine;
import org.eclipse.incquery.gtasm.patternmatcher.incremental.rete.matcher.RetePatternMatcher;
import org.eclipse.incquery.gtasm.patternmatcher.incremental.rete.misc.DeltaMonitor;
import org.eclipse.incquery.gtasm.patternmatcher.incremental.rete.tuple.Tuple;
import org.eclipse.incquery.patternlanguage.emf.core.patternLanguage.Pattern;
import org.eclipse.incquery.patternlanguage.helper.CorePatternLanguageHelper;
import org.eclipse.viatra2.emf.incquery.base.api.NavigationHelper;
import org.eclipse.viatra2.emf.incquery.runtime.api.IMatchProcessor;
import org.eclipse.viatra2.emf.incquery.runtime.api.IMatchUpdateListener;
import org.eclipse.viatra2.emf.incquery.runtime.api.IPatternMatch;
import org.eclipse.viatra2.emf.incquery.runtime.api.IncQueryEngine;
import org.eclipse.viatra2.emf.incquery.runtime.api.IncQueryMatcher;
import org.eclipse.viatra2.emf.incquery.runtime.internal.boundary.CallbackNode;

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
  protected NavigationHelper baseIndex;

  public BaseMatcher(IncQueryEngine engine, RetePatternMatcher patternMatcher, Pattern pattern) throws IncQueryException {
    super();
    this.engine = engine;
    this.patternMatcher = patternMatcher;
    this.reteEngine = engine.getReteEngine();
    this.baseIndex = engine.getBaseIndex();
  }



  // HELPERS
  
  /**
   * Call this to sanitize the pattern before usage.
   * @throws IncQueryException if the pattern has errors
   */
  protected static void checkPattern(IncQueryEngine engine, Pattern pattern) throws IncQueryException {
    final boolean admissible = engine.getSanitizer().admit(pattern);
    if (!admissible) 
      throw new IncQueryException(
        String.format("Could not initialize matcher for pattern %s because sanity check failed; see Error Log for details.", 
            CorePatternLanguageHelper.getFullyQualifiedName(pattern)), 
        "Pattern contains errors");
  }

  protected abstract Match tupleToMatch(Tuple t);

  private static Object[] fEmptyArray;
  protected Object[] emptyArray() {
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
	 * @see org.eclipse.viatra2.emf.incquery.runtime.api.IncQueryMatcher#addCallbackOnMatchUpdate(org.eclipse.viatra2.emf.incquery.runtime.api.IMatchUpdateListener, boolean)
	 */
	@Override
	public void addCallbackOnMatchUpdate(IMatchUpdateListener<Match> listener, boolean fireNow) {
		final CallbackNode<Match> callbackNode = new CallbackNode<Match>(reteEngine.getReteNet().getHeadContainer(), engine, listener) {
			@Override
			public Match statelessConvert(Tuple t) {
		        return tupleToMatch(t);
			}
		};
	    patternMatcher.connect(callbackNode, listener, fireNow);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.viatra2.emf.incquery.runtime.api.IncQueryMatcher#removeCallbackOnMatchUpdate(org.eclipse.viatra2.emf.incquery.runtime.api.IMatchUpdateListener)
	 */
	@Override
	public void removeCallbackOnMatchUpdate(IMatchUpdateListener<Match> listener) {
		patternMatcher.disconnectByTag(listener);
	}
    
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
    return baseIndex.getAfterUpdateCallbacks().add(callback);
  }

  /* (non-Javadoc)
   * @see org.eclipse.viatra2.emf.incquery.runtime.api.IncQueryMatcher#removeCallbackAfterUpdates(java.lang.Runnable)
   */
  @Override
  public boolean removeCallbackAfterUpdates(Runnable callback) {
    return baseIndex.getAfterUpdateCallbacks().remove(callback);
  }
  
  /* (non-Javadoc)
   * @see org.eclipse.viatra2.emf.incquery.runtime.api.IncQueryMatcher#addCallbackOnWipes(java.lang.Runnable)
   */
  @Override
  public boolean addCallbackAfterWipes(Runnable callback) {
    return engine.getAfterWipeCallbacks().add(callback);
  }
  
  /* (non-Javadoc)
   * @see org.eclipse.viatra2.emf.incquery.runtime.api.IncQueryMatcher#removeCallbackOnWipes(java.lang.Runnable)
   */
  @Override
  public boolean removeCallbackAfterWipes(Runnable callback) {
    return engine.getAfterWipeCallbacks().remove(callback);
  }
  
  /* (non-Javadoc)
   * @see org.eclipse.viatra2.emf.incquery.runtime.api.IncQueryMatcher#matchToArray(org.eclipse.viatra2.emf.incquery.runtime.api.IPatternMatch)
   */
  @Override
  public Object[] matchToArray(Match partialMatch) {
    return partialMatch.toArray();
  }
  
  /* (non-Javadoc)
   * @see org.eclipse.viatra2.emf.incquery.runtime.api.IncQueryMatcher#newEmptyMatch()
   */
  @Override
  public Match newEmptyMatch() {
    return arrayToMatch(new Object[getParameterNames().length]);
  }
  
  /* (non-Javadoc)
   * @see org.eclipse.viatra2.emf.incquery.runtime.api.IncQueryMatcher#newMatch(java.lang.Object[])
   */
  @Override
  public Match newMatch(Object... parameters) {
    return arrayToMatch(parameters);
  }
  
  /* (non-Javadoc)
   * @see org.eclipse.viatra2.emf.incquery.runtime.api.IncQueryMatcher#getAllValues(java.lang.String)
   */
  @Override
  public Set<Object> getAllValues(final String parameterName) {
    return rawGetAllValues(getPositionOfParameter(parameterName), emptyArray());
  }
  
  public Set<Object> getAllValues(final String parameterName, Match partialMatch) {
    return rawGetAllValues(getPositionOfParameter(parameterName), partialMatch.toArray());
  };
  
  /* (non-Javadoc)
   * @see org.eclipse.viatra2.emf.incquery.runtime.api.IncQueryMatcher#rawGetAllValues(java.lang.String, java.lang.Object[])
   */
  @Override
  public Set<Object> rawGetAllValues(final int position, Object[] parameters) {
    if(position >= 0 && position < getParameterNames().length) {
      if(parameters.length == getParameterNames().length) {
        if(parameters[position] == null) {
          final Set<Object> results = new HashSet<Object>();
          rawAccumulateAllValues(position, parameters, results);
          return results;
        }
      }
    }
    return null;
  }
  
  /**
   * Uses an existing set to accumulate all values of the parameter with the given name.
   * Since it is a protected method, no error checking or input validation is performed!
   * 
   * @param position position of the parameter for which values are returned
   * @param parameters a parameter array corresponding to a partial match of the
   *  pattern where each non-null field binds the corresponding pattern parameter to a fixed value.
   * @param accumulator the existing set to fill with the values
   */
  protected <T> void rawAccumulateAllValues(final int position, Object[] parameters, final Set<T> accumulator) {
    rawForEachMatch(parameters, new IMatchProcessor<Match>() {
      /*
       * (non-Javadoc)
       * 
       * @see
       * org.eclipse.viatra2.emf.incquery.runtime.api.IMatchProcessor#process
       * (org.eclipse.viatra2.emf.incquery.runtime.api.IPatternMatch)
       */
      @SuppressWarnings("unchecked")
      @Override
      public void process(Match match) {
        accumulator.add((T) match.get(position));
      }
    });
  }
  
  /* (non-Javadoc)
   * @see org.eclipse.viatra2.emf.incquery.runtime.api.IncQueryMatcher#getEngine()
   */
  @Override
  public IncQueryEngine getEngine() {
    return engine;
  }
}
