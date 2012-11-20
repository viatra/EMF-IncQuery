package org.eclipse.viatra2.emf.incquery.triggerengine.api;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.emf.transaction.util.TransactionUtil;
import org.eclipse.viatra2.emf.incquery.runtime.api.IMatcherFactory;
import org.eclipse.viatra2.emf.incquery.runtime.api.IPatternMatch;
import org.eclipse.viatra2.emf.incquery.runtime.api.IncQueryEngine;
import org.eclipse.viatra2.emf.incquery.runtime.api.IncQueryMatcher;
import org.eclipse.viatra2.emf.incquery.triggerengine.firing.AutomaticFiringStrategy;
import org.eclipse.viatra2.emf.incquery.triggerengine.firing.TimedFiringStrategy;
import org.eclipse.viatra2.emf.incquery.triggerengine.notification.ActivationNotificationProvider;
import org.eclipse.viatra2.emf.incquery.triggerengine.notification.IActivationNotificationListener;
import org.eclipse.viatra2.emf.incquery.triggerengine.specific.RecordingRule;

/**
 * An Agenda is associated to each EMF instance model (more precisely {@link IncQueryEngine}
 * or equivalently {@link Notifier} in the context of EMF-IncQuery) and it is 
 * responsible for creating, managing and disposing rules in the Rule Engine. 
 * It provides an unmodifiable view for the collection of applicable activations.
 * <br/><br/>
 * One must register an {@link IActivationNotificationListener} in order to receive 
 * notifications automatically about the changes in the collection of activations.
 * <br/><br/>
 * The Trigger Engine is a collection of strategies which can be used to 
 * fire these activations with pre-defined timings. Such strategies include 
 * the {@link AutomaticFiringStrategy} and {@link TimedFiringStrategy} at the current 
 * state of development. 
 * <br/><br/>
 * Note that, one may instantiate an {@link ActivationMonitor} in order to 
 * process the activations on an individual basis, because the {@link Agenda} always reflects 
 * the most up-to-date state of the activations. 
 * 
 * One may define whether multiple firing of the same activation is allowed; that is, only 
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
	 * Creates a new rule with the specified {@link RuleFactory}.
	 * The upgraded and disappeared states will not be used in the 
	 * lifecycle of rule's activations. 
	 * 
	 * @param factory the {@link IMatcherFactory} of the {@link IncQueryMatcher}
	 * @return the {@link Rule} instance
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
	 * @return the {@link Rule} instance
	 */
	public <Match extends IPatternMatch, Matcher extends IncQueryMatcher<Match>> 
		Rule<Match> createRule(IMatcherFactory<Matcher> factory, boolean upgradedStateUsed, boolean disappearedStateUsed) {
		Rule<Match> rule = ruleFactory.
				createRule(iqEngine, factory, upgradedStateUsed, disappearedStateUsed, allowMultipleFiring);
		rule.addActivationNotificationListener(this, true);
		rules.add(rule);
		return rule;
	}

	/**
	 * Removes a rule from the Agenda. 
	 * 
	 * @param rule the rule to remove
	 */
	public void removeRule(RecordingRule<IPatternMatch> rule) {
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
		if (notContained) {
			for (Activation<? extends IPatternMatch> activation : activations) {
				listener.activationAppeared(activation);
			}
		}
		return notContained;
	}
}
