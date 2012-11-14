package org.eclipse.viatra2.emf.incquery.triggerengine.notification;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.viatra2.emf.incquery.runtime.api.IPatternMatch;
import org.eclipse.viatra2.emf.incquery.triggerengine.api.Activation;

/**
 * Classes implement this interface to provide notifications about the changes in the 
 * collection of activations within the Rule Engine. 
 * 
 * @author Tamas Szabo
 *
 */
public abstract class ActivationNotificationProvider {
	
	protected Set<IActivationNotificationListener> activationNotificationListeners; 
	
	public ActivationNotificationProvider() {
		this.activationNotificationListeners = new HashSet<IActivationNotificationListener>();
	}
	
	/** 
	 * Registers an {@link IActivationNotificationListener} to receive updates on activation 
	 * appearance and disappearance.  
	 * 
	 * <p> The listener can be unregistered via 
	 * {@link #removeActivationNotificationListener(IActivationNotificationListener)}.
	 *  
	 * @param fireNow if true, listener will be immediately invoked on all current activations as a one-time effect. 
     *
	 * @param listener the listener that will be notified of each new activation that appears or disappears, 
	 * starting from now. 
	 */
	public abstract boolean addActivationNotificationListener(IActivationNotificationListener listener, boolean fireNow);
	
	/**
	 * Unregisters a listener registered by 
	 * {@link #addActivationNotificationListener(IActivationNotificationListener, boolean)}.
	 * 
	 * @param listener the listener that will no longer be notified. 
	 */
	public boolean removeActivationNotificationListener(IActivationNotificationListener listener) {
		return this.activationNotificationListeners.remove(listener);
	}
		
	protected void notifyActivationAppearance(Activation<? extends IPatternMatch> activation) {
		for (IActivationNotificationListener listener : this.activationNotificationListeners) {
			listener.activationAppeared(activation);
		}
	}
	
	protected void notifyActivationDisappearance(Activation<? extends IPatternMatch> activation) {
		for (IActivationNotificationListener listener : this.activationNotificationListeners) {
			listener.activationDisappeared(activation);
		}
	}
}
