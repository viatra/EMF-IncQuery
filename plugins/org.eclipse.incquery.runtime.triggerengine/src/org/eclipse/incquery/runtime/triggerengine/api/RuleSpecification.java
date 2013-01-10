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

import org.eclipse.incquery.runtime.api.IMatcherFactory;
import org.eclipse.incquery.runtime.api.IPatternMatch;
import org.eclipse.incquery.runtime.api.IncQueryEngine;
import org.eclipse.incquery.runtime.api.IncQueryMatcher;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimap;

/**
 * @author Abel Hegedus
 *
 * TODO implement rule specification
 *  - Activation Life Cycle
 *  - Jobs related to Activation State
 *  - create Rule Instance with Matcher(Factory)/Pattern
 */
public class RuleSpecification<Match extends IPatternMatch, Matcher extends IncQueryMatcher<Match>> {

    private IMatcherFactory<Matcher> factory;
    private ActivationLifeCycle lifeCycle;
    private Multimap<ActivationState, Job<Match>> jobs;
    
    public RuleSpecification(IMatcherFactory<Matcher> factory, ActivationLifeCycle lifeCycle, Set<Job<Match>> jobs) {
        this.factory = factory;
        this.lifeCycle = lifeCycle;
        if(jobs != null && !jobs.isEmpty()) {
            this.jobs = HashMultimap.create();
            for (Job<Match> job : jobs) {
                this.jobs.put(job.getActivationState(), job);
            }
        }
    }
    
    public RuleInstance<Match, Matcher> instantiateRule(IncQueryEngine engine) {
        RuleInstance<Match,Matcher> instance = new RuleInstance<Match, Matcher>(this, engine);
        return instance;
    }
    
    /**
     * @return the factory
     */
    public IMatcherFactory<Matcher> getFactory() {
        return factory;
    }
    
    /**
     * @return the lifeCycle
     */
    public ActivationLifeCycle getLifeCycle() {
        return lifeCycle;
    }
    
    public Set<Job<Match>> getJobs(ActivationState state){
        return ImmutableSet.copyOf(jobs.get(state));
    }
    
    /**
     * @return the jobs
     */
    public Multimap<ActivationState, Job<Match>> getJobs() {
        return ImmutableMultimap.copyOf(jobs);
    }
}
