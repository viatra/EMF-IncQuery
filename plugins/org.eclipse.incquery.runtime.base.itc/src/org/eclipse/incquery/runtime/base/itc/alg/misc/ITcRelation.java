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

package org.eclipse.incquery.runtime.base.itc.alg.misc;

import java.util.Set;

public interface ITcRelation<V> {

    /**
     * Returns the starting nodes from a transitive closure relation.
     * 
     * @return the set of starting nodes
     */
    public Set<V> getTupleStarts();

    /**
     * Returns the set of nodes that are reachable from the given node.
     * 
     * @param start
     *            the starting node
     * @return the set of reachable nodes
     */
    public Set<V> getTupleEnds(V start);
}
