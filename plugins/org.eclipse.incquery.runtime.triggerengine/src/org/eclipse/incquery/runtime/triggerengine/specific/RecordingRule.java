package org.eclipse.incquery.runtime.triggerengine.specific;


import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.incquery.runtime.api.IPatternMatch;
import org.eclipse.incquery.runtime.api.IncQueryMatcher;
import org.eclipse.incquery.runtime.triggerengine.api.AbstractRule;
import org.eclipse.incquery.runtime.triggerengine.api.Activation;
import org.eclipse.incquery.runtime.triggerengine.api.ActivationState;
import org.eclipse.incquery.runtime.triggerengine.api.IAgenda;

public class RecordingRule<MatchType extends IPatternMatch> extends AbstractRule<MatchType> {
	
	public RecordingRule(IAgenda agenda, IncQueryMatcher<MatchType> matcher, 
			final boolean upgradedStateUsed, 
			final boolean disappearedStateUsed) {
		super(agenda, matcher, upgradedStateUsed, disappearedStateUsed);
		
		this.attributeMonitor = new DefaultAttributeMonitor<MatchType>();
		this.matcher.addCallbackOnMatchUpdate(this, true);
		this.attributeMonitor.addCallbackOnMatchUpdate(this);
	}
	
	public RecordingRule(IAgenda agenda, IncQueryMatcher<MatchType> matcher) {
		this(agenda, matcher, false, false);
	}
	
	public TransactionalEditingDomain getEditingDomain() {
		return this.agenda.getEditingDomain();
	}
	
	@Override
	public void activationFired(Activation<MatchType> activation) {
		if (activation.getState() == ActivationState.APPEARED && !disappearedStateUsed && !upgradedStateUsed) {
			stateMap.get(ActivationState.APPEARED).remove(activation.getPatternMatch());
		}
		if (activation.getState() == ActivationState.DISAPPEARED) {
			attributeMonitor.unregisterFor(activation.getPatternMatch());
			stateMap.get(ActivationState.DISAPPEARED).remove(activation.getPatternMatch());
		}
	}
	
	@Override
	public void dispose() {
		this.attributeMonitor.removeCallbackOnMatchUpdate(this);
		this.attributeMonitor.dispose();
		this.matcher.removeCallbackOnMatchUpdate(this);
	}

	/* (non-Javadoc)
     * @see org.eclipse.viatra2.emf.incquery.triggerengine.api.AbstractRule#createActivation(org.eclipse.viatra2.emf.incquery.runtime.api.IPatternMatch)
     */
    @Override
    protected Activation<MatchType> createActivation(MatchType match) {
            Activation<MatchType> activation;
            activation = new RecordingActivation<MatchType>(this, match);
            activation.setState(ActivationState.APPEARED);
            return activation;
    }
}
