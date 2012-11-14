package org.eclipse.viatra2.emf.incquery.triggerengine.notification;

/**
 * The interface defines the semantics to listen to notifications after an EMF
 * operation. <br/>
 * <br/>
 * An implementing class is for example the {@link Rule} which is called back by
 * the {@link EMFOperationNotificationProvider} instance when it is notified
 * about the end of an EMF operation.
 * 
 * @author Tamas Szabo
 * 
 */
public interface EMFOperationNotificationListener {

	public void afterEMFOperationListener();

}
