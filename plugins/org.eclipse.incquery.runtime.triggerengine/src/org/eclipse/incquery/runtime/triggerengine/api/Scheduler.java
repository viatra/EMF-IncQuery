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

/**
 * @author Abel Hegedus
 * 
 *         define Scheduler interface for trigger engine "ticks" - similar to firing strategy - doesn't have
 *         activation monitor - has trigger engine - automatic scheduler may work with update complete listener
 */
public abstract class Scheduler {
    
    public interface ISchedulerFactory{
        
        Scheduler prepareScheduler(Executor engine);

    }

    private Executor engine;

    protected Scheduler(Executor engine) {
        this.engine = checkNotNull(engine, "Cannot create scheduler with null IncQuery Engine!");
    }

    /**
     * Notifies engine of "tick". Subclasses should call this method to generate "ticks".
     */
    protected void schedule() {
        // TODO return results from engine
        // TODO session in Executor
        engine.schedule();
    }
    
    
}
