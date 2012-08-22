package org.eclipse.viatra2.emf.incquery.gui.wizards.internal.operations;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.ui.actions.WorkspaceModifyOperation;

public class CompositeWorkspaceModifyOperation extends WorkspaceModifyOperation {

	WorkspaceModifyOperation[] operations;
	private String description;
	
	public CompositeWorkspaceModifyOperation(
			WorkspaceModifyOperation[] operations, String description) {
		super();
		this.operations = operations;
		this.description = description;
	}

	@Override
	protected void execute(IProgressMonitor monitor) throws CoreException,
			InvocationTargetException, InterruptedException {
		monitor.beginTask(description, 10 * operations.length);
		for (WorkspaceModifyOperation op : operations) {
			op.run(new SubProgressMonitor(monitor, 10));
		}
		monitor.done();
	}

}
