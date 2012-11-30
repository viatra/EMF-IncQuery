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

import java.util.Collection;

import org.eclipse.incquery.runtime.rete.network.Direction;
import org.eclipse.incquery.runtime.rete.network.ReteContainer;
import org.eclipse.incquery.runtime.rete.tuple.Tuple;
import org.eclipse.incquery.runtime.rete.tuple.TupleMask;

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
    public JoinNode(ReteContainer reteContainer, IterableIndexer primarySlot, Indexer secondarySlot,
            TupleMask complementerSecondaryMask) {
        super(reteContainer, primarySlot, secondarySlot, complementerSecondaryMask);
        // TODO Auto-generated constructor stub
    }

    @Override
    public Tuple calibrate(Tuple primary, Tuple secondary) {
        return unify(primary, secondary);
    }

    // public static Tuple calibrate(Tuple primary, Tuple secondary,
    // TupleMask complementerSecondaryMask) {
    // return complementerSecondaryMask.combine(primary, secondary,
    // Options.enableInheritance, true);
    // }

    @Override
    public void notifyUpdate(Side side, Direction direction, Tuple updateElement, Tuple signature, boolean change) {
        Collection<Tuple> opposites = retrieveOpposites(side, signature);

        if (opposites != null) {
            for (Tuple opposite : opposites) {
                propagateUpdate(direction, unify(side, updateElement, opposite));
            }
        }

        // compensate for coincidence of slots
        if (coincidence) {
            if (opposites != null) {
                for (Tuple opposite : opposites) {
                    if (opposite.equals(updateElement))
                        continue; // INSERT: already joined with itself
                    propagateUpdate(direction, unify(opposite, updateElement));
                }
            }
            if (direction == Direction.REVOKE) // missed joining with itself
                propagateUpdate(direction, unify(updateElement, updateElement));
            //
            // switch(direction) {
            // case INSERT:
            // opposites = new ArrayList<Tuple>(opposites);
            // opposites.remove(updateElement);
            // break;
            // case REVOKE:
            // opposites = (opposites == null) ? new ArrayList<Tuple>() : new ArrayList<Tuple>(opposites);
            // opposites.add(updateElement);
            // break;
            // }
            //
        }
    }

    @Override
    public void pullInto(Collection<Tuple> collector) {
        reteContainer.flushUpdates();

        for (Tuple signature : primarySlot.getSignatures()) {
            Collection<Tuple> primaries = primarySlot.get(signature); // not null due to the contract of
                                                                      // IterableIndex.getSignatures()
            Collection<Tuple> opposites = secondarySlot.get(signature);
            if (opposites != null)
                for (Tuple ps : primaries)
                    for (Tuple opposite : opposites) {
                        collector.add(unify(ps, opposite));
                    }
        }

    }

}
