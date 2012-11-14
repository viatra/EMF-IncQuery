package org.eclipse.viatra2.emf.incquery.triggerengine.api;

import org.eclipse.viatra2.emf.incquery.runtime.api.IMatcherFactory;
import org.eclipse.viatra2.emf.incquery.runtime.api.IPatternMatch;
import org.eclipse.viatra2.emf.incquery.runtime.api.IncQueryEngine;
import org.eclipse.viatra2.emf.incquery.runtime.api.IncQueryMatcher;

/**
 * The {@link RuleFactory} defines the way a {@link Rule} instance is created.
 * 
 * @author Tamas Szabo
 *
 */
public interface RuleFactory {

	public <Match extends IPatternMatch, Matcher extends IncQueryMatcher<Match>> 
		Rule<Match> createRule(IncQueryEngine engine, 
							   IMatcherFactory<Matcher> factory, 
							   boolean upgradedStateUsed, 
							   boolean disappearedStateUsed, 
							   boolean allowMultipleFiring);
	
}
