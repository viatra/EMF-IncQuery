package org.eclipse.viatra2.emf.incquery.testing.queries.correspondingrecords;

import org.eclipse.viatra2.emf.incquery.runtime.api.IncQueryEngine;
import org.eclipse.viatra2.emf.incquery.runtime.api.impl.BaseGeneratedMatcherFactory;
import org.eclipse.viatra2.emf.incquery.runtime.exception.IncQueryRuntimeException;
import org.eclipse.viatra2.emf.incquery.testing.queries.correspondingrecords.CorrespondingRecordsMatch;
import org.eclipse.viatra2.emf.incquery.testing.queries.correspondingrecords.CorrespondingRecordsMatcher;

/**
 * A pattern-specific matcher factory that can instantiate CorrespondingRecordsMatcher in a type-safe way.
 * 
 * @see CorrespondingRecordsMatcher
 * @see CorrespondingRecordsMatch
 * 
 */
public class CorrespondingRecordsMatcherFactory extends BaseGeneratedMatcherFactory<CorrespondingRecordsMatch,CorrespondingRecordsMatcher> {
  @Override
  protected CorrespondingRecordsMatcher instantiate(final IncQueryEngine engine) throws IncQueryRuntimeException {
    return new CorrespondingRecordsMatcher(engine);
    
  }
  
  @Override
  protected String getBundleName() {
    return "org.eclipse.viatra2.emf.incquery.testing.queries";
    
  }
  
  @Override
  protected String patternName() {
    return "org.eclipse.viatra2.emf.incquery.testing.queries.CorrespondingRecords";
    
  }
}
