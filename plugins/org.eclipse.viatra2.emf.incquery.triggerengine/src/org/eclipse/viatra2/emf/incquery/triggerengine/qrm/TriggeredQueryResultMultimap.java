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
package org.eclipse.viatra2.emf.incquery.triggerengine.qrm;

import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.incquery.runtime.api.IMatchProcessor;
import org.eclipse.incquery.runtime.api.IMatcherFactory;
import org.eclipse.incquery.runtime.api.IPatternMatch;
import org.eclipse.incquery.runtime.api.IncQueryEngine;
import org.eclipse.incquery.runtime.api.IncQueryMatcher;
import org.eclipse.incquery.runtime.base.api.QueryResultMultimap;
import org.eclipse.viatra2.emf.incquery.triggerengine.api.ActivationState;
import org.eclipse.viatra2.emf.incquery.triggerengine.api.IAgenda;
import org.eclipse.viatra2.emf.incquery.triggerengine.api.IRule;
import org.eclipse.viatra2.emf.incquery.triggerengine.api.RuleEngine;
import org.eclipse.viatra2.emf.incquery.triggerengine.firing.AutomaticFiringStrategy;

/**
 * @author Abel Hegedus
 *
 */
public abstract class TriggeredQueryResultMultimap<MatchType extends IPatternMatch, KeyType, ValueType> extends QueryResultMultimap<KeyType, ValueType> {

    private IMatchProcessor<MatchType> appearanceProcessor;
    private IMatchProcessor<MatchType> disappearanceProcessor;

    private IAgenda agenda;
    
    /**
     * @param agenda
     */
    protected TriggeredQueryResultMultimap(IAgenda agenda) {
        super(agenda.getLogger());
        this.agenda = agenda;
        
        AutomaticFiringStrategy firingStrategy = new AutomaticFiringStrategy(agenda.newActivationMonitor(true));
        agenda.addUpdateCompleteListener(firingStrategy, true);
        
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
    
    protected TriggeredQueryResultMultimap(IncQueryEngine engine) {
        this(RuleEngine.getInstance().getOrCreateAgenda(engine));
    }
    
    protected TriggeredQueryResultMultimap(Notifier notifier) {
        this(RuleEngine.getInstance().getOrCreateAgenda(notifier));
    }
    
    public <Matcher extends IncQueryMatcher<MatchType>> void addMatcherToMultimapResults(IMatcherFactory<Matcher> factory) {
        IRule<MatchType> newRule = agenda.createRule(factory, false, true);
        if(newRule != null) {
            newRule.setStateChangeProcessor(ActivationState.APPEARED, appearanceProcessor);
            newRule.setStateChangeProcessor(ActivationState.DISAPPEARED, disappearanceProcessor);
        }
    }

    protected abstract KeyType getKeyFromMatch(MatchType match); 
    
    protected abstract ValueType getValueFromMatch(MatchType match);
    
}
