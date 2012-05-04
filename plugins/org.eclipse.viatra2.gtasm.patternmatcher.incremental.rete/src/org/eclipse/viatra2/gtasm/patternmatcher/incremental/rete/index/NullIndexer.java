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
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.network.ReteContainer;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.network.Supplier;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.tuple.FlatTuple;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.tuple.TupleMask;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.tuple.Tuple;


/**
 * Defines a trivial indexer that projects the contents of a memory-equipped node to the empty tuple, and can therefore save space.
 * Can only exist in connection with a memory, and must be operated by another node. Do not attach parents directly!
 * @author Bergmann Gábor
 */
public class NullIndexer extends StandardIndexer implements ProjectionIndexer {

	Collection<Tuple> memory;
	static Object[] empty = {};
	static Tuple nullSignature = new FlatTuple(empty);
	static Collection<Tuple> nullSingleton = Collections.singleton(nullSignature);
	static Collection<Tuple> emptySet = Collections.emptySet();
	
	/**
	 * @param reteContainer
	 * @param tupleWidth the width of the tuples of memoryNode
	 * @param memory the memory whose contents are to be null-indexed
	 * @param parent the parent node that owns the memory
	 */
	public NullIndexer(ReteContainer reteContainer, int tupleWidth, Collection<Tuple> memory, Supplier parent) {
		super(reteContainer, TupleMask.linear(0, tupleWidth));
		this.memory = memory;
		this.parent = parent;
	}

	public Collection<Tuple> get(Tuple signature) {
		if (nullSignature.equals(signature)) return memory.isEmpty()? null :memory;
		else return null;
	}

	public Collection<Tuple> getSignatures() {
		return memory.isEmpty() ? emptySet : nullSingleton;
	}

	public Iterator<Tuple> iterator() {
		return memory.iterator();
	}


	public void appendParent(Supplier supplier) {
		throw new UnsupportedOperationException("A nullIndexer allows no explicit parent nodes");
	}

	public void removeParent(Supplier supplier) {
		throw new UnsupportedOperationException("A nullIndexer allows no explicit parent nodes");
	}


	public void update(Direction direction, Tuple updateElement) {
		throw new UnsupportedOperationException("A nullIndexer allows no explicit parent nodes");
	}

	public void propagate(Direction direction, Tuple updateElement, boolean change) {
		propagate(direction, updateElement, nullSignature, change);
	}

}
