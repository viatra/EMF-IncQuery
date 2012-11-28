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

package org.eclipse.incquery.runtime.rete.index;

import java.util.Collection;

import org.eclipse.incquery.runtime.rete.network.Direction;
import org.eclipse.incquery.runtime.rete.network.ReteContainer;
import org.eclipse.incquery.runtime.rete.tuple.Tuple;
import org.eclipse.incquery.runtime.rete.tuple.TupleMask;

/**
 * Performs a left outer join.
 * 
 * @author Bergmann Gábor
 *
 */
public class OuterJoinNode extends DualInputNode {
	final Tuple defaults;

	/**
	 * @param reteContainer
	 * @param primarySlot
	 * @param secondarySlot
	 * @param complementerSecondaryMask
	 * @param defaults the default line to use instead of missing elements if a left tuple has no match
	 * 
	 */
	public OuterJoinNode(ReteContainer reteContainer,
			IterableIndexer primarySlot, Indexer secondarySlot,
			TupleMask complementerSecondaryMask, Tuple defaults) {
		super(reteContainer, primarySlot, secondarySlot,
				complementerSecondaryMask);
		this.defaults = defaults;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.index.DualInputNode#calibrate(org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.tuple.Tuple, org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.tuple.Tuple)
	 */
	@Override
	public Tuple calibrate(Tuple primary, Tuple secondary) {
		return unify(primary, secondary);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.index.DualInputNode#notifyUpdate(org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.index.DualInputNode.Side, org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.network.Direction, org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.tuple.Tuple, org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.tuple.Tuple, boolean)
	 */
	@Override
	public void notifyUpdate(Side side, Direction direction,
			Tuple updateElement, Tuple signature, boolean change) {
		Collection<Tuple> opposites = retrieveOpposites(side, signature);
		switch (side) {
		case PRIMARY:
			if (opposites != null)
				for (Tuple opposite : opposites) {
					propagateUpdate(direction, unify(updateElement, opposite));
				}
			else 
				propagateUpdate(direction, unifyWithDefaults(updateElement));
			break;
		case SECONDARY:
			if (opposites != null)
				for (Tuple opposite : opposites) {
					propagateUpdate(direction, unify(opposite, updateElement));
					if (change)
						propagateUpdate(direction.opposite(), unifyWithDefaults(opposite));
				}
			break;
		case BOTH:
				for (Tuple opposite : opposites) {
					propagateUpdate(direction, unify(updateElement, opposite));
					if (updateElement.equals(opposite)) continue;
					propagateUpdate(direction, unify(opposite, updateElement));
				}
				if (direction==Direction.REVOKE) // missed joining with itself
					propagateUpdate(direction, unify(updateElement, updateElement));
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.network.Supplier#pullInto(java.util.Collection)
	 */
	public void pullInto(Collection<Tuple> collector) {
		reteContainer.flushUpdates();

		for (Tuple signature : primarySlot.getSignatures()) {
			Collection<Tuple> primaries = primarySlot.get(signature); // not null due to the contract of IterableIndex.getSignatures()
			Collection<Tuple> opposites = secondarySlot.get(signature);
			if (opposites != null)
				for (Tuple ps: primaries) for (Tuple opposite : opposites) {
					collector.add(unify(ps, opposite));
				}
			else
				for (Tuple ps: primaries) {
					collector.add(unifyWithDefaults(ps));
				}
		}
	}

	private Tuple unifyWithDefaults(Tuple ps) {
		return unify(ps, defaults);
	}

}
