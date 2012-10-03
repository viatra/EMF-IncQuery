package org.eclipse.viatra2.emf.incquery.triggerengine;

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
import org.eclipse.viatra2.emf.incquery.runtime.exception.IncQueryException;
import org.eclipse.viatra2.emf.incquery.runtime.extensibility.MatcherFactoryRegistry;
import org.eclipse.viatra2.emf.incquery.triggerengine.firing.ActivationNotificationProvider;
import org.eclipse.viatra2.emf.incquery.triggerengine.specific.RecordingRule;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Pattern;

/**
 * An agenda contains rules defined for EMF-IncQuery patterns. 
 * It keeps track of the activations of the registered rules based on a given {@link IncQueryEngine}.
 * 
 * @author Tamas Szabo
 *
 */
public class Agenda implements ActivationNotificationProvider, Runnable {

	private IncQueryEngine iqEngine;
	private Set<Rule<IPatternMatch>> rules;
	private Set<Runnable> callbacks;
	private Set<ActivationMonitor> monitors;
	private Notifier notifier;
	private TransactionalEditingDomain editingDomain;
	private boolean allowMultipleFiring;
	
	public Agenda(IncQueryEngine iqEngine) {
		this(iqEngine, false);
	}
	
	public Agenda(IncQueryEngine iqEngine, boolean allowMultipleFiring) {
		this.iqEngine = iqEngine;
		this.rules = new HashSet<Rule<IPatternMatch>>();
		this.callbacks = new HashSet<Runnable>();
		this.monitors = new HashSet<ActivationMonitor>();
		this.notifier = iqEngine.getEmfRoot();
		this.editingDomain = TransactionUtil.getEditingDomain(notifier);
		this.allowMultipleFiring = allowMultipleFiring;
	}
	
	public Agenda(Notifier notifier) throws IncQueryException {
		this(EngineManager.getInstance().getIncQueryEngine(notifier));
	}
	
	public Agenda(Notifier notifier, boolean allowMultipleFiring) throws IncQueryException {
		this(EngineManager.getInstance().getIncQueryEngine(notifier), allowMultipleFiring);
	}

	public Rule<? extends IPatternMatch> createRule(Pattern pattern) {
		return createRule(pattern, false, false);
	}
	
	public Notifier getNotifier() {
		return notifier;
	}
	
	public TransactionalEditingDomain getEditingDomain() {
		return editingDomain;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Rule<? extends IPatternMatch> createRule(Pattern pattern, boolean upgradedStateUsed, boolean disappearedStateUsed) {
		IMatcherFactory factory = MatcherFactoryRegistry.getOrCreateMatcherFactory(pattern);
		RecordingRule<IPatternMatch> r = null;
		try {
			r = new RecordingRule<IPatternMatch>(this, factory.getMatcher(iqEngine), upgradedStateUsed, disappearedStateUsed, allowMultipleFiring);
			rules.add(r);
		} catch (IncQueryException e) {
			e.printStackTrace();
		}
		return r;
	}

	/**
	 * This method removes a given Rule instance from the given agenda.
	 * 
	 * @param rule the rule to remove
	 */
	public void removeRule(RecordingRule<IPatternMatch> rule) {
		if (rules.contains(rule)) {
			rule.dispose();
			rules.remove(rule);
		}
	}
	
	public Collection<Rule<IPatternMatch>> getRules() {
		return rules;
	}
	
	public void dispose() {
		for (Rule<IPatternMatch> rule : rules) {
			rule.dispose();
		}
	}

	/**
	 * Returnes the IncQueryEngine instance associated with the agenda.
	 * 
	 * @return
	 */
	public IncQueryEngine getIqEngine() {
		return iqEngine;
	}

	private Collection<Activation<? extends IPatternMatch>> getActivations() {
		Collection<Activation<? extends IPatternMatch>> activations = new HashSet<Activation<? extends IPatternMatch>>();
		for (Rule<? extends IPatternMatch> r : rules) {
			activations.addAll(r.getActivations());
		}
		return Collections.unmodifiableCollection(activations);
	}

	@Override
	public void run() {
		Collection<Activation<? extends IPatternMatch>> activations = getActivations();
		for (ActivationMonitor monitor : monitors) {
			monitor.addActivations(activations);
		}
		for (Runnable callback : callbacks) {
			callback.run();
		}
	}

	@Override
	public boolean addCallbackAfterUpdates(Runnable callback) {
		return this.callbacks.add(callback);
	}

	@Override
	public boolean removeCallbackAfterUpdates(Runnable callback) {
		return this.callbacks.remove(callback);
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
}
