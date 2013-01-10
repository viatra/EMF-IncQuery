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

import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;
import org.eclipse.incquery.runtime.api.IPatternMatch;
import org.eclipse.incquery.runtime.api.IncQueryEngine;
import org.eclipse.incquery.runtime.api.IncQueryMatcher;

/**
 * @author Abel Hegedus
 *
 * TODO implement triggering mechanism
 *  - allows the execution of activations
 *  - move everything form Agenda that is not strictly related to managing the activation set
 *  - possibly allow an Activation Ordering (Comparator<Activation>?)
 */
public class TriggerEngine {

    private IncQueryEngine engine;
    private Agenda agenda;
    private Set<RuleSpecification<IPatternMatch, IncQueryMatcher<IPatternMatch>>> ruleSpecifications;
    
    public TriggerEngine(IncQueryEngine engine) {
        this.engine = engine;
        // TODO create Agenda and other stuff
        
    }

    protected void schedule() {
        // TODO implement default scheduling
    }
    
    /**
     * @return the agenda
     */
    public Agenda getAgenda() {
        return agenda;
    }
    
    @SuppressWarnings("unchecked")
    public <Match extends IPatternMatch, Matcher extends IncQueryMatcher<Match>> void addRuleSpecification(RuleSpecification<Match, Matcher> specification) {
        // TODO implement rule instantiation
        if(ruleSpecifications == null) {
            ruleSpecifications = new HashSet<RuleSpecification<IPatternMatch,IncQueryMatcher<IPatternMatch>>>();
        }
        ruleSpecifications.add((RuleSpecification<IPatternMatch, IncQueryMatcher<IPatternMatch>>) specification);
        
    }
    
    public Logger getLogger() {
        return engine.getLogger();
    }
    
    public void dispose() {
        // TODO implement disposal
    }
    
}
