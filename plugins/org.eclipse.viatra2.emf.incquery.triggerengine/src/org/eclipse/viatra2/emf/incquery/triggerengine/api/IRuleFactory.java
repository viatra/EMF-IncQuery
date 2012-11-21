package org.eclipse.viatra2.emf.incquery.triggerengine.api;

import org.eclipse.viatra2.emf.incquery.runtime.api.IMatcherFactory;
import org.eclipse.viatra2.emf.incquery.runtime.api.IPatternMatch;
import org.eclipse.viatra2.emf.incquery.runtime.api.IncQueryEngine;
import org.eclipse.viatra2.emf.incquery.runtime.api.IncQueryMatcher;

/**
 * The {@link IRuleFactory} defines the way a {@link AbstractRule} instance is created.
 * 
 * @author Tamas Szabo
 *
 */
public interface IRuleFactory {

	public <Match extends IPatternMatch, Matcher extends IncQueryMatcher<Match>> 
		AbstractRule<Match> createRule(IncQueryEngine engine, 
							   IMatcherFactory<Matcher> factory, 
							   boolean upgradedStateUsed, 
							   boolean disappearedStateUsed);
	
}
