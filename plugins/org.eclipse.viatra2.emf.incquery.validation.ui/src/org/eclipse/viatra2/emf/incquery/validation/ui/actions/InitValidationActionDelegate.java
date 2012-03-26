package org.eclipse.viatra2.emf.incquery.validation.ui.actions;

import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.edit.domain.IEditingDomainProvider;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorPart;
import org.eclipse.viatra2.emf.incquery.runtime.exception.IncQueryRuntimeException;
import org.eclipse.viatra2.emf.incquery.validation.ui.editorlink.EditorBoundValidation;

/**
 * @author Bergmann Gábor
 * Solution for any editor that is IEditingDomainProvider.
 */
public class InitValidationActionDelegate implements IEditorActionDelegate {

	IEditorPart currentEditor;
	
	@Override
	public void run(IAction action) {
		
		try {
			if(currentEditor!=null && currentEditor instanceof IEditingDomainProvider)
			{
				IEditingDomainProvider providerEditor = (IEditingDomainProvider) currentEditor;
				ResourceSet resourceSet = providerEditor.getEditingDomain().getResourceSet();
				if (resourceSet!=null) {
					try {
						EditorBoundValidation.INSTANCE.initializeValidatorsOnEditor(currentEditor, resourceSet);
					} catch (IncQueryRuntimeException e) {
						e.printStackTrace();
					}
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
	public void setActiveEditor(IAction action, IEditorPart targetEditor) {

		this.currentEditor = targetEditor;
	}



}
