package org.eclipse.incquery.runtime.triggerengine.firing;

import org.eclipse.emf.transaction.RecordingCommand;
import org.eclipse.emf.transaction.TransactionalEditingDomain;

public class Action extends RecordingCommand {

    private Runnable task;

    public Action(TransactionalEditingDomain domain, Runnable task) {
        super(domain);
        this.task = task;
    }

    @Override
    protected void doExecute() {
        task.run();
    }

}
