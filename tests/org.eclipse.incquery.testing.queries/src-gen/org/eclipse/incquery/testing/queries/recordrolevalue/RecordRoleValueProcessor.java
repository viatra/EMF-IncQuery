package org.eclipse.incquery.testing.queries.recordrolevalue;

import org.eclipse.incquery.runtime.api.IMatchProcessor;
import org.eclipse.incquery.snapshot.EIQSnapshot.MatchRecord;
import org.eclipse.incquery.testing.queries.recordrolevalue.RecordRoleValueMatch;

/**
 * A match processor tailored for the org.eclipse.incquery.testing.queries.RecordRoleValue pattern.
 * 
 * Clients should derive an (anonymous) class that implements the abstract process().
 * 
 */
public abstract class RecordRoleValueProcessor implements IMatchProcessor<RecordRoleValueMatch> {
  /**
   * Defines the action that is to be executed on each match.
   * @param pRecord the value of pattern parameter Record in the currently processed match 
   * @param pRole the value of pattern parameter Role in the currently processed match 
   * 
   */
  public abstract void process(final MatchRecord Record, final Object Role);
  
  @Override
  public void process(final RecordRoleValueMatch match) {
    process(match.getRecord(), match.getRole());  				
    
  }
}
