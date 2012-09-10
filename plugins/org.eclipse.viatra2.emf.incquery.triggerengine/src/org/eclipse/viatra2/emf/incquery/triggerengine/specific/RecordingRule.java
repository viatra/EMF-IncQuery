package org.eclipse.viatra2.emf.incquery.triggerengine.specific;

import java.util.Map;

import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.viatra2.emf.incquery.runtime.api.IPatternMatch;
import org.eclipse.viatra2.emf.incquery.runtime.api.IncQueryMatcher;
import org.eclipse.viatra2.emf.incquery.triggerengine.Activation;
import org.eclipse.viatra2.emf.incquery.triggerengine.ActivationState;
import org.eclipse.viatra2.emf.incquery.triggerengine.Agenda;
import org.eclipse.viatra2.emf.incquery.triggerengine.Rule;
import org.eclipse.viatra2.emf.incquery.triggerengine.notification.NotificationProvider;
import org.eclipse.viatra2.emf.incquery.triggerengine.notification.NotificationProviderListener;
import org.eclipse.viatra2.emf.incquery.triggerengine.notification.ReteBasedNotificationProvider;
import org.eclipse.viatra2.emf.incquery.triggerengine.notification.TransactionBasedNotificationProvider;
import org.eclipse.viatra2.emf.incquery.triggerengine.util.AttributeMonitor;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.misc.DeltaMonitor;

public class RecordingRule<MatchType extends IPatternMatch> extends Rule<MatchType> implements NotificationProviderListener<MatchType> {
	
	private NotificationProvider<MatchType> notificationProvider;
	private AttributeMonitor<MatchType> attributeMonitor;
	private DeltaMonitor<MatchType> deltaMonitor;
	
	public RecordingRule(Agenda agenda, IncQueryMatcher<MatchType> matcher, 
			final boolean upgradedStateUsed, 
			final boolean disappearedStateUsed,
			final boolean allowMultipleFiring) {
		super(agenda, matcher, upgradedStateUsed, disappearedStateUsed, allowMultipleFiring);
		
		if (agenda.getEditingDomain() != null) {
			notificationProvider = new TransactionBasedNotificationProvider<MatchType>(agenda.getEditingDomain());
		}
		else {
			notificationProvider = new ReteBasedNotificationProvider<MatchType>(matcher);
		}
		this.deltaMonitor = matcher.newDeltaMonitor(true);
		this.attributeMonitor = new AttributeMonitor<MatchType>();
		this.notificationProvider.addNotificationProviderListener(this);
		this.attributeMonitor.addNotificationProviderListener(this);
		notificationCallback();
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
		Map<MatchType, Activation<MatchType>> upgradedMap = stateMap.get(ActivationState.UPGRADED);
		Map<MatchType, Activation<MatchType>> appearedMap = stateMap.get(ActivationState.APPEARED);
		Activation<MatchType> activation = upgradedMap.get(match);
		
		//if this method is called the activation associated to the match must be in either appeared or upgraded state
		if (activation != null && activation.isFired()) {
			//upgraded state is not changed
			activation.setFired(false);
		}
		else {
			activation = appearedMap.get(match);
			if (activation != null && activation.isFired()) {
				//changing activation state from appeared to upgraded
				appearedMap.remove(match);
				activation.setFired(false);
				activation.setState(ActivationState.UPGRADED);
				upgradedMap.put(match, activation);
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
		}
		else {
			activation = new RecordingActivation<MatchType>(this, match, allowMultipleFiring);
			activation.setState(ActivationState.APPEARED);
			appearedMap.put(match, activation);

			if (upgradedStateUsed) {
				attributeMonitor.registerFor(match);
			}
		}
	}

	private void processMatchDisappearance(MatchType match) {
		Map<MatchType, Activation<MatchType>> disappearedMap = stateMap.get(ActivationState.DISAPPEARED);
		Map<MatchType, Activation<MatchType>> appearedMap = stateMap.get(ActivationState.APPEARED);
		Map<MatchType, Activation<MatchType>> upgradedMap = stateMap.get(ActivationState.UPGRADED);

		//activation can be in appeared or upgraded state
		Activation<MatchType> activation = appearedMap.get(match);
		if (activation != null) {
			//changing activation state from appeared to disappeared if it was fired
			if (disappearedStateUsed) {
				appearedMap.remove(match);
				activation.setFired(false);
				activation.setState(ActivationState.DISAPPEARED);
				disappearedMap.put(match, activation);
			}
			else {
				appearedMap.remove(match);
				//unregistering change listener from the affected observable values
				attributeMonitor.unregisterFor(match);
			}
		}
		else {
			//changing activation state from upgraded to disappeared if it was fired
			activation = upgradedMap.get(match);
			if (activation.isFired()) {
				upgradedMap.remove(match);
				activation.setFired(false);
				activation.setState(ActivationState.DISAPPEARED);
				disappearedMap.put(match, activation);
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
		attributeMonitor.unregisterForAll();
		deltaMonitor.disconnectFromNetwork();
		notificationProvider.dispose();
	}

	@Override
	public void notificationCallback() {
		for (MatchType newMatch : deltaMonitor.matchFoundEvents) {
			processMatchAppearance(newMatch);
		}
		for (MatchType lostMatch : deltaMonitor.matchLostEvents) {
			processMatchDisappearance(lostMatch);
		}
		for (MatchType modMatch : attributeMonitor.matchModificationEvents) {
			processMatchModification(modMatch);
		}
		
		deltaMonitor.clear();
		attributeMonitor.clear();
		
		this.agenda.run();
	}
}
