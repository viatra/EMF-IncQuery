package org.eclipse.viatra2.emf.incquery.testing.queries.correspondingrecordinmatchsetrecord;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.viatra2.emf.incquery.runtime.api.EngineManager;
import org.eclipse.viatra2.emf.incquery.runtime.api.IMatchProcessor;
import org.eclipse.viatra2.emf.incquery.runtime.api.IMatcherFactory;
import org.eclipse.viatra2.emf.incquery.runtime.api.IncQueryEngine;
import org.eclipse.viatra2.emf.incquery.runtime.api.IncQueryMatcher;
import org.eclipse.viatra2.emf.incquery.runtime.api.impl.BaseGeneratedMatcher;
import org.eclipse.viatra2.emf.incquery.runtime.exception.IncQueryRuntimeException;
import org.eclipse.viatra2.emf.incquery.testing.queries.correspondingrecordinmatchsetrecord.CorrespondingRecordInMatchSetRecordMatch;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.misc.DeltaMonitor;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.tuple.Tuple;

/**
 * Generated pattern matcher API of the CorrespondingRecordInMatchSetRecord pattern, 
 * providing pattern-specific query methods.
 * 
 * 
 * 
 * @Off
 * pattern CorrespondingRecordInMatchSetRecord(
 * 	Record : MatchRecord,
 * 	CorrespodingRecord : MatchRecord,
 * 	ExpectedSet : MatchSetRecord
 * ) = {
 * 	Record != CorrespodingRecord;
 * 	MatchSetRecord.matches(ExpectedSet,CorrespodingRecord);
 * 	find CorrespondingRecords(Record, CorrespodingRecord);
 * }
 * 
 * @see CorrespondingRecordInMatchSetRecordMatch
 * @see CorrespondingRecordInMatchSetRecordMatcherFactory
 * @see CorrespondingRecordInMatchSetRecordProcessor
 * 
 */
public class CorrespondingRecordInMatchSetRecordMatcher extends BaseGeneratedMatcher<CorrespondingRecordInMatchSetRecordMatch> implements IncQueryMatcher<CorrespondingRecordInMatchSetRecordMatch> {
  private final static int POSITION_RECORD = 0;
  
  private final static int POSITION_CORRESPODINGRECORD = 1;
  
  private final static int POSITION_EXPECTEDSET = 2;
  
  /**
   * Initializes the pattern matcher over a given EMF model root (recommended: Resource or ResourceSet). 
   * If a pattern matcher is already constructed with the same root, only a lightweight reference is created.
   * The scope of pattern matching will be the given EMF model root and below (see FAQ for more precise definition).
   * The match set will be incrementally refreshed upon updates from this scope.
   * @param emfRoot the root of the EMF containment hierarchy where the pattern matcher will operate. Recommended: Resource or ResourceSet.
   * @throws IncQueryRuntimeException if an error occurs during pattern matcher creation
   * 
   */
  public CorrespondingRecordInMatchSetRecordMatcher(final Notifier emfRoot) throws IncQueryRuntimeException {
    this(EngineManager.getInstance().getIncQueryEngine(emfRoot));
  }
  
  /**
   * Initializes the pattern matcher within an existing EMF-IncQuery engine. 
   * If the pattern matcher is already constructed in the engine, only a lightweight reference is created.
   * The match set will be incrementally refreshed upon updates.
   * @param engine the existing EMF-IncQuery engine in which this matcher will be created.
   * @throws IncQueryRuntimeException if an error occurs during pattern matcher creation
   * 
   */
  public CorrespondingRecordInMatchSetRecordMatcher(final IncQueryEngine engine) throws IncQueryRuntimeException {
    super(engine, FACTORY);
  }
  
  /**
   * Returns the set of all matches of the pattern that conform to the given fixed values of some parameters.
   * @param pRecord the fixed value of pattern parameter Record, or null if not bound.
   * @param pCorrespodingRecord the fixed value of pattern parameter CorrespodingRecord, or null if not bound.
   * @param pExpectedSet the fixed value of pattern parameter ExpectedSet, or null if not bound.
   * @return matches represented as a CorrespondingRecordInMatchSetRecordMatch object.
   * 
   */
  public Collection<CorrespondingRecordInMatchSetRecordMatch> getAllMatches(final Object pRecord, final Object pCorrespodingRecord, final Object pExpectedSet) {
    return rawGetAllMatches(new Object[]{pRecord, pCorrespodingRecord, pExpectedSet});
  }
  
  /**
   * Returns an arbitrarily chosen match of the pattern that conforms to the given fixed values of some parameters.
   * Neither determinism nor randomness of selection is guaranteed.
   * @param pRecord the fixed value of pattern parameter Record, or null if not bound.
   * @param pCorrespodingRecord the fixed value of pattern parameter CorrespodingRecord, or null if not bound.
   * @param pExpectedSet the fixed value of pattern parameter ExpectedSet, or null if not bound.
   * @return a match represented as a CorrespondingRecordInMatchSetRecordMatch object, or null if no match is found.
   * 
   */
  public CorrespondingRecordInMatchSetRecordMatch getOneArbitraryMatch(final Object pRecord, final Object pCorrespodingRecord, final Object pExpectedSet) {
    return rawGetOneArbitraryMatch(new Object[]{pRecord, pCorrespodingRecord, pExpectedSet});
  }
  
  /**
   * Indicates whether the given combination of specified pattern parameters constitute a valid pattern match,
   * under any possible substitution of the unspecified parameters (if any).
   * @param pRecord the fixed value of pattern parameter Record, or null if not bound.
   * @param pCorrespodingRecord the fixed value of pattern parameter CorrespodingRecord, or null if not bound.
   * @param pExpectedSet the fixed value of pattern parameter ExpectedSet, or null if not bound.
   * @return true if the input is a valid (partial) match of the pattern.
   * 
   */
  public boolean hasMatch(final Object pRecord, final Object pCorrespodingRecord, final Object pExpectedSet) {
    return rawHasMatch(new Object[]{pRecord, pCorrespodingRecord, pExpectedSet});
  }
  
  /**
   * Returns the number of all matches of the pattern that conform to the given fixed values of some parameters.
   * @param pRecord the fixed value of pattern parameter Record, or null if not bound.
   * @param pCorrespodingRecord the fixed value of pattern parameter CorrespodingRecord, or null if not bound.
   * @param pExpectedSet the fixed value of pattern parameter ExpectedSet, or null if not bound.
   * @return the number of pattern matches found.
   * 
   */
  public int countMatches(final Object pRecord, final Object pCorrespodingRecord, final Object pExpectedSet) {
    return rawCountMatches(new Object[]{pRecord, pCorrespodingRecord, pExpectedSet});
  }
  
  /**
   * Executes the given processor on each match of the pattern that conforms to the given fixed values of some parameters.
   * @param pRecord the fixed value of pattern parameter Record, or null if not bound.
   * @param pCorrespodingRecord the fixed value of pattern parameter CorrespodingRecord, or null if not bound.
   * @param pExpectedSet the fixed value of pattern parameter ExpectedSet, or null if not bound.
   * @param processor the action that will process each pattern match.
   * 
   */
  public void forEachMatch(final Object pRecord, final Object pCorrespodingRecord, final Object pExpectedSet, final IMatchProcessor<? super CorrespondingRecordInMatchSetRecordMatch> processor) {
    rawForEachMatch(new Object[]{pRecord, pCorrespodingRecord, pExpectedSet}, processor);
  }
  
  /**
   * Executes the given processor on an arbitrarily chosen match of the pattern that conforms to the given fixed values of some parameters.  
   * Neither determinism nor randomness of selection is guaranteed.
   * @param pRecord the fixed value of pattern parameter Record, or null if not bound.
   * @param pCorrespodingRecord the fixed value of pattern parameter CorrespodingRecord, or null if not bound.
   * @param pExpectedSet the fixed value of pattern parameter ExpectedSet, or null if not bound.
   * @param processor the action that will process the selected match. 
   * @return true if the pattern has at least one match with the given parameter values, false if the processor was not invoked
   * 
   */
  public boolean forOneArbitraryMatch(final Object pRecord, final Object pCorrespodingRecord, final Object pExpectedSet, final IMatchProcessor<? super CorrespondingRecordInMatchSetRecordMatch> processor) {
    return rawForOneArbitraryMatch(new Object[]{pRecord, pCorrespodingRecord, pExpectedSet}, processor);
  }
  
  /**
   * Registers a new filtered delta monitor on this pattern matcher.
   * The DeltaMonitor can be used to track changes (delta) in the set of filtered pattern matches from now on, considering those matches only that conform to the given fixed values of some parameters. 
   * It can also be reset to track changes from a later point in time, 
   * and changes can even be acknowledged on an individual basis. 
   * See {@link DeltaMonitor} for details.
   * @param fillAtStart if true, all current matches are reported as new match events; if false, the delta monitor starts empty.
   * @param pRecord the fixed value of pattern parameter Record, or null if not bound.
   * @param pCorrespodingRecord the fixed value of pattern parameter CorrespodingRecord, or null if not bound.
   * @param pExpectedSet the fixed value of pattern parameter ExpectedSet, or null if not bound.
   * @return the delta monitor.
   * 
   */
  public DeltaMonitor<CorrespondingRecordInMatchSetRecordMatch> newFilteredDeltaMonitor(final boolean fillAtStart, final Object pRecord, final Object pCorrespodingRecord, final Object pExpectedSet) {
    return rawNewFilteredDeltaMonitor(fillAtStart, new Object[]{pRecord, pCorrespodingRecord, pExpectedSet});
  }
  
  /**
   * Retrieve the set of values that occur in matches for Record.
   * @return the Set of all values, null if no parameter with the given name exists, empty set if there are no matches
   * 
   */
  public Set<Object> rawAccumulateAllValuesOfRecord(final Object[] parameters) {
    Set<Object> results = new HashSet<Object>();
    rawAccumulateAllValues(POSITION_RECORD, parameters, results);
    return results;
  }
  
  /**
   * Retrieve the set of values that occur in matches for Record.
   * @return the Set of all values, null if no parameter with the given name exists, empty set if there are no matches
   * 
   */
  public Set<Object> getAllValuesOfRecord() {
    return rawAccumulateAllValuesOfRecord(emptyArray());
  }
  
  /**
   * Retrieve the set of values that occur in matches for Record.
   * @return the Set of all values, null if no parameter with the given name exists, empty set if there are no matches
   * 
   */
  public Set<Object> getAllValuesOfRecord(final CorrespondingRecordInMatchSetRecordMatch partialMatch) {
    return rawAccumulateAllValuesOfRecord(partialMatch.toArray());
  }
  
  /**
   * Retrieve the set of values that occur in matches for Record.
   * @return the Set of all values, null if no parameter with the given name exists, empty set if there are no matches
   * 
   */
  public Set<Object> getAllValuesOfRecord(final Object pCorrespodingRecord, final Object pExpectedSet) {
    Object pRecord = null;
    return rawAccumulateAllValuesOfRecord(new Object[]{pRecord, pCorrespodingRecord, pExpectedSet});
  }
  
  /**
   * Retrieve the set of values that occur in matches for CorrespodingRecord.
   * @return the Set of all values, null if no parameter with the given name exists, empty set if there are no matches
   * 
   */
  public Set<Object> rawAccumulateAllValuesOfCorrespodingRecord(final Object[] parameters) {
    Set<Object> results = new HashSet<Object>();
    rawAccumulateAllValues(POSITION_CORRESPODINGRECORD, parameters, results);
    return results;
  }
  
  /**
   * Retrieve the set of values that occur in matches for CorrespodingRecord.
   * @return the Set of all values, null if no parameter with the given name exists, empty set if there are no matches
   * 
   */
  public Set<Object> getAllValuesOfCorrespodingRecord() {
    return rawAccumulateAllValuesOfCorrespodingRecord(emptyArray());
  }
  
  /**
   * Retrieve the set of values that occur in matches for CorrespodingRecord.
   * @return the Set of all values, null if no parameter with the given name exists, empty set if there are no matches
   * 
   */
  public Set<Object> getAllValuesOfCorrespodingRecord(final CorrespondingRecordInMatchSetRecordMatch partialMatch) {
    return rawAccumulateAllValuesOfCorrespodingRecord(partialMatch.toArray());
  }
  
  /**
   * Retrieve the set of values that occur in matches for CorrespodingRecord.
   * @return the Set of all values, null if no parameter with the given name exists, empty set if there are no matches
   * 
   */
  public Set<Object> getAllValuesOfCorrespodingRecord(final Object pRecord, final Object pExpectedSet) {
    Object pCorrespodingRecord = null;
    return rawAccumulateAllValuesOfCorrespodingRecord(new Object[]{pRecord, pCorrespodingRecord, pExpectedSet});
  }
  
  /**
   * Retrieve the set of values that occur in matches for ExpectedSet.
   * @return the Set of all values, null if no parameter with the given name exists, empty set if there are no matches
   * 
   */
  public Set<Object> rawAccumulateAllValuesOfExpectedSet(final Object[] parameters) {
    Set<Object> results = new HashSet<Object>();
    rawAccumulateAllValues(POSITION_EXPECTEDSET, parameters, results);
    return results;
  }
  
  /**
   * Retrieve the set of values that occur in matches for ExpectedSet.
   * @return the Set of all values, null if no parameter with the given name exists, empty set if there are no matches
   * 
   */
  public Set<Object> getAllValuesOfExpectedSet() {
    return rawAccumulateAllValuesOfExpectedSet(emptyArray());
  }
  
  /**
   * Retrieve the set of values that occur in matches for ExpectedSet.
   * @return the Set of all values, null if no parameter with the given name exists, empty set if there are no matches
   * 
   */
  public Set<Object> getAllValuesOfExpectedSet(final CorrespondingRecordInMatchSetRecordMatch partialMatch) {
    return rawAccumulateAllValuesOfExpectedSet(partialMatch.toArray());
  }
  
  /**
   * Retrieve the set of values that occur in matches for ExpectedSet.
   * @return the Set of all values, null if no parameter with the given name exists, empty set if there are no matches
   * 
   */
  public Set<Object> getAllValuesOfExpectedSet(final Object pRecord, final Object pCorrespodingRecord) {
    Object pExpectedSet = null;
    return rawAccumulateAllValuesOfExpectedSet(new Object[]{pRecord, pCorrespodingRecord, pExpectedSet});
  }
  
  @Override
  public CorrespondingRecordInMatchSetRecordMatch tupleToMatch(final Tuple t) {
    try {
    	return new CorrespondingRecordInMatchSetRecordMatch((java.lang.Object) t.get(POSITION_RECORD), (java.lang.Object) t.get(POSITION_CORRESPODINGRECORD), (java.lang.Object) t.get(POSITION_EXPECTEDSET));	
    } catch(ClassCastException e) {engine.getLogger().logError("Element(s) in tuple not properly typed!",e);	//throw new IncQueryRuntimeException(e.getMessage());
    	return null;
    }
    
  }
  
  @Override
  public CorrespondingRecordInMatchSetRecordMatch arrayToMatch(final Object[] match) {
    try {
    	return new CorrespondingRecordInMatchSetRecordMatch((java.lang.Object) match[POSITION_RECORD], (java.lang.Object) match[POSITION_CORRESPODINGRECORD], (java.lang.Object) match[POSITION_EXPECTEDSET]);
    } catch(ClassCastException e) {engine.getLogger().logError("Element(s) in array not properly typed!",e);	//throw new IncQueryRuntimeException(e.getMessage());
    	return null;
    }
    
  }
  
  @Override
  public CorrespondingRecordInMatchSetRecordMatch newEmptyMatch() {
    return arrayToMatch(new Object[getParameterNames().length]);
  }
  
  public final static IMatcherFactory<CorrespondingRecordInMatchSetRecordMatch,CorrespondingRecordInMatchSetRecordMatcher> FACTORY =  new CorrespondingRecordInMatchSetRecordMatcherFactory();
}
