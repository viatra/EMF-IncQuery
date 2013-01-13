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

import org.eclipse.incquery.runtime.api.IPatternMatch;
import org.eclipse.incquery.runtime.api.IncQueryEngine;
import org.eclipse.incquery.runtime.api.IncQueryMatcher;
import org.eclipse.incquery.runtime.triggerengine.api.Scheduler.ISchedulerFactory;

/**
 * @author Abel Hegedus
 * TODO implement short-hand for trigger engine creation
 *  - IQEngine: the model to work on
 *  - Scheduler: the "tick" generator
 *  - Set<RuleSpecification>: the rules to initialize 
 */
public class TriggerEngineUtil {

    @SuppressWarnings("rawtypes")
    public static TriggerEngine createTriggerEngine(IncQueryEngine engine, ISchedulerFactory schedulerFactory, Set<RuleSpecification> ruleSpecifications) {
        // create TriggerEngine and Agenda for engine
        // initialize rules form ruleSpecifications
        // register TriggerEngine for scheduler
        TriggerEngine triggerEngine = new TriggerEngine(engine);
        
        for (RuleSpecification<IPatternMatch, IncQueryMatcher<IPatternMatch>> ruleSpecification : ruleSpecifications) {
            triggerEngine.addRuleSpecification(ruleSpecification);
        }
        
        schedulerFactory.prepareScheduler(triggerEngine);
        
        return triggerEngine;
    }
    
}
