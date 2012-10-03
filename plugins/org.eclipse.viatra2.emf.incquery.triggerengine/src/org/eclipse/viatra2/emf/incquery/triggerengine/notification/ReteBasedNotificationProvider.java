package org.eclipse.viatra2.emf.incquery.triggerengine.notification;

import org.eclipse.viatra2.emf.incquery.runtime.api.IPatternMatch;
import org.eclipse.viatra2.emf.incquery.runtime.api.IncQueryMatcher;

public class ReteBasedNotificationProvider<T extends IPatternMatch> extends NotificationProvider<T> {

	private IncQueryMatcher<T> matcher;
	private Runnable matchsetProcessor;
	
	public ReteBasedNotificationProvider(IncQueryMatcher<T> matcher) {
		super();
		this.matcher = matcher;
		this.matchsetProcessor = new Runnable() {
			@Override
			public void run() {
				notfiyListeners();
			}
		};
		
		this.matcher.addCallbackAfterUpdates(matchsetProcessor);
	}

	@Override
	public void dispose() {
		this.matcher.removeCallbackAfterUpdates(matchsetProcessor);
		this.listeners.clear();
	}
}
