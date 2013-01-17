package org.eclipse.incquery.tooling.ui.patternregistry.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.xtext.ui.resource.IResourceSetProvider;

import com.google.inject.Inject;

public class RegisterSingleHandler extends AbstractHandler {

    @Inject
    private IResourceSetProvider resourceSetProvider;

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        try {
            ISelection selection = HandlerUtil.getCurrentSelection(event);
            if (selection != null && selection instanceof IStructuredSelection) {
                IStructuredSelection structured = (IStructuredSelection) selection;
                IFile file = (IFile) structured.getFirstElement();
                RegisterHandlersUtil.registerSingleFile(file, resourceSetProvider);
            }
        } catch (Exception exception) {
            throw new ExecutionException("Error loading eiq file.", exception);
        }
        return null;
    }

}
