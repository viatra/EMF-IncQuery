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

package org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.index;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.network.Direction;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.network.Receiver;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.network.ReteContainer;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.network.Supplier;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.tuple.Tuple;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.tuple.TupleMask;


/**
 * Defines a trivial indexer that identically projects the contents of a memory-equipped node, and can therefore save space.
 * Can only exist in connection with a memory, and must be operated by another node. Do not attach parents directly!
 * @author Bergmann Gábor
 */

public class IdentityIndexer extends StandardIndexer implements ProjectionIndexer {
	Collection<Tuple> memory;
	Receiver activeNode;

	/**
	 * @param reteContainer
	 * @param tupleWidth the width of the tuples of memoryNode
	 * @param memory the memory whose contents are to be identity-indexed
	 * @param parent the parent node that owns the memory
	 */
	public IdentityIndexer(ReteContainer reteContainer, int tupleWidth, Collection<Tuple> memory, Supplier parent, Receiver activeNode) {
		super(reteContainer, TupleMask.identity(tupleWidth));
		this.memory = memory;
		this.parent = parent;
		this.activeNode = activeNode;
	}
	
	public Collection<Tuple> get(Tuple signature) {
		if (memory.contains(signature)) {
			return Collections.singleton(signature);
		} else return null;
	}

	public Collection<Tuple> getSignatures() {
		return memory;
	}

	public Iterator<Tuple> iterator() {
		return memory.iterator();
	}
	
	@Override
	public Receiver getActiveNode() {
		return activeNode;
	}


	public void appendParent(Supplier supplier) {
		throw new UnsupportedOperationException("An identityIndexer allows no explicit parent nodes");
	}

	public void removeParent(Supplier supplier) {
		throw new UnsupportedOperationException("An identityIndexer allows no explicit parent nodes");
	}

	public void update(Direction direction, Tuple updateElement) {
		throw new UnsupportedOperationException("An identityIndexer allows no explicit parent nodes");
	}

	public void propagate(Direction direction, Tuple updateElement) {
		propagate(direction, updateElement, updateElement, true);	
	}

}
