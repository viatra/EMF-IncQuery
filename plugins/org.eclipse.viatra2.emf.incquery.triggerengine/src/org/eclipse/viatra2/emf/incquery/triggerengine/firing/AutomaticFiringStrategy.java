package org.eclipse.viatra2.emf.incquery.triggerengine.firing;

import org.eclipse.viatra2.emf.incquery.runtime.api.IPatternMatch;
import org.eclipse.viatra2.emf.incquery.triggerengine.Activation;
import org.eclipse.viatra2.emf.incquery.triggerengine.ActivationMonitor;
import org.eclipse.viatra2.emf.incquery.triggerengine.specific.RecordingActivation;

public class AutomaticFiringStrategy implements Runnable {

	private ActivationMonitor monitor;
	
	public AutomaticFiringStrategy(ActivationMonitor monitor) {
		this.monitor = monitor;
		run();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void run() {
		for (Activation<? extends IPatternMatch> a : monitor.getActivations()) {
			((RecordingActivation<IPatternMatch>) a).fireWithRecording();
		}
		monitor.clear();
	}
}
