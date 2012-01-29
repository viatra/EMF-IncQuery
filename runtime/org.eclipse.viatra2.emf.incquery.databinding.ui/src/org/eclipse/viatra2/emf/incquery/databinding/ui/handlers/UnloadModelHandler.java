package org.eclipse.viatra2.emf.incquery.databinding.ui.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.edit.domain.IEditingDomainProvider;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.viatra2.emf.incquery.databinding.ui.MatchSetViewer;

public class UnloadModelHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		
		try {
			IEditorPart editorPart = HandlerUtil.getActiveEditor(event);
			
			if (editorPart instanceof IEditingDomainProvider) {
				IEditingDomainProvider providerEditor = (IEditingDomainProvider) editorPart;
				ResourceSet resourceSet = providerEditor.getEditingDomain().getResourceSet();
				if (resourceSet.getResources().size() > 0) {
					MatchSetViewer.viewerRoot.removePatternMatcherRoot(editorPart, resourceSet);
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		MatchSetViewer.tableViewer.setInput(null);
		
		return null;
	}
}
