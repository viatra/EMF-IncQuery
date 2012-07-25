package org.eclipse.viatra2.emf.incquery.testing.queries;

import org.eclipse.viatra2.emf.incquery.runtime.api.impl.BaseGeneratedPatternGroup;
import org.eclipse.viatra2.emf.incquery.runtime.exception.IncQueryException;
import org.eclipse.viatra2.emf.incquery.testing.queries.recordrolevalue.RecordRoleValueMatcher;
import org.eclipse.viatra2.emf.incquery.testing.queries.substitutionvalue.SubstitutionValueMatcher;
import org.eclipse.viatra2.emf.incquery.testing.queries.unexpectedmatchrecord.UnexpectedMatchRecordMatcher;

public final class GroupOfFileMatchRecord extends BaseGeneratedPatternGroup {
  public GroupOfFileMatchRecord() throws IncQueryException {
    matcherFactories.add(RecordRoleValueMatcher.factory());
    matcherFactories.add(SubstitutionValueMatcher.factory());
    matcherFactories.add(UnexpectedMatchRecordMatcher.factory());
    
  }
}
