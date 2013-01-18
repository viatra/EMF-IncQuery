/*******************************************************************************
 * Copyright (c) 2010-2012, Zoltan Ujhelyi, Abel Hegedus, Tamas Szabo, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Zoltan Ujhelyi, Abel Hegedus, Tamas Szabo - initial API and implementation
 *******************************************************************************/

package org.eclipse.incquery.validation.runtime;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.incquery.runtime.api.EngineManager;
import org.eclipse.incquery.runtime.api.IMatcherFactory;
import org.eclipse.incquery.runtime.api.IPatternMatch;
import org.eclipse.incquery.runtime.api.IncQueryEngine;
import org.eclipse.incquery.runtime.api.IncQueryMatcher;
import org.eclipse.incquery.runtime.exception.IncQueryException;
import org.eclipse.incquery.runtime.triggerengine.api.ActivationState;
import org.eclipse.incquery.runtime.triggerengine.api.Job;
import org.eclipse.incquery.runtime.triggerengine.api.RuleEngine;
import org.eclipse.incquery.runtime.triggerengine.api.RuleSpecification;
import org.eclipse.incquery.runtime.triggerengine.api.Scheduler.ISchedulerFactory;
import org.eclipse.incquery.runtime.triggerengine.api.TriggerEngineUtil;
import org.eclipse.incquery.runtime.triggerengine.specific.DefaultActivationLifeCycle;
import org.eclipse.incquery.runtime.triggerengine.specific.StatelessJob;
import org.eclipse.incquery.runtime.triggerengine.specific.UpdateCompleteBasedScheduler;
import org.eclipse.ui.IEditorPart;

import com.google.common.collect.Sets;

/**
 * The constraint adapter class is used to collect the constraints and deal with their maintenance for a given EMF
 * instance model. If the validation framework is initialized an instance of this class will be created which handles
 * the creation of the appropriate rules and their jobs.
 * 
 * @author Tamas Szabo
 */
public class ConstraintAdapter {

    private Map<IPatternMatch, IMarker> markerMap;
    private RuleEngine engine;

    @SuppressWarnings("unchecked")
    public ConstraintAdapter(IEditorPart editorPart, Notifier notifier, Logger logger) {
        this.markerMap = new HashMap<IPatternMatch, IMarker>();

        Set<RuleSpecification<? extends IPatternMatch, ? extends IncQueryMatcher<? extends IPatternMatch>>> rules = Sets.newHashSet();

        for (Constraint<IPatternMatch> constraint : ValidationUtil.getConstraintsForEditorId(editorPart.getSite()
                .getId())) {

            Job<IPatternMatch> placerJob = new StatelessJob<IPatternMatch>(ActivationState.APPEARED, new MarkerPlacerJob(this,
                    constraint, logger));
            Job<IPatternMatch> eraserJob = new StatelessJob<IPatternMatch>(ActivationState.DISAPPEARED, new MarkerEraserJob(
                    this, logger));
            Job<IPatternMatch> updaterJob = new StatelessJob<IPatternMatch>(ActivationState.UPDATED, new MarkerUpdaterJob(this,
                    constraint, logger));

            rules.add(new RuleSpecification<IPatternMatch, IncQueryMatcher<IPatternMatch>>(
                    (IMatcherFactory<IncQueryMatcher<IPatternMatch>>) constraint.getMatcherFactory(),
                    DefaultActivationLifeCycle.getDEFAULT(), Sets.newHashSet(placerJob, eraserJob, updaterJob)));
        }

        try {
            IncQueryEngine incQueryEngine = EngineManager.getInstance().getIncQueryEngine(notifier);
            ISchedulerFactory schedulerFactory = UpdateCompleteBasedScheduler.getIQBaseSchedulerFactory(incQueryEngine);
            this.engine = TriggerEngineUtil.createTriggerEngine(incQueryEngine, schedulerFactory, rules);
        } catch (IncQueryException e) {
            IncQueryEngine.getDefaultLogger().error(
                    String.format("Exception occured when creating engine for validation: %s", e.getMessage()), e);
        }
    }

    public void dispose() {
        for (IMarker marker : markerMap.values()) {
            try {
                marker.delete();
            } catch (CoreException e) {
                engine.getIncQueryEngine().getLogger().error(
                        String.format("Exception occured when removing a marker on dispose: %s", e.getMessage()), e);
            }
        }
        engine.dispose();
    }

    public IMarker getMarker(IPatternMatch match) {
        return this.markerMap.get(match);
    }

    public IMarker addMarker(IPatternMatch match, IMarker marker) {
        return this.markerMap.put(match, marker);
    }

    public IMarker removeMarker(IPatternMatch match) {
        return this.markerMap.remove(match);
    }
}
