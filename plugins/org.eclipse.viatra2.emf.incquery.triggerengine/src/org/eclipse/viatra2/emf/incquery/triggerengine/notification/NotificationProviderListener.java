package org.eclipse.viatra2.emf.incquery.triggerengine.notification;

import org.eclipse.viatra2.emf.incquery.runtime.api.IPatternMatch;

public interface NotificationProviderListener<T extends IPatternMatch> {
	
	public void notificationCallback();
	
}
