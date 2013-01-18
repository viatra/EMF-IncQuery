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
package org.eclipse.incquery.runtime.triggerengine.qrm;

import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.incquery.runtime.api.EngineManager;
import org.eclipse.incquery.runtime.api.IMatchProcessor;
import org.eclipse.incquery.runtime.api.IMatcherFactory;
import org.eclipse.incquery.runtime.api.IPatternMatch;
import org.eclipse.incquery.runtime.api.IncQueryEngine;
import org.eclipse.incquery.runtime.api.IncQueryMatcher;
import org.eclipse.incquery.runtime.base.api.QueryResultMultimap;
import org.eclipse.incquery.runtime.exception.IncQueryException;
import org.eclipse.incquery.runtime.triggerengine.api.ActivationState;
import org.eclipse.incquery.runtime.triggerengine.api.Job;
import org.eclipse.incquery.runtime.triggerengine.api.RuleEngine;
import org.eclipse.incquery.runtime.triggerengine.api.RuleSpecification;
import org.eclipse.incquery.runtime.triggerengine.api.TriggerEngineUtil;
import org.eclipse.incquery.runtime.triggerengine.specific.DefaultActivationLifeCycle;
import org.eclipse.incquery.runtime.triggerengine.specific.StatelessJob;
import org.eclipse.incquery.runtime.triggerengine.specific.UpdateCompleteBasedScheduler;

import com.google.common.collect.Sets;

/**
 * @author Abel Hegedus
 * 
 */
public abstract class TriggeredQueryResultMultimap<Match extends IPatternMatch, KeyType, ValueType> extends
        QueryResultMultimap<KeyType, ValueType> {

    private IMatchProcessor<Match> appearanceProcessor;
    private IMatchProcessor<Match> disappearanceProcessor;

    private RuleEngine engine;

    /**
     * @param agenda
     */
    protected TriggeredQueryResultMultimap(RuleEngine engine) {
        super(engine.getIncQueryEngine().getLogger());
        this.engine = engine;

        appearanceProcessor = new IMatchProcessor<Match>() {
            @Override
            public void process(Match match) {
                KeyType key = getKeyFromMatch(match);
                ValueType value = getValueFromMatch(match);
                internalPut(key, value);
            }
        };

        disappearanceProcessor = new IMatchProcessor<Match>() {
            @Override
            public void process(Match match) {
                KeyType key = getKeyFromMatch(match);
                ValueType value = getValueFromMatch(match);
                internalRemove(key, value);
            }
        };
    }

    /**
     * @throws IncQueryException 
     * 
     */
    protected TriggeredQueryResultMultimap(IncQueryEngine engine) {
        this(TriggerEngineUtil.createTriggerEngine(engine, UpdateCompleteBasedScheduler.getIQBaseSchedulerFactory(engine), null));
    }
    
    /**
     * @throws IncQueryException if the {@link IncQueryEngine} creation fails on the {@link Notifier}
     * 
     */
    protected TriggeredQueryResultMultimap(Notifier notifier) throws IncQueryException {
        this(EngineManager.getInstance().getIncQueryEngine(notifier));
    }
    
    
    @SuppressWarnings("unchecked")
    public <Matcher extends IncQueryMatcher<Match>> void addMatcherToMultimapResults(
            IMatcherFactory<Matcher> factory) {
        Job<Match> appearJob = new StatelessJob<Match>(ActivationState.APPEARED, appearanceProcessor);
        Job<Match> disappearJob = new StatelessJob<Match>(ActivationState.DISAPPEARED, disappearanceProcessor);

        engine.addRule(new RuleSpecification<Match, Matcher>(
                factory, DefaultActivationLifeCycle.getDEFAULT_NO_UPDATE(), Sets.newHashSet(appearJob, disappearJob)));
    }

    protected abstract KeyType getKeyFromMatch(Match match);

    protected abstract ValueType getValueFromMatch(Match match);

}
