/*******************************************************************************
 * Copyright (c) 2010-2012, Bergmann Gabor, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Bergmann Gabor - initial API and implementation
 *******************************************************************************/
package org.eclipse.incquery.runtime.rete.network;

/**
 * Base implementation for a Rete node.
 * 
 * @author Bergmann Gabor
 * 
 */
public abstract class BaseNode implements Node {

    protected ReteContainer reteContainer;
    protected long nodeId;
    protected Object tag;

    /**
     * @param reteContainer
     *            the container to create this node in
     */
    public BaseNode(ReteContainer reteContainer) {
        super();
        this.reteContainer = reteContainer;
        this.nodeId = reteContainer.registerNode(this);
    }

    @Override
    public String toString() {
        if (tag != null)
            return "[" + nodeId + "]" + getClass().getSimpleName() + "[[" + tag.toString() + "]]";
        else
            return "[" + nodeId + "]" + getClass().getSimpleName();
    }

    @Override
    public ReteContainer getContainer() {
        return reteContainer;
    }

    @Override
    public long getNodeId() {
        return nodeId;
    }

    @Override
    public Object getTag() {
        return tag;
    }

    @Override
    public void setTag(Object tag) {
        this.tag = tag;
    }

}