package org.eclipse.viatra2.emf.incquery.testing.queries.correctrecordsubstitutionvalue;

import java.util.Arrays;
import org.eclipse.viatra2.emf.incquery.runtime.api.IPatternMatch;
import org.eclipse.viatra2.emf.incquery.runtime.api.impl.BasePatternMatch;
import org.eclipse.viatra2.emf.incquery.snapshot.EIQSnapshot.MatchRecord;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Pattern;

/**
 * Pattern-specific match representation of the CorrectRecordSubstitutionValue pattern, 
 * to be used in conjunction with CorrectRecordSubstitutionValueMatcher.
 * 
 * <p>Class fields correspond to parameters of the pattern. Fields with value null are considered unassigned.
 * Each instance is a (possibly partial) substitution of pattern parameters, 
 * usable to represent a match of the pattern in the result of a query, 
 * or to specify the bound (fixed) input parameters when issuing a query.
 * 
 * @see CorrectRecordSubstitutionValueMatcher
 * @see CorrectRecordSubstitutionValueProcessor
 * 
 */
public final class CorrectRecordSubstitutionValueMatch extends BasePatternMatch implements IPatternMatch {
  private MatchRecord fRecord;
  
  private String fParameterName;
  
  private Object fValue;
  
  private static String[] parameterNames = {"Record", "ParameterName", "Value"};
  
  public CorrectRecordSubstitutionValueMatch(final MatchRecord pRecord, final String pParameterName, final Object pValue) {
    this.fRecord = pRecord;
    this.fParameterName = pParameterName;
    this.fValue = pValue;
    
  }
  
  @Override
  public Object get(final String parameterName) {
    if ("Record".equals(parameterName)) return this.fRecord;
    if ("ParameterName".equals(parameterName)) return this.fParameterName;
    if ("Value".equals(parameterName)) return this.fValue;
    return null;
    
  }
  
  public MatchRecord getRecord() {
    return this.fRecord;
    
  }
  
  public String getParameterName() {
    return this.fParameterName;
    
  }
  
  public Object getValue() {
    return this.fValue;
    
  }
  
  @Override
  public boolean set(final String parameterName, final Object newValue) {
    if ("Record".equals(parameterName) && newValue instanceof org.eclipse.viatra2.emf.incquery.snapshot.EIQSnapshot.MatchRecord) {
    	this.fRecord = (org.eclipse.viatra2.emf.incquery.snapshot.EIQSnapshot.MatchRecord) newValue;
    	return true;
    }
    if ("ParameterName".equals(parameterName) && newValue instanceof java.lang.String) {
    	this.fParameterName = (java.lang.String) newValue;
    	return true;
    }
    if ("Value".equals(parameterName) && newValue instanceof java.lang.Object) {
    	this.fValue = (java.lang.Object) newValue;
    	return true;
    }
    return false;
    
  }
  
  public void setRecord(final MatchRecord pRecord) {
    this.fRecord = pRecord;
    
  }
  
  public void setParameterName(final String pParameterName) {
    this.fParameterName = pParameterName;
    
  }
  
  public void setValue(final Object pValue) {
    this.fValue = pValue;
    
  }
  
  @Override
  public String patternName() {
    return "CorrectRecordSubstitutionValue";
    
  }
  
  @Override
  public String[] parameterNames() {
    return CorrectRecordSubstitutionValueMatch.parameterNames;
    
  }
  
  @Override
  public Object[] toArray() {
    return new Object[]{fRecord, fParameterName, fValue};
    
  }
  
  @Override
  public String prettyPrint() {
    StringBuilder result = new StringBuilder();
    result.append("\"Record\"=" + prettyPrintValue(fRecord) + ", ");
    
    result.append("\"ParameterName\"=" + prettyPrintValue(fParameterName) + ", ");
    
    result.append("\"Value\"=" + prettyPrintValue(fValue)
    );return result.toString();
    
  }
  
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((fRecord == null) ? 0 : fRecord.hashCode()); 
    result = prime * result + ((fParameterName == null) ? 0 : fParameterName.hashCode()); 
    result = prime * result + ((fValue == null) ? 0 : fValue.hashCode()); 
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
    if (!CorrectRecordSubstitutionValueMatch.class.equals(obj.getClass()))
    	return Arrays.deepEquals(toArray(), otherSig.toArray());
    				CorrectRecordSubstitutionValueMatch other = (CorrectRecordSubstitutionValueMatch) obj;
    				if (fRecord == null) {if (other.fRecord != null) return false;}
    				else if (!fRecord.equals(other.fRecord)) return false;
    				if (fParameterName == null) {if (other.fParameterName != null) return false;}
    				else if (!fParameterName.equals(other.fParameterName)) return false;
    				if (fValue == null) {if (other.fValue != null) return false;}
    				else if (!fValue.equals(other.fValue)) return false;
    				return true;
    
  }
  
  @Override
  public Pattern pattern() {
    return CorrectRecordSubstitutionValueMatcher.FACTORY.getPattern();
  }
}
