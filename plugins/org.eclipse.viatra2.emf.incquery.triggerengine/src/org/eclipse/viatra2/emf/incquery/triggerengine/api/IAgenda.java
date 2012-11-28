/*******************************************************************************
 * Copyright (c) 2010-2012, Abel Hegedus, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Abel Hegedus - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra2.emf.incquery.triggerengine.api;

import java.util.Collection;

import org.apache.log4j.Logger;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.incquery.runtime.api.IMatcherFactory;
import org.eclipse.incquery.runtime.api.IPatternMatch;
import org.eclipse.incquery.runtime.api.IncQueryMatcher;
import org.eclipse.viatra2.emf.incquery.triggerengine.firing.IUpdateCompleteProvider;
import org.eclipse.viatra2.emf.incquery.triggerengine.notification.IActivationNotificationProvider;

/**
 * @author Abel Hegedus
 *
 */
public interface IAgenda extends IUpdateCompleteProvider, IActivationNotificationProvider{

    /**
     * Returns the {@link Notifier} instance associated to the Agenda. 
     * 
     * @return the {@link Notifier} instance
     */
    Notifier getNotifier();

    /**
     * Returns the {@link TransactionalEditingDomain} for the underlying {@link Notifier} 
     * (associated to the Agenda) if it is available. 
     * 
     * @return the {@link TransactionalEditingDomain} instance or null if it is not available
     */
    TransactionalEditingDomain getEditingDomain();

    /**
     * @return the allowMultipleFiring
     */
    boolean isAllowMultipleFiring();

    /**
     * Creates a new rule with the specified {@link IRuleFactory}.
     * The upgraded and disappeared states will not be used in the 
     * lifecycle of rule's activations. 
     * 
     * @param factory the {@link IMatcherFactory} of the {@link IncQueryMatcher}
     * @return the {@link AbstractRule} instance
     */
    <Match extends IPatternMatch, Matcher extends IncQueryMatcher<Match>> IRule<Match> createRule(
            IMatcherFactory<Matcher> factory);

    /**
     * Creates a new rule with the specified {@link IRuleFactory}.
     * 
     * @param factory the {@link IMatcherFactory} of the {@link IncQueryMatcher}
     * @param upgradedStateUsed indicates whether the upgraded state is used in the lifecycle of the rule's activations
     * @param disappearedStateUsed indicates whether the disappeared state is used in the lifecycle of the rule's activations
     * @return the {@link AbstractRule} instance
     */
    <Match extends IPatternMatch, Matcher extends IncQueryMatcher<Match>> IRule<Match> createRule(
            IMatcherFactory<Matcher> factory, boolean upgradedStateUsed, boolean disappearedStateUsed);

    /**
     * Removes a rule from the Agenda. 
     * 
     * @param rule the rule to remove
     */
    <MatchType extends IPatternMatch> void removeRule(AbstractRule<MatchType> rule);

    /**
     * Returns the rules that were created in this Agenda instance. 
     * 
     * @return the collection of rules
     */
    Collection<IRule<? extends IPatternMatch>> getRules();

    /**
     * Call this method to properly dispose the Agenda. 
     */
    void dispose();

    /**
     * Returns the logger associated with the Agenda.
     * 
     * @return
     */
    Logger getLogger();

    /**
     * Returns an unmodifiable collection of the applicable activations.
     * 
     * @return the collection of activations
     */
    Collection<Activation<? extends IPatternMatch>> getActivations();

    ActivationMonitor newActivationMonitor(boolean fillAtStart);

}