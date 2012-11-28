package org.eclipse.viatra2.emf.incquery.triggerengine.api;

import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.WeakHashMap;

import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.incquery.emf.incquery.runtime.exception.IncQueryException;
import org.eclipse.viatra2.emf.incquery.runtime.api.EngineManager;
import org.eclipse.viatra2.emf.incquery.runtime.api.IncQueryEngine;
import org.eclipse.viatra2.emf.incquery.triggerengine.specific.DefaultRuleFactory;

/**
 * The AbstractRule engine extends the functionality of EMF-IncQuery by 
 * providing the basic facilities to create transformation rules. 
 * A transformation rule consists of the precondition being an EMF-IncQuery pattern 
 * and the postcondition defined as a portion of an arbitrary Java code. 
 * 
 * <p>This class can be used to instantiate and lookup Agendas for a specific 
 * {@link Notifier} or {@link IncQueryEngine} instance. The Agenda acts as 
 * an up-to-date collection of the fireable rule activations (similar to the 
 * term known from the context of rule based expert systems). 
 * 
 * @author Tamas Szabo
 *
 */
public class RuleEngine {

	private static RuleEngine instance;
	private Map<IncQueryEngine, WeakReference<IAgenda>> agendaMap;
	private IRuleFactory defaultRuleFactory;
	
	public static synchronized RuleEngine getInstance() {
		if (instance == null) {
			instance = new RuleEngine();
		}
		return instance;
	}
	
	protected RuleEngine() {
		this.agendaMap = new WeakHashMap<IncQueryEngine, WeakReference<IAgenda>>();
		this.defaultRuleFactory = new DefaultRuleFactory();
	}
	
	public IAgenda getOrCreateAgenda(Notifier notifier) {
		return getOrCreateAgenda(notifier, false);
	}
	
	public IAgenda getOrCreateAgenda(Notifier notifier, boolean allowMultipleFiring) {
		IncQueryEngine engine;
		try {
			engine = EngineManager.getInstance().getIncQueryEngine(notifier);
			return getOrCreateAgenda(engine, allowMultipleFiring);
		} catch (IncQueryException e) {
			return null;
		}
	}
		
	public IAgenda getOrCreateAgenda(IncQueryEngine engine) {
		return getOrCreateAgenda(engine, false);
	}
	
	public IAgenda getOrCreateAgenda(IncQueryEngine engine, boolean allowMultipleFiring) {
		IAgenda agenda = getAgenda(engine);
		if (agenda == null) {
			Agenda newAgenda = new Agenda(engine, allowMultipleFiring);
			newAgenda.setRuleFactory(defaultRuleFactory);
			agenda = newAgenda;
			agendaMap.put(engine, new WeakReference<IAgenda>(agenda));
		}
		return agenda;
	}
	
	private IAgenda getAgenda(IncQueryEngine iqEngine) {
		WeakReference<IAgenda> agendaRef = agendaMap.get(iqEngine);
		if (agendaRef != null) {
			return agendaRef.get();
		}
		else {
			return null;
		}
	}
}
