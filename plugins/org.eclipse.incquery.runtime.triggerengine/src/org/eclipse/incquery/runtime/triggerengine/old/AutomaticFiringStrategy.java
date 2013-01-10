/*******************************************************************************
 * Copyright (c) 2010-2012, Tamas Szabo, Abel Hegedus, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Tamas Szabo, Abel Hegedus - initial API and implementation
 *******************************************************************************/

package org.eclipse.incquery.runtime.triggerengine.firing;

import org.eclipse.incquery.runtime.api.IPatternMatch;
import org.eclipse.incquery.runtime.triggerengine.api.Activation;
import org.eclipse.incquery.runtime.triggerengine.api.ActivationMonitor;

/**
 * This class automatically fires the applicable activations which are present in the given {@link ActivationMonitor}.
 * It is used by the Validation Framework to automatically create/update/remove problem markers when it is needed.
 * 
 * @author Tamas Szabo
 * 
 */
public class AutomaticFiringStrategy implements IUpdateCompleteListener {

    private final ActivationMonitor monitor;

    public AutomaticFiringStrategy(ActivationMonitor monitor) {
        this.monitor = monitor;
    }

    @Override
    public void updateComplete() {
        if (monitor != null) {
            for (Activation<? extends IPatternMatch> a : monitor.getActivations()) {
                a.fire();
            }
            monitor.clear();
        }
    }

}
