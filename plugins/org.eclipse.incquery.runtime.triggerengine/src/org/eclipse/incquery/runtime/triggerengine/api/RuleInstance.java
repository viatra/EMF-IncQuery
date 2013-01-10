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

import java.util.Comparator;
import java.util.Set;

import org.eclipse.incquery.runtime.api.IPatternMatch;
import org.eclipse.incquery.runtime.api.IncQueryEngine;
import org.eclipse.incquery.runtime.api.IncQueryMatcher;
import org.eclipse.incquery.runtime.exception.IncQueryException;
import org.eclipse.incquery.runtime.triggerengine.api.ActivationLifeCycle.ActivationLifeCycleEvent;
import org.eclipse.incquery.runtime.triggerengine.notification.ActivationNotificationProvider;
import org.eclipse.incquery.runtime.triggerengine.notification.IActivationNotificationListener;
import org.eclipse.incquery.runtime.triggerengine.notification.IActivationNotificationProvider;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Table;
import com.google.common.collect.TreeBasedTable;

/**
 * @author Abel Hegedus
 * 
 *         TODO implement rule instance - manage activation set - reference rule specification - reference matcher -
 *         register match listener on matcher - send activation state changes to listeners
 */
public class RuleInstance<Match extends IPatternMatch, Matcher extends IncQueryMatcher<Match>> implements
        IActivationNotificationProvider {

    private Matcher matcher;
    private final RuleSpecification<Match, Matcher> specification;
    private Table<ActivationState, Match, Activation<Match>> activations;
    private IActivationNotificationProvider activationNotificationProvider;

    /**
     * TODO created only in an {@link Agenda}!
     * 
     * @param specification
     * @param engine
     */
    protected RuleInstance(RuleSpecification<Match, Matcher> specification, IncQueryEngine engine) {
        this.specification = specification;
        this.activations = HashBasedTable.create();
        this.activationNotificationProvider = new ActivationNotificationProvider() {

            @Override
            protected void listenerAdded(IActivationNotificationListener listener, boolean fireNow) {
                if (fireNow) {
                    for (Activation<Match> activation : getActivations()) {
                        listener.activationAppeared(activation);
                    }
                }
            }
        };
        try {
            this.matcher = specification.getFactory().getMatcher(engine);
            // TODO connect to matcher
            // TODO add match update listener
        } catch (IncQueryException e) {
            engine.getLogger().error(
                    String.format("Could not initialize matcher %s in engine %s", specification.getFactory()
                            .getPatternFullyQualifiedName(), engine.getEmfRoot().toString()), e);
        }
    }

    public void fire(Activation<Match> activation) {
        ActivationState activationState = activation.getState();
        Match patternMatch = activation.getPatternMatch();

        if (activations.contains(activationState, patternMatch)) {
            Set<Job<Match>> jobs = specification.getJobs(activationState);
            for (Job<Match> job : jobs) {
                job.execute(activation);
            }
            activationStateTransition(activation, ActivationLifeCycleEvent.ACTIVATION_FIRES);
        }
    }

    protected ActivationState activationStateTransition(Activation<Match> activation, ActivationLifeCycleEvent event) {
        ActivationState activationState = activation.getState();
        ActivationState nextActivationState = specification.getLifeCycle().nextActivationState(activationState, event);
        Match patternMatch = activation.getPatternMatch();
        if (nextActivationState != null) {
            activations.remove(activationState, patternMatch);
            activation.setState(nextActivationState);
            if (!nextActivationState.equals(ActivationState.INACTIVE)) {
                activations.put(nextActivationState, patternMatch, activation);
            }
            return nextActivationState;
        } else {
            return activationState;
        }
    }

    /**
     * @return the matcher
     */
    public IncQueryMatcher<?> getMatcher() {
        return matcher;
    }
    

    /**
     * @return the specification
     */
    public RuleSpecification<Match, Matcher> getSpecification() {
        return specification;
    }

    @Override
    public boolean addActivationNotificationListener(IActivationNotificationListener listener, boolean fireNow) {
        return activationNotificationProvider.addActivationNotificationListener(listener, fireNow);
    }

    @Override
    public boolean removeActivationNotificationListener(IActivationNotificationListener listener) {
        return activationNotificationProvider.removeActivationNotificationListener(listener);
    }

    public Set<Activation<Match>> getActivations() {
        return ImmutableSet.copyOf(activations.values());
    }

    public Set<Activation<Match>> getActivations(ActivationState state) {
        return ImmutableSet.copyOf(activations.row(state).values());
    }

    public void addActivationOrdering(Comparator<Match> activationComparator) {
        Table<ActivationState, Match, Activation<Match>> newActivationsTable = TreeBasedTable.create(null, activationComparator);
        newActivationsTable.putAll(activations);
        activations = newActivationsTable;
    }
    
    public void dispose() {
        // TODO clean up
    }
}
