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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.databinding.observable.Diffs;
import org.eclipse.core.databinding.observable.list.AbstractObservableList;
import org.eclipse.core.databinding.observable.list.ListDiff;
import org.eclipse.core.databinding.observable.list.ListDiffEntry;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.viatra2.emf.incquery.base.itc.alg.incscc.Direction;
import org.eclipse.viatra2.emf.incquery.runtime.api.IMatchProcessor;
import org.eclipse.viatra2.emf.incquery.runtime.api.IMatcherFactory;
import org.eclipse.viatra2.emf.incquery.runtime.api.IPatternMatch;
import org.eclipse.viatra2.emf.incquery.runtime.api.IncQueryEngine;
import org.eclipse.viatra2.emf.incquery.runtime.api.IncQueryMatcher;
import org.eclipse.viatra2.emf.incquery.runtime.extensibility.MatcherFactoryRegistry;
import org.eclipse.viatra2.emf.incquery.triggerengine.api.ActivationState;
import org.eclipse.viatra2.emf.incquery.triggerengine.api.Agenda;
import org.eclipse.viatra2.emf.incquery.triggerengine.api.Rule;
import org.eclipse.viatra2.emf.incquery.triggerengine.api.RuleEngine;
import org.eclipse.viatra2.emf.incquery.triggerengine.firing.AutomaticFiringStrategy;

/**
 * Observable view of a match set for a given {@link IncQueryMatcher} on a model
 *  (match sets of an {@link IncQueryMatcher} are ordered by the order of their appearance).
 *  
 * This implementation uses the {@link RuleEngine} to get notifications for match set changes,
 *  and can be instantiated using either an existing {@link IncQueryMatcher}, or an {@link IMatcherFactory} and
 *  either a {@link Notifier}, {@link IncQueryEngine} or {@link Agenda}.
 * 
 * @author Abel Hegedus
 *
 */
public class ObservablePatternMatchList<Match extends IPatternMatch> extends AbstractObservableList {

    private List<Match> cache = Collections.synchronizedList(new ArrayList<Match>());
    
    /**
     * Creates an observable view of the match set of the given {@link IncQueryMatcher}.
     * 
     * @param matcher the {@link IncQueryMatcher} to use as the source of the observable set
     */
    @SuppressWarnings("unchecked")
    public <Matcher extends IncQueryMatcher<Match>> ObservablePatternMatchList(Matcher matcher) {
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
    public <Matcher extends IncQueryMatcher<Match>> ObservablePatternMatchList(IMatcherFactory<Matcher> factory, Notifier notifier) {
        this(factory, RuleEngine.getInstance().getOrCreateAgenda(notifier));
    }
    
    /**
     * Creates an observable view of the match set of the given {@link IMatcherFactory} initialized on the given {@link IncQueryEngine}.
     * 
     * @param factory the {@link IMatcherFactory} used to create a matcher
     * @param engine the {@link IncQueryEngine} on which the matcher is created
     */
    public <Matcher extends IncQueryMatcher<Match>> ObservablePatternMatchList(IMatcherFactory<Matcher> factory, IncQueryEngine engine) {
        this(factory, RuleEngine.getInstance().getOrCreateAgenda(engine));
    }
    
    /**
     * Creates an observable view of the match set of the given {@link IMatcherFactory} initialized on the given {@link Agenda}.
     * 
     * <p> Note, that no firing strategy will be added to the {@link Agenda}!
     * 
     * @param factory the {@link IMatcherFactory} used to create a matcher
     * @param agenda an existing {@link Agenda} that specifies the used model 
     */
    public <Matcher extends IncQueryMatcher<Match>> ObservablePatternMatchList(IMatcherFactory<Matcher> factory, Agenda agenda) {
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
         rule.setStateChangeProcessor(ActivationState.APPEARED, new IMatchProcessor<Match>() {
             @Override
             public void process(Match match) {
                 matchSetChanged(match, Direction.INSERT);
             }
         });
         rule.setStateChangeProcessor(ActivationState.DISAPPEARED, new IMatchProcessor<Match>() {
             @Override
             public void process(Match match) {
                 matchSetChanged(match, Direction.DELETE);
             }
         });
         AutomaticFiringStrategy firingStrategy = new AutomaticFiringStrategy(agenda.newActivationMonitor(true));
         agenda.getUpdateCompleteProvider().addUpdateCompleteListener(firingStrategy, true);
    }
    
    /**
     * Propagates the match set change to observers.
     * 
     * @param match the {@link IPatternMatch} that appeared or disappeared.
     * @param direction the {@link Direction} specifes that the match appeared or disappeared.
     */
    private void matchSetChanged(Match match, Direction direction) {
        ListDiff diff;
        if(direction == Direction.INSERT) {
            ListDiffEntry diffentry = Diffs.createListDiffEntry(cache.size(), true, match);
            cache.add(match);
            diff = Diffs.createListDiff(diffentry);
        } else {
            final int index = cache.indexOf(match);
            ListDiffEntry diffentry = Diffs.createListDiffEntry(index, false, match);
            cache.remove(match);
            diff = Diffs.createListDiff(diffentry);
            
        }
        fireListChange(diff);
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.core.databinding.observable.list.IObservableList#getElementType()
     */
    @Override
    public Object getElementType() {
        // TODO Auto-generated method stub
        return IPatternMatch.class;
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.databinding.observable.list.AbstractObservableList#doGetSize()
     */
    @Override
    protected int doGetSize() {
        // TODO Auto-generated method stub
        return cache.size();
    }

    /* (non-Javadoc)
     * @see java.util.AbstractList#get(int)
     */
    @Override
    public Object get(int index) {
        // TODO Auto-generated method stub
        return cache.get(index);
    }

}
