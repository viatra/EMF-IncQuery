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

import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.network.Receiver;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.network.ReteContainer;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.network.Supplier;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.tuple.Tuple;


/**
 * Defines a trivial indexer that identically projects the contents of a memory-equipped node, and can therefore save space.
 * Can only exist in connection with a memory, and must be operated by another node. Do not attach parents directly!
 * @author Bergmann Gábor
 */

public class MemoryIdentityIndexer extends IdentityIndexer {
	Collection<Tuple> memory;
	/**
	 * @param reteContainer
	 * @param tupleWidth the width of the tuples of memoryNode
	 * @param memory the memory whose contents are to be identity-indexed
	 * @param parent the parent node that owns the memory
	 */
	public MemoryIdentityIndexer(ReteContainer reteContainer, int tupleWidth, Collection<Tuple> memory, Supplier parent, Receiver activeNode) {
		super(reteContainer, tupleWidth, parent, activeNode);
		this.memory = memory;
	}
	
	/**
	 * @return
	 */
	@Override
	protected Collection<Tuple> getTuples() {
		return memory;
	}

}
