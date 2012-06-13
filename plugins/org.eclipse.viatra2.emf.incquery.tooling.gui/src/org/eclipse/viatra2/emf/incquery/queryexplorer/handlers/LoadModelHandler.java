/*******************************************************************************
 * Copyright (c) 2010-2012, Zoltan Ujhelyi, Tamas Szabo, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Zoltan Ujhelyi, Tamas Szabo - initial API and implementation
 *******************************************************************************/

package org.eclipse.viatra2.emf.incquery.queryexplorer.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.edit.domain.IEditingDomainProvider;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.viatra2.emf.incquery.queryexplorer.QueryExplorer;

public class LoadModelHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {

		IEditorPart editorPart = HandlerUtil.getActiveEditor(event);

		if (editorPart instanceof IEditingDomainProvider) {
			IEditingDomainProvider providerEditor = (IEditingDomainProvider) editorPart;

			ResourceSet resourceSet = providerEditor.getEditingDomain()
					.getResourceSet();
			if (resourceSet.getResources().size() > 0) {
				HandlerUtil.getActivePart(event).getSite().getPage().addPartListener(
								QueryExplorer.getInstance().getModelPartListener());
				QueryExplorer.getInstance().getMatcherTreeViewerRoot().addPatternMatcherRoot(editorPart, resourceSet);
			}
		}

		return null;
	}
}
