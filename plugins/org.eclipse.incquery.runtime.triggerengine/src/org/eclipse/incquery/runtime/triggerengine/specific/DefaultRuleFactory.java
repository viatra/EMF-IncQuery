package org.eclipse.incquery.runtime.triggerengine.specific;

import org.eclipse.incquery.runtime.api.IMatcherFactory;
import org.eclipse.incquery.runtime.api.IPatternMatch;
import org.eclipse.incquery.runtime.api.IncQueryEngine;
import org.eclipse.incquery.runtime.api.IncQueryMatcher;
import org.eclipse.incquery.runtime.exception.IncQueryException;
import org.eclipse.incquery.runtime.triggerengine.api.AbstractRule;
import org.eclipse.incquery.runtime.triggerengine.api.IRuleFactory;
import org.eclipse.incquery.runtime.triggerengine.api.RuleEngine;

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
