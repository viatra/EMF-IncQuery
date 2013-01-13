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

import com.google.common.base.Preconditions;

/**
 * @author Abel Hegedus
 * 
 *         TODO define Scheduler interface for trigger engine "ticks" - similar to firing strategy - doesn't have
 *         activation monitor - has trigger engine - automatic scheduler may work with update complete listener
 */
public abstract class Scheduler {
    
    public interface ISchedulerFactory{
        
        Scheduler prepareScheduler(TriggerEngine engine);

    }

    private TriggerEngine engine;

    protected Scheduler(TriggerEngine engine) {
        Preconditions.checkNotNull(engine);
        this.engine = engine;
    }

    /**
     * Notifies engine of "tick". Subclasses should call this method to generate "ticks".
     */
    protected void schedule() {
        engine.schedule();
    }
    
    
}
