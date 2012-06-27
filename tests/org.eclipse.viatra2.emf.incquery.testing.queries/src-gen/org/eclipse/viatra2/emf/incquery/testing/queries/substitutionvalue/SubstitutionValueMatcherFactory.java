package org.eclipse.viatra2.emf.incquery.testing.queries.substitutionvalue;

import org.eclipse.viatra2.emf.incquery.runtime.api.IncQueryEngine;
import org.eclipse.viatra2.emf.incquery.runtime.api.impl.BaseGeneratedMatcherFactory;
import org.eclipse.viatra2.emf.incquery.runtime.exception.IncQueryRuntimeException;
import org.eclipse.viatra2.emf.incquery.testing.queries.substitutionvalue.SubstitutionValueMatch;
import org.eclipse.viatra2.emf.incquery.testing.queries.substitutionvalue.SubstitutionValueMatcher;

/**
 * A pattern-specific matcher factory that can instantiate SubstitutionValueMatcher in a type-safe way.
 * 
 * @see SubstitutionValueMatcher
 * @see SubstitutionValueMatch
 * 
 */
public class SubstitutionValueMatcherFactory extends BaseGeneratedMatcherFactory<SubstitutionValueMatch,SubstitutionValueMatcher> {
  @Override
  protected SubstitutionValueMatcher instantiate(final IncQueryEngine engine) throws IncQueryRuntimeException {
    return new SubstitutionValueMatcher(engine);
    
  }
  
  @Override
  protected String getBundleName() {
    return "org.eclipse.viatra2.emf.incquery.testing.queries";
    
  }
  
  @Override
  protected String patternName() {
    return "org.eclipse.viatra2.emf.incquery.testing.queries.SubstitutionValue";
    
  }
}
