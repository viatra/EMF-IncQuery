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

package org.eclipse.incquery.runtime.base.itc.alg.incscc;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Union-find data structure implementation. Note that the implementation relies on the correct implementation of the
 * equals method of the type parameter's class.
 * 
 * @author Tamas Szabo
 * 
 * @param <V>
 *            the type parameter of the element's stored in the union-find data structure
 */
public class UnionFind<V> {

    public Map<V, UnionFindNodeProperty<V>> nodeMap;
    public Map<V, Set<V>> setMap;

    /**
     * Instantiate a new union-find data structure.
     */
    public UnionFind() {
        nodeMap = new HashMap<V, UnionFindNodeProperty<V>>();
        setMap = new HashMap<V, Set<V>>();
    }

    /**
     * Creates a new set from the array of elements.
     * 
     * @param nodes
     *            the array of elements
     * @return the root element
     */
    public V makeSet(V[] nodes) {
        if (nodes.length > 1) {
            V root = makeSet(nodes[0]);
            for (int i = 1; i < nodes.length; i++)
                root = union(nodes[i], root);
            return root;
        } else if (nodes.length == 1) {
            return makeSet(nodes[0]);
        } else {
            return null;
        }
    }

    /**
     * This method creates a single set containing the given node.
     * 
     * @param node
     *            the root node of the set
     * @return the root element
     */
    public V makeSet(V node) {
        if (!nodeMap.containsKey(node)) {
            UnionFindNodeProperty<V> prop = new UnionFindNodeProperty<V>(0, node);
            nodeMap.put(node, prop);
            Set<V> set = new HashSet<V>();
            set.add(node);
            setMap.put(node, set);
        }
        return node;
    }

    /**
     * Find method with path compression.
     * 
     * @param node
     *            the node to find
     * @return the root node of the set in which the given node can be found
     */
    public V find(V node) {
        UnionFindNodeProperty<V> prop = nodeMap.get(node);

        if (prop != null) {
            if (prop.parent.equals(node)) {
                return node;
            } else {
                prop.parent = find(prop.parent);
                return prop.parent;
            }
        }
        return null;
    }

    /**
     * Union by rank implementation of the two sets which contain x and y; x and/or y can be a single element from the
     * universe.
     * 
     * @param x
     *            set or single element of the universe
     * @param y
     *            set or single element of the universe
     * @return the new root of the two sets
     */
    public V union(V x, V y) {

        V xRoot = find(x);
        V yRoot = find(y);

        if ((xRoot == null) && (yRoot == null)) {
            makeSet(x);
            makeSet(y);
            return union(x, y);
        }

        else if ((xRoot != null) && (yRoot == null)) {
            makeSet(y);
            return union(x, y);
        }

        else if ((xRoot == null) && (yRoot != null)) {
            makeSet(x);
            return union(x, y);
        }

        else if ((xRoot != null) && (yRoot != null) && !xRoot.equals(yRoot)) {
            UnionFindNodeProperty<V> xRootProp = nodeMap.get(xRoot);
            UnionFindNodeProperty<V> yRootProp = nodeMap.get(yRoot);

            if (xRootProp.rank < yRootProp.rank) {
                xRootProp.parent = yRoot;
                setMap.get(yRoot).addAll(setMap.get(xRoot));
                setMap.remove(xRoot);
                return yRoot;
            } else if (xRootProp.rank > yRootProp.rank) {
                yRootProp.parent = xRoot;
                setMap.get(xRoot).addAll(setMap.get(yRoot));
                setMap.remove(yRoot);
                return xRoot;
            } else {
                yRootProp.parent = xRoot;
                xRootProp.rank += 1;
                setMap.get(xRoot).addAll(setMap.get(yRoot));
                setMap.remove(yRoot);
                return xRoot;
            }
        } else
            return xRoot;
    }

    /**
     * Delete the set whose root is the given node.
     * 
     * @param root
     *            the root node
     */
    public void deleteSet(V root) {
        // if (setMap.containsKey(root))
        for (V n : setMap.get(root)) {
            nodeMap.remove(n);
        }
        setMap.remove(root);
    }
}
