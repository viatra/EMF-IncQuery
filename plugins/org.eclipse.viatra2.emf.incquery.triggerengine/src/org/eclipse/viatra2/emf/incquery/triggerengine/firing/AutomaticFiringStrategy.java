package org.eclipse.viatra2.emf.incquery.triggerengine.firing;

import org.eclipse.viatra2.emf.incquery.runtime.api.IPatternMatch;
import org.eclipse.viatra2.emf.incquery.triggerengine.api.Activation;
import org.eclipse.viatra2.emf.incquery.triggerengine.api.ActivationMonitor;
import org.eclipse.viatra2.emf.incquery.triggerengine.notification.ActivationNotificationListener;
import org.eclipse.viatra2.emf.incquery.triggerengine.specific.RecordingActivation;

/**
 * This class automatically fires the applicable activations 
 * which are present in the given {@link ActivationMonitor}.
 * It is used by the Validation Framework to automatically 
 * create/update/remove problem markers when it is needed.
 * 
 * @author Tamas Szabo
 *
 */
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
