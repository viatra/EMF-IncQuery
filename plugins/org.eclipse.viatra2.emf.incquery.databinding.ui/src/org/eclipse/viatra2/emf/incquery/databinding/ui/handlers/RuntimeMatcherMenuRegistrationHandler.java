package org.eclipse.viatra2.emf.incquery.databinding.ui.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;

public class RuntimeMatcherMenuRegistrationHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IStructuredSelection selection = (IStructuredSelection) HandlerUtil.getCurrentSelection(event);
	
		Object firstElement = selection.getFirstElement();
		if (firstElement != null && firstElement instanceof IFile) {
			IFile iFile = (IFile) firstElement;
			RuntimeMatcherRegistrationJob job = new RuntimeMatcherRegistrationJob("Runtime matcher registration", iFile);
			job.setUser(true);
			job.schedule();
		}
		
		return null;
	}

}
