/*******************************************************************************
 * Copyright (c) 2010-2012, Tamas Szabo, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Tamas Szabo - initial API and implementation
 *******************************************************************************/

package org.eclipse.incquery.runtime.base.itc.igraph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class IBiDirectionalWrapper<V> implements IBiDirectionalGraphDataSource<V>, IGraphObserver<V> {

    private static final long serialVersionUID = -5771114630390029106L;
    private IGraphDataSource<V> gds;
    private HashMap<V, ArrayList<V>> backwardEdges;

    public IBiDirectionalWrapper(IGraphDataSource<V> gds) {
        this.gds = gds;

        this.backwardEdges = new HashMap<V, ArrayList<V>>();

        if (gds.getAllNodes() != null) {
            for (V s : gds.getAllNodes()) {
                for (V t : gds.getTargetNodes(s)) {
                    edgeInserted(s, t);
                }
            }
        }

        gds.attachObserver(this);
    }

    @Override
    public void attachObserver(IGraphObserver<V> go) {
        gds.attachObserver(go);
    }

    @Override
    public void detachObserver(IGraphObserver<V> go) {
        gds.detachObserver(go);
    }

    @Override
    public Set<V> getAllNodes() {
        return gds.getAllNodes();
    }

    @Override
    public List<V> getTargetNodes(V source) {
        return gds.getTargetNodes(source);
    }

    @Override
    public ArrayList<V> getSourceNodes(V target) {
        return backwardEdges.get(target);
    }

    @Override
    public void edgeInserted(V source, V target) {

        if (backwardEdges.get(target) == null) {
            ArrayList<V> tSet = new ArrayList<V>();
            tSet.add(source);
            backwardEdges.put(target, tSet);
        } else {
            backwardEdges.get(target).add(source);
        }

    }

    @Override
    public void edgeDeleted(V source, V target) {

        if (backwardEdges.containsKey(target)) {
            backwardEdges.get(target).remove(source);
            if (backwardEdges.get(target).size() == 0)
                backwardEdges.remove(target);
        }
    }

    @Override
    public void nodeInserted(V n) {

    }

    @Override
    public void nodeDeleted(V n) {
        for (V key : backwardEdges.keySet()) {
            while (backwardEdges.get(key).contains(n))
                backwardEdges.get(key).remove(n);
        }
    }

    @Override
    public String toString() {
        return gds.toString();
    }
}
