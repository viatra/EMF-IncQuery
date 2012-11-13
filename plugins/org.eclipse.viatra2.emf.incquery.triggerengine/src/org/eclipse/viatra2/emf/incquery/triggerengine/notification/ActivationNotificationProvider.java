package org.eclipse.viatra2.emf.incquery.triggerengine.notification;

import org.eclipse.viatra2.emf.incquery.triggerengine.ActivationMonitor;

/**
 * Classes implement this interface to provide notifications about the changes in the 
 * collection of activations within the Rule Engine. 
 * 
 * @author Tamas Szabo
 *
 */
public interface ActivationNotificationProvider {
	
	/**
	 * Registers a listener that will be called each time the collection of activations is modified.
	 * 
	 * @param listener a ActivationNotificationListener to be called after each update
	 * 
	 * @return false if the callback was already registered
	 */
	public boolean addActivationNotificationListener(ActivationNotificationListener listener);
	
	/**
	 * Removes a previously registered listener. See addActivationNotificationListener().
	 * 
	 * @param listener the listener to remove
	 * 
	 * @return false if the callback was not registered
	 */
	public boolean removeActivationNotificationListener(ActivationNotificationListener listener);

	/**
	 * Instantiates a new {@link ActivationMonitor} that will keep track of the changes of activations.
	 * It can be used to monitor and process the activations on an individual basis rather then inspecting 
	 * the most up-to-date state in the {@link Agenda}.
	 * 
	 * @param fillAtStart indicates whether to initialize the monitor with the activations during creation
	 * 
	 * @return the {@link ActivationMonitor} instance
	 */
	public ActivationMonitor newActivationMonitor(boolean fillAtStart);
}
