package org.eclipse.viatra2.emf.incquery.triggerengine.notification;

/**
 * The interface defines the semantics to listen 
 * to notifications after the collection of 
 * activations are updated or modified. 
 * <br/><br/>
 * An implementing class is for example the {@link Agenda} 
 * which is called back by the {@link Rule} instances when 
 * those have updated the activations after an EMF operation. 
 * 
 * @author Tamas Szabo
 *
 */
public interface ActivationNotificationListener {

	public void afterActivationUpdateCallback();
	
}
