package org.eclipse.viatra2.emf.incquery.queryexplorer.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.edit.domain.IEditingDomainProvider;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.viatra2.emf.incquery.queryexplorer.QueryExplorer;
import org.eclipse.viatra2.emf.incquery.queryexplorer.util.PartListener;

public class LoadModelHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		
		try {
			IEditorPart editorPart = HandlerUtil.getActiveEditor(event);
			
			HandlerUtil.getActivePart(event).getSite().getPage().addPartListener(new PartListener());
			
			if (editorPart instanceof IEditingDomainProvider) {
				IEditingDomainProvider providerEditor = (IEditingDomainProvider) editorPart;
				
				ResourceSet resourceSet = providerEditor.getEditingDomain()
						.getResourceSet();
				if (resourceSet.getResources().size() > 0) {
					QueryExplorer.viewerRoot.addPatternMatcherRoot(editorPart,
							resourceSet);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
}
