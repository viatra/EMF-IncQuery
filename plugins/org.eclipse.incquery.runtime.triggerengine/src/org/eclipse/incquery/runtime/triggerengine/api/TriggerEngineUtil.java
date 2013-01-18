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

    private TriggerEngineUtil() {
    }

    private static boolean debug = true;

    public static RuleEngine createTriggerEngine(final IncQueryEngine engine,
            final ISchedulerFactory schedulerFactory, final Set<RuleSpecification<? extends IPatternMatch, ? extends IncQueryMatcher<? extends IPatternMatch>>> ruleSpecifications) {
        Executor executor = new Executor(engine);
        if (debug) {
            engine.getLogger().setLevel((Level) Level.DEBUG);
        }
        if (ruleSpecifications != null) {
            Agenda agenda = executor.getAgenda();
            for (RuleSpecification<?, ?> ruleSpecification : ruleSpecifications) {
                agenda.instantiateRule(ruleSpecification);
            }
        }
        Scheduler scheduler = schedulerFactory.prepareScheduler(executor);
        return TriggerEngine.create(scheduler);
    }

    public static RuleEngine createRuleEngine(final IncQueryEngine engine,
            final Set<RuleSpecification<?, ?>> ruleSpecifications) {
        Agenda agenda = new Agenda(engine);

        if (ruleSpecifications != null) {
            for (RuleSpecification<?, ?> ruleSpecification : ruleSpecifications) {
                agenda.instantiateRule(ruleSpecification);
            }
        }

        return RuleEngine.create(agenda);
    }
}
