package org.eclipse.viatra2.emf.incquery.triggerengine.notification;

import org.eclipse.viatra2.emf.incquery.runtime.exception.IncQueryException;
import org.eclipse.viatra2.emf.incquery.triggerengine.Agenda;

public class ReteBasedEMFOperationNotificationProvider extends EMFOperationNotificationProvider {

	private Agenda agenda;
	private Runnable matchsetProcessor;
	
	public ReteBasedEMFOperationNotificationProvider(Agenda _agenda) {
		super();
		this.agenda = _agenda;
		this.matchsetProcessor = new Runnable() {
			@Override
			public void run() {
				notfiyListeners();
				agenda.afterActivationUpdateCallback();
			}
		};
		
		try {
			this.agenda.getIqEngine().getBaseIndex().getAfterUpdateCallbacks().add(matchsetProcessor);
		} 
		catch (IncQueryException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void dispose() {
		try {
			this.agenda.getIqEngine().getBaseIndex().getAfterUpdateCallbacks().remove(matchsetProcessor);
		} 
		catch (IncQueryException e) {
			e.printStackTrace();
		}
		this.listeners.clear();
	}
}
