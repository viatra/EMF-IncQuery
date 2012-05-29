package org.eclipse.viatra2.emf.incquery.queryexplorer.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.ui.handlers.HandlerUtil;

import com.google.inject.Inject;
import com.google.inject.Injector;

public class RegisterPatternFileHandler extends AbstractHandler {

	@Inject
	Injector injector;

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {

		try {
			IFile file = (IFile) HandlerUtil.getActiveEditorInput(event).getAdapter(IFile.class);
			if (file != null) {
				RuntimeMatcherRegistrator registrator = new RuntimeMatcherRegistrator(file);
				injector.injectMembers(registrator);
				registrator.run();
			}
		} catch (Exception e) {
			throw new ExecutionException("Cannot load pattern file", e);
		}

		return null;
	}
}
