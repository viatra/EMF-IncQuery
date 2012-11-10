/*******************************************************************************
 * Copyright (c) 2010-2012, Zoltan Ujhelyi, Abel Hegedus, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Zoltan Ujhelyi, Abel Hegedus - initial API and implementation
 *******************************************************************************/

package org.eclipse.viatra2.emf.incquery.runtime.ui.gmf.handlers;

import org.eclipse.viatra2.emf.incquery.queryexplorer.handlers.LoadModelHandler;

public class GMFEditorLoadModelHandler extends LoadModelHandler {

    // @Override
    // public Object execute(ExecutionEvent event) throws ExecutionException {
    // IEditorPart editorPart = HandlerUtil.getActiveEditor(event);
    // ResourceSet resourceSet = AdapterUtil.getResourceSetFromIEditorPart(editorPart);
    //
    // if (resourceSet != null && resourceSet.getResources().size() > 0) {
    // MatcherTreeViewerRootKey key = new MatcherTreeViewerRootKey(editorPart, resourceSet);
    // ModelConnector contentModel = new EMFModelConnector(key);
    // QueryExplorer.getInstance().getModelConnectorMap().put(key, contentModel);
    // contentModel.loadModel();
    // }
    //
    // return null;
    //
    // // IEditorPart editorPart = HandlerUtil.getActiveEditor(event);
    // //
    // // if (editorPart instanceof DiagramDocumentEditor) {
    // // DiagramDocumentEditor providerEditor = (DiagramDocumentEditor) editorPart;
    // // ResourceSet resourceSet = providerEditor.getEditingDomain().getResourceSet();
    // // loadModel(providerEditor, resourceSet);
    // // } else if (editorPart instanceof IDiagramWorkbenchPart) {
    // // IDiagramWorkbenchPart providerEditor = (IDiagramWorkbenchPart) editorPart;
    // // ResourceSet resourceSet = providerEditor.getDiagramEditPart().getEditingDomain().getResourceSet();
    // // loadModel(editorPart, resourceSet);
    // // }
    // // return null;
    // }

    // /**
    // * @param providerEditor
    // * @param resourceSet
    // */
    // private void loadModel(IEditorPart providerEditor, ResourceSet resourceSet) {
    // if (resourceSet.getResources().size() > 0) {
    // MatcherTreeViewerRootKey key = new MatcherTreeViewerRootKey(providerEditor, resourceSet);
    // ModelConnector contentModel = new GMFModelConnector(key);
    // QueryExplorer.getInstance().getModelConnectorMap().put(key, contentModel);
    // contentModel.loadModel();
    // }
    // }
}
