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

package org.eclipse.incquery.runtime.rete.boundary;

import org.eclipse.incquery.runtime.rete.matcher.IPatternMatcherContext.GeneralizationQueryDirection;
import org.eclipse.incquery.runtime.rete.matcher.IPatternMatcherRuntimeContext;
import org.eclipse.incquery.runtime.rete.network.Network;
import org.eclipse.incquery.runtime.rete.network.Receiver;
import org.eclipse.incquery.runtime.rete.remote.Address;

public class EntityFeeder extends Feeder {
    protected Object typeObject;

    /**
     * @param receiver
     * @param context
     * @param network
     * @param boundary
     * @param typeObject
     */
    public EntityFeeder(Address<? extends Receiver> receiver, IPatternMatcherRuntimeContext<?> context,
            Network network, ReteBoundary<?> boundary, Object typeObject) {
        super(receiver, context, network, boundary);
        this.typeObject = typeObject;
    }

    @Override
    public void feed() {
        if (typeObject != null) {
            if (context.allowedGeneralizationQueryDirection() == GeneralizationQueryDirection.BOTH)
                context.enumerateDirectUnaryInstances(typeObject, unaryCrawler());
            else
                context.enumerateAllUnaryInstances(typeObject, unaryCrawler());
        } else {
            context.enumerateAllUnaries(unaryCrawler());
        }
    }

}
