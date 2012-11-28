package org.eclipse.viatra2.emf.incquery.triggerengine.specific;

import org.eclipse.incquery.runtime.api.IMatcherFactory;
import org.eclipse.incquery.runtime.api.IPatternMatch;
import org.eclipse.incquery.runtime.api.IncQueryEngine;
import org.eclipse.incquery.runtime.api.IncQueryMatcher;
import org.eclipse.incquery.runtime.exception.IncQueryException;
import org.eclipse.viatra2.emf.incquery.triggerengine.api.AbstractRule;
import org.eclipse.viatra2.emf.incquery.triggerengine.api.RuleEngine;
import org.eclipse.viatra2.emf.incquery.triggerengine.api.IRuleFactory;

public class DefaultRuleFactory implements IRuleFactory {

	@Override
	public <Match extends IPatternMatch, Matcher extends IncQueryMatcher<Match>> AbstractRule<Match> createRule(
			IncQueryEngine engine,
			IMatcherFactory<Matcher> factory, 
			boolean upgradedStateUsed,
			boolean disappearedStateUsed) {
		
		AbstractRule<Match> rule = null;
		try {
			rule = new RecordingRule<Match>(RuleEngine.getInstance().getOrCreateAgenda(engine), 
											factory.getMatcher(engine), 
											upgradedStateUsed, 
											disappearedStateUsed);
		} catch (IncQueryException e) {
			engine.getLogger().error("Error while creating RecordingRule!", e);
		}
		return rule;
	}

}
