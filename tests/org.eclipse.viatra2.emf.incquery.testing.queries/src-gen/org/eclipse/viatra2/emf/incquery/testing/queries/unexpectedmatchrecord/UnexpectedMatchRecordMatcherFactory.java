package org.eclipse.viatra2.emf.incquery.testing.queries.unexpectedmatchrecord;

import org.eclipse.viatra2.emf.incquery.runtime.api.IncQueryEngine;
import org.eclipse.viatra2.emf.incquery.runtime.api.impl.BaseGeneratedMatcherFactory;
import org.eclipse.viatra2.emf.incquery.runtime.exception.IncQueryRuntimeException;
import org.eclipse.viatra2.emf.incquery.testing.queries.unexpectedmatchrecord.UnexpectedMatchRecordMatch;
import org.eclipse.viatra2.emf.incquery.testing.queries.unexpectedmatchrecord.UnexpectedMatchRecordMatcher;

/**
 * A pattern-specific matcher factory that can instantiate UnexpectedMatchRecordMatcher in a type-safe way.
 * 
 * @see UnexpectedMatchRecordMatcher
 * @see UnexpectedMatchRecordMatch
 * 
 */
public class UnexpectedMatchRecordMatcherFactory extends BaseGeneratedMatcherFactory<UnexpectedMatchRecordMatch,UnexpectedMatchRecordMatcher> {
  @Override
  protected UnexpectedMatchRecordMatcher instantiate(final IncQueryEngine engine) throws IncQueryRuntimeException {
    return new UnexpectedMatchRecordMatcher(engine);
    
  }
  
  @Override
  protected String getBundleName() {
    return "org.eclipse.viatra2.emf.incquery.testing.queries";
    
  }
  
  @Override
  protected String patternName() {
    return "org.eclipse.viatra2.emf.incquery.testing.queries.UnexpectedMatchRecord";
    
  }
}
