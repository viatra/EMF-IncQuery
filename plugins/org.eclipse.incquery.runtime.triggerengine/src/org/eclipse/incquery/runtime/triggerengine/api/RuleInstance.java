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

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.incquery.runtime.api.IMatchProcessor;
import org.eclipse.incquery.runtime.api.IMatchUpdateListener;
import org.eclipse.incquery.runtime.api.IPatternMatch;
import org.eclipse.incquery.runtime.api.IncQueryEngine;
import org.eclipse.incquery.runtime.api.IncQueryMatcher;
import org.eclipse.incquery.runtime.api.MatchUpdateAdapter;
import org.eclipse.incquery.runtime.exception.IncQueryException;
import org.eclipse.incquery.runtime.triggerengine.TriggerEngineConstants;
import org.eclipse.incquery.runtime.triggerengine.api.ActivationLifeCycle.ActivationLifeCycleEvent;
import org.eclipse.incquery.runtime.triggerengine.notification.ActivationNotificationProvider;
import org.eclipse.incquery.runtime.triggerengine.notification.AttributeMonitor;
import org.eclipse.incquery.runtime.triggerengine.notification.IActivationNotificationListener;
import org.eclipse.incquery.runtime.triggerengine.notification.IAttributeMonitorListener;
import org.eclipse.incquery.runtime.triggerengine.specific.DefaultAttributeMonitor;
import org.eclipse.incquery.runtime.triggerengine.specific.StatelessJob;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Table;
import com.google.common.collect.TreeBasedTable;

/**
 * TODO write documentation
 *  - manage activation set
 *  - reference rule specification
 *  - reference matcher
 *  - register match listener on matcher
 *  - send activation state changes to listeners
 * 
 * @author Abel Hegedus
 * 
 */
public class RuleInstance<Match extends IPatternMatch, Matcher extends IncQueryMatcher<Match>>{

    /**
     * @author Abel Hegedus
     *
     */
    private final class DefaultActivationNotificationProvider extends ActivationNotificationProvider {
        @Override
        protected void listenerAdded(IActivationNotificationListener listener, boolean fireNow) {
            if (fireNow) {
                for (Activation<Match> activation : getActivations()) {
                    listener.activationChanged(activation, null, ActivationLifeCycleEvent.MATCH_APPEARS);
                }
            }
        }
    }

    /**
     * @author Abel Hegedus
     *
     */
    public final class DefaultMatchAppearProcessor implements IMatchProcessor<Match> {
        /* (non-Javadoc)
         * @see org.eclipse.incquery.runtime.api.IMatchProcessor#process(org.eclipse.incquery.runtime.api.IPatternMatch)
         */
        @Override
        public void process(Match match) {
            Preconditions.checkNotNull(match);
            Map<ActivationState, Activation<Match>> column = activations.column(match);
            if(column.size() > 0) {
                for (Entry<ActivationState, Activation<Match>> entry : column.entrySet()) {
                    activationStateTransition(entry.getValue(), ActivationLifeCycleEvent.MATCH_APPEARS);
                }
            } else {
                Activation<Match> activation = new Activation<Match>(RuleInstance.this, match);
                if(specification.getLifeCycle().containsTo(ActivationState.UPDATED)) {
                    attributeMonitor.registerFor(match);
                }
                activationStateTransition(activation, ActivationLifeCycleEvent.MATCH_APPEARS);
            }
        }
    }

    /**
     * @author Abel Hegedus
     *
     */
    public final class DefaultMatchDisappearProcessor implements IMatchProcessor<Match> {
        /* (non-Javadoc)
         * @see org.eclipse.incquery.runtime.api.IMatchProcessor#process(org.eclipse.incquery.runtime.api.IPatternMatch)
         */
        @Override
        public void process(Match match) {
            Preconditions.checkNotNull(match);
            Map<ActivationState, Activation<Match>> column = activations.column(match);
            if(column.size() > 0) {
                for (Entry<ActivationState, Activation<Match>> entry : column.entrySet()) {
                    activationStateTransition(entry.getValue(), ActivationLifeCycleEvent.MATCH_DISAPPEARS);
                }
            }
        }
    }

    /**
     * @author Abel Hegedus
     *
     */
    public final class DefaultAttributeMonitorListener implements IAttributeMonitorListener<Match> {
        @Override
        public void notifyUpdate(Match match) {
            Preconditions.checkNotNull(match);
            Map<ActivationState, Activation<Match>> column = activations.column(match);
            Preconditions.checkArgument(column.size() == 1);
            for (Entry<ActivationState, Activation<Match>> entry : column.entrySet()) {
                activationStateTransition(entry.getValue(), ActivationLifeCycleEvent.MATCH_UPDATES);
            }
        }

    }

    private Matcher matcher;
    private final RuleSpecification<Match, Matcher> specification;
    private Table<ActivationState, Match, Activation<Match>> activations;
    private ActivationNotificationProvider activationNotificationProvider;
    private IMatchUpdateListener<Match> matchUpdateListener;
    private IAttributeMonitorListener<Match> attributeMonitorListener;
    private AttributeMonitor<Match> attributeMonitor;

    /**
     * created only through a RuleSpec
     * 
     * @param specification
     * @param engine
     */
    protected RuleInstance(RuleSpecification<Match, Matcher> specification, IncQueryEngine engine) {
        Preconditions.checkNotNull(specification);
        Preconditions.checkNotNull(engine);
        this.specification = specification;
        this.activations = TreeBasedTable.create(null, specification.getComparator());
        this.activationNotificationProvider = prepareActivationNotificationProvider();
        Preconditions.checkNotNull(this.activationNotificationProvider);

        IMatchProcessor<Match> matchAppearProcessor = prepareMatchAppearProcessor();
        Preconditions.checkNotNull(matchAppearProcessor);
        IMatchProcessor<Match> matchDisppearProcessor = prepareMatchDisppearProcessor();
        Preconditions.checkNotNull(matchDisppearProcessor);
        this.matchUpdateListener = new MatchUpdateAdapter<Match>(matchAppearProcessor,
                matchDisppearProcessor);

        this.attributeMonitorListener = prepareAttributeMonitorListener();
        Preconditions.checkNotNull(this.attributeMonitorListener);
        this.attributeMonitor = prepareAttributeMonitor();
        Preconditions.checkNotNull(this.attributeMonitor);
        this.attributeMonitor.addCallbackOnMatchUpdate(attributeMonitorListener);
        
        try {
            this.matcher = specification.getFactory().getMatcher(engine);
            this.matcher.addCallbackOnMatchUpdate(matchUpdateListener, true);
        } catch (IncQueryException e) {
            engine.getLogger().error(
                    String.format("Could not initialize matcher %s in engine %s", specification.getFactory()
                            .getPatternFullyQualifiedName(), engine.getEmfRoot().toString()), e);
        }
    }

    protected ActivationNotificationProvider prepareActivationNotificationProvider() {
        return new DefaultActivationNotificationProvider();
    }

    protected IMatchProcessor<Match> prepareMatchAppearProcessor() {
        return new DefaultMatchAppearProcessor();
    }
    
    protected IMatchProcessor<Match> prepareMatchDisppearProcessor() {
        return new DefaultMatchDisappearProcessor();
    }

    protected IAttributeMonitorListener<Match> prepareAttributeMonitorListener() {
        return new DefaultAttributeMonitorListener();
    }
    
    public void fire(Activation<Match> activation, Session session) {
        Preconditions.checkNotNull(activation);
        ActivationState activationState = activation.getState();
        Match patternMatch = activation.getPatternMatch();

        doFire(activation, activationState, patternMatch, session);
    }

    protected void doFire(Activation<Match> activation, ActivationState activationState, Match patternMatch, Session session) {
        if (activations.contains(activationState, patternMatch)) {
            Set<StatelessJob<Match>> jobs = specification.getJobs(activationState);
            for (StatelessJob<Match> job : jobs) {
                job.execute(activation, session);
            }
            activationStateTransition(activation, ActivationLifeCycleEvent.ACTIVATION_FIRES);
            
        }
    }


    protected ActivationState activationStateTransition(Activation<Match> activation, ActivationLifeCycleEvent event) {
        Preconditions.checkNotNull(activation);
        Preconditions.checkNotNull(event);
        ActivationState activationState = activation.getState();
        ActivationState nextActivationState = specification.getLifeCycle().nextActivationState(activationState, event);
        Match patternMatch = activation.getPatternMatch();
        if (nextActivationState != null) {
            activations.remove(activationState, patternMatch);
            activation.setState(nextActivationState);
            if (!nextActivationState.equals(ActivationState.INACTIVE)) {
                activations.put(nextActivationState, patternMatch, activation);
            } else {
                attributeMonitor.unregisterFor(patternMatch);
            }
        } else {
            nextActivationState = activationState;
        }
        activationNotificationProvider.notifyActivationChanged(activation, activationState, event);
        return nextActivationState;
    }
    
    protected AttributeMonitor<Match> prepareAttributeMonitor(){
        return new DefaultAttributeMonitor<Match>();
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
    
    protected boolean addActivationNotificationListener(IActivationNotificationListener listener, boolean fireNow) {
        return activationNotificationProvider.addActivationNotificationListener(listener, fireNow);
    }

    protected boolean removeActivationNotificationListener(IActivationNotificationListener listener) {
        return activationNotificationProvider.removeActivationNotificationListener(listener);
    }

    /**
     * 
     * @return
     */
    public Collection<Activation<Match>> getActivations() {
        if(TriggerEngineConstants.MODIFIABLE_ACTIVATION_COLLECTIONS) {
            return activations.values();
        } else if(TriggerEngineConstants.MUTABLE_ACTIVATION_COLLECTIONS) {
            return Collections.unmodifiableCollection(activations.values());
        } else {
            return ImmutableSet.copyOf(activations.values());
        }
    }

    /**
     * 
     * @param state
     * @return
     */
    public Collection<Activation<Match>> getActivations(ActivationState state) {
        if(TriggerEngineConstants.MODIFIABLE_ACTIVATION_COLLECTIONS) {
            return activations.row(state).values();
        } else if(TriggerEngineConstants.MUTABLE_ACTIVATION_COLLECTIONS) {
            return Collections.unmodifiableCollection(activations.row(state).values());
        } else {
            return ImmutableSet.copyOf(activations.row(state).values());
        }
    }

    public void dispose() {
        this.attributeMonitor.removeCallbackOnMatchUpdate(attributeMonitorListener);
        this.attributeMonitor.dispose();
        this.matcher.removeCallbackOnMatchUpdate(matchUpdateListener);
    }
}
