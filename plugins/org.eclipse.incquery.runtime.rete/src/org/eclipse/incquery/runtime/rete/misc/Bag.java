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

package org.eclipse.incquery.runtime.rete.misc;

import java.util.Collection;
import java.util.LinkedList;

import org.eclipse.incquery.runtime.rete.network.Direction;
import org.eclipse.incquery.runtime.rete.network.ReteContainer;
import org.eclipse.incquery.runtime.rete.tuple.Tuple;

/**
 * @author Gabor Bergmann
 * 
 *         A bag is a container that tuples can be dumped into. Does NOT propagate updates! Optimized for small contents
 *         size OR positive updates only.
 */
public class Bag extends SimpleReceiver {

    public Collection<Tuple> contents;

    public Bag(ReteContainer reteContainer) {
        super(reteContainer);
        contents = new LinkedList<Tuple>();
    }

    @Override
    public void update(Direction direction, Tuple updateElement) {
        if (direction == Direction.INSERT)
            contents.add(updateElement);
        else
            contents.remove(updateElement);
    }

}
