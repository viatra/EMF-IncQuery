package org.eclipse.viatra2.emf.incquery.testing.queries.matchrecordwithsubstitution;

import org.eclipse.viatra2.emf.incquery.runtime.api.IncQueryEngine;
import org.eclipse.viatra2.emf.incquery.runtime.api.impl.BaseGeneratedMatcherFactory;
import org.eclipse.viatra2.emf.incquery.runtime.exception.IncQueryRuntimeException;
import org.eclipse.viatra2.emf.incquery.testing.queries.matchrecordwithsubstitution.MatchRecordWithSubstitutionMatch;
import org.eclipse.viatra2.emf.incquery.testing.queries.matchrecordwithsubstitution.MatchRecordWithSubstitutionMatcher;

/**
 * A pattern-specific matcher factory that can instantiate MatchRecordWithSubstitutionMatcher in a type-safe way.
 * 
 * @see MatchRecordWithSubstitutionMatcher
 * @see MatchRecordWithSubstitutionMatch
 * 
 */
public class MatchRecordWithSubstitutionMatcherFactory extends BaseGeneratedMatcherFactory<MatchRecordWithSubstitutionMatch,MatchRecordWithSubstitutionMatcher> {
  @Override
  protected MatchRecordWithSubstitutionMatcher instantiate(final IncQueryEngine engine) throws IncQueryRuntimeException {
    return new MatchRecordWithSubstitutionMatcher(engine);
    
  }
  
  @Override
  protected String getBundleName() {
    return "org.eclipse.viatra2.emf.incquery.testing.queries";
    
  }
  
  @Override
  protected String patternName() {
    return "org.eclipse.viatra2.emf.incquery.testing.queries.MatchRecordWithSubstitution";
    
  }
}
