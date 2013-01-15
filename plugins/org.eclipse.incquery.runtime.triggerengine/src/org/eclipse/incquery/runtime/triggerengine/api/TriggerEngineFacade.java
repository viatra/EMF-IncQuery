/*******************************************************************************
 * Copyright (c) 2010-2013, Abel Hegedus, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Abel Hegedus - initial API and implementation
 *******************************************************************************/
package org.eclipse.incquery.runtime.triggerengine.api;

import static com.google.common.base.Preconditions.checkNotNull;

import org.eclipse.incquery.runtime.api.IncQueryEngine;

/**
 * @author Abel Hegedus
 *
 */
public class TriggerEngineFacade {

    private TriggerEngine engine;
    
    private TriggerEngineFacade(TriggerEngine engine) {
        this.engine = checkNotNull(engine);
    }
    
    public static TriggerEngineFacade create(TriggerEngine engine) {
        return new TriggerEngineFacade(engine);
    }
    
    public void dispose() {
        // TODO implement
        engine.dispose();
    }
    
    public IncQueryEngine getIncQueryEngine() {
        return engine.getAgenda().getIncQueryEngine();
    }
}
