package org.eclipse.viatra2.emf.incquery.testing.queries.correctrecordsubstitutionvalue;

import org.eclipse.viatra2.emf.incquery.runtime.api.IMatchProcessor;
import org.eclipse.viatra2.emf.incquery.testing.queries.correctrecordsubstitutionvalue.CorrectRecordSubstitutionValueMatch;

/**
 * A match processor tailored for the CorrectRecordSubstitutionValue pattern.
 * 
 * Clients should derive an (anonymous) class that implements the abstract process().
 * 
 */
public abstract class CorrectRecordSubstitutionValueProcessor implements IMatchProcessor<CorrectRecordSubstitutionValueMatch> {
  /**
   * Defines the action that is to be executed on each match.
   * @param pRecord the value of pattern parameter Record in the currently processed match 
   * @param pParameterName the value of pattern parameter ParameterName in the currently processed match 
   * @param pValue the value of pattern parameter Value in the currently processed match 
   * 
   */
  public abstract void process(final Object Record, final String ParameterName, final Object Value);
  
  @Override
  public void process(final CorrectRecordSubstitutionValueMatch match) {
    process(match.getRecord(), match.getParameterName(), match.getValue());  				
    
  }
}
