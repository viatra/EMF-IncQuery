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

import com.google.common.base.Preconditions;

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
        Preconditions.checkNotNull(engine);
        this.engine = engine;
        this.agenda = new Agenda(engine);
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
    public <Match extends IPatternMatch, Matcher extends IncQueryMatcher<Match>> RuleInstance<Match,Matcher> addRuleSpecification(RuleSpecification<Match, Matcher> specification) {
        // TODO implement rule instantiation
        if(ruleSpecifications == null) {
            ruleSpecifications = new HashSet<RuleSpecification<IPatternMatch,IncQueryMatcher<IPatternMatch>>>();
        }
        boolean added = ruleSpecifications.add((RuleSpecification<IPatternMatch, IncQueryMatcher<IPatternMatch>>) specification);
        RuleInstance<Match,Matcher> instance = null;
        if(added) {
            instance = agenda.instantiateRule(specification);
        }
        return instance;
        
    }
    
    public <Match extends IPatternMatch, Matcher extends IncQueryMatcher<Match>> boolean removeRuleSpecification(RuleSpecification<Match, Matcher> specification) {
        // TODO implement rule instantiation
        if(ruleSpecifications != null) {
            boolean removed = ruleSpecifications.remove(specification);
            if(removed) {
                agenda.removeRule(specification);
                return true;
            }
        }
        return false;
    } 
    
    public Logger getLogger() {
        return engine.getLogger();
    }
    
    public void dispose() {
        this.agenda.dispose();
    }
    
}
