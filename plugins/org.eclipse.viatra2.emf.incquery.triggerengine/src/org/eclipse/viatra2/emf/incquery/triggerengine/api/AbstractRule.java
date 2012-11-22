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
public abstract class AbstractRule<MatchType extends IPatternMatch> implements 
	IAttributeMonitorListener<MatchType>, IMatchUpdateListener<MatchType>, IRule<MatchType> {

	private IMatchProcessor<MatchType> afterAppearanceJob;
	private IMatchProcessor<MatchType> afterDisappearanceJob;
	private IMatchProcessor<MatchType> afterModificationJob;
	protected IAgenda agenda;
	protected boolean upgradedStateUsed;
	protected boolean disappearedStateUsed;
	protected IncQueryMatcher<MatchType> matcher;
	protected Map<ActivationState, Map<MatchType, Activation<MatchType>>> stateMap;
	protected AttributeMonitor<MatchType> attributeMonitor;
	protected ActivationNotificationProvider activationProvider;
	
	public AbstractRule(IAgenda agenda, 
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
		
		this.activationProvider = new ActivationNotificationProvider() {
            
            @Override
            protected void listenerAdded(IActivationNotificationListener listener, boolean fireNow) {
                if (fireNow) {
                    for (Activation<MatchType> activation : getActivations()) {
                        listener.activationAppeared(activation);
                    }
                }
            }
        };
	}
	
	/* (non-Javadoc)
     * @see org.eclipse.viatra2.emf.incquery.triggerengine.api.IRule#getPattern()
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
     * @see org.eclipse.viatra2.emf.incquery.triggerengine.api.IRule#getAgenda()
     */
	@Override
    public IAgenda getAgenda() {
		return agenda;
	}
	
	/* (non-Javadoc)
     * @see org.eclipse.viatra2.emf.incquery.triggerengine.api.IRule#getActivations()
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
    		activationProvider.notifyActivationAppearance(activation);
    	}
    	else {
    		activation = appearedMap.get(match);
    		if (activation != null && activation.isFired()) {
    			//changing activation state from appeared to upgraded
    			appearedMap.remove(match);
    			activation.setFired(false);
    			activation.setState(ActivationState.UPDATED);
    			updatedMap.put(match, activation);
    			activationProvider.notifyActivationAppearance(activation);
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
    		activationProvider.notifyActivationDisappearance(activation);
    	}
    	else {
    		activation = createActivation(match);
    		appearedMap.put(match, activation);
    		
    		if (upgradedStateUsed) {
    			attributeMonitor.registerFor(match);
    		}
    		
    		activationProvider.notifyActivationAppearance(activation);
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
    			activationProvider.notifyActivationAppearance(activation);
    		}
    		else {
    			appearedMap.remove(match);
    			//unregistering change listener from the affected observable values
    			attributeMonitor.unregisterFor(match);
    			activationProvider.notifyActivationDisappearance(activation);
    		}
    	}
    	else {
    		//changing activation state from updated to disappeared if it was fired
    		activation = updatedMap.get(match);
    		if (activation.isFired()) {
    		    activation.setFired(false);
    		}
            updatedMap.remove(match);
            activation.setState(ActivationState.DISAPPEARED);
            disappearedMap.put(match, activation);
   			activationProvider.notifyActivationAppearance(activation);
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

    /* (non-Javadoc)
     * @see org.eclipse.viatra2.emf.incquery.triggerengine.notification.IActivationNotificationProvider#addActivationNotificationListener(org.eclipse.viatra2.emf.incquery.triggerengine.notification.IActivationNotificationListener, boolean)
     */
    @Override
    public boolean addActivationNotificationListener(IActivationNotificationListener listener, boolean fireNow) {
        return activationProvider.addActivationNotificationListener(listener, fireNow);
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.viatra2.emf.incquery.triggerengine.notification.IActivationNotificationProvider#removeActivationNotificationListener(org.eclipse.viatra2.emf.incquery.triggerengine.notification.IActivationNotificationListener)
     */
    @Override
    public boolean removeActivationNotificationListener(IActivationNotificationListener listener) {
        return activationProvider.removeActivationNotificationListener(listener);
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.viatra2.emf.incquery.triggerengine.api.IRule#setStateChangeProcessor(org.eclipse.viatra2.emf.incquery.triggerengine.api.ActivationState, org.eclipse.viatra2.emf.incquery.runtime.api.IMatchProcessor)
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
     * @see org.eclipse.viatra2.emf.incquery.triggerengine.api.IRule#getStateChangeProcessor(org.eclipse.viatra2.emf.incquery.triggerengine.api.ActivationState)
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
