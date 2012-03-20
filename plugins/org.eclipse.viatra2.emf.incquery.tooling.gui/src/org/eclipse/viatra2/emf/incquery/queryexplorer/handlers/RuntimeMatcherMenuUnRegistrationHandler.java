package org.eclipse.viatra2.emf.incquery.queryexplorer.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.handlers.HandlerUtil;

public class RuntimeMatcherMenuUnRegistrationHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IStructuredSelection selection = (IStructuredSelection) HandlerUtil.getCurrentSelection(event);
		
		Object firstElement = selection.getFirstElement();
		if (firstElement != null && firstElement instanceof IFile) {
			IFile file = (IFile) firstElement;
			
			RuntimeMatcherUnRegistrationJob job = new RuntimeMatcherUnRegistrationJob(file);
			Display.getCurrent().syncExec(job);
		}		
		return null;
	}

}
