package org.eclipse.viatra2.emf.incquery.validation.runtime;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.viatra2.emf.incquery.runtime.api.IPatternMatch;
import org.eclipse.viatra2.emf.incquery.runtime.api.impl.BaseGeneratedMatcher;
import org.eclipse.viatra2.emf.incquery.runtime.api.impl.BaseGeneratedMatcherFactory;

public abstract class Constraint<MatchType extends IPatternMatch> {

	public abstract String getMessage();

	public abstract EObject getLocationObject(MatchType signature);

	public String prettyPrintSignature(MatchType signature) {
		return signature.prettyPrint();
	}

	public Object[] extractAffectedElements(MatchType signature) {
		return signature.toArray();
	}
	
	public abstract int getSeverity();
	
	public abstract BaseGeneratedMatcherFactory<MatchType, ? extends BaseGeneratedMatcher<MatchType>> getMatcherFactory();
}
