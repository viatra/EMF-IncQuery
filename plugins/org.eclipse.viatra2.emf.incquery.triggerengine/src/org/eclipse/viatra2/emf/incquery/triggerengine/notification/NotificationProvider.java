package org.eclipse.viatra2.emf.incquery.triggerengine.notification;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.viatra2.emf.incquery.runtime.api.IPatternMatch;

public abstract class NotificationProvider<T extends IPatternMatch> {

	protected List<NotificationProviderListener<T>> listeners;
	
	public NotificationProvider() {
		this.listeners = new ArrayList<NotificationProviderListener<T>>();
	}
	
	public void addNotificationProviderListener(NotificationProviderListener<T> listener) {
		this.listeners.add(listener);
	}
	
	public void removeNotificationProviderListener(NotificationProviderListener<T> listener) {
		this.listeners.remove(listener);
	}
	
	public void notfiyListeners() {
		for (NotificationProviderListener<T> listener : listeners) {
			listener.notificationCallback();
		}
	}
	
	public abstract void dispose();
}
