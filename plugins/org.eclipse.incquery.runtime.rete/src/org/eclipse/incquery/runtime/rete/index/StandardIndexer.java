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

package org.eclipse.incquery.runtime.rete.index;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.incquery.runtime.rete.network.Direction;
import org.eclipse.incquery.runtime.rete.network.ReteContainer;
import org.eclipse.incquery.runtime.rete.network.Supplier;
import org.eclipse.incquery.runtime.rete.tuple.Tuple;
import org.eclipse.incquery.runtime.rete.tuple.TupleMask;


/**
 * An abstract standard implementation of the Indexer interface, providing common bookkeeping functionality.
 * @author Bergmann Gábor
 *
 */
public abstract class StandardIndexer implements Indexer {
	
	protected ReteContainer reteContainer;
	protected long nodeId;
	protected Object tag;
	protected Supplier parent;
	protected List<IndexerListener> listeners;
	protected TupleMask mask;

	public StandardIndexer(ReteContainer reteContainer, TupleMask mask) {
		super();
		this.reteContainer = reteContainer;
		this.nodeId = reteContainer.registerNode(this);
		this.parent = null;
		this.mask = mask;
		this.listeners = new ArrayList<IndexerListener>();
	}
	
	protected void propagate(Direction direction, Tuple updateElement, Tuple signature, boolean change) {
		for (IndexerListener listener : listeners) {
			listener.notifyIndexerUpdate(direction, updateElement, signature, change);
		}
	}

	/**
	 * @return the mask
	 */
	public TupleMask getMask() {
		return mask;
	}

	public Supplier getParent() {
		return parent;
	}

	public void attachListener(IndexerListener listener) {
		listeners.add(listener);
	}

	public void detachListener(IndexerListener listener) {
		listeners.remove(listener);
	}
	
	public Collection<IndexerListener> getListeners() {
		return listeners;
	}

	public ReteContainer getContainer() {
		return reteContainer;
	}

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

	@Override
	public String toString() {
		if (tag != null)
			return "[" + nodeId+ "]" + getClass().getSimpleName() + "("+ parent + "/"+ mask+")" + " [[" + tag.toString() + "]]";
		else
			return "[" + nodeId+ "]" + getClass().getSimpleName() + "("+ parent + "/"+ mask+")";
	};

}
