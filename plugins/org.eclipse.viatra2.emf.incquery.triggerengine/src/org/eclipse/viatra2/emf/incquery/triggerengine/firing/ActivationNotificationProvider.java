package org.eclipse.viatra2.emf.incquery.triggerengine.firing;

import org.eclipse.viatra2.emf.incquery.triggerengine.ActivationMonitor;


public interface ActivationNotificationProvider {
	
	/**
	 * Registers a callback that will be run each time the set of activations is modified.
	 * 
	 * @param callback a Runnable to execute after each update.
	 * 
	 * @return false if the callback was already registered.
	 */
	public boolean addCallbackAfterUpdates(Runnable callback);
	
	/**
	 * Removes a previously registered callback. See addCallbackAfterUpdates().
	 * 
	 * @param callback the callback to remove.
	 * 
	 * @return false if the callback was not registered.
	 */
	public boolean removeCallbackAfterUpdates(Runnable callback);

	/**
	 * Instantiates a new {@link ActivationMonitor} that will keep track of the changes of activations.
	 * 
	 * @param fillAtStart indicates whether to initialize the monitor with the activations during creation
	 * 
	 * @return the {@link ActivationMonitor} instance
	 */
	public ActivationMonitor newActivationMonitor(boolean fillAtStart);
}
