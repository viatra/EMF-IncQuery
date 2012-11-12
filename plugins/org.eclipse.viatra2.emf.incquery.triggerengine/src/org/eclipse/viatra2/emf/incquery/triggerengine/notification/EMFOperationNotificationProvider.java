package org.eclipse.viatra2.emf.incquery.triggerengine.notification;

import java.util.ArrayList;
import java.util.List;

/**
 * This is the base class for notification providers triggered by EMF operations. 
 * Subclasses are used by the {@link Agenda} to determine when the activations of 
 * the corresponding rules should be updated. 
 * 
 * @author Tamas Szabo
 *
 */
public abstract class EMFOperationNotificationProvider {

	protected List<EMFOperationNotificationListener> listeners;
	
	public EMFOperationNotificationProvider() {
		this.listeners = new ArrayList<EMFOperationNotificationListener>();
	}
	
	public void addNotificationProviderListener(EMFOperationNotificationListener listener) {
		this.listeners.add(listener);
	}
	
	public void removeNotificationProviderListener(EMFOperationNotificationListener listener) {
		this.listeners.remove(listener);
	}
	
	public void notfiyListeners() {
		for (EMFOperationNotificationListener listener : listeners) {
			listener.afterEMFOperationListener();
		}
	}
	
	public abstract void dispose();
}
