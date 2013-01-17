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
import java.util.Set;

import org.eclipse.incquery.runtime.api.IPatternMatch;
import org.eclipse.incquery.runtime.api.IncQueryEngine;
import org.eclipse.incquery.runtime.api.IncQueryMatcher;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

/**
 * @author Abel Hegedus
 *
 * triggering mechanism
 *  - allows the execution of activations
 *  - move everything form Agenda that is not strictly related to managing the activation set
 *  - possibly allow an Activation Ordering (Comparator<Activation>?)
 */
public class Executor {

    private Agenda agenda;
    private Set<RuleSpecification<IPatternMatch, IncQueryMatcher<IPatternMatch>>> ruleSpecifications;
    private Context context;
    
    protected Executor(IncQueryEngine engine) {
        this(engine, new Context(), null);
    }

    protected Executor(IncQueryEngine engine, Context context) {
        this(engine, context, null);
    }
    
    @SuppressWarnings("rawtypes")
    protected Executor(IncQueryEngine engine, Comparator<RuleSpecification> comparator) {
        this(engine, new Context(), comparator);
    }

    @SuppressWarnings("rawtypes")
    protected Executor(IncQueryEngine engine, Context context, Comparator<RuleSpecification> comparator) {
        this.context = checkNotNull(context, "Cannot create trigger engine with null context!");
        agenda = new Agenda(engine);
        if(comparator != null) {
            this.ruleSpecifications = Sets.newTreeSet(comparator);
        } else {
            this.ruleSpecifications = Sets.newHashSet();
        }
    }

    protected void schedule() {
        
        Set<Activation<?>> enabledActivations = agenda.getEnabledActivations();
        while(!enabledActivations.isEmpty()) {
            Activation<?> activation = enabledActivations.iterator().next();
            activation.fire(context);
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
                            activation.fire(context);
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
    
    /**
     * @return the context
     */
    public Context getContext() {
        return context;
    }
    
    @SuppressWarnings("unchecked")
    public <Match extends IPatternMatch, Matcher extends IncQueryMatcher<Match>> RuleInstance<Match,Matcher> addRuleSpecification(RuleSpecification<Match, Matcher> specification) {
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
