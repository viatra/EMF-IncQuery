package org.eclipse.viatra2.emf.incquery.databinding.ui.actions;

import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.edit.domain.IEditingDomainProvider;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.viatra2.emf.incquery.databinding.ui.MatchSetViewer;

/**
 * Responsible to handle the 'Unload model' viewAction.
 * The treeviewer will show only IEditingDomainProviders.
 * 
 * @author Tamas Szabo
 *
 */
public class UnloadModelActionDelegate implements IViewActionDelegate {
	
	private MatchSetViewer viewer;
	
	@Override
	public void run(IAction action) {
		try {
			IEditorPart editorPart = viewer.getSite().getWorkbenchWindow().getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
			
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
		viewer.getTableViewer().setInput(null);
	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		
	}

	@Override
	public void init(IViewPart view) {
		if (view instanceof MatchSetViewer) {
			viewer = ((MatchSetViewer) view);
		}
	}

}
