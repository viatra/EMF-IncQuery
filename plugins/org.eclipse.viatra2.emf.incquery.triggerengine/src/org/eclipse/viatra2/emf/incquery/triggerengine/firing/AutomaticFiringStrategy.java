package org.eclipse.viatra2.emf.incquery.triggerengine.firing;

import org.eclipse.viatra2.emf.incquery.runtime.api.IPatternMatch;
import org.eclipse.viatra2.emf.incquery.triggerengine.Activation;
import org.eclipse.viatra2.emf.incquery.triggerengine.ActivationMonitor;
import org.eclipse.viatra2.emf.incquery.triggerengine.notification.ActivationNotificationListener;
import org.eclipse.viatra2.emf.incquery.triggerengine.specific.RecordingActivation;

public class AutomaticFiringStrategy implements ActivationNotificationListener {

	private ActivationMonitor monitor;
	
	public AutomaticFiringStrategy(ActivationMonitor monitor) {
		this.monitor = monitor;
		afterActivationUpdateCallback();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void afterActivationUpdateCallback() {
		for (Activation<? extends IPatternMatch> a : monitor.getActivations()) {
			((RecordingActivation<IPatternMatch>) a).fireWithRecording();
		}
		monitor.clear();
	}
}
