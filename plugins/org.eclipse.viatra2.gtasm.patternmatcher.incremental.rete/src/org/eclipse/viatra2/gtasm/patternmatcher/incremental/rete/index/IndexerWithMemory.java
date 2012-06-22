/*******************************************************************************
 * Copyright (c) 2004-2009 Gabor Bergmann and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Gabor Bergmann - initial API and implementation
 *******************************************************************************/

package org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.index;

import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.network.Direction;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.network.Receiver;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.network.ReteContainer;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.network.Supplier;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.tuple.MaskedTupleMemory;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.tuple.Tuple;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.tuple.TupleMask;

/**
 * @author Bergmann Gábor
 *
 */
public abstract class IndexerWithMemory extends StandardIndexer implements Receiver {

	protected MaskedTupleMemory memory;

	/**
	 * @param reteContainer
	 * @param mask
	 */
	public IndexerWithMemory(ReteContainer reteContainer, TupleMask mask) {
		super(reteContainer, mask);
		this.memory = new MaskedTupleMemory(mask);
		reteContainer.registerClearable(memory);		
	}

	public void update(Direction direction, Tuple updateElement) {
		Tuple signature = mask.transform(updateElement);
		boolean change = (direction == Direction.INSERT) ? memory.add(
				updateElement, signature) : memory.remove(updateElement,
				signature);
		update(direction, updateElement,signature, change);
	}

	/**
	 * Refined version of update
	 */
	protected abstract void update(Direction direction, Tuple updateElement, Tuple signature, boolean change);

	public void appendParent(Supplier supplier) {
		if (parent == null) 
			parent = supplier;
		else
			throw new UnsupportedOperationException("Illegal RETE edge: " + this + " already has a parent (" +  
					parent + ") and cannot connect to additional parent (" + supplier + 
					"). ");
	}

	public void removeParent(Supplier supplier) {
		if (parent == supplier) 
			parent = null;
		else
			throw new IllegalArgumentException("Illegal RETE edge removal: the parent of " + this + " is not " + supplier);
	}

}