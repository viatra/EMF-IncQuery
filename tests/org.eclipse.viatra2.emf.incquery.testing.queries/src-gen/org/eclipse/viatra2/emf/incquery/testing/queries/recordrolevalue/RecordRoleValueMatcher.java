package org.eclipse.viatra2.emf.incquery.testing.queries.recordrolevalue;

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
import org.eclipse.viatra2.emf.incquery.runtime.exception.IncQueryException;
import org.eclipse.viatra2.emf.incquery.snapshot.EIQSnapshot.MatchRecord;
import org.eclipse.viatra2.emf.incquery.testing.queries.recordrolevalue.RecordRoleValueMatch;
import org.eclipse.viatra2.emf.incquery.testing.queries.recordrolevalue.RecordRoleValueMatcherFactory;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.misc.DeltaMonitor;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.tuple.Tuple;

/**
 * Generated pattern matcher API of the RecordRoleValue pattern, 
 * providing pattern-specific query methods.
 * 
 * 
 * 
 *  
 * 
 * @QueryExplorer(display = false)
 * @DerivedFeature(feature = \"role\")
 * pattern RecordRoleValue(
 * 	Record : MatchRecord,
 * 	Role
 * ) = {
 * 	MatchSetRecord.filter(_MS,Record);
 * 	RecordRole::Filter == Role;
 * } or {
 * 	MatchSetRecord.matches(_MS,Record);
 * 	RecordRole::Match == Role;
 * }
 * 
 * @see RecordRoleValueMatch
 * @see RecordRoleValueMatcherFactory
 * @see RecordRoleValueProcessor
 * 
 */
public class RecordRoleValueMatcher extends BaseGeneratedMatcher<RecordRoleValueMatch> implements IncQueryMatcher<RecordRoleValueMatch> {
  private final static int POSITION_RECORD = 0;
  
  private final static int POSITION_ROLE = 1;
  
  /**
   * Initializes the pattern matcher over a given EMF model root (recommended: Resource or ResourceSet). 
   * If a pattern matcher is already constructed with the same root, only a lightweight reference is created.
   * The scope of pattern matching will be the given EMF model root and below (see FAQ for more precise definition).
   * The match set will be incrementally refreshed upon updates from this scope.
   * @param emfRoot the root of the EMF containment hierarchy where the pattern matcher will operate. Recommended: Resource or ResourceSet.
   * @throws IncQueryException if an error occurs during pattern matcher creation
   * 
   */
  public RecordRoleValueMatcher(final Notifier emfRoot) throws IncQueryException {
    this(EngineManager.getInstance().getIncQueryEngine(emfRoot));
  }
  
  /**
   * Initializes the pattern matcher within an existing EMF-IncQuery engine. 
   * If the pattern matcher is already constructed in the engine, only a lightweight reference is created.
   * The match set will be incrementally refreshed upon updates.
   * @param engine the existing EMF-IncQuery engine in which this matcher will be created.
   * @throws IncQueryException if an error occurs during pattern matcher creation
   * 
   */
  public RecordRoleValueMatcher(final IncQueryEngine engine) throws IncQueryException {
    super(engine, factory());
  }
  
  /**
   * Returns the set of all matches of the pattern that conform to the given fixed values of some parameters.
   * @param pRecord the fixed value of pattern parameter Record, or null if not bound.
   * @param pRole the fixed value of pattern parameter Role, or null if not bound.
   * @return matches represented as a RecordRoleValueMatch object.
   * 
   */
  public Collection<RecordRoleValueMatch> getAllMatches(final MatchRecord pRecord, final Object pRole) {
    return rawGetAllMatches(new Object[]{pRecord, pRole});
  }
  
  /**
   * Returns an arbitrarily chosen match of the pattern that conforms to the given fixed values of some parameters.
   * Neither determinism nor randomness of selection is guaranteed.
   * @param pRecord the fixed value of pattern parameter Record, or null if not bound.
   * @param pRole the fixed value of pattern parameter Role, or null if not bound.
   * @return a match represented as a RecordRoleValueMatch object, or null if no match is found.
   * 
   */
  public RecordRoleValueMatch getOneArbitraryMatch(final MatchRecord pRecord, final Object pRole) {
    return rawGetOneArbitraryMatch(new Object[]{pRecord, pRole});
  }
  
  /**
   * Indicates whether the given combination of specified pattern parameters constitute a valid pattern match,
   * under any possible substitution of the unspecified parameters (if any).
   * @param pRecord the fixed value of pattern parameter Record, or null if not bound.
   * @param pRole the fixed value of pattern parameter Role, or null if not bound.
   * @return true if the input is a valid (partial) match of the pattern.
   * 
   */
  public boolean hasMatch(final MatchRecord pRecord, final Object pRole) {
    return rawHasMatch(new Object[]{pRecord, pRole});
  }
  
  /**
   * Returns the number of all matches of the pattern that conform to the given fixed values of some parameters.
   * @param pRecord the fixed value of pattern parameter Record, or null if not bound.
   * @param pRole the fixed value of pattern parameter Role, or null if not bound.
   * @return the number of pattern matches found.
   * 
   */
  public int countMatches(final MatchRecord pRecord, final Object pRole) {
    return rawCountMatches(new Object[]{pRecord, pRole});
  }
  
  /**
   * Executes the given processor on each match of the pattern that conforms to the given fixed values of some parameters.
   * @param pRecord the fixed value of pattern parameter Record, or null if not bound.
   * @param pRole the fixed value of pattern parameter Role, or null if not bound.
   * @param processor the action that will process each pattern match.
   * 
   */
  public void forEachMatch(final MatchRecord pRecord, final Object pRole, final IMatchProcessor<? super RecordRoleValueMatch> processor) {
    rawForEachMatch(new Object[]{pRecord, pRole}, processor);
  }
  
  /**
   * Executes the given processor on an arbitrarily chosen match of the pattern that conforms to the given fixed values of some parameters.  
   * Neither determinism nor randomness of selection is guaranteed.
   * @param pRecord the fixed value of pattern parameter Record, or null if not bound.
   * @param pRole the fixed value of pattern parameter Role, or null if not bound.
   * @param processor the action that will process the selected match. 
   * @return true if the pattern has at least one match with the given parameter values, false if the processor was not invoked
   * 
   */
  public boolean forOneArbitraryMatch(final MatchRecord pRecord, final Object pRole, final IMatchProcessor<? super RecordRoleValueMatch> processor) {
    return rawForOneArbitraryMatch(new Object[]{pRecord, pRole}, processor);
  }
  
  /**
   * Registers a new filtered delta monitor on this pattern matcher.
   * The DeltaMonitor can be used to track changes (delta) in the set of filtered pattern matches from now on, considering those matches only that conform to the given fixed values of some parameters. 
   * It can also be reset to track changes from a later point in time, 
   * and changes can even be acknowledged on an individual basis. 
   * See {@link DeltaMonitor} for details.
   * @param fillAtStart if true, all current matches are reported as new match events; if false, the delta monitor starts empty.
   * @param pRecord the fixed value of pattern parameter Record, or null if not bound.
   * @param pRole the fixed value of pattern parameter Role, or null if not bound.
   * @return the delta monitor.
   * 
   */
  public DeltaMonitor<RecordRoleValueMatch> newFilteredDeltaMonitor(final boolean fillAtStart, final MatchRecord pRecord, final Object pRole) {
    return rawNewFilteredDeltaMonitor(fillAtStart, new Object[]{pRecord, pRole});
  }
  
  /**
   * Returns a new (partial) Match object for the matcher. 
   * This can be used e.g. to call the matcher with a partial match. 
   * @param pRecord the fixed value of pattern parameter Record, or null if not bound.
   * @param pRole the fixed value of pattern parameter Role, or null if not bound.
   * @return the (partial) match object.
   * 
   */
  public RecordRoleValueMatch newMatch(final MatchRecord pRecord, final Object pRole) {
    return new RecordRoleValueMatch(pRecord, pRole);
    
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
  public Set<MatchRecord> getAllValuesOfRecord(final RecordRoleValueMatch partialMatch) {
    return rawAccumulateAllValuesOfRecord(partialMatch.toArray());
  }
  
  /**
   * Retrieve the set of values that occur in matches for Record.
   * @return the Set of all values, null if no parameter with the given name exists, empty set if there are no matches
   * 
   */
  public Set<MatchRecord> getAllValuesOfRecord(final Object pRole) {
    MatchRecord pRecord = null;
    return rawAccumulateAllValuesOfRecord(new Object[]{pRecord, pRole});
  }
  
  /**
   * Retrieve the set of values that occur in matches for Role.
   * @return the Set of all values, null if no parameter with the given name exists, empty set if there are no matches
   * 
   */
  public Set<Object> rawAccumulateAllValuesOfRole(final Object[] parameters) {
    Set<Object> results = new HashSet<Object>();
    rawAccumulateAllValues(POSITION_ROLE, parameters, results);
    return results;
  }
  
  /**
   * Retrieve the set of values that occur in matches for Role.
   * @return the Set of all values, null if no parameter with the given name exists, empty set if there are no matches
   * 
   */
  public Set<Object> getAllValuesOfRole() {
    return rawAccumulateAllValuesOfRole(emptyArray());
  }
  
  /**
   * Retrieve the set of values that occur in matches for Role.
   * @return the Set of all values, null if no parameter with the given name exists, empty set if there are no matches
   * 
   */
  public Set<Object> getAllValuesOfRole(final RecordRoleValueMatch partialMatch) {
    return rawAccumulateAllValuesOfRole(partialMatch.toArray());
  }
  
  /**
   * Retrieve the set of values that occur in matches for Role.
   * @return the Set of all values, null if no parameter with the given name exists, empty set if there are no matches
   * 
   */
  public Set<Object> getAllValuesOfRole(final MatchRecord pRecord) {
    Object pRole = null;
    return rawAccumulateAllValuesOfRole(new Object[]{pRecord, pRole});
  }
  
  @Override
  public RecordRoleValueMatch tupleToMatch(final Tuple t) {
    try {
    	return new RecordRoleValueMatch((org.eclipse.viatra2.emf.incquery.snapshot.EIQSnapshot.MatchRecord) t.get(POSITION_RECORD), (java.lang.Object) t.get(POSITION_ROLE));	
    } catch(ClassCastException e) {engine.getLogger().error("Element(s) in tuple not properly typed!",e);	//throw new IncQueryRuntimeException(e.getMessage());
    	return null;
    }
    
  }
  
  @Override
  public RecordRoleValueMatch arrayToMatch(final Object[] match) {
    try {
    	return new RecordRoleValueMatch((org.eclipse.viatra2.emf.incquery.snapshot.EIQSnapshot.MatchRecord) match[POSITION_RECORD], (java.lang.Object) match[POSITION_ROLE]);
    } catch(ClassCastException e) {engine.getLogger().error("Element(s) in array not properly typed!",e);	//throw new IncQueryRuntimeException(e.getMessage());
    	return null;
    }
    
  }
  
  /**
   * @return the singleton instance of the factory of this pattern
   * @throws IncQueryException if the pattern definition could not be loaded
   * 
   */
  public static IMatcherFactory<RecordRoleValueMatcher> factory() throws IncQueryException {
    return RecordRoleValueMatcherFactory.instance();
  }
}
