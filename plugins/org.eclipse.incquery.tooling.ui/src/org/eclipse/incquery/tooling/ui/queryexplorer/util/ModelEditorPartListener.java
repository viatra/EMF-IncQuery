/*******************************************************************************
 * Copyright (c) 2010-2012, Zoltan Ujhelyi, Tamas Szabo, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Zoltan Ujhelyi, Tamas Szabo - initial API and implementation
 *   Andras Okros - rework to use adapters
 *******************************************************************************/
package org.eclipse.incquery.tooling.ui.queryexplorer.util;

import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.incquery.tooling.ui.queryexplorer.QueryExplorer;
import org.eclipse.incquery.tooling.ui.queryexplorer.adapters.AdapterUtil;
import org.eclipse.incquery.tooling.ui.queryexplorer.content.matcher.MatcherTreeViewerRootKey;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPart;

/**
 * The PartListener is used to observe {@link IEditorPart} close actions.
 */
public class ModelEditorPartListener extends BasePartListener {

    private static ModelEditorPartListener instance;

    protected ModelEditorPartListener() {

    }

    public static synchronized ModelEditorPartListener getInstance() {
        if (instance == null) {
            instance = new ModelEditorPartListener();
        }
        return instance;
    }

    @Override
    public void partClosed(IWorkbenchPart part) {
        if (part instanceof IEditorPart) {
            IEditorPart editorPart = (IEditorPart) part;
            ResourceSet resourceSet = AdapterUtil.getResourceSetFromIEditorPart(editorPart);
            if (resourceSet != null && resourceSet.getResources().size() > 0) {
                MatcherTreeViewerRootKey key = new MatcherTreeViewerRootKey(editorPart, resourceSet);
                if (QueryExplorer.getInstance() != null) {
                    QueryExplorer.getInstance().getModelConnectorMap().get(key).unloadModel();
                }
            }
        }
    }

}
