package org.eclipse.viatra2.emf.incquery.testing.queries.correctrecordsubstitutionvalue;

import org.eclipse.viatra2.emf.incquery.runtime.api.IncQueryEngine;
import org.eclipse.viatra2.emf.incquery.runtime.api.impl.BaseGeneratedMatcherFactory;
import org.eclipse.viatra2.emf.incquery.runtime.exception.IncQueryRuntimeException;
import org.eclipse.viatra2.emf.incquery.testing.queries.correctrecordsubstitutionvalue.CorrectRecordSubstitutionValueMatch;
import org.eclipse.viatra2.emf.incquery.testing.queries.correctrecordsubstitutionvalue.CorrectRecordSubstitutionValueMatcher;

/**
 * A pattern-specific matcher factory that can instantiate CorrectRecordSubstitutionValueMatcher in a type-safe way.
 * 
 * @see CorrectRecordSubstitutionValueMatcher
 * @see CorrectRecordSubstitutionValueMatch
 * 
 */
public class CorrectRecordSubstitutionValueMatcherFactory extends BaseGeneratedMatcherFactory<CorrectRecordSubstitutionValueMatch,CorrectRecordSubstitutionValueMatcher> {
  @Override
  protected CorrectRecordSubstitutionValueMatcher instantiate(final IncQueryEngine engine) throws IncQueryRuntimeException {
    return new CorrectRecordSubstitutionValueMatcher(engine);
    
  }
  
  @Override
  protected String getBundleName() {
    return "org.eclipse.viatra2.emf.incquery.testing.queries";
    
  }
  
  @Override
  protected String patternName() {
    return "org.eclipse.viatra2.emf.incquery.testing.queries.CorrectRecordSubstitutionValue";
    
  }
}
