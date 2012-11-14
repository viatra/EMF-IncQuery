package org.eclipse.viatra2.emf.incquery.triggerengine.firing;

import org.eclipse.viatra2.emf.incquery.runtime.api.IPatternMatch;
import org.eclipse.viatra2.emf.incquery.triggerengine.api.Activation;
import org.eclipse.viatra2.emf.incquery.triggerengine.api.ActivationMonitor;
import org.eclipse.viatra2.emf.incquery.triggerengine.notification.IActivationNotificationListener;
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
public class AutomaticFiringStrategy implements IActivationNotificationListener {

	@Override
	public void activationAppeared(Activation<? extends IPatternMatch> activation) {
		((RecordingActivation<? extends IPatternMatch>) activation).fireWithRecording();
	}

	@Override
	public void activationDisappeared(Activation<? extends IPatternMatch> activation) {
		
	}
}
