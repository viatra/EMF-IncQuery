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

package org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.network;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.tuple.Tuple;


/**
 * @author Gabor Bergmann
 * 
 */
public abstract class StandardNode implements Supplier {
	protected ReteContainer reteContainer;
	protected long nodeId;
	protected Object tag;
	protected List<Receiver> children;

	public StandardNode(ReteContainer reteContainer) {
		this.reteContainer = reteContainer;
		this.nodeId = reteContainer.registerNode(this);
		children = new LinkedList<Receiver>();
	}

	protected void propagateUpdate(Direction direction, Tuple updateElement) {
		for (Receiver r : children)
			reteContainer.sendUpdateInternal(r, direction, updateElement);
	}

	public void appendChild(Receiver receiver) {
		children.add(receiver);
	}

	public void removeChild(Receiver receiver) {
		children.remove(receiver);
	}

	@Override
	public String toString() {
		if (tag != null)
			return "[" + nodeId+ "]" + getClass().getSimpleName() + "[[" + tag.toString() + "]]";
		else
			return "[" + nodeId+ "]" + getClass().getSimpleName();
	};

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.network.Node#getNetwork()
	 */
	public ReteContainer getContainer() {
		return reteContainer;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.network.Node#getNodeId()
	 */
	public long getNodeId() {
		return nodeId;
	}

	/**
	 * @return the tag
	 */
	public Object getTag() {
		return tag;
	}

	/**
	 * @param tag
	 *            the tag to set
	 */
	public void setTag(Object tag) {
		this.tag = tag;
	}

}
