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

package org.eclipse.incquery.runtime.rete.remote;

import java.util.Collection;

import org.eclipse.incquery.runtime.rete.network.Direction;
import org.eclipse.incquery.runtime.rete.network.ReteContainer;
import org.eclipse.incquery.runtime.rete.single.SingleInputNode;
import org.eclipse.incquery.runtime.rete.tuple.Tuple;

/**
 * This node receives updates from a remote supplier; no local updates are expected.
 * 
 * @author Gabor Bergmann
 * 
 */
public class RemoteSupplier extends SingleInputNode {

    RemoteReceiver counterpart;

    /**
     * @param reteContainer
     * @param remoteAddress
     */
    public RemoteSupplier(ReteContainer reteContainer, RemoteReceiver counterpart) {
        super(reteContainer);
        this.counterpart = counterpart;
        counterpart.addTarget(reteContainer.makeAddress(this));
    }

    public void pullInto(Collection<Tuple> collector) {
        Collection<Tuple> pulled = counterpart.remotePull();
        collector.addAll(pulled);
    }

    public void update(Direction direction, Tuple updateElement) {
        propagateUpdate(direction, updateElement);
    }

}
