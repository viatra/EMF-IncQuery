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
import org.eclipse.viatra2.emf.incquery.queryexplorer.QueryExplorer;
import org.eclipse.viatra2.emf.incquery.queryexplorer.content.matcher.MatcherTreeViewerRootKey;
import org.eclipse.viatra2.emf.incquery.queryexplorer.handlers.util.ContentModel;
import org.eclipse.viatra2.emf.incquery.queryexplorer.handlers.util.EMFContentModel;

/**
 * Default Resource and EObject loader. 
 * 
 * @author Tamas Szabo
 *
 */
public class LoadResourceHandler extends LoadModelHandler {
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IEditorPart editorPart = HandlerUtil.getActiveEditor(event);
		
		if (editorPart instanceof ISelectionProvider) {
			ISelectionProvider selectionProvider = (ISelectionProvider) editorPart;
			if (selectionProvider.getSelection() instanceof TreeSelection) {
				Object object = ((TreeSelection) selectionProvider.getSelection()).getFirstElement();
				Resource resource = null;
				if (object instanceof Resource) {
					resource = (Resource) object;
				}
				else if (object instanceof EObject) {
					resource = ((EObject) object).eResource();
				}
				MatcherTreeViewerRootKey key = new MatcherTreeViewerRootKey(editorPart, resource);
				ContentModel contentModel = new EMFContentModel(key);
				QueryExplorer.contentModelMap.put(key, contentModel);
				contentModel.loadModel();
			}
		}
		
		return null;
	}
}
