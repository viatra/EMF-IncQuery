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
package org.eclipse.incquery.runtime.rete.misc;

import java.util.Collection;
import java.util.Collections;

import org.eclipse.incquery.runtime.rete.network.BaseNode;
import org.eclipse.incquery.runtime.rete.network.Receiver;
import org.eclipse.incquery.runtime.rete.network.ReteContainer;
import org.eclipse.incquery.runtime.rete.network.Supplier;

/**
 * @author Bergmann Gabor
 *
 */
public abstract class SimpleReceiver extends BaseNode implements Receiver {

	protected Supplier parent = null;

	/**
	 * @param reteContainer
	 */
	public SimpleReceiver(ReteContainer reteContainer) {
		super(reteContainer);
	}

	@Override
	public void appendParent(Supplier supplier) {
		if (parent == null) 
			parent = supplier;
		else
			throw new UnsupportedOperationException("Illegal RETE edge: " + this + " already has a parent (" +  
					parent + ") and cannot connect to additional parent (" + supplier + 
					") as it is not a Uniqueness Enforcer Node. ");
	}

	@Override
	public void removeParent(Supplier supplier) {
		if (parent == supplier) 
			parent = null;
		else
			throw new IllegalArgumentException("Illegal RETE edge removal: the parent of " + this + " is not " + supplier);
	}

	@Override
	public Collection<Supplier> getParents() {
		if (parent == null) 
			return Collections.emptySet();
		else 
			return Collections.singleton(parent);
	}

	/**
	 * Disconnects this node from the network. Can be called publicly.
	 * @pre: child nodes, if any, must already be disconnected.
	 */
	public void disconnectFromNetwork() {
		if (parent != null) 
			reteContainer.disconnect(parent, this);
	}

}