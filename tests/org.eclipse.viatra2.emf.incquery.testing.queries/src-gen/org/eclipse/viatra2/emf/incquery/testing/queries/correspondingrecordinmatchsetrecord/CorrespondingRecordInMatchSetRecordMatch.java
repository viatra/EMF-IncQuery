package org.eclipse.viatra2.emf.incquery.testing.queries.correspondingrecordinmatchsetrecord;

import java.util.Arrays;
import org.eclipse.viatra2.emf.incquery.runtime.api.IPatternMatch;
import org.eclipse.viatra2.emf.incquery.runtime.api.impl.BasePatternMatch;
import org.eclipse.viatra2.emf.incquery.snapshot.EIQSnapshot.MatchRecord;
import org.eclipse.viatra2.emf.incquery.snapshot.EIQSnapshot.MatchSetRecord;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Pattern;

/**
 * Pattern-specific match representation of the CorrespondingRecordInMatchSetRecord pattern, 
 * to be used in conjunction with CorrespondingRecordInMatchSetRecordMatcher.
 * 
 * <p>Class fields correspond to parameters of the pattern. Fields with value null are considered unassigned.
 * Each instance is a (possibly partial) substitution of pattern parameters, 
 * usable to represent a match of the pattern in the result of a query, 
 * or to specify the bound (fixed) input parameters when issuing a query.
 * 
 * @see CorrespondingRecordInMatchSetRecordMatcher
 * @see CorrespondingRecordInMatchSetRecordProcessor
 * 
 */
public final class CorrespondingRecordInMatchSetRecordMatch extends BasePatternMatch implements IPatternMatch {
  private MatchRecord fRecord;
  
  private MatchRecord fCorrespodingRecord;
  
  private MatchSetRecord fExpectedSet;
  
  private static String[] parameterNames = {"Record", "CorrespodingRecord", "ExpectedSet"};
  
  public CorrespondingRecordInMatchSetRecordMatch(final MatchRecord pRecord, final MatchRecord pCorrespodingRecord, final MatchSetRecord pExpectedSet) {
    this.fRecord = pRecord;
    this.fCorrespodingRecord = pCorrespodingRecord;
    this.fExpectedSet = pExpectedSet;
    
  }
  
  @Override
  public Object get(final String parameterName) {
    if ("Record".equals(parameterName)) return this.fRecord;
    if ("CorrespodingRecord".equals(parameterName)) return this.fCorrespodingRecord;
    if ("ExpectedSet".equals(parameterName)) return this.fExpectedSet;
    return null;
    
  }
  
  public MatchRecord getRecord() {
    return this.fRecord;
    
  }
  
  public MatchRecord getCorrespodingRecord() {
    return this.fCorrespodingRecord;
    
  }
  
  public MatchSetRecord getExpectedSet() {
    return this.fExpectedSet;
    
  }
  
  @Override
  public boolean set(final String parameterName, final Object newValue) {
    if ("Record".equals(parameterName) && newValue instanceof org.eclipse.viatra2.emf.incquery.snapshot.EIQSnapshot.MatchRecord) {
    	this.fRecord = (org.eclipse.viatra2.emf.incquery.snapshot.EIQSnapshot.MatchRecord) newValue;
    	return true;
    }
    if ("CorrespodingRecord".equals(parameterName) && newValue instanceof org.eclipse.viatra2.emf.incquery.snapshot.EIQSnapshot.MatchRecord) {
    	this.fCorrespodingRecord = (org.eclipse.viatra2.emf.incquery.snapshot.EIQSnapshot.MatchRecord) newValue;
    	return true;
    }
    if ("ExpectedSet".equals(parameterName) && newValue instanceof org.eclipse.viatra2.emf.incquery.snapshot.EIQSnapshot.MatchSetRecord) {
    	this.fExpectedSet = (org.eclipse.viatra2.emf.incquery.snapshot.EIQSnapshot.MatchSetRecord) newValue;
    	return true;
    }
    return false;
    
  }
  
  public void setRecord(final MatchRecord pRecord) {
    this.fRecord = pRecord;
    
  }
  
  public void setCorrespodingRecord(final MatchRecord pCorrespodingRecord) {
    this.fCorrespodingRecord = pCorrespodingRecord;
    
  }
  
  public void setExpectedSet(final MatchSetRecord pExpectedSet) {
    this.fExpectedSet = pExpectedSet;
    
  }
  
  @Override
  public String patternName() {
    return "CorrespondingRecordInMatchSetRecord";
    
  }
  
  @Override
  public String[] parameterNames() {
    return CorrespondingRecordInMatchSetRecordMatch.parameterNames;
    
  }
  
  @Override
  public Object[] toArray() {
    return new Object[]{fRecord, fCorrespodingRecord, fExpectedSet};
    
  }
  
  @Override
  public String prettyPrint() {
    StringBuilder result = new StringBuilder();
    result.append("\"Record\"=" + prettyPrintValue(fRecord) + ", ");
    
    result.append("\"CorrespodingRecord\"=" + prettyPrintValue(fCorrespodingRecord) + ", ");
    
    result.append("\"ExpectedSet\"=" + prettyPrintValue(fExpectedSet)
    );return result.toString();
    
  }
  
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((fRecord == null) ? 0 : fRecord.hashCode()); 
    result = prime * result + ((fCorrespodingRecord == null) ? 0 : fCorrespodingRecord.hashCode()); 
    result = prime * result + ((fExpectedSet == null) ? 0 : fExpectedSet.hashCode()); 
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
    if (!CorrespondingRecordInMatchSetRecordMatch.class.equals(obj.getClass()))
    	return Arrays.deepEquals(toArray(), otherSig.toArray());
    				CorrespondingRecordInMatchSetRecordMatch other = (CorrespondingRecordInMatchSetRecordMatch) obj;
    				if (fRecord == null) {if (other.fRecord != null) return false;}
    				else if (!fRecord.equals(other.fRecord)) return false;
    				if (fCorrespodingRecord == null) {if (other.fCorrespodingRecord != null) return false;}
    				else if (!fCorrespodingRecord.equals(other.fCorrespodingRecord)) return false;
    				if (fExpectedSet == null) {if (other.fExpectedSet != null) return false;}
    				else if (!fExpectedSet.equals(other.fExpectedSet)) return false;
    				return true;
    
  }
  
  @Override
  public Pattern pattern() {
    return CorrespondingRecordInMatchSetRecordMatcher.FACTORY.getPattern();
  }
}
