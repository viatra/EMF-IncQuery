package org.eclipse.viatra2.emf.incquery.triggerengine.api;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.emf.transaction.util.TransactionUtil;
import org.eclipse.viatra2.emf.incquery.base.api.NavigationHelper;
import org.eclipse.viatra2.emf.incquery.runtime.api.IMatcherFactory;
import org.eclipse.viatra2.emf.incquery.runtime.api.IPatternMatch;
import org.eclipse.viatra2.emf.incquery.runtime.api.IncQueryEngine;
import org.eclipse.viatra2.emf.incquery.runtime.api.IncQueryMatcher;
import org.eclipse.viatra2.emf.incquery.runtime.exception.IncQueryException;
import org.eclipse.viatra2.emf.incquery.triggerengine.firing.AutomaticFiringStrategy;
import org.eclipse.viatra2.emf.incquery.triggerengine.firing.IQBaseCallbackUpdateCompleteProvider;
import org.eclipse.viatra2.emf.incquery.triggerengine.firing.TimedFiringStrategy;
import org.eclipse.viatra2.emf.incquery.triggerengine.firing.TransactionUpdateCompleteProvider;
import org.eclipse.viatra2.emf.incquery.triggerengine.firing.UpdateCompleteProvider;
import org.eclipse.viatra2.emf.incquery.triggerengine.notification.ActivationNotificationProvider;
import org.eclipse.viatra2.emf.incquery.triggerengine.notification.IActivationNotificationListener;

/**
 * An Agenda is associated to each EMF instance model (more precisely {@link IncQueryEngine}
 * or equivalently {@link Notifier} in the context of EMF-IncQuery) and it is 
 * responsible for creating, managing and disposing rules in the AbstractRule Engine. 
 * It provides an unmodifiable view for the collection of applicable activations.
 * 
 * <p>One must register an {@link IActivationNotificationListener} in order to receive 
 * notifications automatically about the changes in the collection of activations.
 * 
 * <p>The Trigger Engine is a collection of strategies which can be used to 
 * fire these activations with pre-defined timings. Such strategies include 
 * the {@link AutomaticFiringStrategy} and {@link TimedFiringStrategy} at the current 
 * state of development. 
 * 
 * <p>Note that, one may instantiate an {@link ActivationMonitor} in order to 
 * process the activations on an individual basis, because the {@link Agenda} always reflects 
 * the most up-to-date state of the activations. 
 * 
 * <p>One may define whether multiple firing of the same activation is allowed; that is, only 
 * the Appeared state will be used from the lifecycle of {@link Activation}s and consecutive 
 * firing of a previously applied {@link Activation} is possible. For more 
 * information on the lifecycle see {@link Activation}. Multiple firing is used 
 * for example in Design Space Exploration scenarios. 
 * 
 * @author Tamas Szabo
 *
 */
public class Agenda extends ActivationNotificationProvider 
	implements IActivationNotificationListener {

	private IncQueryEngine iqEngine;
	private Set<Rule<? extends IPatternMatch>> rules;
	private Set<ActivationMonitor> monitors;
	private Notifier notifier;
	private TransactionalEditingDomain editingDomain;
	private boolean allowMultipleFiring;
	private RuleFactory ruleFactory;
	private Collection<Activation<? extends IPatternMatch>> activations;
	private UpdateCompleteProvider updateCompleteProvider;
	
	/**
	 * Instantiates a new Agenda instance with the given {@link IncQueryEngine}.
	 * Multiple firing of the same activation is not allowed.
	 * 
	 * @param iqEngine the {@link IncQueryEngine} instance
	 */
	protected Agenda(IncQueryEngine iqEngine) {
		this(iqEngine, false);
	}
	
	/**
	 * Instantiates a new Agenda instance with the given {@link IncQueryEngine} 
	 * and sets whether multiple allowing is allowed. 
	 * 
	 * @param iqEngine the {@link IncQueryEngine} instance
	 * @param allowMultipleFiring indicates whether multiple firing is allowed
	 */
	protected Agenda(IncQueryEngine iqEngine, boolean allowMultipleFiring) {
		this.iqEngine = iqEngine;
		this.rules = new HashSet<Rule<? extends IPatternMatch>>();
		this.monitors = new HashSet<ActivationMonitor>();
		this.notifier = iqEngine.getEmfRoot();
		this.editingDomain = TransactionUtil.getEditingDomain(notifier);
		this.allowMultipleFiring = allowMultipleFiring;
		this.activations = new HashSet<Activation<? extends IPatternMatch>>();
		
		if (this.editingDomain != null) {
		    updateCompleteProvider = new TransactionUpdateCompleteProvider(editingDomain);
	    } else {
	        NavigationHelper helper;
            try {
                helper = iqEngine.getBaseIndex();
                updateCompleteProvider = new IQBaseCallbackUpdateCompleteProvider(helper);
            } catch (IncQueryException e) {
                getLogger().error("The base index cannot be constructed for the engine!", e);
            }
	    }
	}
	
	/**
	 * Sets the {@link RuleFactory} for the Agenda.
	 * 
	 * @param ruleFactory the {@link RuleFactory} instance
	 */
	public void setRuleFactory(RuleFactory ruleFactory) {
		this.ruleFactory = ruleFactory;
	}
	
	/**
	 * Returns the {@link Notifier} instance associated to the Agenda. 
	 * 
	 * @return the {@link Notifier} instance
	 */
	public Notifier getNotifier() {
		return notifier;
	}
	
	/**
	 * Returns the {@link TransactionalEditingDomain} for the underlying {@link Notifier} 
	 * (associated to the Agenda) if it is available. 
	 * 
	 * @return the {@link TransactionalEditingDomain} instance or null if it is not available
	 */
	public TransactionalEditingDomain getEditingDomain() {
		return editingDomain;
	}
	
	/**
     * @return the allowMultipleFiring
     */
    public boolean isAllowMultipleFiring() {
        return allowMultipleFiring;
    }

    /**
	 * Creates a new rule with the specified {@link RuleFactory}.
	 * The upgraded and disappeared states will not be used in the 
	 * lifecycle of rule's activations. 
	 * 
	 * @param factory the {@link IMatcherFactory} of the {@link IncQueryMatcher}
	 * @return the {@link AbstractRule} instance
	 */
	public <Match extends IPatternMatch, Matcher extends IncQueryMatcher<Match>> 
	Rule<Match> createRule(IMatcherFactory<Matcher> factory) {
		return createRule(factory, false, false);
	}
	
	/**
	 * Creates a new rule with the specified {@link RuleFactory}.
	 * 
	 * @param factory the {@link IMatcherFactory} of the {@link IncQueryMatcher}
	 * @param upgradedStateUsed indicates whether the upgraded state is used in the lifecycle of the rule's activations
	 * @param disappearedStateUsed indicates whether the disappeared state is used in the lifecycle of the rule's activations
	 * @return the {@link AbstractRule} instance
	 */
	public <Match extends IPatternMatch, Matcher extends IncQueryMatcher<Match>> 
		Rule<Match> createRule(IMatcherFactory<Matcher> factory, boolean upgradedStateUsed, boolean disappearedStateUsed) {
		AbstractRule<Match> rule = ruleFactory.
				createRule(iqEngine, factory, upgradedStateUsed, disappearedStateUsed);
		rule.addActivationNotificationListener(this, true);
		rules.add(rule);
		return rule;
	}

	/**
	 * Removes a rule from the Agenda. 
	 * 
	 * @param rule the rule to remove
	 */
	public <MatchType extends IPatternMatch> void removeRule(AbstractRule<MatchType> rule) {
		if (rules.contains(rule)) {
			rule.removeActivationNotificationListener(this);
			rule.dispose();
			rules.remove(rule);
		}
	}
	
	/**
	 * Returns the rules that were created in this Agenda instance. 
	 * 
	 * @return the collection of rules
	 */
	public Collection<Rule<? extends IPatternMatch>> getRules() {
		return rules;
	}
	
	/**
	 * Call this method to properly dispose the Agenda. 
	 */
	public void dispose() {
	    if(updateCompleteProvider != null) {
	        updateCompleteProvider.dispose();
	    }
	    for (Rule<? extends IPatternMatch> rule : rules) {
			rule.dispose();
		}
	}

	/**
	 * Returns the logger associated with the Agenda.
	 * 
	 * @return
	 */
	public Logger getLogger() {
		return iqEngine.getLogger();
	}

	/**
	 * Returns an unmodifiable collection of the applicable activations.
	 * 
	 * @return the collection of activations
	 */
	public Collection<Activation<? extends IPatternMatch>> getActivations() {
		return Collections.unmodifiableCollection(activations);
	}
	
	/**
     * @return the updateCompleteProvider
     */
    public UpdateCompleteProvider getUpdateCompleteProvider() {
        return updateCompleteProvider;
    }

    /**
     * @param updateCompleteProvider the updateCompleteProvider to set
     */
    public void setUpdateCompleteProvider(UpdateCompleteProvider updateCompleteProvider) {
        this.updateCompleteProvider = updateCompleteProvider;
    }

    @Override
	public void activationAppeared(Activation<? extends IPatternMatch> activation) {
		this.activations.add(activation);
		
		for (ActivationMonitor monitor : monitors) {
			monitor.addActivation(activation);
		}
		
		notifyActivationAppearance(activation);
	}

	@Override
	public void activationDisappeared(Activation<? extends IPatternMatch> activation) {
		this.activations.remove(activation);
		
		for (ActivationMonitor monitor : monitors) {
			monitor.removeActivation(activation);
		}
		
		notifyActivationDisappearance(activation);
	}
	
	public ActivationMonitor newActivationMonitor(boolean fillAtStart) {
		ActivationMonitor monitor = new ActivationMonitor();
		if (fillAtStart) {
			for (Activation<? extends IPatternMatch> activation : activations) {
				monitor.addActivation(activation);
			}
		}
		monitors.add(monitor);
		return monitor;
	}

	@Override
	public boolean addActivationNotificationListener(IActivationNotificationListener listener, boolean fireNow) {
		boolean notContained = this.activationNotificationListeners.add(listener);
		if (notContained && fireNow) {
			for (Activation<? extends IPatternMatch> activation : activations) {
				listener.activationAppeared(activation);
			}
		}
		return notContained;
	}
}
