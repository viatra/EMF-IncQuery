package org.eclipse.viatra2.emf.incquery.testing.queries.incorrectsubstitution;

import java.util.Arrays;
import org.eclipse.viatra2.emf.incquery.runtime.api.IPatternMatch;
import org.eclipse.viatra2.emf.incquery.runtime.api.impl.BasePatternMatch;
import org.eclipse.viatra2.emf.incquery.snapshot.EIQSnapshot.MatchRecord;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Pattern;

/**
 * Pattern-specific match representation of the IncorrectSubstitution pattern, 
 * to be used in conjunction with IncorrectSubstitutionMatcher.
 * 
 * <p>Class fields correspond to parameters of the pattern. Fields with value null are considered unassigned.
 * Each instance is a (possibly partial) substitution of pattern parameters, 
 * usable to represent a match of the pattern in the result of a query, 
 * or to specify the bound (fixed) input parameters when issuing a query.
 * 
 * @see IncorrectSubstitutionMatcher
 * @see IncorrectSubstitutionProcessor
 * 
 */
public final class IncorrectSubstitutionMatch extends BasePatternMatch implements IPatternMatch {
  private MatchRecord fRecord;
  
  private MatchRecord fCorrespondingRecord;
  
  private static String[] parameterNames = {"Record", "CorrespondingRecord"};
  
  public IncorrectSubstitutionMatch(final MatchRecord pRecord, final MatchRecord pCorrespondingRecord) {
    this.fRecord = pRecord;
    this.fCorrespondingRecord = pCorrespondingRecord;
    
  }
  
  @Override
  public Object get(final String parameterName) {
    if ("Record".equals(parameterName)) return this.fRecord;
    if ("CorrespondingRecord".equals(parameterName)) return this.fCorrespondingRecord;
    return null;
    
  }
  
  public MatchRecord getRecord() {
    return this.fRecord;
    
  }
  
  public MatchRecord getCorrespondingRecord() {
    return this.fCorrespondingRecord;
    
  }
  
  @Override
  public boolean set(final String parameterName, final Object newValue) {
    if ("Record".equals(parameterName) && newValue instanceof org.eclipse.viatra2.emf.incquery.snapshot.EIQSnapshot.MatchRecord) {
    	this.fRecord = (org.eclipse.viatra2.emf.incquery.snapshot.EIQSnapshot.MatchRecord) newValue;
    	return true;
    }
    if ("CorrespondingRecord".equals(parameterName) && newValue instanceof org.eclipse.viatra2.emf.incquery.snapshot.EIQSnapshot.MatchRecord) {
    	this.fCorrespondingRecord = (org.eclipse.viatra2.emf.incquery.snapshot.EIQSnapshot.MatchRecord) newValue;
    	return true;
    }
    return false;
    
  }
  
  public void setRecord(final MatchRecord pRecord) {
    this.fRecord = pRecord;
    
  }
  
  public void setCorrespondingRecord(final MatchRecord pCorrespondingRecord) {
    this.fCorrespondingRecord = pCorrespondingRecord;
    
  }
  
  @Override
  public String patternName() {
    return "IncorrectSubstitution";
    
  }
  
  @Override
  public String[] parameterNames() {
    return IncorrectSubstitutionMatch.parameterNames;
    
  }
  
  @Override
  public Object[] toArray() {
    return new Object[]{fRecord, fCorrespondingRecord};
    
  }
  
  @Override
  public String prettyPrint() {
    StringBuilder result = new StringBuilder();
    result.append("\"Record\"=" + prettyPrintValue(fRecord) + ", ");
    
    result.append("\"CorrespondingRecord\"=" + prettyPrintValue(fCorrespondingRecord)
    );return result.toString();
    
  }
  
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((fRecord == null) ? 0 : fRecord.hashCode()); 
    result = prime * result + ((fCorrespondingRecord == null) ? 0 : fCorrespondingRecord.hashCode()); 
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
    if (!IncorrectSubstitutionMatch.class.equals(obj.getClass()))
    	return Arrays.deepEquals(toArray(), otherSig.toArray());
    				IncorrectSubstitutionMatch other = (IncorrectSubstitutionMatch) obj;
    				if (fRecord == null) {if (other.fRecord != null) return false;}
    				else if (!fRecord.equals(other.fRecord)) return false;
    				if (fCorrespondingRecord == null) {if (other.fCorrespondingRecord != null) return false;}
    				else if (!fCorrespondingRecord.equals(other.fCorrespondingRecord)) return false;
    				return true;
    
  }
  
  @Override
  public Pattern pattern() {
    return IncorrectSubstitutionMatcher.FACTORY.getPattern();
  }
}
