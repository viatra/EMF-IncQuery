package org.eclipse.incquery.testing.queries.recordrolevalue;

import org.eclipse.incquery.runtime.api.IncQueryEngine;
import org.eclipse.incquery.runtime.api.impl.BaseGeneratedMatcherFactory;
import org.eclipse.incquery.runtime.exception.IncQueryException;
import org.eclipse.incquery.runtime.extensibility.IMatcherFactoryProvider;
import org.eclipse.incquery.testing.queries.recordrolevalue.RecordRoleValueMatcher;

/**
 * A pattern-specific matcher factory that can instantiate RecordRoleValueMatcher in a type-safe way.
 * 
 * @see RecordRoleValueMatcher
 * @see RecordRoleValueMatch
 * 
 */
public class RecordRoleValueMatcherFactory extends BaseGeneratedMatcherFactory<RecordRoleValueMatcher> {
  /**
   * @return the singleton instance of the matcher factory
   * @throws IncQueryException if the pattern definition could not be loaded
   * 
   */
  public static RecordRoleValueMatcherFactory instance() throws IncQueryException {
    try {
    	return LazyHolder.INSTANCE;
    } catch (ExceptionInInitializerError err) {
    	processInitializerError(err);
    	throw err;
    }
    
  }
  
  @Override
  protected RecordRoleValueMatcher instantiate(final IncQueryEngine engine) throws IncQueryException {
    return new RecordRoleValueMatcher(engine);
    
  }
  
  @Override
  protected String getBundleName() {
    return "org.eclipse.incquery.testing.queries";
    
  }
  
  @Override
  protected String patternName() {
    return "org.eclipse.incquery.testing.queries.RecordRoleValue";
    
  }
  
  private RecordRoleValueMatcherFactory() throws IncQueryException {
    super();
  }
  public static class Provider implements IMatcherFactoryProvider<RecordRoleValueMatcherFactory> {
    @Override
    public RecordRoleValueMatcherFactory get() throws IncQueryException {
      return instance();
    }
  }
  
  private static class LazyHolder {
    private final static RecordRoleValueMatcherFactory INSTANCE = make();
    
    public static RecordRoleValueMatcherFactory make() {
      try {
      	return new RecordRoleValueMatcherFactory();
      } catch (IncQueryException ex) {
      	throw new RuntimeException	(ex);
      }
      
    }
  }
  
}
