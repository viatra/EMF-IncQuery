package org.eclipse.viatra2.emf.incquery.triggerengine.notification;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.incquery.runtime.api.IPatternMatch;
import org.eclipse.viatra2.emf.incquery.triggerengine.api.Activation;

/**
 * Classes implement this interface to provide notifications about the changes in the 
 * collection of activations within the AbstractRule Engine. 
 * 
 * @author Tamas Szabo
 *
 */
public abstract class ActivationNotificationProvider implements IActivationNotificationProvider {
	
	protected Set<IActivationNotificationListener> activationNotificationListeners;
	
	public ActivationNotificationProvider() {
		this.activationNotificationListeners = new HashSet<IActivationNotificationListener>();
	}
	
	/* (non-Javadoc)
     * @see org.eclipse.viatra2.emf.incquery.triggerengine.notification.IActivationNotificationProvider#addActivationNotificationListener(org.eclipse.viatra2.emf.incquery.triggerengine.notification.IActivationNotificationListener, boolean)
     */
	@Override
    public boolean addActivationNotificationListener(IActivationNotificationListener listener, boolean fireNow) {
	    boolean notContained = this.activationNotificationListeners.add(listener);
        if (notContained) {
            listenerAdded(listener, fireNow);
        }
        return notContained;
	}
	
	protected abstract void listenerAdded(IActivationNotificationListener listener, boolean fireNow);
	
	/* (non-Javadoc)
     * @see org.eclipse.viatra2.emf.incquery.triggerengine.notification.IActivationNotificationProvider#removeActivationNotificationListener(org.eclipse.viatra2.emf.incquery.triggerengine.notification.IActivationNotificationListener)
     */
	@Override
    public boolean removeActivationNotificationListener(IActivationNotificationListener listener) {
		return this.activationNotificationListeners.remove(listener);
	}
		
	public void notifyActivationAppearance(Activation<? extends IPatternMatch> activation) {
		for (IActivationNotificationListener listener : this.activationNotificationListeners) {
			listener.activationAppeared(activation);
		}
	}
	
	public void notifyActivationDisappearance(Activation<? extends IPatternMatch> activation) {
		for (IActivationNotificationListener listener : this.activationNotificationListeners) {
			listener.activationDisappeared(activation);
		}
	}
}
