package org.eclipse.viatra2.emf.incquery.testing.queries.correspondingrecords;

import org.eclipse.viatra2.emf.incquery.runtime.api.IMatchProcessor;
import org.eclipse.viatra2.emf.incquery.snapshot.EIQSnapshot.MatchRecord;
import org.eclipse.viatra2.emf.incquery.testing.queries.correspondingrecords.CorrespondingRecordsMatch;

/**
 * A match processor tailored for the CorrespondingRecords pattern.
 * 
 * Clients should derive an (anonymous) class that implements the abstract process().
 * 
 */
public abstract class CorrespondingRecordsProcessor implements IMatchProcessor<CorrespondingRecordsMatch> {
  /**
   * Defines the action that is to be executed on each match.
   * @param pRecord the value of pattern parameter Record in the currently processed match 
   * @param pCorrespondingRecord the value of pattern parameter CorrespondingRecord in the currently processed match 
   * 
   */
  public abstract void process(final MatchRecord Record, final MatchRecord CorrespondingRecord);
  
  @Override
  public void process(final CorrespondingRecordsMatch match) {
    process(match.getRecord(), match.getCorrespondingRecord());  				
    
  }
}
