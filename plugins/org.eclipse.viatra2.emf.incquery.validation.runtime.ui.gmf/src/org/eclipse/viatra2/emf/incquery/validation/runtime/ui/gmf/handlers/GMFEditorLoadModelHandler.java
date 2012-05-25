package org.eclipse.viatra2.emf.incquery.validation.runtime.ui.gmf.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.edit.domain.IEditingDomainProvider;
import org.eclipse.gmf.runtime.diagram.ui.resources.editor.parts.DiagramDocumentEditor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.viatra2.emf.incquery.queryexplorer.QueryExplorer;

public class GMFEditorLoadModelHandler extends AbstractHandler implements
		IHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IEditorPart editorPart = HandlerUtil.getActiveEditor(event);
		
		if (editorPart instanceof DiagramDocumentEditor) {
			DiagramDocumentEditor providerEditor = (DiagramDocumentEditor) editorPart;
			
			ResourceSet resourceSet = providerEditor.getEditingDomain().getResourceSet();
			if (resourceSet.getResources().size() > 0) {
				HandlerUtil.getActivePart(event).getSite().getPage().addPartListener(QueryExplorer.getInstance().getModelPartListener());
				QueryExplorer.getInstance().getMatcherTreeViewerRoot().addPatternMatcherRoot(editorPart, resourceSet);
			}
		}
		return null;
	}

}
