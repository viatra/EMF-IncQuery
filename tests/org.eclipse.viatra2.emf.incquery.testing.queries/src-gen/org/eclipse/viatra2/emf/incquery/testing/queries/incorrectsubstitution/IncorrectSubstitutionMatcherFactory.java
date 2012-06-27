package org.eclipse.viatra2.emf.incquery.testing.queries.incorrectsubstitution;

import org.eclipse.viatra2.emf.incquery.runtime.api.IncQueryEngine;
import org.eclipse.viatra2.emf.incquery.runtime.api.impl.BaseGeneratedMatcherFactory;
import org.eclipse.viatra2.emf.incquery.runtime.exception.IncQueryRuntimeException;
import org.eclipse.viatra2.emf.incquery.testing.queries.incorrectsubstitution.IncorrectSubstitutionMatch;
import org.eclipse.viatra2.emf.incquery.testing.queries.incorrectsubstitution.IncorrectSubstitutionMatcher;

/**
 * A pattern-specific matcher factory that can instantiate IncorrectSubstitutionMatcher in a type-safe way.
 * 
 * @see IncorrectSubstitutionMatcher
 * @see IncorrectSubstitutionMatch
 * 
 */
public class IncorrectSubstitutionMatcherFactory extends BaseGeneratedMatcherFactory<IncorrectSubstitutionMatch,IncorrectSubstitutionMatcher> {
  @Override
  protected IncorrectSubstitutionMatcher instantiate(final IncQueryEngine engine) throws IncQueryRuntimeException {
    return new IncorrectSubstitutionMatcher(engine);
    
  }
  
  @Override
  protected String getBundleName() {
    return "org.eclipse.viatra2.emf.incquery.testing.queries";
    
  }
  
  @Override
  protected String patternName() {
    return "org.eclipse.viatra2.emf.incquery.testing.queries.IncorrectSubstitution";
    
  }
}
