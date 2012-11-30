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

package org.eclipse.incquery.runtime.base.itc.alg.misc.bfs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.incquery.runtime.base.itc.igraph.IBiDirectionalGraphDataSource;
import org.eclipse.incquery.runtime.base.itc.igraph.IGraphDataSource;

public class BFS<V> {

    /**
     * Performs a breadth first search on the given graph to determine whether source is reachable from target.
     * 
     * @param <V>
     *            the type parameter of the nodes in the graph
     * @param source
     *            the source node
     * @param target
     *            the target node
     * @param graph
     *            the graph data source
     * @return true if source is reachable from target, false otherwise
     */
    public static <V> boolean isReachable(V source, V target, IGraphDataSource<V> graph) {
        List<V> nodeQueue = new ArrayList<V>();
        Map<V, Boolean> visited = new HashMap<V, Boolean>();

        nodeQueue.add(source);
        visited.put(source, true);

        boolean ret = _isReachable(target, graph, nodeQueue, visited);
        return ret;
    }

    private static <V> boolean _isReachable(V target, IGraphDataSource<V> graph, List<V> nodeQueue,
            Map<V, Boolean> visited) {

        while (!nodeQueue.isEmpty()) {
            V node = nodeQueue.remove(0);
            List<V> targets = graph.getTargetNodes(node);
            if (targets != null) {
                for (V _node : targets) {

                    if (_node.equals(target))
                        return true;

                    if (visited.get(_node) == null) {
                        visited.put(_node, true);
                        nodeQueue.add(_node);
                    }
                }
            }
        }

        return false;
    }

    public static <V> Set<V> reachableSources(IBiDirectionalGraphDataSource<V> graph, V target) {
        Set<V> retSet = new HashSet<V>();
        retSet.add(target);
        List<V> nodeQueue = new ArrayList<V>();
        nodeQueue.add(target);

        _reachableSources(graph, nodeQueue, retSet);

        return retSet;
    }

    private static <V> void _reachableSources(IBiDirectionalGraphDataSource<V> graph, List<V> nodeQueue, Set<V> retSet) {
        while (!nodeQueue.isEmpty()) {
            V node = nodeQueue.remove(0);
            List<V> sourceNodes = graph.getSourceNodes(node);

            if (sourceNodes != null) {
                for (V _node : graph.getSourceNodes(node)) {

                    if (!retSet.contains(_node)) {
                        retSet.add(_node);
                        nodeQueue.add(_node);
                    }
                }
            }
        }
    }

    public static <V> Set<V> reachableTargets(IGraphDataSource<V> graph, V source) {
        Set<V> retSet = new HashSet<V>();
        retSet.add(source);
        List<V> nodeQueue = new ArrayList<V>();
        nodeQueue.add(source);

        _reachableTargets(graph, nodeQueue, retSet);

        return retSet;
    }

    private static <V> void _reachableTargets(IGraphDataSource<V> graph, List<V> nodeQueue, Set<V> retSet) {
        while (!nodeQueue.isEmpty()) {
            V node = nodeQueue.remove(0);

            for (V _node : graph.getTargetNodes(node)) {

                if (!retSet.contains(_node)) {
                    retSet.add(_node);
                    nodeQueue.add(_node);
                }
            }
        }
    }

    /**
     * Performs a breadth first search on the given graph and collects all the nodes along the path from source to
     * target if such path exists.
     * 
     * @param <V>
     *            the type parameter of the nodes in the graph
     * @param source
     *            the source node
     * @param target
     *            the target node
     * @param graph
     *            the graph data source
     * @return the set of nodes along the path
     */
    public static <V> Set<V> collectNodesAlongPath(V source, V target, IGraphDataSource<V> graph) {
        Set<V> path = new HashSet<V>();
        _collectNodesAlongPath(source, target, graph, path);
        return path;
    }

    private static <V> boolean _collectNodesAlongPath(V node, V target, IGraphDataSource<V> graph, Set<V> path) {

        boolean res = false;

        // end recursion
        if (node.equals(target)) {
            path.add(node);
            return true;
        } else {
            for (V _nodeT : graph.getTargetNodes(node)) {
                res = (_collectNodesAlongPath(_nodeT, target, graph, path)) || res;
            }
            if (res)
                path.add(node);
            return res;
        }
    }
}
