package org.eclipse.viatra2.emf.incquery.testing.queries.matchrecordwithsubstitution;

import org.eclipse.viatra2.emf.incquery.runtime.api.IMatchProcessor;
import org.eclipse.viatra2.emf.incquery.snapshot.EIQSnapshot.MatchRecord;
import org.eclipse.viatra2.emf.incquery.snapshot.EIQSnapshot.MatchSubstitutionRecord;
import org.eclipse.viatra2.emf.incquery.testing.queries.matchrecordwithsubstitution.MatchRecordWithSubstitutionMatch;

/**
 * A match processor tailored for the MatchRecordWithSubstitution pattern.
 * 
 * Clients should derive an (anonymous) class that implements the abstract process().
 * 
 */
public abstract class MatchRecordWithSubstitutionProcessor implements IMatchProcessor<MatchRecordWithSubstitutionMatch> {
  /**
   * Defines the action that is to be executed on each match.
   * @param pRecord the value of pattern parameter Record in the currently processed match 
   * @param pParameterName the value of pattern parameter ParameterName in the currently processed match 
   * @param pSubstitution the value of pattern parameter Substitution in the currently processed match 
   * 
   */
  public abstract void process(final MatchRecord Record, final String ParameterName, final MatchSubstitutionRecord Substitution);
  
  @Override
  public void process(final MatchRecordWithSubstitutionMatch match) {
    process(match.getRecord(), match.getParameterName(), match.getSubstitution());  				
    
  }
}
