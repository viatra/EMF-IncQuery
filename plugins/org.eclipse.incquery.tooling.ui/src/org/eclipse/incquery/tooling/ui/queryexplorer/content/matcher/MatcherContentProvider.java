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

package org.eclipse.incquery.tooling.ui.queryexplorer.content.matcher;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

public class MatcherContentProvider implements ITreeContentProvider {

    private MatcherTreeViewerRoot input;

    public MatcherContentProvider() {

    }

    @Override
    public void dispose() {
        input = null;
    }

    @Override
    public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        if (newInput != null && newInput instanceof MatcherTreeViewerRoot) {
            input = (MatcherTreeViewerRoot) newInput;
        }
    }

    @Override
    public Object[] getElements(Object inputElement) {
        if (inputElement instanceof MatcherTreeViewerRoot) {
            return input.getRoots().toArray();
        }
        return null;
    }

    @Override
    public Object[] getChildren(Object parentElement) {
        if (parentElement instanceof MatcherTreeViewerRoot) {
            return input.getRoots().toArray();
        } else if (parentElement instanceof ObservablePatternMatcherRoot) {
            return ((ObservablePatternMatcherRoot) parentElement).getMatchers().toArray();
        } else if (parentElement instanceof ObservablePatternMatcher) {
            return ((ObservablePatternMatcher) parentElement).getMatches().toArray();
        }
        return null;
    }

    @Override
    public Object getParent(Object element) {
        if (element instanceof ObservablePatternMatcherRoot) {
            return input;
        } else if (element instanceof ObservablePatternMatcher) {
            return ((ObservablePatternMatcher) element).getParent();
        } else if (element instanceof ObservablePatternMatch) {
            return ((ObservablePatternMatch) element).getParent();
        }
        return null;
    }

    @Override
    public boolean hasChildren(Object element) {
        if (element != null) {
            if (element instanceof MatcherTreeViewerRoot) {
                return (!input.getRoots().isEmpty());
            } else if (element instanceof ObservablePatternMatcherRoot) {
                return (!((ObservablePatternMatcherRoot) element).getMatchers().isEmpty());
            } else if (element instanceof ObservablePatternMatcher) {
                return (!(((ObservablePatternMatcher) element).getMatches().isEmpty()));
            }
        }
        return false;
    }

}
