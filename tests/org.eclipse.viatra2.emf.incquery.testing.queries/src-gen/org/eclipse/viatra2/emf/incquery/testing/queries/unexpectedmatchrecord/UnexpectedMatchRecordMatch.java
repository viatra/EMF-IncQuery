package org.eclipse.viatra2.emf.incquery.testing.queries.unexpectedmatchrecord;

import java.util.Arrays;
import org.eclipse.viatra2.emf.incquery.runtime.api.IPatternMatch;
import org.eclipse.viatra2.emf.incquery.runtime.api.impl.BasePatternMatch;
import org.eclipse.viatra2.emf.incquery.runtime.exception.IncQueryException;
import org.eclipse.viatra2.emf.incquery.snapshot.EIQSnapshot.MatchRecord;
import org.eclipse.viatra2.emf.incquery.snapshot.EIQSnapshot.MatchSetRecord;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Pattern;

/**
 * Pattern-specific match representation of the UnexpectedMatchRecord pattern, 
 * to be used in conjunction with UnexpectedMatchRecordMatcher.
 * 
 * <p>Class fields correspond to parameters of the pattern. Fields with value null are considered unassigned.
 * Each instance is a (possibly partial) substitution of pattern parameters, 
 * usable to represent a match of the pattern in the result of a query, 
 * or to specify the bound (fixed) input parameters when issuing a query.
 * 
 * @see UnexpectedMatchRecordMatcher
 * @see UnexpectedMatchRecordProcessor
 * 
 */
public final class UnexpectedMatchRecordMatch extends BasePatternMatch {
  private MatchSetRecord fActualSet;
  
  private MatchSetRecord fExpectedSet;
  
  private MatchRecord fRecord;
  
  private static String[] parameterNames = {"ActualSet", "ExpectedSet", "Record"};
  
  UnexpectedMatchRecordMatch(final MatchSetRecord pActualSet, final MatchSetRecord pExpectedSet, final MatchRecord pRecord) {
    this.fActualSet = pActualSet;
    this.fExpectedSet = pExpectedSet;
    this.fRecord = pRecord;
    
  }
  
  @Override
  public Object get(final String parameterName) {
    if ("ActualSet".equals(parameterName)) return this.fActualSet;
    if ("ExpectedSet".equals(parameterName)) return this.fExpectedSet;
    if ("Record".equals(parameterName)) return this.fRecord;
    return null;
    
  }
  
  public MatchSetRecord getActualSet() {
    return this.fActualSet;
    
  }
  
  public MatchSetRecord getExpectedSet() {
    return this.fExpectedSet;
    
  }
  
  public MatchRecord getRecord() {
    return this.fRecord;
    
  }
  
  @Override
  public boolean set(final String parameterName, final Object newValue) {
    if ("ActualSet".equals(parameterName) ) {
    	this.fActualSet = (org.eclipse.viatra2.emf.incquery.snapshot.EIQSnapshot.MatchSetRecord) newValue;
    	return true;
    }
    if ("ExpectedSet".equals(parameterName) ) {
    	this.fExpectedSet = (org.eclipse.viatra2.emf.incquery.snapshot.EIQSnapshot.MatchSetRecord) newValue;
    	return true;
    }
    if ("Record".equals(parameterName) ) {
    	this.fRecord = (org.eclipse.viatra2.emf.incquery.snapshot.EIQSnapshot.MatchRecord) newValue;
    	return true;
    }
    return false;
    
  }
  
  public void setActualSet(final MatchSetRecord pActualSet) {
    this.fActualSet = pActualSet;
    
  }
  
  public void setExpectedSet(final MatchSetRecord pExpectedSet) {
    this.fExpectedSet = pExpectedSet;
    
  }
  
  public void setRecord(final MatchRecord pRecord) {
    this.fRecord = pRecord;
    
  }
  
  @Override
  public String patternName() {
    return "UnexpectedMatchRecord";
    
  }
  
  @Override
  public String[] parameterNames() {
    return UnexpectedMatchRecordMatch.parameterNames;
    
  }
  
  @Override
  public Object[] toArray() {
    return new Object[]{fActualSet, fExpectedSet, fRecord};
    
  }
  
  @Override
  public String prettyPrint() {
    StringBuilder result = new StringBuilder();
    result.append("\"ActualSet\"=" + prettyPrintValue(fActualSet) + ", ");
    result.append("\"ExpectedSet\"=" + prettyPrintValue(fExpectedSet) + ", ");
    result.append("\"Record\"=" + prettyPrintValue(fRecord));
    return result.toString();
    
  }
  
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((fActualSet == null) ? 0 : fActualSet.hashCode()); 
    result = prime * result + ((fExpectedSet == null) ? 0 : fExpectedSet.hashCode()); 
    result = prime * result + ((fRecord == null) ? 0 : fRecord.hashCode()); 
    return result; 
    
  }
  
  @Override
  public boolean equals(final Object obj) {
    if (this == obj)
    	return true;
    if (obj == null)
    	return false;
    if (!(obj instanceof IPatternMatch))
    	return false;
    IPatternMatch otherSig  = (IPatternMatch) obj;
    if (!pattern().equals(otherSig.pattern()))
    	return false;
    if (!UnexpectedMatchRecordMatch.class.equals(obj.getClass()))
    	return Arrays.deepEquals(toArray(), otherSig.toArray());
    UnexpectedMatchRecordMatch other = (UnexpectedMatchRecordMatch) obj;
    if (fActualSet == null) {if (other.fActualSet != null) return false;}
    else if (!fActualSet.equals(other.fActualSet)) return false;
    if (fExpectedSet == null) {if (other.fExpectedSet != null) return false;}
    else if (!fExpectedSet.equals(other.fExpectedSet)) return false;
    if (fRecord == null) {if (other.fRecord != null) return false;}
    else if (!fRecord.equals(other.fRecord)) return false;
    return true;
  }
  
  @Override
  public Pattern pattern() {
    try {
    	return UnexpectedMatchRecordMatcher.factory().getPattern();
    } catch (IncQueryException ex) {
     	// This cannot happen, as the match object can only be instantiated if the matcher factory exists
     	throw new IllegalStateException	(ex);
    }
    
  }
}
