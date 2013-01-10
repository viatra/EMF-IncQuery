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
package org.eclipse.incquery.databinding.runtime.collection;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.databinding.observable.Diffs;
import org.eclipse.core.databinding.observable.set.AbstractObservableSet;
import org.eclipse.core.databinding.observable.set.SetDiff;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.incquery.databinding.runtime.api.IncQueryObservables;
import org.eclipse.incquery.runtime.api.EngineManager;
import org.eclipse.incquery.runtime.api.IMatcherFactory;
import org.eclipse.incquery.runtime.api.IPatternMatch;
import org.eclipse.incquery.runtime.api.IncQueryEngine;
import org.eclipse.incquery.runtime.api.IncQueryMatcher;
import org.eclipse.incquery.runtime.exception.IncQueryException;
import org.eclipse.incquery.runtime.extensibility.MatcherFactoryRegistry;
import org.eclipse.incquery.runtime.triggerengine.api.Agenda;
import org.eclipse.incquery.runtime.triggerengine.api.RuleSpecification;
import org.eclipse.incquery.runtime.triggerengine.api.TriggerEngine;
import org.eclipse.incquery.runtime.triggerengine.api.TriggerEngineUtil;
import org.eclipse.incquery.runtime.triggerengine.specific.UpdateCompleteBasedScheduler;

import com.google.common.collect.Sets;

/**
 * Observable view of a match set for a given {@link IncQueryMatcher} on a model (match sets of an
 * {@link IncQueryMatcher} are not ordered by default).
 * 
 * <p>
 * This implementation uses the {@link TriggerEngine} to get notifications for match set changes, and can be instantiated
 * using either an existing {@link IncQueryMatcher}, or an {@link IMatcherFactory} and either a {@link Notifier},
 * {@link IncQueryEngine} or {@link Agenda}.
 * 
 * @author Abel Hegedus
 * 
 */
public class ObservablePatternMatchSet<Match extends IPatternMatch> extends AbstractObservableSet {

    private final Set<Match> cache = Collections.synchronizedSet(new HashSet<Match>());
    private final SetCollectionUpdate updater = new SetCollectionUpdate();

    /**
     * Creates an observable view of the match set of the given {@link IncQueryMatcher}.
     * 
     * <p>
     * Consider using {@link IncQueryObservables#observeMatchesAsSet} instead!
     * 
     * @param matcher
     *            the {@link IncQueryMatcher} to use as the source of the observable set
     * @throws IncQueryException if the {@link IncQueryEngine} base index is not available
     */
    @SuppressWarnings("unchecked")
    public <Matcher extends IncQueryMatcher<Match>> ObservablePatternMatchSet(Matcher matcher) throws IncQueryException {
        this((IMatcherFactory<Matcher>) MatcherFactoryRegistry
                .getOrCreateMatcherFactory(matcher.getPattern()), matcher.getEngine());
    }

    /**
     * Creates an observable view of the match set of the given {@link IMatcherFactory} initialized on the given
     * {@link Notifier}.
     * 
     * <p>
     * Consider using {@link IncQueryObservables#observeMatchesAsSet} instead!
     * 
     * @param factory
     *            the {@link IMatcherFactory} used to create a matcher
     * @param notifier
     *            the {@link Notifier} on which the matcher is created
     * @throws IncQueryException if the {@link IncQueryEngine} creation fails on the {@link Notifier}
     */
    public <Matcher extends IncQueryMatcher<Match>> ObservablePatternMatchSet(IMatcherFactory<Matcher> factory,
            Notifier notifier) throws IncQueryException {
        this(factory, EngineManager.getInstance().getIncQueryEngine(notifier));
    }

    /**
     * Creates an observable view of the match set of the given {@link IMatcherFactory} initialized on the given
     * {@link IncQueryEngine}.
     * 
     * <p>
     * Consider using {@link IncQueryObservables#observeMatchesAsSet} instead!
     * 
     * @param factory
     *            the {@link IMatcherFactory} used to create a matcher
     * @param engine
     *            the {@link IncQueryEngine} on which the matcher is created
     * @throws IncQueryException if the {@link IncQueryEngine} base index is not available
     */
    @SuppressWarnings("rawtypes")
    public <Matcher extends IncQueryMatcher<Match>> ObservablePatternMatchSet(IMatcherFactory<Matcher> factory,
            IncQueryEngine engine) throws IncQueryException {
        RuleSpecification specification = ObservableCollectionHelper.createRuleSpecification(updater, factory);
        TriggerEngineUtil.createTriggerEngine(engine,
                UpdateCompleteBasedScheduler.getIQBaseSchedulerFactory(engine), Sets.newHashSet(specification));
    }

    /**
     * Creates an observable view of the match set of the given {@link IMatcherFactory} initialized on the given
     * {@link IncQueryEngine}.
     * 
     * <p>
     * Consider using {@link IncQueryObservables#observeMatchesAsSet} instead!
     * 
     * @param factory
     *            the {@link IMatcherFactory} used to create a matcher
     * @param agenda
     *            an existing {@link Agenda} that specifies the used model
     */
    public <Matcher extends IncQueryMatcher<Match>> ObservablePatternMatchSet(IMatcherFactory<Matcher> factory,
            TriggerEngine engine) {
        super();
        RuleSpecification<Match, Matcher> specification = ObservableCollectionHelper.createRuleSpecification(updater, factory);
        engine.addRuleSpecification(specification);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.core.databinding.observable.list.IObservableList#getElementType()
     */
    @Override
    public Object getElementType() {
        return IPatternMatch.class;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.core.databinding.observable.set.AbstractObservableSet#getWrappedSet()
     */
    @Override
    protected Set<Match> getWrappedSet() {
        return cache;
    }

    public class SetCollectionUpdate implements IObservablePatternMatchCollectionUpdate<Match>{
        
        @SuppressWarnings("unchecked")
        @Override
        public void addMatch(Match match) {
            cache.add(match);
            SetDiff diff = Diffs.createSetDiff(Sets.newHashSet(match), Collections.EMPTY_SET);
            fireSetChange(diff);
        }
    
        @SuppressWarnings("unchecked")
        @Override
        public void removeMatch(Match match) {
            cache.remove(match);
            SetDiff diff = Diffs.createSetDiff(Collections.EMPTY_SET, Sets.newHashSet(match));
            fireSetChange(diff);
        }
    
    }

}
