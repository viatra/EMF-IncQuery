/*******************************************************************************
 * Copyright (c) 2010-2012, Tamas Szabo, Abel Hegedus, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Tamas Szabo, Abel Hegedus - initial API and implementation
 *******************************************************************************/

package org.eclipse.incquery.runtime.triggerengine.api;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.incquery.runtime.api.IPatternMatch;
import org.eclipse.incquery.runtime.api.IncQueryEngine;
import org.eclipse.incquery.runtime.api.IncQueryMatcher;
import org.eclipse.incquery.runtime.triggerengine.TriggerEngineConstants;
import org.eclipse.incquery.runtime.triggerengine.api.ActivationLifeCycle.ActivationLifeCycleEvent;
import org.eclipse.incquery.runtime.triggerengine.notification.IActivationNotificationListener;
import org.eclipse.incquery.runtime.triggerengine.old.AutomaticFiringStrategy;
import org.eclipse.incquery.runtime.triggerengine.old.TimedFiringStrategy;

import com.google.common.base.Preconditions;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.google.common.collect.SetMultimap;
import com.google.common.collect.TreeMultimap;

/**
 * An Agenda is associated to each EMF instance model (more precisely {@link IncQueryEngine} or equivalently
 * {@link Notifier} in the context of EMF-IncQuery) and it is responsible for creating, managing and disposing rules in
 * the AbstractRule Engine. It provides an unmodifiable view for the collection of applicable activations.
 * 
 * <p>
 * One must register an {@link IActivationNotificationListener} in order to receive notifications automatically about
 * the changes in the collection of activations.
 * 
 * <p>
 * The Trigger Engine is a collection of strategies which can be used to fire these activations with pre-defined
 * timings. Such strategies include the {@link AutomaticFiringStrategy} and {@link TimedFiringStrategy} at the current
 * state of development.
 * 
 * <p>
 * 
 * One may define whether multiple firing of the same activation is allowed; that is, only the Appeared state will be
 * used from the lifecycle of {@link Activation}s and consecutive firing of a previously applied {@link Activation} is
 * possible. For more information on the lifecycle see {@link Activation}. Multiple firing is used for example in Design
 * Space Exploration scenarios.
 * 
 * TODO rewrite code comments - multiple firing is implicitly allowed by defining a job that wokrs in the Fired state -
 * life-cycle is defined separately (see {@link ActivationLifeCycle}) - {@link Scheduler} and {@link TriggerEngine} are
 * separated from Agenda
 * 
 * @author Tamas Szabo
 * 
 */
public class Agenda {

    private final IncQueryEngine iqEngine;
    private final Map<RuleSpecification<IPatternMatch, IncQueryMatcher<IPatternMatch>>,RuleInstance<IPatternMatch, IncQueryMatcher<IPatternMatch>>> ruleInstanceMap;
    private Multimap<ActivationState, Activation<?>> activations;
    private final IActivationNotificationListener activationListener;

    /**
     * Instantiates a new Agenda instance with the given {@link IncQueryEngine}.
     * 
     * @param iqEngine
     *            the {@link IncQueryEngine} instance
     */
    protected Agenda(IncQueryEngine iqEngine) {
        Preconditions.checkNotNull(iqEngine);
        this.iqEngine = iqEngine;
        this.ruleInstanceMap = new HashMap<RuleSpecification<IPatternMatch, IncQueryMatcher<IPatternMatch>>,RuleInstance<IPatternMatch, IncQueryMatcher<IPatternMatch>>>();
        this.activations = HashMultimap.create();

        this.activationListener = new IActivationNotificationListener() {

            @Override
            public void activationChanged(Activation<? extends IPatternMatch> activation, ActivationState oldState,
                    ActivationLifeCycleEvent event) {
                activations.remove(oldState, activation);
                ActivationState state = activation.getState();
                switch (state) {
                case INACTIVE:
                    // do nothing
                    break;
                default:
                    activations.put(state, activation);
                    break;
                }
            }
        };

    }

    /*public Notifier getNotifier() {
        return iqEngine.getEmfRoot();
    }*/
    
    @SuppressWarnings("unchecked")
    protected <Match extends IPatternMatch, Matcher extends IncQueryMatcher<Match>> RuleInstance<Match, Matcher> instantiateRule(
            RuleSpecification<Match, Matcher> specification) {
        RuleInstance<Match, Matcher> rule = specification.instantiateRule(iqEngine);
        rule.addActivationNotificationListener(activationListener, true);
        ruleInstanceMap.put((RuleSpecification<IPatternMatch, IncQueryMatcher<IPatternMatch>>) specification,(RuleInstance<IPatternMatch, IncQueryMatcher<IPatternMatch>>) rule);
        return rule;
    }

    protected <Match extends IPatternMatch, Matcher extends IncQueryMatcher<Match>> boolean removeRule(
            RuleInstance<Match, Matcher> rule) {
        if (ruleInstanceMap.containsValue(rule)) {
            rule.removeActivationNotificationListener(activationListener);
            rule.dispose();
            ruleInstanceMap.remove(rule.getSpecification());
            return true;
        }
        return false;
    }
    
    protected <Match extends IPatternMatch, Matcher extends IncQueryMatcher<Match>> boolean removeRule(
            RuleSpecification<Match, Matcher> ruleSpecification) {
        RuleInstance<IPatternMatch, IncQueryMatcher<IPatternMatch>> rule = ruleInstanceMap.get(ruleSpecification);
        if (rule != null) {
            rule.removeActivationNotificationListener(activationListener);
            rule.dispose();
            ruleInstanceMap.remove(rule);
            return true;
        }
        return false;
    }
    protected void dispose() {
        for (RuleInstance<IPatternMatch, IncQueryMatcher<IPatternMatch>> rule : ruleInstanceMap.values()) {
            rule.dispose();
        }
    }

    public Logger getLogger() {
        return iqEngine.getLogger();
    }

    /**
     * @return the ruleInstanceMap
     */
    public Map<RuleSpecification<IPatternMatch, IncQueryMatcher<IPatternMatch>>, RuleInstance<IPatternMatch, IncQueryMatcher<IPatternMatch>>> getRuleInstanceMap() {
        return Collections.unmodifiableMap(ruleInstanceMap);
    }
    
    /**
     * @return the set of rule instances
     */
    public Set<RuleInstance<IPatternMatch, IncQueryMatcher<IPatternMatch>>> getRuleInstances() {
        return ImmutableSet.copyOf(ruleInstanceMap.values());
    }
    
    @SuppressWarnings("unchecked")
    public <Match extends IPatternMatch, Matcher extends IncQueryMatcher<Match>> RuleInstance<Match, Matcher> getInstance(
            RuleSpecification<Match, Matcher> ruleSpecification) {
        return (RuleInstance<Match, Matcher>) ruleInstanceMap.get(ruleSpecification);
    }

    /**
     * @return the activations
     */
    public Multimap<ActivationState, Activation<?>> getActivations() {
        if (TriggerEngineConstants.MODIFIABLE_ACTIVATION_COLLECTIONS) {
            return activations;
        } else if (TriggerEngineConstants.MUTABLE_ACTIVATION_COLLECTIONS) {
            return Multimaps.unmodifiableMultimap(activations);
        } else {
            return ImmutableMultimap.copyOf(activations);
        }
    }

    public Collection<Activation<?>> getActivations(ActivationState state) {
        if (TriggerEngineConstants.MODIFIABLE_ACTIVATION_COLLECTIONS) {
            return activations.get(state);
        } else if (TriggerEngineConstants.MUTABLE_ACTIVATION_COLLECTIONS) {
            return Collections.unmodifiableCollection(activations.get(state));
        } else {
            if (activations instanceof SetMultimap) {
                return ImmutableSet.copyOf(activations.get(state));
            } else {
                return ImmutableList.copyOf(activations.get(state));
            }
        }
    }

    public <Match extends IPatternMatch, Matcher extends IncQueryMatcher<Match>> Collection<Activation<Match>> getActivations(RuleSpecification<Match, Matcher> ruleSpecification) {
        RuleInstance<Match, Matcher> instance = getInstance(ruleSpecification);
        if(instance == null) {
            return Collections.emptySet();
        } else {
            return instance.getActivations();
        }
    }

    /**
     * 
     * @return
     */
    public Collection<Activation<?>> getAllActivations() {
        if (TriggerEngineConstants.MODIFIABLE_ACTIVATION_COLLECTIONS) {
            return activations.values();
        } else if (TriggerEngineConstants.MUTABLE_ACTIVATION_COLLECTIONS) {
            return Collections.unmodifiableCollection(activations.values());
        } else {
            return ImmutableList.copyOf(activations.values());
        }
    }

    protected void addActivationOrdering(Comparator<Activation<?>> activationComparator) {
        if(activationComparator != null) {
            TreeMultimap<ActivationState, Activation<?>> newActivations = TreeMultimap.create(null, activationComparator);
            newActivations.putAll(activations);
            activations = newActivations;
        }
    }

}
