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

package org.eclipse.incquery.tooling.ui.content;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.incquery.runtime.util.XmiModelUtil;
import org.eclipse.incquery.tooling.core.project.IncQueryNature;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

public class QueryFolderFilter extends ViewerFilter {

    @Override
    public boolean select(Viewer viewer, Object parentElement, Object element) {
        try {
            // Filter only active on IncQuery projects
            if (parentElement instanceof IProject) {
                IProject project = (IProject) parentElement;
                if (project.hasNature(IncQueryNature.NATURE_ID))
                    return true;
            }
            if (element instanceof IFolder) {
                IFolder folder = (IFolder) element;
                if (XmiModelUtil.XMI_OUTPUT_FOLDER.equals(folder.getName())) {
                    return false;
                }
            }
        } catch (CoreException e) {
            // If exception is thrown, simply ignore it, and filter nothing
        }
        return true;
    }

}
