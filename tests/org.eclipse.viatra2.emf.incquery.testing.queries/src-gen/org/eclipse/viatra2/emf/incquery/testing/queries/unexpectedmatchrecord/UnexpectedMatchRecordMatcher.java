package org.eclipse.viatra2.emf.incquery.testing.queries.unexpectedmatchrecord;

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
import org.eclipse.viatra2.emf.incquery.snapshot.EIQSnapshot.MatchRecord;
import org.eclipse.viatra2.emf.incquery.snapshot.EIQSnapshot.MatchSetRecord;
import org.eclipse.viatra2.emf.incquery.testing.queries.unexpectedmatchrecord.UnexpectedMatchRecordMatch;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.misc.DeltaMonitor;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.tuple.Tuple;

/**
 * Generated pattern matcher API of the UnexpectedMatchRecord pattern, 
 * providing pattern-specific query methods.
 * 
 * 
 * 
 * pattern UnexpectedMatchRecord(
 * 	ActualSet : MatchSetRecord,
 * 	ExpectedSet : MatchSetRecord,
 * 	Record : MatchRecord
 * ) = {
 * 	MatchSetRecord.matches(ActualSet, Record);
 * 	MatchSetRecord.patternQualifiedName(ActualSet,PatternName);
 * 	MatchSetRecord.patternQualifiedName(ExpectedSet,PatternName);	
 * 	ActualSet != ExpectedSet;
 * 	neg find CorrespondingRecordInMatchSetRecord(Record, CorrespodingRecord, ExpectedSet);
 * }
 * 
 * @see UnexpectedMatchRecordMatch
 * @see UnexpectedMatchRecordMatcherFactory
 * @see UnexpectedMatchRecordProcessor
 * 
 */
public class UnexpectedMatchRecordMatcher extends BaseGeneratedMatcher<UnexpectedMatchRecordMatch> implements IncQueryMatcher<UnexpectedMatchRecordMatch> {
  private final static int POSITION_ACTUALSET = 0;
  
  private final static int POSITION_EXPECTEDSET = 1;
  
  private final static int POSITION_RECORD = 2;
  
  /**
   * Initializes the pattern matcher over a given EMF model root (recommended: Resource or ResourceSet). 
   * If a pattern matcher is already constructed with the same root, only a lightweight reference is created.
   * The scope of pattern matching will be the given EMF model root and below (see FAQ for more precise definition).
   * The match set will be incrementally refreshed upon updates from this scope.
   * @param emfRoot the root of the EMF containment hierarchy where the pattern matcher will operate. Recommended: Resource or ResourceSet.
   * @throws IncQueryRuntimeException if an error occurs during pattern matcher creation
   * 
   */
  public UnexpectedMatchRecordMatcher(final Notifier emfRoot) throws IncQueryRuntimeException {
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
  public UnexpectedMatchRecordMatcher(final IncQueryEngine engine) throws IncQueryRuntimeException {
    super(engine, FACTORY);
  }
  
  /**
   * Returns the set of all matches of the pattern that conform to the given fixed values of some parameters.
   * @param pActualSet the fixed value of pattern parameter ActualSet, or null if not bound.
   * @param pExpectedSet the fixed value of pattern parameter ExpectedSet, or null if not bound.
   * @param pRecord the fixed value of pattern parameter Record, or null if not bound.
   * @return matches represented as a UnexpectedMatchRecordMatch object.
   * 
   */
  public Collection<UnexpectedMatchRecordMatch> getAllMatches(final MatchSetRecord pActualSet, final MatchSetRecord pExpectedSet, final MatchRecord pRecord) {
    return rawGetAllMatches(new Object[]{pActualSet, pExpectedSet, pRecord});
  }
  
  /**
   * Returns an arbitrarily chosen match of the pattern that conforms to the given fixed values of some parameters.
   * Neither determinism nor randomness of selection is guaranteed.
   * @param pActualSet the fixed value of pattern parameter ActualSet, or null if not bound.
   * @param pExpectedSet the fixed value of pattern parameter ExpectedSet, or null if not bound.
   * @param pRecord the fixed value of pattern parameter Record, or null if not bound.
   * @return a match represented as a UnexpectedMatchRecordMatch object, or null if no match is found.
   * 
   */
  public UnexpectedMatchRecordMatch getOneArbitraryMatch(final MatchSetRecord pActualSet, final MatchSetRecord pExpectedSet, final MatchRecord pRecord) {
    return rawGetOneArbitraryMatch(new Object[]{pActualSet, pExpectedSet, pRecord});
  }
  
  /**
   * Indicates whether the given combination of specified pattern parameters constitute a valid pattern match,
   * under any possible substitution of the unspecified parameters (if any).
   * @param pActualSet the fixed value of pattern parameter ActualSet, or null if not bound.
   * @param pExpectedSet the fixed value of pattern parameter ExpectedSet, or null if not bound.
   * @param pRecord the fixed value of pattern parameter Record, or null if not bound.
   * @return true if the input is a valid (partial) match of the pattern.
   * 
   */
  public boolean hasMatch(final MatchSetRecord pActualSet, final MatchSetRecord pExpectedSet, final MatchRecord pRecord) {
    return rawHasMatch(new Object[]{pActualSet, pExpectedSet, pRecord});
  }
  
  /**
   * Returns the number of all matches of the pattern that conform to the given fixed values of some parameters.
   * @param pActualSet the fixed value of pattern parameter ActualSet, or null if not bound.
   * @param pExpectedSet the fixed value of pattern parameter ExpectedSet, or null if not bound.
   * @param pRecord the fixed value of pattern parameter Record, or null if not bound.
   * @return the number of pattern matches found.
   * 
   */
  public int countMatches(final MatchSetRecord pActualSet, final MatchSetRecord pExpectedSet, final MatchRecord pRecord) {
    return rawCountMatches(new Object[]{pActualSet, pExpectedSet, pRecord});
  }
  
  /**
   * Executes the given processor on each match of the pattern that conforms to the given fixed values of some parameters.
   * @param pActualSet the fixed value of pattern parameter ActualSet, or null if not bound.
   * @param pExpectedSet the fixed value of pattern parameter ExpectedSet, or null if not bound.
   * @param pRecord the fixed value of pattern parameter Record, or null if not bound.
   * @param processor the action that will process each pattern match.
   * 
   */
  public void forEachMatch(final MatchSetRecord pActualSet, final MatchSetRecord pExpectedSet, final MatchRecord pRecord, final IMatchProcessor<? super UnexpectedMatchRecordMatch> processor) {
    rawForEachMatch(new Object[]{pActualSet, pExpectedSet, pRecord}, processor);
  }
  
  /**
   * Executes the given processor on an arbitrarily chosen match of the pattern that conforms to the given fixed values of some parameters.  
   * Neither determinism nor randomness of selection is guaranteed.
   * @param pActualSet the fixed value of pattern parameter ActualSet, or null if not bound.
   * @param pExpectedSet the fixed value of pattern parameter ExpectedSet, or null if not bound.
   * @param pRecord the fixed value of pattern parameter Record, or null if not bound.
   * @param processor the action that will process the selected match. 
   * @return true if the pattern has at least one match with the given parameter values, false if the processor was not invoked
   * 
   */
  public boolean forOneArbitraryMatch(final MatchSetRecord pActualSet, final MatchSetRecord pExpectedSet, final MatchRecord pRecord, final IMatchProcessor<? super UnexpectedMatchRecordMatch> processor) {
    return rawForOneArbitraryMatch(new Object[]{pActualSet, pExpectedSet, pRecord}, processor);
  }
  
  /**
   * Registers a new filtered delta monitor on this pattern matcher.
   * The DeltaMonitor can be used to track changes (delta) in the set of filtered pattern matches from now on, considering those matches only that conform to the given fixed values of some parameters. 
   * It can also be reset to track changes from a later point in time, 
   * and changes can even be acknowledged on an individual basis. 
   * See {@link DeltaMonitor} for details.
   * @param fillAtStart if true, all current matches are reported as new match events; if false, the delta monitor starts empty.
   * @param pActualSet the fixed value of pattern parameter ActualSet, or null if not bound.
   * @param pExpectedSet the fixed value of pattern parameter ExpectedSet, or null if not bound.
   * @param pRecord the fixed value of pattern parameter Record, or null if not bound.
   * @return the delta monitor.
   * 
   */
  public DeltaMonitor<UnexpectedMatchRecordMatch> newFilteredDeltaMonitor(final boolean fillAtStart, final MatchSetRecord pActualSet, final MatchSetRecord pExpectedSet, final MatchRecord pRecord) {
    return rawNewFilteredDeltaMonitor(fillAtStart, new Object[]{pActualSet, pExpectedSet, pRecord});
  }
  
  /**
   * Retrieve the set of values that occur in matches for ActualSet.
   * @return the Set of all values, null if no parameter with the given name exists, empty set if there are no matches
   * 
   */
  public Set<MatchSetRecord> rawAccumulateAllValuesOfActualSet(final Object[] parameters) {
    Set<MatchSetRecord> results = new HashSet<MatchSetRecord>();
    rawAccumulateAllValues(POSITION_ACTUALSET, parameters, results);
    return results;
  }
  
  /**
   * Retrieve the set of values that occur in matches for ActualSet.
   * @return the Set of all values, null if no parameter with the given name exists, empty set if there are no matches
   * 
   */
  public Set<MatchSetRecord> getAllValuesOfActualSet() {
    return rawAccumulateAllValuesOfActualSet(emptyArray());
  }
  
  /**
   * Retrieve the set of values that occur in matches for ActualSet.
   * @return the Set of all values, null if no parameter with the given name exists, empty set if there are no matches
   * 
   */
  public Set<MatchSetRecord> getAllValuesOfActualSet(final UnexpectedMatchRecordMatch partialMatch) {
    return rawAccumulateAllValuesOfActualSet(partialMatch.toArray());
  }
  
  /**
   * Retrieve the set of values that occur in matches for ActualSet.
   * @return the Set of all values, null if no parameter with the given name exists, empty set if there are no matches
   * 
   */
  public Set<MatchSetRecord> getAllValuesOfActualSet(final MatchSetRecord pExpectedSet, final MatchRecord pRecord) {
    MatchSetRecord pActualSet = null;
    return rawAccumulateAllValuesOfActualSet(new Object[]{pActualSet, pExpectedSet, pRecord});
  }
  
  /**
   * Retrieve the set of values that occur in matches for ExpectedSet.
   * @return the Set of all values, null if no parameter with the given name exists, empty set if there are no matches
   * 
   */
  public Set<MatchSetRecord> rawAccumulateAllValuesOfExpectedSet(final Object[] parameters) {
    Set<MatchSetRecord> results = new HashSet<MatchSetRecord>();
    rawAccumulateAllValues(POSITION_EXPECTEDSET, parameters, results);
    return results;
  }
  
  /**
   * Retrieve the set of values that occur in matches for ExpectedSet.
   * @return the Set of all values, null if no parameter with the given name exists, empty set if there are no matches
   * 
   */
  public Set<MatchSetRecord> getAllValuesOfExpectedSet() {
    return rawAccumulateAllValuesOfExpectedSet(emptyArray());
  }
  
  /**
   * Retrieve the set of values that occur in matches for ExpectedSet.
   * @return the Set of all values, null if no parameter with the given name exists, empty set if there are no matches
   * 
   */
  public Set<MatchSetRecord> getAllValuesOfExpectedSet(final UnexpectedMatchRecordMatch partialMatch) {
    return rawAccumulateAllValuesOfExpectedSet(partialMatch.toArray());
  }
  
  /**
   * Retrieve the set of values that occur in matches for ExpectedSet.
   * @return the Set of all values, null if no parameter with the given name exists, empty set if there are no matches
   * 
   */
  public Set<MatchSetRecord> getAllValuesOfExpectedSet(final MatchSetRecord pActualSet, final MatchRecord pRecord) {
    MatchSetRecord pExpectedSet = null;
    return rawAccumulateAllValuesOfExpectedSet(new Object[]{pActualSet, pExpectedSet, pRecord});
  }
  
  /**
   * Retrieve the set of values that occur in matches for Record.
   * @return the Set of all values, null if no parameter with the given name exists, empty set if there are no matches
   * 
   */
  public Set<MatchRecord> rawAccumulateAllValuesOfRecord(final Object[] parameters) {
    Set<MatchRecord> results = new HashSet<MatchRecord>();
    rawAccumulateAllValues(POSITION_RECORD, parameters, results);
    return results;
  }
  
  /**
   * Retrieve the set of values that occur in matches for Record.
   * @return the Set of all values, null if no parameter with the given name exists, empty set if there are no matches
   * 
   */
  public Set<MatchRecord> getAllValuesOfRecord() {
    return rawAccumulateAllValuesOfRecord(emptyArray());
  }
  
  /**
   * Retrieve the set of values that occur in matches for Record.
   * @return the Set of all values, null if no parameter with the given name exists, empty set if there are no matches
   * 
   */
  public Set<MatchRecord> getAllValuesOfRecord(final UnexpectedMatchRecordMatch partialMatch) {
    return rawAccumulateAllValuesOfRecord(partialMatch.toArray());
  }
  
  /**
   * Retrieve the set of values that occur in matches for Record.
   * @return the Set of all values, null if no parameter with the given name exists, empty set if there are no matches
   * 
   */
  public Set<MatchRecord> getAllValuesOfRecord(final MatchSetRecord pActualSet, final MatchSetRecord pExpectedSet) {
    MatchRecord pRecord = null;
    return rawAccumulateAllValuesOfRecord(new Object[]{pActualSet, pExpectedSet, pRecord});
  }
  
  @Override
  public UnexpectedMatchRecordMatch tupleToMatch(final Tuple t) {
    try {
    	return new UnexpectedMatchRecordMatch((org.eclipse.viatra2.emf.incquery.snapshot.EIQSnapshot.MatchSetRecord) t.get(POSITION_ACTUALSET), (org.eclipse.viatra2.emf.incquery.snapshot.EIQSnapshot.MatchSetRecord) t.get(POSITION_EXPECTEDSET), (org.eclipse.viatra2.emf.incquery.snapshot.EIQSnapshot.MatchRecord) t.get(POSITION_RECORD));	
    } catch(ClassCastException e) {engine.getLogger().logError("Element(s) in tuple not properly typed!",e);	//throw new IncQueryRuntimeException(e.getMessage());
    	return null;
    }
    
  }
  
  @Override
  public UnexpectedMatchRecordMatch arrayToMatch(final Object[] match) {
    try {
    	return new UnexpectedMatchRecordMatch((org.eclipse.viatra2.emf.incquery.snapshot.EIQSnapshot.MatchSetRecord) match[POSITION_ACTUALSET], (org.eclipse.viatra2.emf.incquery.snapshot.EIQSnapshot.MatchSetRecord) match[POSITION_EXPECTEDSET], (org.eclipse.viatra2.emf.incquery.snapshot.EIQSnapshot.MatchRecord) match[POSITION_RECORD]);
    } catch(ClassCastException e) {engine.getLogger().logError("Element(s) in array not properly typed!",e);	//throw new IncQueryRuntimeException(e.getMessage());
    	return null;
    }
    
  }
  
  @Override
  public UnexpectedMatchRecordMatch newEmptyMatch() {
    return arrayToMatch(new Object[getParameterNames().length]);
  }
  
  public final static IMatcherFactory<UnexpectedMatchRecordMatch,UnexpectedMatchRecordMatcher> FACTORY =  new UnexpectedMatchRecordMatcherFactory();
}
