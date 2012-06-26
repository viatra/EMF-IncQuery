package org.eclipse.viatra2.emf.incquery.testing.queries.correspondingrecordinmatchsetrecord;

import org.eclipse.viatra2.emf.incquery.runtime.api.IncQueryEngine;
import org.eclipse.viatra2.emf.incquery.runtime.api.impl.BaseGeneratedMatcherFactory;
import org.eclipse.viatra2.emf.incquery.runtime.exception.IncQueryRuntimeException;
import org.eclipse.viatra2.emf.incquery.testing.queries.correspondingrecordinmatchsetrecord.CorrespondingRecordInMatchSetRecordMatch;
import org.eclipse.viatra2.emf.incquery.testing.queries.correspondingrecordinmatchsetrecord.CorrespondingRecordInMatchSetRecordMatcher;

/**
 * A pattern-specific matcher factory that can instantiate CorrespondingRecordInMatchSetRecordMatcher in a type-safe way.
 * 
 * @see CorrespondingRecordInMatchSetRecordMatcher
 * @see CorrespondingRecordInMatchSetRecordMatch
 * 
 */
public class CorrespondingRecordInMatchSetRecordMatcherFactory extends BaseGeneratedMatcherFactory<CorrespondingRecordInMatchSetRecordMatch,CorrespondingRecordInMatchSetRecordMatcher> {
  @Override
  protected CorrespondingRecordInMatchSetRecordMatcher instantiate(final IncQueryEngine engine) throws IncQueryRuntimeException {
    return new CorrespondingRecordInMatchSetRecordMatcher(engine);
    
  }
  
  @Override
  protected String getBundleName() {
    return "org.eclipse.viatra2.emf.incquery.testing.queries";
    
  }
  
  @Override
  protected String patternName() {
    return "org.eclipse.viatra2.emf.incquery.testing.queries.CorrespondingRecordInMatchSetRecord";
    
  }
}
