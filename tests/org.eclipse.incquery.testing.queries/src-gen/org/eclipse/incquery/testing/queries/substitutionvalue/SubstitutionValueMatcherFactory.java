package org.eclipse.incquery.testing.queries.substitutionvalue;

import org.eclipse.incquery.runtime.api.IncQueryEngine;
import org.eclipse.incquery.runtime.api.impl.BaseGeneratedMatcherFactory;
import org.eclipse.incquery.runtime.exception.IncQueryException;
import org.eclipse.incquery.runtime.extensibility.IMatcherFactoryProvider;
import org.eclipse.incquery.testing.queries.substitutionvalue.SubstitutionValueMatcher;

/**
 * A pattern-specific matcher factory that can instantiate SubstitutionValueMatcher in a type-safe way.
 * 
 * @see SubstitutionValueMatcher
 * @see SubstitutionValueMatch
 * 
 */
public class SubstitutionValueMatcherFactory extends BaseGeneratedMatcherFactory<SubstitutionValueMatcher> {
  /**
   * @return the singleton instance of the matcher factory
   * @throws IncQueryException if the pattern definition could not be loaded
   * 
   */
  public static SubstitutionValueMatcherFactory instance() throws IncQueryException {
    try {
    	return LazyHolder.INSTANCE;
    } catch (ExceptionInInitializerError err) {
    	processInitializerError(err);
    	throw err;
    }
    
  }
  
  @Override
  protected SubstitutionValueMatcher instantiate(final IncQueryEngine engine) throws IncQueryException {
    return new SubstitutionValueMatcher(engine);
    
  }
  
  @Override
  protected String getBundleName() {
    return "org.eclipse.incquery.testing.queries";
    
  }
  
  @Override
  protected String patternName() {
    return "org.eclipse.incquery.testing.queries.SubstitutionValue";
    
  }
  
  private SubstitutionValueMatcherFactory() throws IncQueryException {
    super();
  }
  public static class Provider implements IMatcherFactoryProvider<SubstitutionValueMatcherFactory> {
    @Override
    public SubstitutionValueMatcherFactory get() throws IncQueryException {
      return instance();
    }
  }
  
  private static class LazyHolder {
    private final static SubstitutionValueMatcherFactory INSTANCE = make();
    
    public static SubstitutionValueMatcherFactory make() {
      try {
      	return new SubstitutionValueMatcherFactory();
      } catch (IncQueryException ex) {
      	throw new RuntimeException	(ex);
      }
      
    }
  }
  
}
