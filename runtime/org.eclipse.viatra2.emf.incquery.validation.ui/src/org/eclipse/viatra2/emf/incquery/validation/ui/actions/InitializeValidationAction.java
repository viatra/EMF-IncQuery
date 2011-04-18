package org.eclipse.viatra2.emf.incquery.validation.ui.actions;


import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.viatra2.emf.incquery.runtime.exception.IncQueryRuntimeException;
import org.eclipse.viatra2.emf.incquery.validation.ui.editorlink.EditorBoundValidation;

public class InitializeValidationAction implements IViewActionDelegate, IEditorActionDelegate {

	private Object selectedElement=null;

	
	/**
	 * Reference to the currently active editor.
	 */
	private IEditorPart editor;
	/**
	 * Constructor for CreateMarkerAction.
	 */
	public InitializeValidationAction() {
		super();
	}

	public void init(IViewPart view){}
	/**
	 * @see IObjectActionDelegate#setActivePart(IAction, IWorkbenchPart)
	 */
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		this.editor = targetPart.getSite().getWorkbenchWindow().getActivePage().getActiveEditor();

	}

	/**
	 * @see IActionDelegate#run(IAction)
	 */
	public void run(IAction action) {
		this.editor = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
		if (selectedElement instanceof EObject) {
			EObject selectedElement2 = (EObject)selectedElement;
			Resource eResource = selectedElement2.eResource();
			Notifier emfRoot = eResource == null ? selectedElement2 : eResource;
			
			try {
				EditorBoundValidation.INSTANCE.initializeValidatorsOnEditor(editor, emfRoot);
			} catch (IncQueryRuntimeException e) {
				e.printStackTrace();
			}
		}
	}

	
	/**
	 * @see IActionDelegate#selectionChanged(IAction, ISelection)
	 */
	public void selectionChanged(IAction action, ISelection selection) {
		action.setEnabled(false);
		//look for selected Element
		try {
			Object e=((IStructuredSelection)selection).getFirstElement();
			System.out.println(e);
			if (e instanceof EObject) {
				// this action is only compatible with EObjects
				action.setEnabled(true);
				this.selectedElement=e;
				
			}
		} catch (Throwable t) {
			t.printStackTrace(); // TODO this is ugly
		}

	}
	public void setActiveEditor(IAction action, IEditorPart targetEditor){
		this.editor = targetEditor;
	}
}
