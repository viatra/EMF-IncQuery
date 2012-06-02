package org.eclipse.viatra2.emf.incquery.validation.runtime.ui.handlers;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.edit.domain.IEditingDomainProvider;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.handlers.HandlerUtil;

public class InitValidatorsForEditorHandler extends InitValidatorsForSelectionHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IEditorPart activeEditor = HandlerUtil.getActiveEditor(event);
		
		if(activeEditor instanceof IEditingDomainProvider) {
			IEditingDomainProvider provider = (IEditingDomainProvider) activeEditor;
			ResourceSet resourceSet = provider.getEditingDomain().getResourceSet();
			if (resourceSet != null) {
				initializeAdapters(activeEditor, resourceSet);
			}
		}
		
		return null;
	}

}
