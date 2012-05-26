package org.eclipse.viatra2.emf.incquery.validation.runtime;

import java.util.Set;

import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.viatra2.emf.incquery.runtime.api.IPatternMatch;

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
		if (part instanceof IEditorPart) {
			IEditorPart closedEditor = (IEditorPart) part;
			
			Set<ConstraintAdapter<IPatternMatch>> adapters = ValidationUtil.getAdapterMap().get(closedEditor);
			
			if (adapters != null) {
				for (ConstraintAdapter<IPatternMatch> adapter : adapters) {
					adapter.dispose();
				}
			}
			
			closedEditor.getEditorSite().getPage().removePartListener(ValidationUtil.editorPartListener);
		}
	}

	@Override
	public void partDeactivated(IWorkbenchPart part) {

	}

	@Override
	public void partOpened(IWorkbenchPart part) {

	}
}
