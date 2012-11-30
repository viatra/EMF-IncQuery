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

import org.eclipse.incquery.runtime.rete.network.ReteContainer;
import org.eclipse.incquery.runtime.rete.tuple.Tuple;

/**
 * An aggregation node that simply counts the number of tuples conforming to the signature.
 * 
 * @author Bergmann Gábor
 * 
 */
public class CountNode extends AggregatorNode {

    public CountNode(ReteContainer reteContainer, ProjectionIndexer projection) {
        super(reteContainer, projection);
    }

    int sizeOf(Collection<Tuple> group) {
        return group == null ? 0 : group.size();
    }

    @Override
    public Object aggregateGroup(Tuple signature, Collection<Tuple> group) {
        return sizeOf(group);
    }

}
