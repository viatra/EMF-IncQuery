package org.eclipse.viatra2.emf.incquery.testing.queries.matchrecordwithsubstitution;

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
import org.eclipse.viatra2.emf.incquery.testing.queries.matchrecordwithsubstitution.MatchRecordWithSubstitutionMatch;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.misc.DeltaMonitor;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.tuple.Tuple;

/**
 * Generated pattern matcher API of the MatchRecordWithSubstitution pattern, 
 * providing pattern-specific query methods.
 * 
 * 
 * 
 * @Off
 * pattern MatchRecordWithSubstitution(
 * 	Record : MatchRecord,
 * 	ParameterName : EString,
 * 	Substitution : MatchSubstitutionRecord
 * ) = {
 * 	MatchRecord.substitutions(Record,Substitution);
 * 	MatchSubstitutionRecord.parameterName(Substitution, ParameterName);
 * }
 * 
 * @see MatchRecordWithSubstitutionMatch
 * @see MatchRecordWithSubstitutionMatcherFactory
 * @see MatchRecordWithSubstitutionProcessor
 * 
 */
public class MatchRecordWithSubstitutionMatcher extends BaseGeneratedMatcher<MatchRecordWithSubstitutionMatch> implements IncQueryMatcher<MatchRecordWithSubstitutionMatch> {
  private final static int POSITION_RECORD = 0;
  
  private final static int POSITION_PARAMETERNAME = 1;
  
  private final static int POSITION_SUBSTITUTION = 2;
  
  /**
   * Initializes the pattern matcher over a given EMF model root (recommended: Resource or ResourceSet). 
   * If a pattern matcher is already constructed with the same root, only a lightweight reference is created.
   * The scope of pattern matching will be the given EMF model root and below (see FAQ for more precise definition).
   * The match set will be incrementally refreshed upon updates from this scope.
   * @param emfRoot the root of the EMF containment hierarchy where the pattern matcher will operate. Recommended: Resource or ResourceSet.
   * @throws IncQueryRuntimeException if an error occurs during pattern matcher creation
   * 
   */
  public MatchRecordWithSubstitutionMatcher(final Notifier emfRoot) throws IncQueryRuntimeException {
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
  public MatchRecordWithSubstitutionMatcher(final IncQueryEngine engine) throws IncQueryRuntimeException {
    super(engine, FACTORY);
  }
  
  /**
   * Returns the set of all matches of the pattern that conform to the given fixed values of some parameters.
   * @param pRecord the fixed value of pattern parameter Record, or null if not bound.
   * @param pParameterName the fixed value of pattern parameter ParameterName, or null if not bound.
   * @param pSubstitution the fixed value of pattern parameter Substitution, or null if not bound.
   * @return matches represented as a MatchRecordWithSubstitutionMatch object.
   * 
   */
  public Collection<MatchRecordWithSubstitutionMatch> getAllMatches(final Object pRecord, final String pParameterName, final Object pSubstitution) {
    return rawGetAllMatches(new Object[]{pRecord, pParameterName, pSubstitution});
  }
  
  /**
   * Returns an arbitrarily chosen match of the pattern that conforms to the given fixed values of some parameters.
   * Neither determinism nor randomness of selection is guaranteed.
   * @param pRecord the fixed value of pattern parameter Record, or null if not bound.
   * @param pParameterName the fixed value of pattern parameter ParameterName, or null if not bound.
   * @param pSubstitution the fixed value of pattern parameter Substitution, or null if not bound.
   * @return a match represented as a MatchRecordWithSubstitutionMatch object, or null if no match is found.
   * 
   */
  public MatchRecordWithSubstitutionMatch getOneArbitraryMatch(final Object pRecord, final String pParameterName, final Object pSubstitution) {
    return rawGetOneArbitraryMatch(new Object[]{pRecord, pParameterName, pSubstitution});
  }
  
  /**
   * Indicates whether the given combination of specified pattern parameters constitute a valid pattern match,
   * under any possible substitution of the unspecified parameters (if any).
   * @param pRecord the fixed value of pattern parameter Record, or null if not bound.
   * @param pParameterName the fixed value of pattern parameter ParameterName, or null if not bound.
   * @param pSubstitution the fixed value of pattern parameter Substitution, or null if not bound.
   * @return true if the input is a valid (partial) match of the pattern.
   * 
   */
  public boolean hasMatch(final Object pRecord, final String pParameterName, final Object pSubstitution) {
    return rawHasMatch(new Object[]{pRecord, pParameterName, pSubstitution});
  }
  
  /**
   * Returns the number of all matches of the pattern that conform to the given fixed values of some parameters.
   * @param pRecord the fixed value of pattern parameter Record, or null if not bound.
   * @param pParameterName the fixed value of pattern parameter ParameterName, or null if not bound.
   * @param pSubstitution the fixed value of pattern parameter Substitution, or null if not bound.
   * @return the number of pattern matches found.
   * 
   */
  public int countMatches(final Object pRecord, final String pParameterName, final Object pSubstitution) {
    return rawCountMatches(new Object[]{pRecord, pParameterName, pSubstitution});
  }
  
  /**
   * Executes the given processor on each match of the pattern that conforms to the given fixed values of some parameters.
   * @param pRecord the fixed value of pattern parameter Record, or null if not bound.
   * @param pParameterName the fixed value of pattern parameter ParameterName, or null if not bound.
   * @param pSubstitution the fixed value of pattern parameter Substitution, or null if not bound.
   * @param processor the action that will process each pattern match.
   * 
   */
  public void forEachMatch(final Object pRecord, final String pParameterName, final Object pSubstitution, final IMatchProcessor<? super MatchRecordWithSubstitutionMatch> processor) {
    rawForEachMatch(new Object[]{pRecord, pParameterName, pSubstitution}, processor);
  }
  
  /**
   * Executes the given processor on an arbitrarily chosen match of the pattern that conforms to the given fixed values of some parameters.  
   * Neither determinism nor randomness of selection is guaranteed.
   * @param pRecord the fixed value of pattern parameter Record, or null if not bound.
   * @param pParameterName the fixed value of pattern parameter ParameterName, or null if not bound.
   * @param pSubstitution the fixed value of pattern parameter Substitution, or null if not bound.
   * @param processor the action that will process the selected match. 
   * @return true if the pattern has at least one match with the given parameter values, false if the processor was not invoked
   * 
   */
  public boolean forOneArbitraryMatch(final Object pRecord, final String pParameterName, final Object pSubstitution, final IMatchProcessor<? super MatchRecordWithSubstitutionMatch> processor) {
    return rawForOneArbitraryMatch(new Object[]{pRecord, pParameterName, pSubstitution}, processor);
  }
  
  /**
   * Registers a new filtered delta monitor on this pattern matcher.
   * The DeltaMonitor can be used to track changes (delta) in the set of filtered pattern matches from now on, considering those matches only that conform to the given fixed values of some parameters. 
   * It can also be reset to track changes from a later point in time, 
   * and changes can even be acknowledged on an individual basis. 
   * See {@link DeltaMonitor} for details.
   * @param fillAtStart if true, all current matches are reported as new match events; if false, the delta monitor starts empty.
   * @param pRecord the fixed value of pattern parameter Record, or null if not bound.
   * @param pParameterName the fixed value of pattern parameter ParameterName, or null if not bound.
   * @param pSubstitution the fixed value of pattern parameter Substitution, or null if not bound.
   * @return the delta monitor.
   * 
   */
  public DeltaMonitor<MatchRecordWithSubstitutionMatch> newFilteredDeltaMonitor(final boolean fillAtStart, final Object pRecord, final String pParameterName, final Object pSubstitution) {
    return rawNewFilteredDeltaMonitor(fillAtStart, new Object[]{pRecord, pParameterName, pSubstitution});
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
  public Set<Object> getAllValuesOfRecord(final MatchRecordWithSubstitutionMatch partialMatch) {
    return rawAccumulateAllValuesOfRecord(partialMatch.toArray());
  }
  
  /**
   * Retrieve the set of values that occur in matches for Record.
   * @return the Set of all values, null if no parameter with the given name exists, empty set if there are no matches
   * 
   */
  public Set<Object> getAllValuesOfRecord(final String pParameterName, final Object pSubstitution) {
    Object pRecord = null;
    return rawAccumulateAllValuesOfRecord(new Object[]{pRecord, pParameterName, pSubstitution});
  }
  
  /**
   * Retrieve the set of values that occur in matches for ParameterName.
   * @return the Set of all values, null if no parameter with the given name exists, empty set if there are no matches
   * 
   */
  public Set<String> rawAccumulateAllValuesOfParameterName(final Object[] parameters) {
    Set<String> results = new HashSet<String>();
    rawAccumulateAllValues(POSITION_PARAMETERNAME, parameters, results);
    return results;
  }
  
  /**
   * Retrieve the set of values that occur in matches for ParameterName.
   * @return the Set of all values, null if no parameter with the given name exists, empty set if there are no matches
   * 
   */
  public Set<String> getAllValuesOfParameterName() {
    return rawAccumulateAllValuesOfParameterName(emptyArray());
  }
  
  /**
   * Retrieve the set of values that occur in matches for ParameterName.
   * @return the Set of all values, null if no parameter with the given name exists, empty set if there are no matches
   * 
   */
  public Set<String> getAllValuesOfParameterName(final MatchRecordWithSubstitutionMatch partialMatch) {
    return rawAccumulateAllValuesOfParameterName(partialMatch.toArray());
  }
  
  /**
   * Retrieve the set of values that occur in matches for ParameterName.
   * @return the Set of all values, null if no parameter with the given name exists, empty set if there are no matches
   * 
   */
  public Set<String> getAllValuesOfParameterName(final Object pRecord, final Object pSubstitution) {
    String pParameterName = null;
    return rawAccumulateAllValuesOfParameterName(new Object[]{pRecord, pParameterName, pSubstitution});
  }
  
  /**
   * Retrieve the set of values that occur in matches for Substitution.
   * @return the Set of all values, null if no parameter with the given name exists, empty set if there are no matches
   * 
   */
  public Set<Object> rawAccumulateAllValuesOfSubstitution(final Object[] parameters) {
    Set<Object> results = new HashSet<Object>();
    rawAccumulateAllValues(POSITION_SUBSTITUTION, parameters, results);
    return results;
  }
  
  /**
   * Retrieve the set of values that occur in matches for Substitution.
   * @return the Set of all values, null if no parameter with the given name exists, empty set if there are no matches
   * 
   */
  public Set<Object> getAllValuesOfSubstitution() {
    return rawAccumulateAllValuesOfSubstitution(emptyArray());
  }
  
  /**
   * Retrieve the set of values that occur in matches for Substitution.
   * @return the Set of all values, null if no parameter with the given name exists, empty set if there are no matches
   * 
   */
  public Set<Object> getAllValuesOfSubstitution(final MatchRecordWithSubstitutionMatch partialMatch) {
    return rawAccumulateAllValuesOfSubstitution(partialMatch.toArray());
  }
  
  /**
   * Retrieve the set of values that occur in matches for Substitution.
   * @return the Set of all values, null if no parameter with the given name exists, empty set if there are no matches
   * 
   */
  public Set<Object> getAllValuesOfSubstitution(final Object pRecord, final String pParameterName) {
    Object pSubstitution = null;
    return rawAccumulateAllValuesOfSubstitution(new Object[]{pRecord, pParameterName, pSubstitution});
  }
  
  @Override
  public MatchRecordWithSubstitutionMatch tupleToMatch(final Tuple t) {
    try {
    	return new MatchRecordWithSubstitutionMatch((java.lang.Object) t.get(POSITION_RECORD), (java.lang.String) t.get(POSITION_PARAMETERNAME), (java.lang.Object) t.get(POSITION_SUBSTITUTION));	
    } catch(ClassCastException e) {engine.getLogger().logError("Element(s) in tuple not properly typed!",e);	//throw new IncQueryRuntimeException(e.getMessage());
    	return null;
    }
    
  }
  
  @Override
  public MatchRecordWithSubstitutionMatch arrayToMatch(final Object[] match) {
    try {
    	return new MatchRecordWithSubstitutionMatch((java.lang.Object) match[POSITION_RECORD], (java.lang.String) match[POSITION_PARAMETERNAME], (java.lang.Object) match[POSITION_SUBSTITUTION]);
    } catch(ClassCastException e) {engine.getLogger().logError("Element(s) in array not properly typed!",e);	//throw new IncQueryRuntimeException(e.getMessage());
    	return null;
    }
    
  }
  
  @Override
  public MatchRecordWithSubstitutionMatch newEmptyMatch() {
    return arrayToMatch(new Object[getParameterNames().length]);
  }
  
  public final static IMatcherFactory<MatchRecordWithSubstitutionMatch,MatchRecordWithSubstitutionMatcher> FACTORY =  new MatchRecordWithSubstitutionMatcherFactory();
}
