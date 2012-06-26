package org.eclipse.viatra2.emf.incquery.testing.queries.recordrolevalue;

import org.eclipse.viatra2.emf.incquery.runtime.api.IncQueryEngine;
import org.eclipse.viatra2.emf.incquery.runtime.api.impl.BaseGeneratedMatcherFactory;
import org.eclipse.viatra2.emf.incquery.runtime.exception.IncQueryRuntimeException;
import org.eclipse.viatra2.emf.incquery.testing.queries.recordrolevalue.RecordRoleValueMatch;
import org.eclipse.viatra2.emf.incquery.testing.queries.recordrolevalue.RecordRoleValueMatcher;

/**
 * A pattern-specific matcher factory that can instantiate RecordRoleValueMatcher in a type-safe way.
 * 
 * @see RecordRoleValueMatcher
 * @see RecordRoleValueMatch
 * 
 */
public class RecordRoleValueMatcherFactory extends BaseGeneratedMatcherFactory<RecordRoleValueMatch,RecordRoleValueMatcher> {
  @Override
  protected RecordRoleValueMatcher instantiate(final IncQueryEngine engine) throws IncQueryRuntimeException {
    return new RecordRoleValueMatcher(engine);
    
  }
  
  @Override
  protected String getBundleName() {
    return "org.eclipse.viatra2.emf.incquery.testing.queries";
    
  }
  
  @Override
  protected String patternName() {
    return "org.eclipse.viatra2.emf.incquery.testing.queries.RecordRoleValue";
    
  }
}
