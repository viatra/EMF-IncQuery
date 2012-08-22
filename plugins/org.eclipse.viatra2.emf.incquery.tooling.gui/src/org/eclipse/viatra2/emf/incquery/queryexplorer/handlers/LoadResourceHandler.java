package org.eclipse.viatra2.emf.incquery.queryexplorer.handlers;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.handlers.HandlerUtil;

public class LoadResourceHandler extends LoadModelHandler {
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IEditorPart editor = HandlerUtil.getActiveEditor(event);
		
		if (editor instanceof ISelectionProvider) {
			ISelectionProvider selectionProvider = (ISelectionProvider) editor;
			if (selectionProvider.getSelection() instanceof TreeSelection) {
				Object object = ((TreeSelection) selectionProvider.getSelection()).getFirstElement();
				if (object instanceof Resource) {
					loadModel(event, editor, (Resource) object);
				}
				else if (object instanceof EObject) {
					loadModel(event, editor, ((EObject) object).eResource());
				}
			}
		}
		
		return null;
	}
}
