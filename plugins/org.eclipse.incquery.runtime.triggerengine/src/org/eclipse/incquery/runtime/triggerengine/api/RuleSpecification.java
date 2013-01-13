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

import java.util.Collections;
import java.util.Comparator;
import java.util.Set;

import org.eclipse.incquery.runtime.api.IMatcherFactory;
import org.eclipse.incquery.runtime.api.IPatternMatch;
import org.eclipse.incquery.runtime.api.IncQueryEngine;
import org.eclipse.incquery.runtime.api.IncQueryMatcher;
import org.eclipse.incquery.runtime.triggerengine.TriggerEngineConstants;

import com.google.common.base.Preconditions;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimap;

/**
 * @author Abel Hegedus
 *
 * implement rule specification
 *  - Activation Life Cycle
 *  - Jobs related to Activation State
 *  - create Rule Instance with Matcher(Factory)/Pattern
 */
public class RuleSpecification<Match extends IPatternMatch, Matcher extends IncQueryMatcher<Match>> {

    private final IMatcherFactory<Matcher> factory;
    private final ActivationLifeCycle lifeCycle;
    private final Multimap<ActivationState, Job<Match>> jobs;
    private final Comparator<Match> comparator;
    
    public RuleSpecification(IMatcherFactory<Matcher> factory, ActivationLifeCycle lifeCycle, Set<Job<Match>> jobs) {
        this(factory, lifeCycle, jobs, null);
    }
    
    /**
     * 
     */
    public RuleSpecification(IMatcherFactory<Matcher> factory, ActivationLifeCycle lifeCycle, Set<Job<Match>> jobs, Comparator<Match> comparator) {
        Preconditions.checkNotNull(factory);
        Preconditions.checkNotNull(lifeCycle);
        this.factory = factory;
        this.lifeCycle = lifeCycle;
        this.jobs = HashMultimap.create();
        if(jobs != null && !jobs.isEmpty()) {
            for (Job<Match> job : jobs) {
                this.jobs.put(job.getActivationState(), job);
            }
        }
        this.comparator = comparator;
    }
    
    protected RuleInstance<Match, Matcher> instantiateRule(IncQueryEngine engine) {
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
     * 
     * @return the lifeCycle
     */
    public ActivationLifeCycle getLifeCycle() {
        if(TriggerEngineConstants.ALLOW_RUNTIME_LIFECYCLE_CHANGES) {
            return lifeCycle;
        } else {
            return ActivationLifeCycle.copyOf(lifeCycle);
        }
    }
    
    public Set<Job<Match>> getJobs(ActivationState state){
        if(TriggerEngineConstants.MUTABLE_JOBLISTS) {
            return Collections.unmodifiableSet((Set<Job<Match>>) jobs.get(state));
        } else {
            return ImmutableSet.copyOf(jobs.get(state));
        }
    }
    
    /**
     * @return the jobs
     */
    public Multimap<ActivationState, Job<Match>> getJobs() {
        if(TriggerEngineConstants.MUTABLE_JOBLISTS) {
            return jobs;
        } else {
            return ImmutableMultimap.copyOf(jobs);
        }
    }
    
    /**
     * @return the comparator
     */
    public Comparator<Match> getComparator() {
        return comparator;
    }
}
