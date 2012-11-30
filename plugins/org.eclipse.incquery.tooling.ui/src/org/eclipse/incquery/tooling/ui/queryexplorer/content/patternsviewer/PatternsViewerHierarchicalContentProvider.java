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

package org.eclipse.incquery.tooling.ui.queryexplorer.content.patternsviewer;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

public class PatternsViewerHierarchicalContentProvider implements ITreeContentProvider {

    public PatternsViewerHierarchicalContentProvider() {

    }

    @Override
    public void dispose() {
    }

    @Override
    public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
    }

    @Override
    public Object[] getElements(Object inputElement) {
        if (inputElement != null && inputElement instanceof PatternsViewerInput) {
            return ((PatternsViewerInput) inputElement).getChildren();
        }
        return null;
    }

    @Override
    public Object[] getChildren(Object parentElement) {
        if (parentElement != null && parentElement instanceof PatternComposite) {
            return ((PatternComposite) parentElement).getDirectChildren().toArray();
        }
        return null;
    }

    @Override
    public Object getParent(Object element) {
        if (element != null && element instanceof PatternComponent) {
            return ((PatternComponent) element).getParent();
        }
        return null;
    }

    @Override
    public boolean hasChildren(Object element) {
        if (element != null && element instanceof PatternComposite) {
            return ((PatternComposite) element).getDirectChildren().size() > 0;
        }
        return false;
    }

}
