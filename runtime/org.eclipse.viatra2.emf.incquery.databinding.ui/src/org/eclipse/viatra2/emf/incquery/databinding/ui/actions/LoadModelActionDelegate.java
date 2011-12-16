package org.eclipse.viatra2.emf.incquery.databinding.ui.actions;

import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.edit.domain.IEditingDomainProvider;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.viatra2.emf.incquery.databinding.ui.MatchSetViewer;

/**
 * Responsible to handle the 'Load model' viewAction.
 * The treeviewer will show only IEditingDomainProviders.
 * 
 * @author Tamas Szabo
 *
 */
public class LoadModelActionDelegate implements IViewActionDelegate {

	private MatchSetViewer viewer;
	private IWorkbenchPage page;

	@Override
	public void run(IAction action) {
		try {
			IEditorPart editorPart = page.getActiveEditor();
			
			if (editorPart instanceof IEditingDomainProvider) {
				IEditingDomainProvider providerEditor = (IEditingDomainProvider) editorPart;
				
				ResourceSet resourceSet = providerEditor.getEditingDomain()
						.getResourceSet();
				if (resourceSet.getResources().size() > 0) {
					MatchSetViewer.viewerRoot.addPatternMatcherRoot(editorPart,
							resourceSet);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {

	}

	@Override
	public void init(IViewPart view) {
		if (view instanceof MatchSetViewer) {
			viewer = (MatchSetViewer) view;
			page = viewer.getSite().getWorkbenchWindow().getWorkbench()
					.getActiveWorkbenchWindow().getActivePage();
			page.addPartListener(new PartListener());
		}
	}

	/**
	 * The PartListener is used to observer EditorPart close actions.
	 * 
	 * @author Tamas Szabo
	 *
	 */
	private class PartListener implements IPartListener {

		@Override
		public void partActivated(IWorkbenchPart part) {

		}

		@Override
		public void partBroughtToTop(IWorkbenchPart part) {

		}

		@Override
		public void partClosed(IWorkbenchPart part) {
			//IEditorPart closedEditorPart = part.getSite().getPage().getActiveEditor();

			if (part != null && part instanceof IEditorPart) {
				IEditorPart closedEditor = (IEditorPart) part;
				
				if (closedEditor instanceof IEditingDomainProvider) {
					ResourceSet resourceSet = ((IEditingDomainProvider) closedEditor).getEditingDomain().getResourceSet();
					if (resourceSet.getResources().size() > 0) {
						MatchSetViewer.viewerRoot.removePatternMatcherRoot(closedEditor, resourceSet);
					}
				}
			}

		}

		@Override
		public void partDeactivated(IWorkbenchPart part) {

		}

		@Override
		public void partOpened(IWorkbenchPart part) {

		}
	}
}
