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

import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.network.Direction;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.network.ReteContainer;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.tuple.TupleMask;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.tuple.Tuple;


/**
 * @author Gabor Bergmann
 * 
 */
public class JoinNode extends DualInputNode {

	/**
	 * @param reteContainer
	 * @param primarySlot
	 * @param secondarySlot
	 */
	public JoinNode(ReteContainer reteContainer, IterableIndexer primarySlot,
			Indexer secondarySlot, TupleMask complementerSecondaryMask) {
		super(reteContainer, primarySlot, secondarySlot,
				complementerSecondaryMask);
		// TODO Auto-generated constructor stub
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.index.DualInputNode#calibrate(org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.tuple.Tuple,
	 * org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.tuple.Tuple)
	 */
	@Override
	public Tuple calibrate(Tuple primary, Tuple secondary) {
		return unify(primary, secondary);
	}

//	public static Tuple calibrate(Tuple primary, Tuple secondary,
//			TupleMask complementerSecondaryMask) {
//		return complementerSecondaryMask.combine(primary, secondary,
//				Options.enableInheritance, true);
//	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.index.DualInputNode#notifyUpdate(org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.index.DualInputNode.Side,
	 * org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.network.Direction, org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.tuple.Tuple, org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.tuple.Tuple, boolean)
	 */
	@Override
	public void notifyUpdate(Side side, Direction direction,
			Tuple updateElement, Tuple signature, boolean change) {
		Collection<Tuple> opposites = retrieveOpposites(side, signature);
		if (opposites != null)
			for (Tuple opposite : opposites) {
				propagateUpdate(direction, unify(side, updateElement, opposite));
			}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.network.Supplier#pullInto(org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.network.Receiver)
	 */
	public void pullInto(Collection<Tuple> collector) {
		reteContainer.flushUpdates();

		for (Tuple signature : primarySlot.getSignatures()) {
			Collection<Tuple> primaries = primarySlot.get(signature); // not null due to the contract of IterableIndex.getSignatures()
			Collection<Tuple> opposites = secondarySlot.get(signature);
			if (opposites != null)
				for (Tuple ps: primaries) for (Tuple opposite : opposites) {
					collector.add(unify(Side.PRIMARY, ps, opposite));
				}
		}

	}

}
