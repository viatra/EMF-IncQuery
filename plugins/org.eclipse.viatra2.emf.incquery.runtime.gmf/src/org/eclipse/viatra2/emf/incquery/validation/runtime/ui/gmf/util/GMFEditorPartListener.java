package org.eclipse.viatra2.emf.incquery.validation.runtime.ui.gmf.util;

import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.gmf.runtime.diagram.ui.resources.editor.parts.DiagramDocumentEditor;
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
public class GMFEditorPartListener implements IPartListener {
	
	public static GMFEditorPartListener instance = new GMFEditorPartListener();
	
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
			if (closedEditor instanceof DiagramDocumentEditor) {
				DiagramDocumentEditor providerEditor = (DiagramDocumentEditor) closedEditor;
				
				ResourceSet resourceSet = providerEditor.getEditingDomain().getResourceSet();
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
