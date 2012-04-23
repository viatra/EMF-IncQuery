package org.eclipse.viatra2.emf.incquery.queryexplorer.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.ui.handlers.HandlerUtil;

import com.google.inject.Inject;
import com.google.inject.Injector;

public class RuntimeMatcherEditorRegistrationHandler extends AbstractHandler {

	@Inject
	Injector injector;
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IFile file = (IFile) HandlerUtil.getActiveEditorInput(event).getAdapter(IFile.class);	
		if (file != null) {
			RuntimeMatcherRegistrator registrator = new RuntimeMatcherRegistrator(file, injector);
			registrator.run();
		}
		return null;
	}

}
