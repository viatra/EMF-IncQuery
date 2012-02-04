package org.eclipse.viatra2.emf.incquery.matchsetviewer.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.handlers.HandlerUtil;

public class RuntimeMatcherEditorUnRegistrationHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IFile file = (IFile) HandlerUtil.getActiveEditorInput(event).getAdapter(IFile.class);	
		if (file != null) {
			RuntimeMatcherUnRegistrationJob job = new RuntimeMatcherUnRegistrationJob(file);
			Display.getCurrent().syncExec(job);
		}
		return null;
	}

}
