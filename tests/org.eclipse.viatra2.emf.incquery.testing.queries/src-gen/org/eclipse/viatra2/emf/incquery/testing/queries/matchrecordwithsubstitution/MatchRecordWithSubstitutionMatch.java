package org.eclipse.viatra2.emf.incquery.testing.queries.matchrecordwithsubstitution;

import java.util.Arrays;
import org.eclipse.viatra2.emf.incquery.runtime.api.IPatternMatch;
import org.eclipse.viatra2.emf.incquery.runtime.api.impl.BasePatternMatch;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Pattern;

/**
 * Pattern-specific match representation of the MatchRecordWithSubstitution pattern, 
 * to be used in conjunction with MatchRecordWithSubstitutionMatcher.
 * 
 * <p>Class fields correspond to parameters of the pattern. Fields with value null are considered unassigned.
 * Each instance is a (possibly partial) substitution of pattern parameters, 
 * usable to represent a match of the pattern in the result of a query, 
 * or to specify the bound (fixed) input parameters when issuing a query.
 * 
 * @see MatchRecordWithSubstitutionMatcher
 * @see MatchRecordWithSubstitutionProcessor
 * 
 */
public final class MatchRecordWithSubstitutionMatch extends BasePatternMatch implements IPatternMatch {
  private Object fRecord;
  
  private String fParameterName;
  
  private Object fSubstitution;
  
  private static String[] parameterNames = {"Record", "ParameterName", "Substitution"};
  
  public MatchRecordWithSubstitutionMatch(final Object pRecord, final String pParameterName, final Object pSubstitution) {
    this.fRecord = pRecord;
    this.fParameterName = pParameterName;
    this.fSubstitution = pSubstitution;
    
  }
  
  @Override
  public Object get(final String parameterName) {
    if ("Record".equals(parameterName)) return this.fRecord;
    if ("ParameterName".equals(parameterName)) return this.fParameterName;
    if ("Substitution".equals(parameterName)) return this.fSubstitution;
    return null;
    
  }
  
  public Object getRecord() {
    return this.fRecord;
    
  }
  
  public String getParameterName() {
    return this.fParameterName;
    
  }
  
  public Object getSubstitution() {
    return this.fSubstitution;
    
  }
  
  @Override
  public boolean set(final String parameterName, final Object newValue) {
    if ("Record".equals(parameterName) && newValue instanceof java.lang.Object) {
    	this.fRecord = (java.lang.Object) newValue;
    	return true;
    }
    if ("ParameterName".equals(parameterName) && newValue instanceof java.lang.String) {
    	this.fParameterName = (java.lang.String) newValue;
    	return true;
    }
    if ("Substitution".equals(parameterName) && newValue instanceof java.lang.Object) {
    	this.fSubstitution = (java.lang.Object) newValue;
    	return true;
    }
    return false;
    
  }
  
  public void setRecord(final Object pRecord) {
    this.fRecord = pRecord;
    
  }
  
  public void setParameterName(final String pParameterName) {
    this.fParameterName = pParameterName;
    
  }
  
  public void setSubstitution(final Object pSubstitution) {
    this.fSubstitution = pSubstitution;
    
  }
  
  @Override
  public String patternName() {
    return "MatchRecordWithSubstitution";
    
  }
  
  @Override
  public String[] parameterNames() {
    return MatchRecordWithSubstitutionMatch.parameterNames;
    
  }
  
  @Override
  public Object[] toArray() {
    return new Object[]{fRecord, fParameterName, fSubstitution};
    
  }
  
  @Override
  public String prettyPrint() {
    StringBuilder result = new StringBuilder();
    result.append("\"Record\"=" + prettyPrintValue(fRecord) + ", ");
    
    result.append("\"ParameterName\"=" + prettyPrintValue(fParameterName) + ", ");
    
    result.append("\"Substitution\"=" + prettyPrintValue(fSubstitution)
    );return result.toString();
    
  }
  
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((fRecord == null) ? 0 : fRecord.hashCode()); 
    result = prime * result + ((fParameterName == null) ? 0 : fParameterName.hashCode()); 
    result = prime * result + ((fSubstitution == null) ? 0 : fSubstitution.hashCode()); 
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
    if (!MatchRecordWithSubstitutionMatch.class.equals(obj.getClass()))
    	return Arrays.deepEquals(toArray(), otherSig.toArray());
    				MatchRecordWithSubstitutionMatch other = (MatchRecordWithSubstitutionMatch) obj;
    				if (fRecord == null) {if (other.fRecord != null) return false;}
    				else if (!fRecord.equals(other.fRecord)) return false;
    				if (fParameterName == null) {if (other.fParameterName != null) return false;}
    				else if (!fParameterName.equals(other.fParameterName)) return false;
    				if (fSubstitution == null) {if (other.fSubstitution != null) return false;}
    				else if (!fSubstitution.equals(other.fSubstitution)) return false;
    				return true;
    
  }
  
  @Override
  public Pattern pattern() {
    return MatchRecordWithSubstitutionMatcher.FACTORY.getPattern();
  }
}
