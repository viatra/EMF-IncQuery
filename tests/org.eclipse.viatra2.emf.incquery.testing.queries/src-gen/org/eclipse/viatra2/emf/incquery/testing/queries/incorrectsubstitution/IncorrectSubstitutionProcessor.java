package org.eclipse.viatra2.emf.incquery.testing.queries.incorrectsubstitution;

import org.eclipse.viatra2.emf.incquery.runtime.api.IMatchProcessor;
import org.eclipse.viatra2.emf.incquery.snapshot.EIQSnapshot.MatchRecord;
import org.eclipse.viatra2.emf.incquery.testing.queries.incorrectsubstitution.IncorrectSubstitutionMatch;

/**
 * A match processor tailored for the IncorrectSubstitution pattern.
 * 
 * Clients should derive an (anonymous) class that implements the abstract process().
 * 
 */
public abstract class IncorrectSubstitutionProcessor implements IMatchProcessor<IncorrectSubstitutionMatch> {
  /**
   * Defines the action that is to be executed on each match.
   * @param pRecord the value of pattern parameter Record in the currently processed match 
   * @param pCorrespondingRecord the value of pattern parameter CorrespondingRecord in the currently processed match 
   * 
   */
  public abstract void process(final MatchRecord Record, final MatchRecord CorrespondingRecord);
  
  @Override
  public void process(final IncorrectSubstitutionMatch match) {
    process(match.getRecord(), match.getCorrespondingRecord());  				
    
  }
}
