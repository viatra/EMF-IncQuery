package org.eclipse.viatra2.emf.incquery.triggerengine.specific;

import java.util.Map;

import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.viatra2.emf.incquery.runtime.api.IPatternMatch;
import org.eclipse.viatra2.emf.incquery.runtime.api.IncQueryMatcher;
import org.eclipse.viatra2.emf.incquery.triggerengine.api.Activation;
import org.eclipse.viatra2.emf.incquery.triggerengine.api.Agenda;
import org.eclipse.viatra2.emf.incquery.triggerengine.api.Rule;
import org.eclipse.viatra2.emf.incquery.triggerengine.notification.IActivationNotificationListener;
import org.eclipse.viatra2.emf.incquery.triggerengine.util.ActivationState;
import org.eclipse.viatra2.emf.incquery.triggerengine.util.DefaultAttributeMonitor;

public class RecordingRule<MatchType extends IPatternMatch> extends Rule<MatchType> {
	
	private DefaultAttributeMonitor<MatchType> attributeMonitor;
	
	public RecordingRule(Agenda agenda, IncQueryMatcher<MatchType> matcher, 
			final boolean upgradedStateUsed, 
			final boolean disappearedStateUsed,
			final boolean allowMultipleFiring) {
		super(agenda, matcher, upgradedStateUsed, disappearedStateUsed, allowMultipleFiring);
		
		this.attributeMonitor = new DefaultAttributeMonitor<MatchType>();
		this.matcher.addCallbackOnMatchUpdate(this, true);
		this.attributeMonitor.addCallbackOnMatchUpdate(this);
	}
	
	public RecordingRule(Agenda agenda, IncQueryMatcher<MatchType> matcher) {
		this(agenda, matcher, false, false, false);
	}
	
	public TransactionalEditingDomain getEditingDomain() {
		return this.agenda.getEditingDomain();
	}
	
	/**
	 * This method is only used when upgradedStateUsed flag is true
	 * @param match
	 */
	private void processMatchModification(MatchType match) {
		Map<MatchType, Activation<MatchType>> updatedMap = stateMap.get(ActivationState.UPDATED);
		Map<MatchType, Activation<MatchType>> appearedMap = stateMap.get(ActivationState.APPEARED);
		Activation<MatchType> activation = updatedMap.get(match);
		
		//if this method is called the activation associated to the match must be in either appeared or upgraded state
		if (activation != null) {
			//upgraded state is not changed
			activation.setFired(false);
			notifyActivationAppearance(activation);
		}
		else {
			activation = appearedMap.get(match);
			if (activation != null && activation.isFired()) {
				//changing activation state from appeared to upgraded
				appearedMap.remove(match);
				activation.setFired(false);
				activation.setState(ActivationState.UPDATED);
				updatedMap.put(match, activation);
				notifyActivationAppearance(activation);
			}
		}
	}
	
	private void processMatchAppearance(MatchType match) {
		Map<MatchType, Activation<MatchType>> disappearedMap = stateMap.get(ActivationState.DISAPPEARED);
		Map<MatchType, Activation<MatchType>> appearedMap = stateMap.get(ActivationState.APPEARED);
		
		//activation can be in not exists or disappeared state
		Activation<MatchType> activation = disappearedMap.get(match);
		if (activation != null) {
			//if disappearedStateUsed flag is false no activation will be inserted into disappearedMap
			disappearedMap.remove(match);
			activation.setFired(true);
			activation.setState(ActivationState.APPEARED);
			appearedMap.put(match, activation);
			notifyActivationDisappearance(activation);
		}
		else {
			activation = new RecordingActivation<MatchType>(this, match, allowMultipleFiring);
			activation.setState(ActivationState.APPEARED);
			appearedMap.put(match, activation);
			
			if (upgradedStateUsed) {
				attributeMonitor.registerFor(match);
			}
			
			notifyActivationAppearance(activation);
		}
	}

	private void processMatchDisappearance(MatchType match) {
		Map<MatchType, Activation<MatchType>> disappearedMap = stateMap.get(ActivationState.DISAPPEARED);
		Map<MatchType, Activation<MatchType>> appearedMap = stateMap.get(ActivationState.APPEARED);
		Map<MatchType, Activation<MatchType>> updatedMap = stateMap.get(ActivationState.UPDATED);

		//activation can be in appeared or updated state
		Activation<MatchType> activation = appearedMap.get(match);
		if (activation != null) {
			//changing activation state from appeared to disappeared if it was fired
			if (activation.isFired() && disappearedStateUsed) {
				appearedMap.remove(match);
				activation.setFired(false);
				activation.setState(ActivationState.DISAPPEARED);
				disappearedMap.put(match, activation);
				notifyActivationAppearance(activation);
			}
			else {
				appearedMap.remove(match);
				//unregistering change listener from the affected observable values
				attributeMonitor.unregisterFor(match);
				notifyActivationDisappearance(activation);
			}
		}
		else {
			//changing activation state from updated to disappeared if it was fired
			activation = updatedMap.get(match);
			if (activation.isFired()) {
				updatedMap.remove(match);
				activation.setFired(false);
				activation.setState(ActivationState.DISAPPEARED);
				disappearedMap.put(match, activation);
				notifyActivationAppearance(activation);
			}
		}
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

	@Override
	public void notifyAppearance(MatchType match) {
		processMatchAppearance(match);
	}

	@Override
	public void notifyDisappearance(MatchType match) {
		processMatchDisappearance(match);
	}

	@Override
	public void notifyUpdate(MatchType match) {
		processMatchModification(match);
	}

	@Override
	public boolean addActivationNotificationListener(IActivationNotificationListener listener, boolean fireNow) {
		boolean notContained = this.activationNotificationListeners.add(listener);
		if (notContained) {
			for (Activation<MatchType> activation : getActivations()) {
				listener.activationAppeared(activation);
			}
		}
		return notContained;
	}
}
