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
 * 
 * @author Abel Hegedus
 * 
 */
public final class TriggerEngineUtil {

    private TriggerEngineUtil() {}
    
    @SuppressWarnings("rawtypes")
    public static TriggerEngineFacade createTriggerEngine(IncQueryEngine engine, ISchedulerFactory schedulerFactory, Set<RuleSpecification> ruleSpecifications) {
        // create TriggerEngine and Agenda for engine
        TriggerEngine triggerEngine = new TriggerEngine(engine);
        
        // initialize rules form ruleSpecifications
        for (RuleSpecification<IPatternMatch, IncQueryMatcher<IPatternMatch>> ruleSpecification : ruleSpecifications) {
            triggerEngine.addRuleSpecification(ruleSpecification);
        }
        
        // register TriggerEngine for scheduler
        schedulerFactory.prepareScheduler(triggerEngine);
        
        return TriggerEngineFacade.create(triggerEngine);
    }

}
