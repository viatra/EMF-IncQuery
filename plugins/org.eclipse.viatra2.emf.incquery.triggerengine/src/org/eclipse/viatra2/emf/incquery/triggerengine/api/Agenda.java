package org.eclipse.viatra2.emf.incquery.triggerengine.api;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.emf.transaction.util.TransactionUtil;
import org.eclipse.viatra2.emf.incquery.runtime.api.EngineManager;
import org.eclipse.viatra2.emf.incquery.runtime.api.IMatcherFactory;
import org.eclipse.viatra2.emf.incquery.runtime.api.IPatternMatch;
import org.eclipse.viatra2.emf.incquery.runtime.api.IncQueryEngine;
import org.eclipse.viatra2.emf.incquery.runtime.api.IncQueryMatcher;
import org.eclipse.viatra2.emf.incquery.runtime.exception.IncQueryException;
import org.eclipse.viatra2.emf.incquery.triggerengine.firing.AutomaticFiringStrategy;
import org.eclipse.viatra2.emf.incquery.triggerengine.firing.TimedFiringStrategy;
import org.eclipse.viatra2.emf.incquery.triggerengine.notification.ActivationNotificationListener;
import org.eclipse.viatra2.emf.incquery.triggerengine.notification.ActivationNotificationProvider;
import org.eclipse.viatra2.emf.incquery.triggerengine.notification.EMFOperationNotificationProvider;
import org.eclipse.viatra2.emf.incquery.triggerengine.notification.ReteBasedEMFOperationNotificationProvider;
import org.eclipse.viatra2.emf.incquery.triggerengine.notification.TransactionBasedEMFOperationNotificationProvider;
import org.eclipse.viatra2.emf.incquery.triggerengine.specific.RecordingRule;

/**
 * An Agenda is associated to each EMF instance model (more precisely {@link IncQueryEngine}
 * or equivalently {@link Notifier} in the context of EMF-IncQuery) and it is 
 * responsible for creating, managing and disposing rules in the Rule Engine. 
 * It provides an unmodifiable view for the collection of applicable activations.
 * <br/><br/>
 * One must register an {@link ActivationNotificationListener} in order to receive 
 * notifications automatically about the changes in the collection of activations.
 * <br/><br/>
 * The Trigger Engine is a collection of strategies which can be used to 
 * fire these activations with pre-defined timings. Such strategies include 
 * the {@link AutomaticFiringStrategy} and {@link TimedFiringStrategy} at the current 
 * state of development. 
 * <br/><br/>
 * Note that, one may instantiate an {@link ActivationMonitor} in order to 
 * process the activations on an individual basis, because the Agenda always reflects 
 * the most up-to-date state of the activations. 
 * 
 * One may define whether multiple firing of the same activation; that is only 
 * the Appeared state will be used from the lifecycle of activations. For more 
 * information on the lifecycle see {@link Activation}. Multiple firing is used 
 * for example in Design Space Exploration scenarios. 
 * 
 * @author Tamas Szabo
 *
 */
public class Agenda implements ActivationNotificationProvider, ActivationNotificationListener {

	private IncQueryEngine iqEngine;
	private Set<Rule<? extends IPatternMatch>> rules;
	private Set<ActivationNotificationListener> listeners;
	private Set<ActivationMonitor> monitors;
	private Notifier notifier;
	private TransactionalEditingDomain editingDomain;
	private boolean allowMultipleFiring;
	private RuleFactory ruleFactory;
	private EMFOperationNotificationProvider notificationProvider;
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
		this.listeners = new HashSet<ActivationNotificationListener>();
		this.monitors = new HashSet<ActivationMonitor>();
		this.notifier = iqEngine.getEmfRoot();
		this.editingDomain = TransactionUtil.getEditingDomain(notifier);
		this.allowMultipleFiring = allowMultipleFiring;
		this.activations = new HashSet<Activation<? extends IPatternMatch>>();
		
		if (this.editingDomain != null) {
			notificationProvider = new TransactionBasedEMFOperationNotificationProvider(this);
		}
		else {
			notificationProvider = new ReteBasedEMFOperationNotificationProvider(this);
		}
	}
	
	/**
	 * Instantiates a new Agenda instance with the given {@link Notifier}. 
	 * Multiple firing of the same activation is not allowed.
	 * 
	 * @param notifier the {@link Notifier} instance
	 * @throws IncQueryException
	 */
	protected Agenda(Notifier notifier) throws IncQueryException {
		this(EngineManager.getInstance().getIncQueryEngine(notifier));
	}
	
	/**
	 * Instantiates a new Agenda instance with the given {@link Notifier} 
	 * and sets whether multiple allowing is allowed.  
	 * 
	 * @param notifier the {@link Notifier} instance
	 * @param allowMultipleFiring indicates whether multiple firing is allowed
	 * @throws IncQueryException
	 */
	protected Agenda(Notifier notifier, boolean allowMultipleFiring) throws IncQueryException {
		this(EngineManager.getInstance().getIncQueryEngine(notifier), allowMultipleFiring);
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
		Rule<Match> rule = ruleFactory.createRule(iqEngine, factory, upgradedStateUsed, disappearedStateUsed, allowMultipleFiring);
		notificationProvider.addNotificationProviderListener(rule);
		
		//this call is necessary to initialize the collection of activations
		rule.afterEMFOperationListener();
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
			notificationProvider.removeNotificationProviderListener(rule);
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
		
		notificationProvider.dispose();
	}

	/**
	 * Returns the IncQueryEngine instance associated with the Agenda.
	 * 
	 * @return
	 */
	public IncQueryEngine getIqEngine() {
		return iqEngine;
	}

	/**
	 * Returns an unmodifiable collection of the applicable activations.
	 * 
	 * @return the collection of activations
	 */
	public Collection<Activation<? extends IPatternMatch>> getActivations() {
		activations.clear();
		for (Rule<? extends IPatternMatch> r : rules) {
			activations.addAll(r.getActivations());
		}
		return Collections.unmodifiableCollection(activations);
	}

	@Override
	public boolean addActivationNotificationListener(ActivationNotificationListener listener) {
		return this.listeners.add(listener);
	}

	@Override
	public boolean removeActivationNotificationListener(ActivationNotificationListener listener) {
		return this.listeners.remove(listener);
	}
	
	@Override
	public ActivationMonitor newActivationMonitor(boolean fillAtStart) {
		ActivationMonitor monitor = new ActivationMonitor();
		if (fillAtStart) {
			monitor.addActivations(getActivations());
		}
		monitors.add(monitor);
		return monitor;
	}

	@Override
	public void afterActivationUpdateCallback() {
		Collection<Activation<? extends IPatternMatch>> activations = getActivations();
		for (ActivationMonitor monitor : monitors) {
			monitor.addActivations(activations);
		}
		
		for (ActivationNotificationListener listener : listeners) {
			listener.afterActivationUpdateCallback();
		}
	}
}
