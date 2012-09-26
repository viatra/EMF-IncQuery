package org.eclipse.viatra2.emf.incquery.testing.queries.substitutionvalue;

import java.util.Arrays;
import org.eclipse.viatra2.emf.incquery.runtime.api.IPatternMatch;
import org.eclipse.viatra2.emf.incquery.runtime.api.impl.BasePatternMatch;
import org.eclipse.viatra2.emf.incquery.runtime.exception.IncQueryException;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Pattern;

/**
 * Pattern-specific match representation of the SubstitutionValue pattern, 
 * to be used in conjunction with SubstitutionValueMatcher.
 * 
 * <p>Class fields correspond to parameters of the pattern. Fields with value null are considered unassigned.
 * Each instance is a (possibly partial) substitution of pattern parameters, 
 * usable to represent a match of the pattern in the result of a query, 
 * or to specify the bound (fixed) input parameters when issuing a query.
 * 
 * @see SubstitutionValueMatcher
 * @see SubstitutionValueProcessor
 * 
 */
public final class SubstitutionValueMatch extends BasePatternMatch {
  private Object fSubstitution;
  
  private Object fValue;
  
  private static String[] parameterNames = {"Substitution", "Value"};
  
  SubstitutionValueMatch(final Object pSubstitution, final Object pValue) {
    this.fSubstitution = pSubstitution;
    this.fValue = pValue;
    
  }
  
  @Override
  public Object get(final String parameterName) {
    if ("Substitution".equals(parameterName)) return this.fSubstitution;
    if ("Value".equals(parameterName)) return this.fValue;
    return null;
    
  }
  
  public Object getSubstitution() {
    return this.fSubstitution;
    
  }
  
  public Object getValue() {
    return this.fValue;
    
  }
  
  @Override
  public boolean set(final String parameterName, final Object newValue) {
    if ("Substitution".equals(parameterName) && newValue instanceof java.lang.Object) {
    	this.fSubstitution = (java.lang.Object) newValue;
    	return true;
    }
    if ("Value".equals(parameterName) && newValue instanceof java.lang.Object) {
    	this.fValue = (java.lang.Object) newValue;
    	return true;
    }
    return false;
    
  }
  
  public void setSubstitution(final Object pSubstitution) {
    this.fSubstitution = pSubstitution;
    
  }
  
  public void setValue(final Object pValue) {
    this.fValue = pValue;
    
  }
  
  @Override
  public String patternName() {
    return "SubstitutionValue";
    
  }
  
  @Override
  public String[] parameterNames() {
    return SubstitutionValueMatch.parameterNames;
    
  }
  
  @Override
  public Object[] toArray() {
    return new Object[]{fSubstitution, fValue};
    
  }
  
  @Override
  public String prettyPrint() {
    StringBuilder result = new StringBuilder();
    result.append("\"Substitution\"=" + prettyPrintValue(fSubstitution) + ", ");
    result.append("\"Value\"=" + prettyPrintValue(fValue));
    return result.toString();
    
  }
  
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((fSubstitution == null) ? 0 : fSubstitution.hashCode()); 
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
    if (!SubstitutionValueMatch.class.equals(obj.getClass()))
    	return Arrays.deepEquals(toArray(), otherSig.toArray());
    SubstitutionValueMatch other = (SubstitutionValueMatch) obj;
    if (fSubstitution == null) {if (other.fSubstitution != null) return false;}
    else if (!fSubstitution.equals(other.fSubstitution)) return false;
    if (fValue == null) {if (other.fValue != null) return false;}
    else if (!fValue.equals(other.fValue)) return false;
    return true;
  }
  
  @Override
  public Pattern pattern() {
    try {
    	return SubstitutionValueMatcher.factory().getPattern();
    } catch (IncQueryException ex) {
     	// This cannot happen, as the match object can only be instantiated if the matcher factory exists
     	throw new IllegalStateException	(ex);
    }
    
  }
}
