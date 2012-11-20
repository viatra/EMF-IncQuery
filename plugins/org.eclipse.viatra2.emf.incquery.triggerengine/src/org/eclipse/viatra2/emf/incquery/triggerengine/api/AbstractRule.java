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
import org.eclipse.viatra2.emf.incquery.triggerengine.notification.AttributeMonitor;
import org.eclipse.viatra2.emf.incquery.triggerengine.notification.IActivationNotificationListener;
import org.eclipse.viatra2.emf.incquery.triggerengine.notification.IAttributeMonitorListener;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Pattern;

/**
 * A {@link AbstractRule} defines a transformation step in the context of the AbstractRule Engine. 
 * Each rule is assigned a precondition (Left Hand Side - LHS) which is an EMF-IncQuery pattern and 
 * a postcondition (Right Hand Side - RHS) which is an {@link IMatchProcessor} instance. 
 * 
 * <p>The {@link AbstractRule} keeps track of its activations and they can be queried and executed at some 
 * point in time. 
 * 
 * @author Tamas Szabo
 *
 * @param <MatchType> the type of the pattern match
 */
public abstract class AbstractRule<MatchType extends IPatternMatch> extends ActivationNotificationProvider implements 
	IAttributeMonitorListener<MatchType>, IMatchUpdateListener<MatchType>, Rule<MatchType> {

	private IMatchProcessor<MatchType> afterAppearanceJob;
	private IMatchProcessor<MatchType> afterDisappearanceJob;
	private IMatchProcessor<MatchType> afterModificationJob;
	protected Agenda agenda;
	protected boolean upgradedStateUsed;
	protected boolean disappearedStateUsed;
	protected IncQueryMatcher<MatchType> matcher;
	protected Map<ActivationState, Map<MatchType, Activation<MatchType>>> stateMap;
	protected AttributeMonitor<MatchType> attributeMonitor;
	
	public AbstractRule(Agenda agenda, 
				IncQueryMatcher<MatchType> matcher, 
				boolean upgradedStateUsed, 
				boolean disappearedStateUsed) {
		this.agenda = agenda;
		this.matcher = matcher;
		this.upgradedStateUsed = upgradedStateUsed;
		this.disappearedStateUsed = disappearedStateUsed;
		this.stateMap = new HashMap<ActivationState, Map<MatchType,Activation<MatchType>>>();
		this.stateMap.put(ActivationState.APPEARED, new HashMap<MatchType, Activation<MatchType>>());
		this.stateMap.put(ActivationState.DISAPPEARED, new HashMap<MatchType, Activation<MatchType>>());
		this.stateMap.put(ActivationState.UPDATED, new HashMap<MatchType, Activation<MatchType>>());
	}
	
	/* (non-Javadoc)
     * @see org.eclipse.viatra2.emf.incquery.triggerengine.api.Rule#getPattern()
     */
	@Override
    public Pattern getPattern() {
		return matcher.getPattern();
	}
	
	/**
	 * This method is called when the activation for the rule is fired.
	 * Subtypes may use this to step the state machine of the activation.
	 * 
	 * @param activation the activation that was fired
	 */
	public abstract void activationFired(Activation<MatchType> activation);
	
	/* (non-Javadoc)
     * @see org.eclipse.viatra2.emf.incquery.triggerengine.api.Rule#getAgenda()
     */
	@Override
    public Agenda getAgenda() {
		return agenda;
	}
	
	/* (non-Javadoc)
     * @see org.eclipse.viatra2.emf.incquery.triggerengine.api.Rule#getActivations()
     */
	@Override
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

    /**
     * This method is only used when upgradedStateUsed flag is true
     * @param match
     */
    protected void processMatchModification(MatchType match) {
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

    protected void processMatchAppearance(MatchType match) {
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
    		activation = createActivation(match);
    		appearedMap.put(match, activation);
    		
    		if (upgradedStateUsed) {
    			attributeMonitor.registerFor(match);
    		}
    		
    		notifyActivationAppearance(activation);
    	}
    }

    protected abstract Activation<MatchType> createActivation(MatchType match);

    protected void processMatchDisappearance(MatchType match) {
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
    
    /* (non-Javadoc)
     * @see org.eclipse.viatra2.emf.incquery.triggerengine.api.Rule#setStateChangeProcessor(org.eclipse.viatra2.emf.incquery.triggerengine.api.ActivationState, org.eclipse.viatra2.emf.incquery.runtime.api.IMatchProcessor)
     */
    @Override
    public void setStateChangeProcessor(ActivationState newState, IMatchProcessor<MatchType> processor) {
        switch (newState) {
        case APPEARED:
            afterAppearanceJob = processor;
            break;
        case DISAPPEARED:
            afterDisappearanceJob = processor;
            break;
        case UPDATED:
            afterModificationJob = processor;
            break;
        }
    }
    
   /* (non-Javadoc)
     * @see org.eclipse.viatra2.emf.incquery.triggerengine.api.Rule#getStateChangeProcessor(org.eclipse.viatra2.emf.incquery.triggerengine.api.ActivationState)
     */
    @Override
    public IMatchProcessor<MatchType> getStateChangeProcessor(ActivationState newState) {
        switch (newState) {
        case APPEARED:
            return afterAppearanceJob;
        case DISAPPEARED:
            return afterDisappearanceJob;
        case UPDATED:
            return afterModificationJob;
        default:
            return null;
        }
    } 
}
