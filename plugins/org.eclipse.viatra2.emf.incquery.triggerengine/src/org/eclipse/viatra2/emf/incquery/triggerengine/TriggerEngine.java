package org.eclipse.viatra2.emf.incquery.triggerengine;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.viatra2.emf.incquery.runtime.api.EngineManager;
import org.eclipse.viatra2.emf.incquery.runtime.api.IncQueryEngine;
import org.eclipse.viatra2.emf.incquery.runtime.exception.IncQueryException;

public class TriggerEngine {

	private static TriggerEngine instance;
	private Map<IncQueryEngine, Agenda> agendaMap;
	
	public static TriggerEngine getInstance() {
		if (instance == null) {
			instance = new TriggerEngine();
		}
		return instance;
	}
	
	protected TriggerEngine() {
		this.agendaMap = new HashMap<IncQueryEngine, Agenda>();
	}
	
	
	
	public Agenda createAgenda(Notifier notifier) {
		IncQueryEngine engine;
		try {
			engine = EngineManager.getInstance().getIncQueryEngine(notifier);
			return createAgenda(engine);
		} catch (IncQueryException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public Agenda createAgenda(IncQueryEngine iqEngine) {
		Agenda agenda = agendaMap.get(iqEngine);
		if (agenda == null) {
			agenda = new Agenda(iqEngine);
			agendaMap.put(iqEngine, agenda);
		}
		return agenda;
	}

	public Collection<Agenda> getAgendas() {
		return agendaMap.values();
	}
}
