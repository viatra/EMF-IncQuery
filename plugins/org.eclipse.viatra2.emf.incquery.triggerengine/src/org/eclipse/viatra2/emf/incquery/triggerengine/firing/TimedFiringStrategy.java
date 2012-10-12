package org.eclipse.viatra2.emf.incquery.triggerengine.firing;

import org.eclipse.viatra2.emf.incquery.runtime.api.IPatternMatch;
import org.eclipse.viatra2.emf.incquery.triggerengine.Activation;
import org.eclipse.viatra2.emf.incquery.triggerengine.ActivationMonitor;

public class TimedFiringStrategy implements Runnable {

	private long interval;
	private volatile boolean interrupted = false;
	private ActivationMonitor monitor;
	
	public TimedFiringStrategy(ActivationMonitor monitor, long interval) {
		this.interval = interval;
		this.monitor = monitor;
		new FiringThread().start();
	}
	
	private class FiringThread extends Thread {
		
		public FiringThread() {
			this.setName("TimedFiringStrategy [interval: "+interval+"]");
		}
		
		@Override
		public void run() {
			while (!interrupted) {
				for (Activation<? extends IPatternMatch> a : monitor.getActivations()) {
					a.fire();
				}
				
				try {
					Thread.sleep(interval);
				} 
				catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public void dispose() {
		interrupted = true;
	}

	@Override
	public void run() {}
}
