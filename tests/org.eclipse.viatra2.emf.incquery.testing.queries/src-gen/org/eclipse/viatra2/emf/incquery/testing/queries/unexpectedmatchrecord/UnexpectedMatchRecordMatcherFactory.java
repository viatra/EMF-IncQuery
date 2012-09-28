package org.eclipse.viatra2.emf.incquery.testing.queries.unexpectedmatchrecord;

import org.eclipse.viatra2.emf.incquery.runtime.api.IncQueryEngine;
import org.eclipse.viatra2.emf.incquery.runtime.api.impl.BaseGeneratedMatcherFactory;
import org.eclipse.viatra2.emf.incquery.runtime.exception.IncQueryException;
import org.eclipse.viatra2.emf.incquery.runtime.extensibility.IMatcherFactoryProvider;
import org.eclipse.viatra2.emf.incquery.testing.queries.unexpectedmatchrecord.UnexpectedMatchRecordMatcher;

/**
 * A pattern-specific matcher factory that can instantiate UnexpectedMatchRecordMatcher in a type-safe way.
 * 
 * @see UnexpectedMatchRecordMatcher
 * @see UnexpectedMatchRecordMatch
 * 
 */
public class UnexpectedMatchRecordMatcherFactory extends BaseGeneratedMatcherFactory<UnexpectedMatchRecordMatcher> {
  /**
   * @return the singleton instance of the matcher factory
   * @throws IncQueryException if the pattern definition could not be loaded
   * 
   */
  public static UnexpectedMatchRecordMatcherFactory instance() throws IncQueryException {
    try {
    	return LazyHolder.INSTANCE;
    } catch (ExceptionInInitializerError err) {
    	processInitializerError(err);
    	throw err;
    }
    
  }
  
  @Override
  protected UnexpectedMatchRecordMatcher instantiate(final IncQueryEngine engine) throws IncQueryException {
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
  
  private UnexpectedMatchRecordMatcherFactory() throws IncQueryException {
    super();
  }
  public static class Provider implements IMatcherFactoryProvider<UnexpectedMatchRecordMatcherFactory> {
    @Override
    public UnexpectedMatchRecordMatcherFactory get() throws IncQueryException {
      return instance();
    }
  }
  
  private static class LazyHolder {
    private final static UnexpectedMatchRecordMatcherFactory INSTANCE = make();
    
    public static UnexpectedMatchRecordMatcherFactory make() {
      try {
      	return new UnexpectedMatchRecordMatcherFactory();
      } catch (IncQueryException ex) {
      	throw new RuntimeException	(ex);
      }
      
    }
  }
  
}
