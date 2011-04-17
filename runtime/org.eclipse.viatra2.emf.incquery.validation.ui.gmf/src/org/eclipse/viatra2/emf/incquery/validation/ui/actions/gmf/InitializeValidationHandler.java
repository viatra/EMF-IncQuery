/*******************************************************************************
 * Copyright (c) 2004-2010 Gabor Bergmann and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Gabor Bergmann - initial API and implementation
 *******************************************************************************/

package org.eclipse.viatra2.emf.incquery.validation.ui.actions.gmf;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.gmf.runtime.diagram.ui.editparts.GraphicalEditPart;
import org.eclipse.gmf.runtime.notation.View;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.viatra2.emf.incquery.validation.core.ValidationUtil;

/**
 * @author Bergmann Gábor
 *
 */
public class InitializeValidationHandler extends AbstractHandler {

	/* (non-Javadoc)
	 * @see org.eclipse.core.commands.IHandler#execute(org.eclipse.core.commands.ExecutionEvent)
	 */
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IFile file = null;
		Resource resource = null;
		
		IEditorPart activeEditor = HandlerUtil.getActiveEditor(event);
		if (activeEditor!=null) {
			IEditorInput editorInput = activeEditor.getEditorInput();
			if (editorInput != null && editorInput instanceof FileEditorInput) {
				file = ((FileEditorInput)editorInput).getFile();
			}
		}
		if (file==null) throw new ExecutionException("Must invoke on an editor with file input");
		
		ISelection selection =  HandlerUtil.getCurrentSelectionChecked(event);
		Object firstElement = ((IStructuredSelection)selection).getFirstElement();
		if (firstElement != null && firstElement instanceof GraphicalEditPart) {
			GraphicalEditPart gep = (GraphicalEditPart)firstElement;
			Object model = gep.getModel();
			if (model != null && model instanceof View) {
				View model2 = (View)model;
				EObject element = model2.getElement();
				if (element != null) {//  && element instanceof Element) {
					resource = element.eResource();
				}
			}
		}
		if (file==null) throw new ExecutionException("Must select a node or diagram representing an UML model or model element.");
	
		try {
			ValidationUtil.initValidators(resource, file);
		}
		catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

}
