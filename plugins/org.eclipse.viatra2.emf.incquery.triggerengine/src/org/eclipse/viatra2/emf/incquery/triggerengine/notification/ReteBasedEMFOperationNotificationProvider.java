package org.eclipse.viatra2.emf.incquery.triggerengine.notification;

import org.eclipse.viatra2.emf.incquery.base.api.NavigationHelper;
import org.eclipse.viatra2.emf.incquery.runtime.api.IncQueryEngine;
import org.eclipse.viatra2.emf.incquery.runtime.exception.IncQueryException;
import org.eclipse.viatra2.emf.incquery.triggerengine.api.Agenda;

/**
 * The notification mechanism of this class is based on the {@link NavigationHelper} 
 * of the corresponding {@link IncQueryEngine}. (See {@link Agenda} for 
 * connection between {@link IncQueryEngine} and {@link Agenda}.) It notifies its 
 * listeners when the index of the {@link NavigationHelper} has changed. 
 * 
 * @author Tamas Szabo
 *
 */
public class ReteBasedEMFOperationNotificationProvider extends EMFOperationNotificationProvider {

	private Agenda agenda;
	private Runnable matchsetProcessor;
	private NavigationHelper helper;
	
	public ReteBasedEMFOperationNotificationProvider(Agenda agenda) {
		super();
		this.agenda = agenda;
		this.matchsetProcessor = new MatchSetProcessor();
		
		try {
			helper = this.agenda.getIqEngine().getBaseIndex();
		}
		catch (IncQueryException e) {
			this.agenda.getIqEngine().getLogger().error("The base index cannot be constructed for the engine!", e);
		}
		
		if (helper != null) {
			helper.getAfterUpdateCallbacks().add(matchsetProcessor);
		}
	}
	
	private class MatchSetProcessor implements Runnable {
		@Override
		public void run() {
			notfiyListeners();
			agenda.afterActivationUpdateCallback();
		}
	}

	@Override
	public void dispose() {
		if (helper != null) {
			helper.getAfterUpdateCallbacks().remove(matchsetProcessor);
		}
		this.listeners.clear();
	}
}
