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

package org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.single;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.index.IdentityIndexer;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.index.NullIndexer;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.network.Direction;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.network.ReteContainer;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.network.Supplier;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.tuple.Tuple;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.tuple.TupleMemory;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.util.Options;


/**
 * Ensures that no identical copies get to the output. Only one replica of each
 * pattern substitution may traverse this node.
 * 
 * @author Gabor Bergmann
 */
public class UniquenessEnforcerNode extends SingleInputNode {

	protected Collection<Supplier> parents;
	protected TupleMemory memory;
	protected NullIndexer nullIndexer;
	protected IdentityIndexer identityIndexer;
	protected int tupleWidth;
	
	public UniquenessEnforcerNode(ReteContainer reteContainer, int tupleWidth) {
		super(reteContainer);
		parents = new ArrayList<Supplier>();
		memory = new TupleMemory();
		this.tupleWidth = tupleWidth;
		reteContainer.registerClearable(memory);
		
		if (Options.employTrivialIndexers) {
			nullIndexer = new NullIndexer(reteContainer, tupleWidth, memory, this, this);
			reteContainer.getLibrary().registerSpecializedProjectionIndexer(this, nullIndexer);
			identityIndexer = new IdentityIndexer(reteContainer, tupleWidth, memory, this, this);
			reteContainer.getLibrary().registerSpecializedProjectionIndexer(this, identityIndexer);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.network.Receiver#update(org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.network.Direction, org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.tuple.Tuple)
	 */
	public void update(Direction direction, Tuple updateElement) {
		boolean change;
		if (direction == Direction.INSERT) {
			change = memory.add(updateElement);
		} else { // REVOKE
			try {
				change = memory.remove(updateElement);
			} catch (java.lang.NullPointerException ex) {
				// TODO UGLY, but will it find our problems?
				change = false;
				System.err
						.println("Duplicate deletion of " + updateElement + " was detected in UniquenessEnforcer " + this);
				ex.printStackTrace();
			}
		}
		if (change) {
			propagateUpdate(direction, updateElement);
			
			// trivial projectionIndexers
			if (Options.employTrivialIndexers) {
				identityIndexer.propagate(direction, updateElement);
				boolean radical = (direction==Direction.REVOKE && memory.isEmpty()) || (direction==Direction.INSERT && memory.size()==1);
				nullIndexer.propagate(direction, updateElement, radical);
			}

		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.network.Supplier#pullInto(org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.network.Receiver)
	 */
	public void pullInto(Collection<Tuple> collector) {
		collector.addAll(memory);
	}

	public NullIndexer getNullIndexer() {
		return nullIndexer;
	}

	public IdentityIndexer getIdentityIndexer() {
		return identityIndexer;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.single.SingleInputNode#appendParent(org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.network.Supplier)
	 */
	@Override
	public void appendParent(Supplier supplier) {
		parents.add(supplier);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.single.SingleInputNode#removeParent(org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.network.Supplier)
	 */
	@Override
	public void removeParent(Supplier supplier) {
		parents.remove(supplier);
	}
	
	//
	// public void tearOff() {
	// for (Supplier parent : new LinkedList<Supplier>(parents) )
	// {
	// network.disconnectAndDesynchronize(parent, this);
	// }
	// }
	//
	// /**
	// * @return the dirty
	// */
	// public boolean isDirty() {
	// return dirty;
	// }
	//
	// /**
	// * @param dirty the dirty to set
	// */
	// public void setDirty(boolean dirty) {
	// this.dirty = dirty;
	// }

}
