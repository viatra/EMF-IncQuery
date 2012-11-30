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

import java.util.Collection;

import org.eclipse.incquery.runtime.rete.network.Direction;
import org.eclipse.incquery.runtime.rete.network.ReteContainer;
import org.eclipse.incquery.runtime.rete.tuple.Tuple;

public abstract class TransformerNode extends SingleInputNode {

    public TransformerNode(ReteContainer reteContainer) {
        super(reteContainer);
    }

    protected abstract Tuple transform(Tuple input);

    public void pullInto(Collection<Tuple> collector) {
        for (Tuple ps : reteContainer.pullPropagatedContents(this)) {
            collector.add(transform(ps));
        }
    }

    public void update(Direction direction, Tuple updateElement) {
        propagateUpdate(direction, transform(updateElement));
    }

}
