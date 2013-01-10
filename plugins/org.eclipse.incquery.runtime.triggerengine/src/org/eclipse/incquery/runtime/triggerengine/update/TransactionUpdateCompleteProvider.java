/*******************************************************************************
 * Copyright (c) 2010-2012, Abel Hegedus, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Abel Hegedus - initial API and implementation
 *******************************************************************************/
package org.eclipse.incquery.runtime.triggerengine.update;

import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.emf.transaction.TransactionalEditingDomain.Lifecycle;
import org.eclipse.emf.transaction.TransactionalEditingDomainEvent;
import org.eclipse.emf.transaction.TransactionalEditingDomainListener;
import org.eclipse.emf.transaction.util.TransactionUtil;
import org.eclipse.emf.workspace.impl.EMFOperationTransaction;
import org.eclipse.incquery.runtime.triggerengine.specific.RecordingJob;

/**
 * @author Abel Hegedus
 * 
 */
public class TransactionUpdateCompleteProvider extends UpdateCompleteProvider {
    private TransactionListener transactionListener;
    private TransactionalEditingDomain editingDomain;
    private Lifecycle lifecycle;

    public TransactionUpdateCompleteProvider(TransactionalEditingDomain editingDomain) {
        this.transactionListener = new TransactionListener();
        this.editingDomain = editingDomain;
        this.lifecycle = TransactionUtil.getAdapter(this.editingDomain, Lifecycle.class);
        this.lifecycle.addTransactionalEditingDomainListener(transactionListener);
    }

    private class TransactionListener implements TransactionalEditingDomainListener {

        @Override
        public void transactionStarting(TransactionalEditingDomainEvent event) {
        }

        @Override
        public void transactionInterrupted(TransactionalEditingDomainEvent event) {
        }

        @Override
        public void transactionStarted(TransactionalEditingDomainEvent event) {
        }

        @Override
        public void transactionClosing(TransactionalEditingDomainEvent event) {
        }

        @Override
        public void transactionClosed(TransactionalEditingDomainEvent event) {
            boolean needsNotification = true;

            /*
             * Omit notifications about the executions of the assigned jobs in the RecordingActivation Applying a rule
             * in the IRule Engine will result the job to be executed under an EMFOperationTransaction transaction
             */
            if (event.getTransaction() instanceof EMFOperationTransaction) {
                EMFOperationTransaction transaction = (EMFOperationTransaction) event.getTransaction();
                // FIXME this is a really ugly hack!
                if (transaction.getCommand().getLabel().equals(RecordingJob.RECORDING_JOB)) {
                    needsNotification = false;
                }
            }

            if (needsNotification) {
                updateCompleted();
            }
        }

        @Override
        public void editingDomainDisposing(TransactionalEditingDomainEvent event) {
        }
    }

    @Override
    public void dispose() {
        this.lifecycle.removeTransactionalEditingDomainListener(transactionListener);
        super.dispose();
    }
}
