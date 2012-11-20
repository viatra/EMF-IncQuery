package org.eclipse.viatra2.emf.incquery.triggerengine.notification;

import org.eclipse.viatra2.emf.incquery.runtime.api.IPatternMatch;
import org.eclipse.viatra2.emf.incquery.triggerengine.api.Activation;

/**
 * The interface is used to observe the changes in the collection of activations.
 * <br/><br/>
 * An implementing class is for example the {@link Agenda} 
 * which is called back by the {@link AbstractRule} instances when 
 * those have updated the activations after an EMF operation. 
 * 
 * @author Tamas Szabo
 *
 */
public interface IActivationNotificationListener {

	public void activationAppeared(Activation<? extends IPatternMatch> activation);
	
	public void activationDisappeared(Activation<? extends IPatternMatch> activation);
	
}
