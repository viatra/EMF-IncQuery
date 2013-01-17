/*******************************************************************************
 * Copyright (c) 2004-2008 Gabor Bergmann and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Gabor Bergmann - initial API and implementation
 *******************************************************************************/

package org.eclipse.incquery.runtime.rete.util;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.eclipse.incquery.runtime.rete.collections.CollectionsFactory;

/**
 * @author Gabor Bergmann
 * 
 */
public class UnionFind<T> implements Map<T, T> {
    class Node {
        public T value;
        public Node parent;
        public int rank;

        public Node(T value) {
            this.value = value;
            parent = this;
            rank = 0;
        }
    }

    protected Map<T, Node> nodes;

    public UnionFind() {
        nodes = //new HashMap<T, Node>();
                CollectionsFactory.getMap();
    }

    protected Node find(Node n) {
        if (n.parent == n)
            return n;
        n.parent = find(n.parent);
        return n.parent;
    }

    public T find(T x) {
        Node node = nodes.get(x);
        if (node == null)
            return x;
        else
            return find(node).value;
    }

    public boolean isRoot(T x) {
        Node node = nodes.get(x);
        if (node == null)
            return true;
        else
            return node == node.parent;
    }

    protected Node retrieveOrCreate(T x) {
        Node node = nodes.get(x);
        if (node == null) {
            node = new Node(x);
            nodes.put(x, node);
            return node;
        } else
            return find(node);
    }

    public void unite(T a, T b) {
        Node aRoot = retrieveOrCreate(a);
        Node bRoot = retrieveOrCreate(b);

        if (aRoot.rank > bRoot.rank)
            bRoot.parent = aRoot;
        else if (aRoot.rank < bRoot.rank)
            aRoot.parent = bRoot;
        else if (aRoot != bRoot) {
            bRoot.parent = aRoot;
            aRoot.rank++;
        }

    }

    public void clear() {
        nodes.clear();
    }

    @SuppressWarnings("unchecked")
    public T get(Object key) {
        return find((T) key);
    }

    public boolean containsKey(Object key) {
        throw new UnsupportedOperationException();
    }

    public boolean containsValue(Object value) {
        throw new UnsupportedOperationException();
    }

    public Set<java.util.Map.Entry<T, T>> entrySet() {
        throw new UnsupportedOperationException();
    }

    public boolean isEmpty() {
        throw new UnsupportedOperationException();
    }

    public Set<T> keySet() {
        throw new UnsupportedOperationException();
    }

    public T put(T key, T value) {
        throw new UnsupportedOperationException();
    }

    public void putAll(Map<? extends T, ? extends T> m) {
        throw new UnsupportedOperationException();
    }

    public T remove(Object key) {
        throw new UnsupportedOperationException();
    }

    public int size() {
        throw new UnsupportedOperationException();
    }

    public Collection<T> values() {
        throw new UnsupportedOperationException();
    }

}
