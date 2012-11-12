package org.eclipse.viatra2.emf.incquery.triggerengine;

import org.eclipse.viatra2.emf.incquery.runtime.api.IMatcherFactory;
import org.eclipse.viatra2.emf.incquery.runtime.api.IPatternMatch;
import org.eclipse.viatra2.emf.incquery.runtime.api.IncQueryEngine;
import org.eclipse.viatra2.emf.incquery.runtime.api.IncQueryMatcher;

public interface RuleFactory {

	public <Match extends IPatternMatch, Matcher extends IncQueryMatcher<Match>> 
		Rule<Match> createRule(IncQueryEngine engine, 
							   IMatcherFactory<Matcher> factory, 
							   boolean upgradedStateUsed, 
							   boolean disappearedStateUsed, 
							   boolean allowMultipleFiring);
	
}
