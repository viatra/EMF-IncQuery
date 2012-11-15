package org.eclipse.viatra2.emf.incquery.triggerengine.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.viatra2.emf.incquery.runtime.api.IMatchProcessor;
import org.eclipse.viatra2.emf.incquery.runtime.api.IMatchUpdateListener;
import org.eclipse.viatra2.emf.incquery.runtime.api.IPatternMatch;
import org.eclipse.viatra2.emf.incquery.runtime.api.IncQueryMatcher;
import org.eclipse.viatra2.emf.incquery.triggerengine.notification.ActivationNotificationProvider;
import org.eclipse.viatra2.emf.incquery.triggerengine.notification.IAttributeMonitorListener;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Pattern;

/**
 * A {@link Rule} defines a transformation step in the context of the Rule Engine. 
 * Each rule is assigned a precondition (Left Hand Side - LHS) which is an EMF-IncQuery pattern and 
 * a postcondition (Right Hand Side - RHS) which is an {@link IMatchProcessor} instance. 
 * 
 * The {@link Rule} keeps track of its activations and they can be queried and executed at some 
 * point in time. 
 * 
 * @author Tamas Szabo
 *
 * @param <MatchType> the type of the pattern match
 */
public abstract class Rule<MatchType extends IPatternMatch> extends ActivationNotificationProvider implements 
	IAttributeMonitorListener<MatchType>, IMatchUpdateListener<MatchType> {

	public IMatchProcessor<MatchType> afterAppearanceJob;
	public IMatchProcessor<MatchType> afterDisappearanceJob;
	public IMatchProcessor<MatchType> afterModificationJob;
	protected Agenda agenda;
	protected boolean upgradedStateUsed;
	protected boolean disappearedStateUsed;
	protected IncQueryMatcher<MatchType> matcher;
	protected Map<ActivationState, Map<MatchType, Activation<MatchType>>> stateMap;
	protected boolean allowMultipleFiring;
	
	public Rule(Agenda agenda, 
				IncQueryMatcher<MatchType> matcher, 
				boolean upgradedStateUsed, 
				boolean disappearedStateUsed,
				boolean allowMultipleFiring) {
		this.agenda = agenda;
		this.matcher = matcher;
		this.upgradedStateUsed = upgradedStateUsed;
		this.disappearedStateUsed = disappearedStateUsed;
		this.stateMap = new HashMap<ActivationState, Map<MatchType,Activation<MatchType>>>();
		this.stateMap.put(ActivationState.APPEARED, new HashMap<MatchType, Activation<MatchType>>());
		this.stateMap.put(ActivationState.DISAPPEARED, new HashMap<MatchType, Activation<MatchType>>());
		this.stateMap.put(ActivationState.UPDATED, new HashMap<MatchType, Activation<MatchType>>());
		this.allowMultipleFiring = allowMultipleFiring;
	}
	
	public abstract void dispose();
	
	public Pattern getPattern() {
		return matcher.getPattern();
	}
	
	public abstract void activationFired(Activation<MatchType> activation);
	
	public Agenda getAgenda() {
		return agenda;
	}
	
	public List<Activation<MatchType>> getActivations() {
		List<Activation<MatchType>> activations = new ArrayList<Activation<MatchType>>();
		for (ActivationState as : stateMap.keySet()) {
			for (MatchType match : stateMap.get(as).keySet()) {
				Activation<MatchType> a = stateMap.get(as).get(match);
				if (!a.isFired()) {
					activations.add(a);
				}
			}
		}
		return activations;
	}
}
