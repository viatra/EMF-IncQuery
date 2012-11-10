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
package org.eclipse.viatra2.emf.incquery.queryexplorer.adapters;

import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.ui.IEditorPart;
import org.eclipse.viatra2.emf.incquery.gui.IncQueryGUIPlugin;

/**
 * A simple util class for the adapter calls. Returns typesafe objects and checks for errors as well.
 */
public class AdapterUtil {

    private static ILog logger = IncQueryGUIPlugin.getDefault().getLog();

    /**
     * @param editorPart
     *            which can be loaded into the system
     * @return a {@link ResourceSet} instance which is used by the given editorpart
     */
    public static ResourceSet getResourceSetFromIEditorPart(IEditorPart editorPart) {
        if (editorPart != null) {
            Object adaptedObject = editorPart.getAdapter(ResourceSet.class);
            if (adaptedObject != null) {
                return (ResourceSet) adaptedObject;
            } else {
                logger.log(new Status(IStatus.ERROR, IncQueryGUIPlugin.PLUGIN_ID, "EditorPart " + editorPart.getTitle()
                        + " (type: " + editorPart.getClass().getSimpleName()
                        + ") cannot provide a ResourceSet object for the QueryExplorer."));
            }
        }
        return null;
    }

}
