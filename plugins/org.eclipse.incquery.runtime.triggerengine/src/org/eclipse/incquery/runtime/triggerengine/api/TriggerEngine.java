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
 */
public class TriggerEngine extends RuleEngine{

    private Scheduler scheduler;

    protected TriggerEngine(Scheduler scheduler) {
        super(checkNotNull(scheduler, "Cannot create trigger engine with null scheduler!").getExecutor().getAgenda());
        this.scheduler = scheduler;
    }

    public static RuleEngine create(Scheduler scheduler) {
        return new TriggerEngine(scheduler);
    }

    public void dispose() {
        scheduler.dispose();
    }

    /**
     * @return the scheduler
     */
    protected Scheduler getScheduler() {
        return scheduler;
    }
    
}
