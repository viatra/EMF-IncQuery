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

package org.eclipse.incquery.runtime.rete.remote;

import org.eclipse.incquery.runtime.rete.network.Node;
import org.eclipse.incquery.runtime.rete.network.ReteContainer;

/**
 * Remote identifier of a node of type T.
 * 
 * @author Gabor Bergmann
 * 
 */
public class Address<T extends Node> {
    ReteContainer container;
    Long nodeId;
    /**
     * Feel free to leave null e.g. if node is in a separate JVM.
     */
    T nodeCache;

    /**
     * Address of local node (use only for containers in the same VM!)
     */
    public static <N extends Node> Address<N> of(N node) {
        return new Address<N>(node);
    }

    /**
     * General constructor.
     * 
     * @param container
     * @param nodeId
     */
    public Address(ReteContainer container, Long nodeId) {
        super();
        this.container = container;
        this.nodeId = nodeId;
    }

    /**
     * Local-only constructor. (use only for containers in the same VM!)
     * 
     * @param T
     *            the node to address
     */
    public Address(T node) {
        super();
        this.nodeCache = node;
        this.container = node.getContainer();
        this.nodeId = node.getNodeId();
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((container == null) ? 0 : container.hashCode());
        result = prime * result + ((nodeId == null) ? 0 : nodeId.hashCode());
        return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof Address<?>))
            return false;
        final Address<?> other = (Address<?>) obj;
        if (container == null) {
            if (other.container != null)
                return false;
        } else if (!container.equals(other.container))
            return false;
        if (nodeId == null) {
            if (other.nodeId != null)
                return false;
        } else if (!nodeId.equals(other.nodeId))
            return false;
        return true;
    }

    /**
     * @return the container
     */
    public ReteContainer getContainer() {
        return container;
    }

    /**
     * @param container
     *            the container to set
     */
    public void setContainer(ReteContainer container) {
        this.container = container;
    }

    /**
     * @return the nodeId
     */
    public Long getNodeId() {
        return nodeId;
    }

    /**
     * @param nodeId
     *            the nodeId to set
     */
    public void setNodeId(Long nodeId) {
        this.nodeId = nodeId;
    }

    public T getNodeCache() {
        return nodeCache;
    }

    public void setNodeCache(T nodeCache) {
        this.nodeCache = nodeCache;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        if (nodeCache == null)
            return "A(" + nodeId + " @ " + container + ")";
        else
            return "A(" + nodeCache + ")";

    }

}
