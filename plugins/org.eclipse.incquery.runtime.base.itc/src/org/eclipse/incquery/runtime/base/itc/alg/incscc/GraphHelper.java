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

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.eclipse.incquery.runtime.base.itc.graphimpl.Graph;

/**
 * @author Tamas Szabo
 * 
 */
public class GraphHelper<V> {

    private IncSCCAlg<V> alg;

    public GraphHelper(IncSCCAlg<V> alg) {
        this.alg = alg;
    }

    /**
     * Return the SCCs from which the SCC represented by the root node is reachable. Note that an SCC can be present
     * multiple times in the returned list (multiple edges between the two SCCs).
     * 
     * @param root
     * @return the list of reachable target SCCs
     */
    public List<V> getSourceSCCsOfSCC(V root) {
        List<V> sourceSCCs = new ArrayList<V>();

        for (V containedNode : alg.sccs.setMap.get(root)) {
            List<V> sourceNodes = alg.gds.getSourceNodes(containedNode);

            if (sourceNodes != null) {
                for (V source : sourceNodes) {
                    sourceSCCs.add(alg.sccs.find(source));
                }
            }
        }

        return sourceSCCs;
    }

    /**
     * Return the SCCs which are reachable from the SCC represented by the root node. Note that an SCC can be present
     * multiple times in the returned list (multiple edges between the two SCCs).
     * 
     * @param root
     * @return the list of reachable target SCCs
     */
    public List<V> getTargetSCCsOfSCC(V root) {

        List<V> targetSCCs = new ArrayList<V>();

        for (V containedNode : alg.sccs.setMap.get(root)) {
            List<V> targetNodes = alg.gds.getTargetNodes(containedNode);

            if (targetNodes != null) {
                for (V target : targetNodes) {
                    targetSCCs.add(alg.sccs.find(target));
                }
            }
        }

        return targetSCCs;
    }

    public int getEdgeCount(V node) {
        return getEdgeCount(node, node);
    }

    public int getEdgeCount(V source, V target) {
        if (this.alg.gds.getTargetNodes(source) == null) {
            return 0;
        } else {
            int count = 0;
            for (V n : this.alg.gds.getTargetNodes(source)) {
                if (n.equals(target)) {
                    count++;
                }
            }
            return count;
        }
    }

    /**
     * Returns the Graph for the given root node in the union find structure.
     * 
     * @param root
     *            the root node
     * @return the graph for the subtree
     */
    public Graph<V> getGraphOfSCC(V root) {

        Graph<V> g = new Graph<V>();
        Set<V> nodeSet = alg.sccs.setMap.get(root);

        if (nodeSet != null) {
            for (V node : nodeSet) {
                g.insertNode(node);
            }
            for (V node : nodeSet) {

                List<V> sources = (alg.gds.getSourceNodes(node) == null) ? null : new ArrayList<V>(
                        alg.gds.getSourceNodes(node));

                if (sources != null) {
                    for (V _s : sources) {
                        if (nodeSet.contains(_s)) {
                            g.insertEdge(_s, node);
                        }
                    }
                }
            }
        }

        return g;
    }
}
