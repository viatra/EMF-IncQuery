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
import java.util.Vector;

import org.eclipse.incquery.runtime.rete.network.Direction;
import org.eclipse.incquery.runtime.rete.network.Receiver;
import org.eclipse.incquery.runtime.rete.network.ReteContainer;
import org.eclipse.incquery.runtime.rete.network.Supplier;
import org.eclipse.incquery.runtime.rete.tuple.MaskedTupleMemory;
import org.eclipse.incquery.runtime.rete.tuple.Tuple;
import org.eclipse.incquery.runtime.rete.tuple.TupleMask;

/**
 * @author Bergmann Gábor
 * 
 */
public abstract class IndexerWithMemory extends StandardIndexer implements Receiver {

    protected MaskedTupleMemory memory;

    public MaskedTupleMemory getMemory() {
        return memory;
    }

    /**
     * @param reteContainer
     * @param mask
     */
    public IndexerWithMemory(ReteContainer reteContainer, TupleMask mask) {
        super(reteContainer, mask);
        this.memory = new MaskedTupleMemory(mask);
        reteContainer.registerClearable(memory);
    }

    @Override
    public void update(Direction direction, Tuple updateElement) {
        Tuple signature = mask.transform(updateElement);
        boolean change = (direction == Direction.INSERT) ? memory.add(updateElement, signature) : memory.remove(
                updateElement, signature);
        update(direction, updateElement, signature, change);
    }

    /**
     * Refined version of update
     */
    protected abstract void update(Direction direction, Tuple updateElement, Tuple signature, boolean change);

    @Override
    public void appendParent(Supplier supplier) {
        if (parent == null)
            parent = supplier;
        else
            throw new UnsupportedOperationException("Illegal RETE edge: " + this + " already has a parent (" + parent
                    + ") and cannot connect to additional parent (" + supplier + "). ");
    }

    @Override
    public void removeParent(Supplier supplier) {
        if (parent == supplier)
            parent = null;
        else
            throw new IllegalArgumentException("Illegal RETE edge removal: the parent of " + this + " is not "
                    + supplier);
    }

    @Override
    public Collection<Supplier> getParents() {
        Vector<Supplier> v = new Vector<Supplier>();
        v.add(parent);
        return v;
    }

}