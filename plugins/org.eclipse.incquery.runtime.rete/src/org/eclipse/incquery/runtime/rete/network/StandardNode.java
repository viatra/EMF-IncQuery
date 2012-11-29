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

package org.eclipse.incquery.runtime.rete.network;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.incquery.runtime.rete.index.GenericProjectionIndexer;
import org.eclipse.incquery.runtime.rete.index.ProjectionIndexer;
import org.eclipse.incquery.runtime.rete.tuple.Tuple;
import org.eclipse.incquery.runtime.rete.tuple.TupleMask;


/**
 * Base implementation for a supplier node.
 * @author Gabor Bergmann
 * 
 */
public abstract class StandardNode extends BaseNode implements Supplier {
	protected List<Receiver> children = new LinkedList<Receiver>();

	public StandardNode(ReteContainer reteContainer) {
		super(reteContainer);
	}

	protected void propagateUpdate(Direction direction, Tuple updateElement) {
		for (Receiver r : children)
			reteContainer.sendUpdateInternal(r, direction, updateElement);
	}

	@Override
    public void appendChild(Receiver receiver) {
		children.add(receiver);
	}

	@Override
    public void removeChild(Receiver receiver) {
		children.remove(receiver);
	}
	
	@Override
	public Collection<Receiver> getReceivers() {
		return children;
	}

	@Override
	public ProjectionIndexer constructIndex(TupleMask mask) {
		final GenericProjectionIndexer indexer = new GenericProjectionIndexer(reteContainer, mask);
		reteContainer.connectAndSynchronize(this, indexer);
		return indexer;
	}

}
