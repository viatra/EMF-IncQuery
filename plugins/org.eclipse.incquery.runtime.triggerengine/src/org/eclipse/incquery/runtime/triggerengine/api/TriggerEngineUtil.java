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

import java.util.Set;

import org.apache.log4j.Level;
import org.eclipse.incquery.runtime.api.IPatternMatch;
import org.eclipse.incquery.runtime.api.IncQueryEngine;
import org.eclipse.incquery.runtime.api.IncQueryMatcher;
import org.eclipse.incquery.runtime.triggerengine.api.Scheduler.ISchedulerFactory;

/**
 * 
 * @author Abel Hegedus
 * 
 */
public final class TriggerEngineUtil {

    private TriggerEngineUtil() {}
    
    private static boolean debug = true;
    
    @SuppressWarnings("rawtypes")
    public static TriggerEngine createTriggerEngine(final IncQueryEngine engine, final ISchedulerFactory schedulerFactory, final Set<RuleSpecification> ruleSpecifications) {
        // create Executor and Agenda for engine
        Executor executor = new Executor(engine);
        if(debug) {
            engine.getLogger().setLevel((Level) Level.DEBUG);
        }
        if(ruleSpecifications != null) {
            // initialize rules form ruleSpecifications
            for (RuleSpecification<IPatternMatch, IncQueryMatcher<IPatternMatch>> ruleSpecification : ruleSpecifications) {
                executor.addRuleSpecification(ruleSpecification);
            }
        }
        
        // register Executor for scheduler
        schedulerFactory.prepareScheduler(executor);
        
        return TriggerEngine.create(executor);
    }

}
