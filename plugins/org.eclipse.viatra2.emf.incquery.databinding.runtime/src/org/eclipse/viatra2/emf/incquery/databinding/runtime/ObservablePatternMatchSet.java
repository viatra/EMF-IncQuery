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
package org.eclipse.viatra2.emf.incquery.databinding.runtime;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.databinding.observable.Diffs;
import org.eclipse.core.databinding.observable.set.AbstractObservableSet;
import org.eclipse.core.databinding.observable.set.SetDiff;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.viatra2.emf.incquery.base.itc.alg.incscc.Direction;
import org.eclipse.viatra2.emf.incquery.runtime.api.IMatchProcessor;
import org.eclipse.viatra2.emf.incquery.runtime.api.IMatcherFactory;
import org.eclipse.viatra2.emf.incquery.runtime.api.IPatternMatch;
import org.eclipse.viatra2.emf.incquery.runtime.api.IncQueryEngine;
import org.eclipse.viatra2.emf.incquery.runtime.api.IncQueryMatcher;
import org.eclipse.viatra2.emf.incquery.runtime.extensibility.MatcherFactoryRegistry;
import org.eclipse.viatra2.emf.incquery.triggerengine.api.Agenda;
import org.eclipse.viatra2.emf.incquery.triggerengine.api.Rule;
import org.eclipse.viatra2.emf.incquery.triggerengine.api.RuleEngine;

import com.google.common.collect.Sets;

/**
 * Observable view of a match set for a given {@link IncQueryMatcher} on a model
 *  (match sets of an {@link IncQueryMatcher} are not ordered by default).
 *  
 * This implementation uses the {@link RuleEngine} to get notifications for match set changes,
 *  and can be instantiated using either an existing {@link IncQueryMatcher}, or an {@link IMatcherFactory} and
 *  either a {@link Notifier}, {@link IncQueryEngine} or {@link Agenda}.
 * 
 * @author Abel Hegedus
 *
 */
public class ObservablePatternMatchSet<Match extends IPatternMatch> extends AbstractObservableSet {

    private final Set<Match> cache = Collections.synchronizedSet(new HashSet<Match>());
    
    /**
     * Creates an observable view of the match set of the given {@link IncQueryMatcher}.
     * 
     * @param matcher the {@link IncQueryMatcher} to use as the source of the observable set
     */
    @SuppressWarnings("unchecked")
    public <Matcher extends IncQueryMatcher<Match>> ObservablePatternMatchSet(Matcher matcher) {
        super();
        IMatcherFactory<Matcher> matcherFactory = (IMatcherFactory<Matcher>) MatcherFactoryRegistry.getOrCreateMatcherFactory(matcher.getPattern());
        createRuleInAgenda(matcherFactory, RuleEngine.getInstance().getOrCreateAgenda(matcher.getEngine()));
    }
    
    /**
     * Creates an observable view of the match set of the given {@link IMatcherFactory} initialized on the given {@link Notifier}.
     * 
     * @param factory the {@link IMatcherFactory} used to create a matcher
     * @param notifier the {@link Notifier} on which the matcher is created
     */
    public <Matcher extends IncQueryMatcher<Match>> ObservablePatternMatchSet(IMatcherFactory<Matcher> factory, Notifier notifier) {
        this(factory, RuleEngine.getInstance().getOrCreateAgenda(notifier));
    }
    
    /**
     * Creates an observable view of the match set of the given {@link IMatcherFactory} initialized on the given {@link IncQueryEngine}.
     * 
     * @param factory the {@link IMatcherFactory} used to create a matcher
     * @param engine the {@link IncQueryEngine} on which the matcher is created
     */
    public <Matcher extends IncQueryMatcher<Match>> ObservablePatternMatchSet(IMatcherFactory<Matcher> factory, IncQueryEngine engine) {
        this(factory, RuleEngine.getInstance().getOrCreateAgenda(engine));
    }
    
    /**
     * Creates an observable view of the match set of the given {@link IMatcherFactory} initialized on the given {@link IncQueryEngine}.
     * 
     * @param factory the {@link IMatcherFactory} used to create a matcher
     * @param agenda an existing {@link Agenda} that specifies the used model 
     */
    public <Matcher extends IncQueryMatcher<Match>> ObservablePatternMatchSet(IMatcherFactory<Matcher> factory, Agenda agenda) {
        super();
        createRuleInAgenda(factory, agenda);
    }
    
    /**
     * Creates the rule used for updating the results in the given agenda. 
     * 
     * @param factory the {@link IMatcherFactory} used to create the rule
     * @param agenda an existing {@link Agenda} where the rule is created
     */
    private <Matcher extends IncQueryMatcher<Match>> void createRuleInAgenda(IMatcherFactory<Matcher> factory, Agenda agenda) {
         Rule<Match> rule = agenda.createRule(factory, false, true);
         rule.afterAppearanceJob = new IMatchProcessor<Match>() {
             @Override
             public void process(Match match) {
                 matchSetChanged(match, Direction.INSERT);
             }
         };
         rule.afterDisappearanceJob = new IMatchProcessor<Match>() {
             @Override
             public void process(Match match) {
                 matchSetChanged(match, Direction.DELETE);
             }
         };
    }
    
    /**
     * Propagates the match set change to observers.
     * 
     * @param match the {@link IPatternMatch} that appeared or disappeared.
     * @param direction the {@link Direction} specifes that the match appeared or disappeared.
     */
    @SuppressWarnings("unchecked")
    private void matchSetChanged(Match match, Direction direction) {
        SetDiff diff;
        if(direction == Direction.INSERT) {
            cache.add(match);
            diff = Diffs.createSetDiff(Sets.newHashSet(match), Collections.EMPTY_SET);
        } else {
            cache.remove(match);
            diff = Diffs.createSetDiff(Collections.EMPTY_SET, Sets.newHashSet(match));
        }
        fireSetChange(diff);
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.core.databinding.observable.list.IObservableList#getElementType()
     */
    @Override
    public Object getElementType() {
        return IPatternMatch.class;
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.databinding.observable.set.AbstractObservableSet#getWrappedSet()
     */
    @Override
    protected Set<Match> getWrappedSet() {
        return cache;
    }

}
