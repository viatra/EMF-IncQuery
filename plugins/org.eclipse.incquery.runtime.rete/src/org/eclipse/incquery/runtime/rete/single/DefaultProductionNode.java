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

import java.util.HashMap;
import java.util.Iterator;

import org.eclipse.incquery.runtime.rete.network.Production;
import org.eclipse.incquery.runtime.rete.network.ReteContainer;
import org.eclipse.incquery.runtime.rete.tuple.Tuple;

/**
 * Default implementation of the Production node, based on UniquenessEnforcerNode
 * 
 * @author Gabor Bergmann
 */
public class DefaultProductionNode extends UniquenessEnforcerNode implements Production {

    protected HashMap<Object, Integer> posMapping;

    // protected HashMap<TupleMask, Indexer> projections;

    /**
     * @param reteContainer
     * @param posMapping
     */
    public DefaultProductionNode(ReteContainer reteContainer, HashMap<Object, Integer> posMapping) {
        super(reteContainer, posMapping.size());
        this.posMapping = posMapping;
        // this.projections= new HashMap<TupleMask, Indexer>();
    }

    /**
     * @return the posMapping
     */
    public HashMap<Object, Integer> getPosMapping() {
        return posMapping;
    }

    public Iterator<Tuple> iterator() {
        return memory.iterator();
    }
}
