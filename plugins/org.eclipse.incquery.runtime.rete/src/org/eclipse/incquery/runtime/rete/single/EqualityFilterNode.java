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

package org.eclipse.incquery.runtime.rete.single;

import org.eclipse.incquery.runtime.rete.network.ReteContainer;
import org.eclipse.incquery.runtime.rete.tuple.Tuple;

public class EqualityFilterNode extends FilterNode {

    int[] indices;
    int first;

    /**
     * @param reteContainer
     * @param indices
     *            indices of the Tuple that should hold equal values
     */
    public EqualityFilterNode(ReteContainer reteContainer, int[] indices) {
        super(reteContainer);
        this.indices = indices;
        first = indices[0];
    }

    @Override
    public boolean check(Tuple ps) {
        Object firstElement = ps.get(first);
        for (int i = 1 /* first is omitted */; i < indices.length; i++) {
            if (!ps.get(indices[i]).equals(firstElement))
                return false;
        }
        return true;
    }

}
