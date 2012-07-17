package org.eclipse.viatra2.emf.incquery.testing.queries;

import java.util.HashSet;
import java.util.Set;
import org.eclipse.viatra2.emf.incquery.runtime.api.IMatcherFactory;
import org.eclipse.viatra2.emf.incquery.runtime.api.impl.BaseGeneratedPatternGroup;
import org.eclipse.viatra2.emf.incquery.testing.queries.recordrolevalue.RecordRoleValueMatcher;
import org.eclipse.viatra2.emf.incquery.testing.queries.substitutionvalue.SubstitutionValueMatcher;
import org.eclipse.viatra2.emf.incquery.testing.queries.unexpectedmatchrecord.UnexpectedMatchRecordMatcher;

public final class GroupOfFileMatchRecord extends BaseGeneratedPatternGroup {
  @Override
  protected Set<IMatcherFactory<?>> getMatcherFactories() {
    Set<IMatcherFactory<?>> result = new HashSet<IMatcherFactory<?>>();
    result.add(RecordRoleValueMatcher.FACTORY);
    result.add(SubstitutionValueMatcher.FACTORY);
    result.add(UnexpectedMatchRecordMatcher.FACTORY);
    return result;
  }
}
