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
package org.eclipse.incquery.tooling.ui.queryexplorer.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.incquery.tooling.ui.queryexplorer.QueryExplorer;
import org.eclipse.incquery.tooling.ui.queryexplorer.adapters.AdapterUtil;
import org.eclipse.incquery.tooling.ui.queryexplorer.content.matcher.MatcherTreeViewerRootKey;
import org.eclipse.incquery.tooling.ui.queryexplorer.handlers.util.EMFModelConnector;
import org.eclipse.incquery.tooling.ui.queryexplorer.handlers.util.ModelConnector;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 * Default 'Load model' handler, default ResourceSet loader.
 * 
 * @author Tamas Szabo
 */
public class LoadModelHandler extends AbstractHandler {

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        IEditorPart editorPart = HandlerUtil.getActiveEditor(event);
        ResourceSet resourceSet = AdapterUtil.getResourceSetFromIEditorPart(editorPart);

        if (resourceSet != null && resourceSet.getResources().size() > 0) {
            MatcherTreeViewerRootKey key = new MatcherTreeViewerRootKey(editorPart, resourceSet);
            ModelConnector contentModel = new EMFModelConnector(key);
            QueryExplorer.getInstance().getModelConnectorMap().put(key, contentModel);
            contentModel.loadModel();
        }

        return null;
    }

}
