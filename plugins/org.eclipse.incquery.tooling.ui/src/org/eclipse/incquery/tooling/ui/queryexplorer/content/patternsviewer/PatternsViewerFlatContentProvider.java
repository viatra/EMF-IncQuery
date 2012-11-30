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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

public class PatternsViewerFlatContentProvider implements ITreeContentProvider {

    public PatternsViewerFlatContentProvider() {
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
            return getLeavesOrComponentsWithLeaves((PatternComposite) parentElement).toArray();
        }
        return null;
    }

    // OK
    @Override
    public Object getParent(Object element) {
        if (element != null && element instanceof PatternLeaf) {
            return ((PatternComponent) element).getParent();
        } else if (element != null && element instanceof PatternComposite) {
            return ((PatternComposite) element).getRoot();
        }
        return null;
    }

    // OK
    @Override
    public boolean hasChildren(Object element) {
        if (element != null && element instanceof PatternComposite) {
            return ((PatternComposite) element).getDirectLeaves().size() > 0;
        }
        return false;
    }

    private List<PatternComponent> getLeavesOrComponentsWithLeaves(PatternComposite composite) {
        List<PatternComponent> components = new ArrayList<PatternComponent>();
        for (PatternComponent pc : composite.getDirectChildren()) {
            if (pc instanceof PatternLeaf) {
                components.add(pc);
            } else {
                PatternComposite comp = (PatternComposite) pc;
                if (comp.getDirectLeaves().size() > 0) {
                    components.add(pc);
                } else {
                    components.addAll(getLeavesOrComponentsWithLeaves(comp));
                }
            }
        }
        return components;
    }
}
