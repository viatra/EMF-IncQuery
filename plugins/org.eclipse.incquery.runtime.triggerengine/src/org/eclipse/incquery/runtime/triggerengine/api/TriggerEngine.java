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

import java.util.Set;

import org.eclipse.incquery.runtime.api.IPatternMatch;
import org.eclipse.incquery.runtime.api.IncQueryEngine;
import org.eclipse.incquery.runtime.api.IncQueryMatcher;

import com.google.common.collect.ImmutableSet;

/**
 * @author Abel Hegedus
 * 
 */
public class TriggerEngine extends RuleEngine{

    private Executor engine;

    protected TriggerEngine(Executor engine) {
        super(checkNotNull(engine, "Cannot create trigger engine with null executor!").getAgenda());
        this.engine = engine;
    }

    public static TriggerEngine create(Executor engine) {
        return new TriggerEngine(engine);
    }

    public IncQueryEngine getIncQueryEngine() {
        return engine.getAgenda().getIncQueryEngine();
    }

    public <Match extends IPatternMatch, Matcher extends IncQueryMatcher<Match>> boolean addRuleSpecification(RuleSpecification<Match, Matcher> specification) {
        checkNotNull(specification, "Rule specification must be specified!");
        RuleInstance<Match,Matcher> instance = engine.addRuleSpecification(specification);
        return instance != null;
    }
    
    public Set<RuleSpecification<IPatternMatch, IncQueryMatcher<IPatternMatch>>> getRuleSpecifications() {
        return ImmutableSet.copyOf(engine.getRuleSpecifications());
    }

    /*public <Match extends IPatternMatch, Matcher extends IncQueryMatcher<Match>> boolean removeRuleSpecification(RuleSpecification<Match, Matcher> specification) {
        checkNotNull(specification, "Rule specification must be specified!");
        return engine.removeRuleSpecification(specification);
    }*/

    public void dispose() {
        engine.dispose();
    }

    
    
}
