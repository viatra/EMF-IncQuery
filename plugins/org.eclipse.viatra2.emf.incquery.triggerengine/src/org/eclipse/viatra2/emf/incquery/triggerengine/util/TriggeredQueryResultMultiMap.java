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
package org.eclipse.viatra2.emf.incquery.triggerengine.util;

import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.viatra2.emf.incquery.base.api.QueryResultMultimap;
import org.eclipse.viatra2.emf.incquery.runtime.api.IMatchProcessor;
import org.eclipse.viatra2.emf.incquery.runtime.api.IMatcherFactory;
import org.eclipse.viatra2.emf.incquery.runtime.api.IPatternMatch;
import org.eclipse.viatra2.emf.incquery.runtime.api.IncQueryEngine;
import org.eclipse.viatra2.emf.incquery.runtime.api.IncQueryMatcher;
import org.eclipse.viatra2.emf.incquery.triggerengine.api.Agenda;
import org.eclipse.viatra2.emf.incquery.triggerengine.api.Rule;
import org.eclipse.viatra2.emf.incquery.triggerengine.api.RuleEngine;
import org.eclipse.viatra2.emf.incquery.triggerengine.firing.AutomaticFiringStrategy;

/**
 * @author Abel Hegedus
 *
 */
public abstract class TriggeredQueryResultMultiMap<MatchType extends IPatternMatch, KeyType, ValueType> extends QueryResultMultimap<KeyType, ValueType> {

    private IMatchProcessor<MatchType> appearanceProcessor;
    private IMatchProcessor<MatchType> disappearanceProcessor;

    private Agenda agenda;
    
    /**
     * @param agenda
     */
    protected TriggeredQueryResultMultiMap(Agenda agenda) {
        super(agenda.getIqEngine().getLogger());
        this.agenda = agenda;
        
        AutomaticFiringStrategy firingStrategy = new AutomaticFiringStrategy();
        agenda.addActivationNotificationListener(firingStrategy, true);
        
        appearanceProcessor = new IMatchProcessor<MatchType>() {
            @Override
            public void process(MatchType match) {
                KeyType key = getKeyFromMatch(match);
                ValueType value = getValueFromMatch(match);
                internalPut(key, value);
            }
        };
        
        disappearanceProcessor = new IMatchProcessor<MatchType>() {
            @Override
            public void process(MatchType match) {
                KeyType key = getKeyFromMatch(match);
                ValueType value = getValueFromMatch(match);
                internalRemove(key, value);
            }
        };
    }
    
    protected TriggeredQueryResultMultiMap(IncQueryEngine engine) {
        this(RuleEngine.getInstance().getOrCreateAgenda(engine));
    }
    
    protected TriggeredQueryResultMultiMap(Notifier notifier) {
        this(RuleEngine.getInstance().getOrCreateAgenda(notifier));
    }
    
    public <Matcher extends IncQueryMatcher<MatchType>> void addMatcherToMultimapResults(IMatcherFactory<Matcher> factory) {
        Rule<MatchType> newRule = agenda.createRule(factory, false, true);
        if(newRule != null) {
            newRule.afterAppearanceJob = appearanceProcessor;
            newRule.afterDisappearanceJob = disappearanceProcessor;
        }

        //agenda.afterActivationUpdateCallback();
    }

    protected abstract KeyType getKeyFromMatch(MatchType match); 
    
    protected abstract ValueType getValueFromMatch(MatchType match);
    
}
