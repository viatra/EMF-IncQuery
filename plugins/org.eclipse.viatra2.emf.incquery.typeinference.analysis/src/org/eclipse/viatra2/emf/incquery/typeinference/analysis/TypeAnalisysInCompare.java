package org.eclipse.viatra2.emf.incquery.typeinference.analysis;

import org.eclipse.viatra2.emf.incquery.runtime.exception.IncQueryException;
import org.eclipse.viatra2.emf.incquery.typeinference.tautologyccompare.TautologycCompareMatcher;
import org.eclipse.viatra2.emf.incquery.typeinference.unsatisfiablecompare.UnsatisfiableCompareMatcher;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.CompareConstraint;
import org.eclipse.viatra2.patternlanguage.eMFPatternLanguage.PatternModel;

public class TypeAnalisysInCompare extends QueryAnalysisOnPattern{
	
	TautologycCompareMatcher tautologycCompareMatcher;
	UnsatisfiableCompareMatcher unsatisfiableCompareMatcher;
	
	public TypeAnalisysInCompare(PatternModel patternModel) throws TypeAnalysisException {
		super(patternModel);
		
		try {
			this.tautologycCompareMatcher = new TautologycCompareMatcher(this.resourceSet);
			this.unsatisfiableCompareMatcher = new UnsatisfiableCompareMatcher(this.resourceSet);
		} catch (IncQueryException e) {
			throw new TypeAnalysisException("The matchers can not be created.");
		}
	}

	@Override
	protected void initMatchers() throws TypeAnalysisException {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void getMaches() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void releaseMatchers() {
		// TODO Auto-generated method stub
		
	}
	
	public boolean isUnsatisfiableCompare(CompareConstraint compareConstraint) throws TypeAnalysisException {
		return false;//this.handleMatchResult(this.unsatisfiableCompareMatcher.getAllMatches(compareConstraint)) != null;
	}
	
	public boolean isTautologycCompare(CompareConstraint compareConstraint) throws TypeAnalysisException {
		return false;//this.handleMatchResult(this.tautologycCompareMatcher.getAllMatches(compareConstraint)) != null;
	}
}
