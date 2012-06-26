package org.eclipse.viatra2.emf.incquery.testing.queries.correspondingrecordinmatchsetrecord;

import org.eclipse.viatra2.emf.incquery.runtime.api.IMatchProcessor;
import org.eclipse.viatra2.emf.incquery.snapshot.EIQSnapshot.MatchRecord;
import org.eclipse.viatra2.emf.incquery.snapshot.EIQSnapshot.MatchSetRecord;
import org.eclipse.viatra2.emf.incquery.testing.queries.correspondingrecordinmatchsetrecord.CorrespondingRecordInMatchSetRecordMatch;

/**
 * A match processor tailored for the CorrespondingRecordInMatchSetRecord pattern.
 * 
 * Clients should derive an (anonymous) class that implements the abstract process().
 * 
 */
public abstract class CorrespondingRecordInMatchSetRecordProcessor implements IMatchProcessor<CorrespondingRecordInMatchSetRecordMatch> {
  /**
   * Defines the action that is to be executed on each match.
   * @param pRecord the value of pattern parameter Record in the currently processed match 
   * @param pCorrespodingRecord the value of pattern parameter CorrespodingRecord in the currently processed match 
   * @param pExpectedSet the value of pattern parameter ExpectedSet in the currently processed match 
   * 
   */
  public abstract void process(final MatchRecord Record, final MatchRecord CorrespodingRecord, final MatchSetRecord ExpectedSet);
  
  @Override
  public void process(final CorrespondingRecordInMatchSetRecordMatch match) {
    process(match.getRecord(), match.getCorrespodingRecord(), match.getExpectedSet());  				
    
  }
}
