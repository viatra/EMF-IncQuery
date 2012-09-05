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

package org.eclipse.viatra2.emf.incquery.queryexplorer.util;

import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.edit.domain.IEditingDomainProvider;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.viatra2.emf.incquery.queryexplorer.QueryExplorer;
import org.eclipse.viatra2.emf.incquery.queryexplorer.content.matcher.MatcherTreeViewerRootKey;

/**
 * The PartListener is used to observe {@link IEditorPart} close actions.
 * 
 * @author Tamas Szabo
 *
 */
public class ModelEditorPartListener extends BasePartListener {
	
	private static ModelEditorPartListener instance;
	
	protected ModelEditorPartListener() {
		
	}
	
	public synchronized static ModelEditorPartListener getInstance() {
		if (instance == null) {
			instance = new ModelEditorPartListener();
		}
		return instance;
	}
	
	@Override
	public void partClosed(IWorkbenchPart part) {
		if (part != null && part instanceof IEditorPart) {
			IEditorPart closedEditor = (IEditorPart) part;
			
			if (closedEditor instanceof IEditingDomainProvider) {
				ResourceSet resourceSet = ((IEditingDomainProvider) closedEditor).getEditingDomain().getResourceSet();
				if (resourceSet.getResources().size() > 0) {
					MatcherTreeViewerRootKey key = new MatcherTreeViewerRootKey(closedEditor, resourceSet);
					QueryExplorer.contentModelMap.get(key).unloadModel();
				}
			}
		}

	}

}
