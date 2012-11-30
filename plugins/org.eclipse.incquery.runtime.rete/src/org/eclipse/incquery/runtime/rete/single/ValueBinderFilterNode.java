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

/**
 * A filter node that keeps only those tuples that contain a certain value at a certain position.
 * 
 * @author Bergmann G�bor
 * 
 */
public class ValueBinderFilterNode extends FilterNode {

    int bindingIndex;
    Object bindingValue;

    /**
     * @param reteContainer
     * @param bindingIndex
     *            the position in the tuple that should be bound
     * @param bindingValue
     *            the value to which the tuple has to be bound
     */
    public ValueBinderFilterNode(ReteContainer reteContainer, int bindingIndex, Object bindingValue) {
        super(reteContainer);
        this.bindingIndex = bindingIndex;
        this.bindingValue = bindingValue;
    }

    @Override
    public boolean check(Tuple ps) {
        return bindingValue.equals(ps.get(bindingIndex));
    }

}
