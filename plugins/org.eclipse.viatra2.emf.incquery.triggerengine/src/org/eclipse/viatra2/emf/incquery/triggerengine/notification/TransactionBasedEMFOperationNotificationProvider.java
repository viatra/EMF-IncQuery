package org.eclipse.viatra2.emf.incquery.triggerengine.notification;

import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.emf.transaction.TransactionalEditingDomain.Lifecycle;
import org.eclipse.emf.transaction.TransactionalEditingDomainEvent;
import org.eclipse.emf.transaction.TransactionalEditingDomainListener;
import org.eclipse.emf.transaction.util.TransactionUtil;
import org.eclipse.emf.workspace.impl.EMFOperationTransaction;
import org.eclipse.viatra2.emf.incquery.triggerengine.api.Agenda;

/**
 * The notification mechanism of this class is based on the transactions 
 * occuring in the {@link TransactionalEditingDomain} of the associated {@link Agenda}.
 * The {@link TransactionBasedEMFOperationNotificationProvider} listens to the 
 * transaction closing events by registering a {@link TransactionListener}.
 * 
 * @author Tamas Szabo
 *
 */
public class TransactionBasedEMFOperationNotificationProvider extends EMFOperationNotificationProvider {

	private TransactionListener transactionListener;
	private TransactionalEditingDomain editingDomain;
	private Lifecycle lifecycle;
	private Agenda agenda;
	
	public TransactionBasedEMFOperationNotificationProvider(Agenda agenda) {
		super();
		this.agenda = agenda;
		this.transactionListener = new TransactionListener();
		this.editingDomain = agenda.getEditingDomain();
		this.lifecycle = TransactionUtil.getAdapter(this.editingDomain, Lifecycle.class);
		this.lifecycle.addTransactionalEditingDomainListener(transactionListener);
	}
	
	private class TransactionListener implements TransactionalEditingDomainListener {

		@Override
		public void transactionStarting(TransactionalEditingDomainEvent event) {}

		@Override
		public void transactionInterrupted(TransactionalEditingDomainEvent event) {}

		@Override
		public void transactionStarted(TransactionalEditingDomainEvent event) {}

		@Override
		public void transactionClosing(TransactionalEditingDomainEvent event) {}

		@Override
		public void transactionClosed(TransactionalEditingDomainEvent event) {
			boolean needsNotification = true;
			
			/*Omit notifications about the executions of the assigned jobs in the RecordingActivation
			Applying a rule in the Rule Engine will result the job to be executed under an 
			EMFOperationTransaction transaction*/
			if (event.getTransaction() instanceof EMFOperationTransaction) {
				EMFOperationTransaction transaction = (EMFOperationTransaction) event.getTransaction();
				// FIXME this is a really ugly hack!
				if (transaction.getCommand().getLabel().equals("RecordingActivation")) {
					needsNotification = false;
				}
			}
			
			if (needsNotification) {
				notfiyListeners();
				agenda.afterActivationUpdateCallback();
			}
		}

		@Override
		public void editingDomainDisposing(TransactionalEditingDomainEvent event) {}
	}

	@Override
	public void dispose() {
		this.lifecycle.removeTransactionalEditingDomainListener(transactionListener);
		this.listeners.clear();
	}
}
