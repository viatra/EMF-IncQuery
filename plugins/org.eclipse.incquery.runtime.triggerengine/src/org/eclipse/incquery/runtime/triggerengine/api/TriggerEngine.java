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

import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.incquery.runtime.api.IPatternMatch;
import org.eclipse.incquery.runtime.api.IncQueryEngine;
import org.eclipse.incquery.runtime.api.IncQueryMatcher;

import com.google.common.collect.ImmutableSet;

/**
 * @author Abel Hegedus
 *
 * triggering mechanism
 *  - allows the execution of activations
 *  - move everything form Agenda that is not strictly related to managing the activation set
 *  - possibly allow an Activation Ordering (Comparator<Activation>?)
 */
public class TriggerEngine {

    private Agenda agenda;
    private Set<RuleSpecification<IPatternMatch, IncQueryMatcher<IPatternMatch>>> ruleSpecifications;
    private Session session;
    
    protected TriggerEngine(IncQueryEngine engine) {
        this(engine, new Session(), null);
    }

    protected TriggerEngine(IncQueryEngine engine, Session session) {
        this(engine, session, null);
    }
    
    @SuppressWarnings("rawtypes")
    protected TriggerEngine(IncQueryEngine engine, Comparator<RuleSpecification> comparator) {
        this(engine, new Session(), comparator);
    }

    @SuppressWarnings("rawtypes")
    protected TriggerEngine(IncQueryEngine engine, Session session, Comparator<RuleSpecification> comparator) {
        this.session = checkNotNull(session, "Cannot create trigger engine with null session!");
        agenda = new Agenda(engine);
        this.ruleSpecifications = new TreeSet<RuleSpecification<IPatternMatch,IncQueryMatcher<IPatternMatch>>>(comparator);
    }

    protected void schedule() {
        
        Set<Activation<?>> enabledActivations = agenda.getEnabledActivations();
        while(!enabledActivations.isEmpty()) {
            Activation<?> activation = enabledActivations.iterator().next();
            activation.fire(session);
        }
        
        /*for (RuleSpecification<IPatternMatch, IncQueryMatcher<IPatternMatch>> spec : ruleSpecifications) {
            RuleInstance<IPatternMatch,IncQueryMatcher<IPatternMatch>> instance = agenda.getInstance(spec);
            
            // only activations of enabled states are gathered
            for (ActivationState state : enabledStates) {
                // ensures that each activation keeps its state until it is fired
                activations.putAll(state, instance.getActivations(state));
            }
            
            if(!activations.isEmpty()) {
                // hashmap is not ordered, so we use the natural ordering of states
                for (ActivationState activationState : enabledStates) {
                    for (Activation<IPatternMatch> activation : activations.get(activationState)) {
                        // ensure that an earlier firing did not cause this activation to alter its state
                        if(activation.getState().equals(activationState)) {
                            activation.fire(session);
                        }
                    }
                }
            }
        }*/
    }
    
    /**
     * @return the agenda
     */
    public Agenda getAgenda() {
        return agenda;
    }
    
    @SuppressWarnings("unchecked")
    public <Match extends IPatternMatch, Matcher extends IncQueryMatcher<Match>> RuleInstance<Match,Matcher> addRuleSpecification(RuleSpecification<Match, Matcher> specification) {
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
        if(ruleSpecifications != null) {
            boolean removed = ruleSpecifications.remove(specification);
            if(removed) {
                agenda.removeRule(specification);
                return true;
            }
        }
        return false;
    } 
    
    /**
     * @return the ruleSpecifications
     */
    public Set<RuleSpecification<IPatternMatch, IncQueryMatcher<IPatternMatch>>> getRuleSpecifications() {
        return ImmutableSet.copyOf(ruleSpecifications);
    }
    
    public void dispose() {
        agenda.dispose();
    }
    
}
