package org.eclipse.viatra2.emf.incquery.queryexplorer.util;

import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.edit.domain.IEditingDomainProvider;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.viatra2.emf.incquery.queryexplorer.QueryExplorer;

/**
 * The PartListener is used to observe EditorPart close actions.
 * 
 * @author Tamas Szabo
 *
 */
public class ModelEditorPartListener implements IPartListener {
	
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
					if (QueryExplorer.getInstance() != null) {
						QueryExplorer.getInstance().getMatcherTreeViewerRoot().removePatternMatcherRoot(closedEditor, resourceSet);
					}
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
