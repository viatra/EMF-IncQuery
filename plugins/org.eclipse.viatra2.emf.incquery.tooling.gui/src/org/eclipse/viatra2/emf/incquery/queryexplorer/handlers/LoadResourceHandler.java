/*******************************************************************************
 * Copyright (c) 2010-2012, Tamas Szabo, Zoltan Ujhelyi, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Tamas Szabo, Zoltan Ujhelyi - initial API and implementation
 *******************************************************************************/

package org.eclipse.viatra2.emf.incquery.queryexplorer.handlers;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 * Default Resource and EObject loader. 
 * 
 * @author Tamas Szabo
 *
 */
public class LoadResourceHandler extends LoadModelHandler {
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IEditorPart editor = HandlerUtil.getActiveEditor(event);
		
		if (editor instanceof ISelectionProvider) {
			ISelectionProvider selectionProvider = (ISelectionProvider) editor;
			if (selectionProvider.getSelection() instanceof TreeSelection) {
				Object object = ((TreeSelection) selectionProvider.getSelection()).getFirstElement();
				if (object instanceof Resource) {
					loadModel(editor, (Resource) object);
				}
				else if (object instanceof EObject) {
					loadModel(editor, ((EObject) object).eResource());
				}
			}
		}
		
		return null;
	}
}
