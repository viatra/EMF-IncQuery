/*******************************************************************************
 * Copyright (c) 2010-2012, Andras Okros, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Andras Okros - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra2.emf.incquery.runtime.ui.gmf.adapters;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.gmf.runtime.diagram.ui.parts.IDiagramWorkbenchPart;
import org.eclipse.gmf.runtime.diagram.ui.resources.editor.parts.DiagramDocumentEditor;

/**
 * This AdapterFactory is responsible for processing the GMF model inputs, and return the underlying ResourceSet
 * instances from it.
 */
@SuppressWarnings("rawtypes")
public class AdapterFactoryForGMFEditors implements IAdapterFactory {

    @Override
    public Object getAdapter(Object adaptableObject, Class adapterType) {
        if (adapterType == ResourceSet.class) {
            if (adaptableObject instanceof DiagramDocumentEditor) {
                DiagramDocumentEditor diagramDocumentEditor = (DiagramDocumentEditor) adaptableObject;
                return diagramDocumentEditor.getEditingDomain().getResourceSet();
            } else if (adaptableObject instanceof IDiagramWorkbenchPart) {
                IDiagramWorkbenchPart diagramWorkbenchPart = (IDiagramWorkbenchPart) adaptableObject;
                return diagramWorkbenchPart.getDiagramEditPart().getEditingDomain().getResourceSet();
            }
        }
        return null;
    }

    @Override
    public Class[] getAdapterList() {
        return new Class[] { ResourceSet.class };
    }

}
