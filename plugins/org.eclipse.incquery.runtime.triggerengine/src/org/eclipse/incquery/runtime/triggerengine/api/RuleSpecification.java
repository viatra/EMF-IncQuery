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

import java.util.Collection;
import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.incquery.runtime.api.IMatcherFactory;
import org.eclipse.incquery.runtime.api.IPatternMatch;
import org.eclipse.incquery.runtime.api.IncQueryEngine;
import org.eclipse.incquery.runtime.api.IncQueryMatcher;

import com.google.common.collect.HashMultimap;
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
    private final Set<ActivationState> enabledStates; 
    
    public RuleSpecification(IMatcherFactory<Matcher> factory, ActivationLifeCycle lifeCycle, Set<Job<Match>> jobs) {
        this(factory, lifeCycle, jobs, null);
    }
    
    /**
     * 
     */
    public RuleSpecification(IMatcherFactory<Matcher> factory, ActivationLifeCycle lifeCycle, Set<Job<Match>> jobs, Comparator<Match> comparator) {
        this.factory = checkNotNull(factory, "Cannot create rule specification with null matcher factory!");
        this.lifeCycle = checkNotNull(ActivationLifeCycle.copyOf(lifeCycle), "Cannot create rule specification with null life cycle!");
        this.jobs = HashMultimap.create();
        Set<ActivationState> states = new TreeSet<ActivationState>();
        if(jobs != null && !jobs.isEmpty()) {
            for (Job<Match> job : jobs) {
                ActivationState state = job.getActivationState();
                this.jobs.put(state, job);
                states.add(state);
            }
        }
        this.enabledStates = ImmutableSet.copyOf(states);
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
        return lifeCycle;
    }
    
    /**
     * @return the enabledStates
     */
    public Set<ActivationState> getEnabledStates() {
        return enabledStates;
    }
    
    public Collection<Job<Match>> getJobs(ActivationState state){
        return jobs.get(state);
    }
    
    /**
     * @return the jobs
     */
    public Multimap<ActivationState, Job<Match>> getJobs() {
        return jobs;
    }
    
    /**
     * @return the comparator
     */
    public Comparator<Match> getComparator() {
        return comparator;
    }
}
