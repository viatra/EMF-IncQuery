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
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import org.apache.log4j.Logger;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.incquery.runtime.api.IPatternMatch;
import org.eclipse.incquery.runtime.api.IncQueryEngine;
import org.eclipse.incquery.runtime.api.IncQueryMatcher;
import org.eclipse.incquery.runtime.triggerengine.notification.IActivationNotificationListener;
import org.eclipse.incquery.runtime.triggerengine.old.AutomaticFiringStrategy;
import org.eclipse.incquery.runtime.triggerengine.old.TimedFiringStrategy;

import com.google.common.collect.Sets;

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
 * One may define whether multiple firing of the same activation is allowed; that is, only the Appeared state will be
 * used from the lifecycle of {@link Activation}s and consecutive firing of a previously applied {@link Activation} is
 * possible. For more information on the lifecycle see {@link Activation}. Multiple firing is used for example in Design
 * Space Exploration scenarios.
 * 
 * @author Tamas Szabo
 * 
 */
public class Agenda {

    private final IncQueryEngine iqEngine;
    private final Set<RuleInstance<IPatternMatch, IncQueryMatcher<IPatternMatch>>> ruleInstances;
    private Collection<Activation<?>> activations;
    private final IActivationNotificationListener activationListener;

    /**
     * Instantiates a new Agenda instance with the given {@link IncQueryEngine}.
     * 
     * @param iqEngine
     *            the {@link IncQueryEngine} instance
     */
    protected Agenda(IncQueryEngine iqEngine) {
        this.iqEngine = iqEngine;
        this.ruleInstances = new HashSet<RuleInstance<IPatternMatch, IncQueryMatcher<IPatternMatch>>>();
        this.activations = new HashSet<Activation<?>>();

        this.activationListener = new IActivationNotificationListener() {

            @Override
            public void activationDisappeared(Activation<? extends IPatternMatch> activation) {
                activations.remove(activation);
            }

            @Override
            public void activationAppeared(Activation<? extends IPatternMatch> activation) {
                activations.add(activation);
            }
        };

    }

    public Notifier getNotifier() {
        return iqEngine.getEmfRoot();
    }

    @SuppressWarnings("unchecked")
    public <Match extends IPatternMatch, Matcher extends IncQueryMatcher<Match>> RuleInstance<Match, Matcher> createRule(
            RuleSpecification<Match, Matcher> specification) {
        RuleInstance<Match, Matcher> rule = specification.instantiateRule(iqEngine);
        rule.addActivationNotificationListener(activationListener, true);
        ruleInstances.add((RuleInstance<IPatternMatch, IncQueryMatcher<IPatternMatch>>) rule);
        return rule;
    }

    public <Match extends IPatternMatch, Matcher extends IncQueryMatcher<Match>> void removeRule(
            RuleInstance<Match, Matcher> rule) {
        if (ruleInstances.contains(rule)) {
            rule.removeActivationNotificationListener(activationListener);
            rule.dispose();
            ruleInstances.remove(rule);
        }
    }

    public void dispose() {
        for (RuleInstance<IPatternMatch, IncQueryMatcher<IPatternMatch>> rule : ruleInstances) {
            rule.dispose();
        }
    }

    public Logger getLogger() {
        return iqEngine.getLogger();
    }

    public Collection<Activation<? extends IPatternMatch>> getActivations() {
        return Collections.unmodifiableCollection(activations);
    }

    public void addActivationOrdering(Comparator<Activation<?>> activationComparator) {
        TreeSet<Activation<?>> newActivations = Sets.newTreeSet(activationComparator);
        newActivations.addAll(activations);
        activations = newActivations;
    }
    
}
