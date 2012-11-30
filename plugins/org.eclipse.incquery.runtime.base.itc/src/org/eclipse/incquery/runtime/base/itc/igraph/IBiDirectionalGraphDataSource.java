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

import java.util.List;

/**
 * This interface extends the functionality of IGraphDataSource with an extra method. One can query the source nodes of
 * those edges which have a given target node.
 * 
 * @author Tamas Szabo
 * 
 * @param <V>
 *            the type of the nodes in the graph
 */
public interface IBiDirectionalGraphDataSource<V> extends IGraphDataSource<V> {

    /**
     * Returns the source nodes of those edges that end with target. The nodes are returned as a {@link List} as
     * multiple edges can be present between two arbitrary nodes. If no such node can be found than the method should
     * return null.
     * 
     * @param target
     *            the target node
     * @return the list of source nodes or null if no sources can be found
     */
    public List<V> getSourceNodes(V target);
}
