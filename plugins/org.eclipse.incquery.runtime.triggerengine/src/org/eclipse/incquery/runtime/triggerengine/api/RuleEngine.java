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

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimap;

/**
 * @author Abel Hegedus
 *
 */
public class RuleEngine {

    private Agenda agenda;
    
    protected RuleEngine(Agenda agenda) {
        this.agenda = checkNotNull(agenda, "Cannot create rule engine with null agenda!");
    }
    
    public static RuleEngine create(Agenda agenda) {
        return new RuleEngine(agenda);
    }
    
    /**
     * @return the agenda
     */
    protected Agenda getAgenda() {
        return agenda;
    }
    
    public IncQueryEngine getIncQueryEngine() {
        return getAgenda().getIncQueryEngine();
    }

    public Multimap<ActivationState, Activation<?>> getActivations() {
        return ImmutableMultimap.copyOf(agenda.getActivations());
    }

    public Set<Activation<?>> getActivations(ActivationState state) {
        checkNotNull(state, "Activation state must be specified!");
        return ImmutableSet.copyOf(agenda.getActivations(state));
    }

    public <Match extends IPatternMatch, Matcher extends IncQueryMatcher<Match>> Set<Activation<Match>> getActivations(
            RuleSpecification<Match, Matcher> specification) {
        checkNotNull(specification, "Rule specification must be specified!");
        return ImmutableSet.copyOf(agenda.getActivations(specification));
    }
    
    public <Match extends IPatternMatch, Matcher extends IncQueryMatcher<Match>> Set<Activation<Match>> getActivations(
            RuleSpecification<Match, Matcher> specification, ActivationState state) {
        checkNotNull(specification, "Rule specification must be specified!");
        checkNotNull(state, "Activation state must be specified!");
        return ImmutableSet.copyOf(agenda.getInstance(specification).getActivations(state));
    }
    
    public <Match extends IPatternMatch, Matcher extends IncQueryMatcher<Match>> boolean addRule(RuleSpecification<Match, Matcher> specification) {
        checkNotNull(specification, "Rule specification must be specified!");
        RuleInstance<Match,Matcher> instance = agenda.instantiateRule(specification);
        return instance != null;
    }
    
    public Set<RuleSpecification<IPatternMatch, IncQueryMatcher<IPatternMatch>>> getRules() {
        return ImmutableSet.copyOf(agenda.getRuleInstanceMap().keySet());
    }

    public <Match extends IPatternMatch, Matcher extends IncQueryMatcher<Match>> boolean removeRule(RuleSpecification<Match, Matcher> specification) {
        checkNotNull(specification, "Rule specification must be specified!");
        return agenda.removeRule(specification);
    }
    
    public void dispose() {
        agenda.dispose();
    } 
}
