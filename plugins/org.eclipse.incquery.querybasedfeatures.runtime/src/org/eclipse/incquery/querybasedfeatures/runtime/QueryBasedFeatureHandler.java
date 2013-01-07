/*******************************************************************************
 * Copyright (c) 2004-2011 Abel Hegedus and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Abel Hegedus - initial API and implementation
 *******************************************************************************/
package org.eclipse.incquery.querybasedfeatures.runtime;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.util.EcoreEList;
import org.eclipse.incquery.runtime.api.IMatchProcessor;
import org.eclipse.incquery.runtime.api.IPatternMatch;
import org.eclipse.incquery.runtime.api.IncQueryEngine;
import org.eclipse.incquery.runtime.api.IncQueryMatcher;
import org.eclipse.incquery.runtime.exception.IncQueryException;
import org.eclipse.incquery.runtime.extensibility.MatcherFactoryRegistry;
import org.eclipse.incquery.runtime.rete.misc.DeltaMonitor;

/**
 * @author Abel Hegedus
 * 
 *         FIXME write AggregateHandler if any EDataType should be allowed TODO notifications could be static final? to
 *         ensure message ordering
 * 
 */
@SuppressWarnings("rawtypes")
public class QueryBasedFeatureHandler implements IQueryBasedFeatureHandler {

    /**
     * @author Abel Hegedus
     * 
     */
    private final class DerivedFeatureWipeCallback implements Runnable {

        @SuppressWarnings("unchecked")
        @Override
        public void run() {
            String patternName = matcher.getPatternName();
            try {
                matcher = (IncQueryMatcher<IPatternMatch>) MatcherFactoryRegistry.getMatcherFactory(patternName)
                        .getMatcher(matcher.getEngine());
            } catch (IncQueryException e) {
                matcher.getEngine().getLogger()
                        .error("[IncqueryFeatureHandler] Exception during wipe callback: " + e.getMessage(), e);
            }
            dm = matcher.newDeltaMonitor(false);
        }
    }

    /**
     * @author Abel Hegedus
     * 
     */
    private final class DerivedFeatureCallback implements Runnable {

        @Override
        public void run() {
            // after each model update, check the delta monitor
            // FIXME should be: after each complete transaction, check the delta monitor
            try {
                updateMemory.clear();
                dm.matchFoundEvents.removeAll(processNewMatches(dm.matchFoundEvents));
                dm.matchLostEvents.removeAll(processLostMatches(dm.matchLostEvents));
                checkUnhandledNewMatch();
                sendNextNotfication();
            } catch (IncQueryException e) {
                matcher.getEngine().getLogger()
                        .error("[IncqueryFeatureHandler] Exception during update: " + e.getMessage(), e);
            }
        }
    }

    private IncQueryMatcher<IPatternMatch> matcher;
    private DeltaMonitor<IPatternMatch> dm;
    private Runnable processMatchesRunnable;
    // private final InternalEObject source;
    private final EStructuralFeature feature;
    private String sourceParamName;
    private String targetParamName;
    private QueryBasedFeatureKind kind;

    private final Map<InternalEObject, Object> updateMemory = new HashMap<InternalEObject, Object>();
    private final Map<InternalEObject, Integer> counterMemory = new HashMap<InternalEObject, Integer>();
    private final Map<InternalEObject, Object> singleRefMemory = new HashMap<InternalEObject, Object>();
    private boolean keepCache = true;
    private boolean initialized = false;

    private final List<ENotificationImpl> notifications = new ArrayList<ENotificationImpl>();

    /*
     * could use EObjectEList or similar to have notifications handled by EMF, but notification sending must be delayed
     * in order to avoid infinite notification loop
     */
    private final Map<InternalEObject, List<Object>> manyRefMemory = new HashMap<InternalEObject, List<Object>>();
    private Runnable processWipeRunnable;

    /**
	 * 
	 */
    public QueryBasedFeatureHandler(EStructuralFeature feature) {
        // this.source = source;
        this.feature = feature;
        if (feature.isMany()) {
            kind = QueryBasedFeatureKind.MANY_REFERENCE;
        } else {
            kind = QueryBasedFeatureKind.SINGLE_REFERENCE;
        }
        // initialize(matcher, sourceParamName, targetParamName);
    }

    @SuppressWarnings("unchecked")
    protected void initialize(final IncQueryMatcher matcher, String sourceParamName, String targetParamName) {
        if (initialized) {
            IncQueryEngine.getDefaultLogger().error("[IncqueryFeatureHandler] Feature already initialized!");
            return;
        }
        initialized = true;
        this.matcher = matcher;
        this.sourceParamName = sourceParamName;
        this.targetParamName = targetParamName;
        if (matcher.getPositionOfParameter(sourceParamName) == null) {
            matcher.getEngine().getLogger()
                    .error("[IncqueryFeatureHandler] Source parameter " + sourceParamName + " not found!");
        }
        if (targetParamName != null && matcher.getPositionOfParameter(targetParamName) == null) {
            matcher.getEngine().getLogger()
                    .error("[IncqueryFeatureHandler] Target parameter " + targetParamName + " not found!");
        }
        if ((targetParamName == null) != (kind == QueryBasedFeatureKind.COUNTER)) {
            matcher.getEngine().getLogger()
                    .error("[IncqueryFeatureHandler] Invalid configuration (no targetParamName needed for Counter)!");
        }
        // IPatternMatch partialMatch = matcher.newEmptyMatch();
        // partialMatch.set(sourceParamName, source);
        this.dm = matcher.newDeltaMonitor(true);
        this.processMatchesRunnable = new DerivedFeatureCallback();
        this.processWipeRunnable = new DerivedFeatureWipeCallback();
    }

    private void sendNextNotfication() {
        while (!notifications.isEmpty()) {
            ENotificationImpl remove = notifications.remove(0);
            // matcher.getEngine().getLogger().logError(this + " : " +remove.toString());
            ((Notifier) remove.getNotifier()).eNotify(remove);
        }
    }

    /**
     * 
     * @param source
     * @param feature
     * @param matcher
     * @param sourceParamName
     */
    public QueryBasedFeatureHandler(EStructuralFeature feature, QueryBasedFeatureKind kind) {
        this(feature);
        this.kind = kind;
        if (kind == QueryBasedFeatureKind.SUM && !(feature instanceof EAttribute)) {
            IncQueryEngine.getDefaultLogger().error(
                    "[IncqueryFeatureHandler] Invalid configuration (Aggregate can be used only with EAttribute)!");
        }
    }

    /**
	 * 
	 */
    public QueryBasedFeatureHandler(EStructuralFeature feature, QueryBasedFeatureKind kind, boolean keepCache) {
        this(feature, kind);
        this.keepCache = keepCache;
    }

    /**
     * Call this once to start handling callbacks.
     */
    protected void startMonitoring() {
        matcher.addCallbackAfterUpdates(processMatchesRunnable);
        matcher.addCallbackAfterWipes(processWipeRunnable);
        processMatchesRunnable.run();
    }

    @Override
    public Object getValue(Object source) {
        switch (kind) {
        case SUM: // fall-through
        case COUNTER:
            return getIntValue(source);
        case SINGLE_REFERENCE:
            return getSingleReferenceValue(source);
        case MANY_REFERENCE:
            return getManyReferenceValue(source);
        case ITERATION:
            return getValueIteration(source);
        }
        return null;
    }

    @Override
    public int getIntValue(Object source) {
        Integer result = counterMemory.get(source);
        if (result == null) {
            result = 0;
        }
        return result;
    }

    @Override
    public Object getSingleReferenceValue(Object source) {
        if (keepCache) {
            return singleRefMemory.get(source);
        } else {
            if (!initialized) {
                return null;
            }
            IPatternMatch match = matcher.newEmptyMatch();
            match.set(sourceParamName, source);
            if (matcher.countMatches(match) > 1) {
                matcher.getEngine()
                        .getLogger()
                        .warn("[IncqueryFeatureHandler] Single reference derived feature has multiple possible values, returning one arbitrary value");
            }
            IPatternMatch patternMatch = matcher.getOneArbitraryMatch(match);
            if (patternMatch != null) {
                return patternMatch.get(targetParamName);
            } else {
                return null;
            }
        }
    }

    @Override
    public List<?> getManyReferenceValue(Object source) {
        if (keepCache) {
            List<Object> values = manyRefMemory.get(source);
            if (values == null) {
                values = new ArrayList<Object>();
            }
            return values;
        } else {
            final List<Object> values = new ArrayList<Object>();
            if (!initialized) {
                return values;
            }
            IPatternMatch match = matcher.newEmptyMatch();
            match.set(sourceParamName, source);
            matcher.forEachMatch(match, new IMatchProcessor<IPatternMatch>() {

                @Override
                public void process(IPatternMatch match) {
                    values.add(match.get(targetParamName));
                }
            });
            return values;// matcher.getAllValues(targetParamName, match);
        }
    }

    /**
     * @param singleRefMemory
     *            the singleRefMemory to set
     */
    private void setSingleRefMemory(InternalEObject source, Object singleRefMemory) {
        if (keepCache) {
            this.singleRefMemory.put(source, singleRefMemory);
        }
    }

    private void addToManyRefMemory(InternalEObject source, Object added) {
        if (keepCache) {
            List<Object> values = manyRefMemory.get(source);
            if (values == null) {
                values = new ArrayList<Object>();
                manyRefMemory.put(source, values);
            }
            values.add(added);
        }
    }

    private void removeFromManyRefMemory(InternalEObject source, Object removed) {
        if (keepCache) {
            List<Object> values = manyRefMemory.get(source);
            if (values == null) {
                matcher.getEngine()
                        .getLogger()
                        .error("[IncqueryFeatureHandler] Space-time continuum breached (should never happen): removing from list that doesn't exist");
            }
            values.remove(removed);
        }
    }

    @Override
    public EList getManyReferenceValueAsEList(Object source) {
        Collection<?> values = getManyReferenceValue(source);
        if (values.size() > 0) {
            return new EcoreEList.UnmodifiableEList((InternalEObject) source, feature, values.size(), values.toArray());
        } else {
            return new EcoreEList.UnmodifiableEList((InternalEObject) source, feature, 0, null);
        }
    }

    private Collection<IPatternMatch> processNewMatches(Collection<IPatternMatch> signatures) throws IncQueryException {
        List<IPatternMatch> processed = new ArrayList<IPatternMatch>();
        for (IPatternMatch signature : signatures) {
            if (kind == QueryBasedFeatureKind.ITERATION) {
                ENotificationImpl notification = newMatchIteration(signature);
                if (notification != null) {
                    notifications.add(notification);
                }
            } else {
                Object target = signature.get(targetParamName);
                InternalEObject source = (InternalEObject) signature.get(sourceParamName);
                if (target != null) {
                    if (kind == QueryBasedFeatureKind.SUM) {
                        increaseCounter(source, (Integer) target);
                    } else {
                        if (feature.isMany()) {
                            notifications.add(new ENotificationImpl(source, Notification.ADD, feature, null, target));
                            addToManyRefMemory(source, target);
                        } else {
                            if (updateMemory.get(source) != null) {
                                matcher.getEngine()
                                        .getLogger()
                                        .error("[IncqueryFeatureHandler] Space-time continuum breached (should never happen): update memory already set for given source");
                            } else {
                                // must handle later (either in lost matches or after that)
                                updateMemory.put(source, target);
                            }
                        }
                    }
                } else if (kind == QueryBasedFeatureKind.COUNTER) {
                    increaseCounter(source, 1);
                }
            }
            processed.add(signature);
        }
        return processed;
    }

    /**
     * @throws CoreException
     */
    private void increaseCounter(InternalEObject source, int delta) throws IncQueryException {
        Integer value = getIntValue(source);
        if (value <= Integer.MAX_VALUE - delta) {
            int tempMemory = value + delta;
            notifications.add(new ENotificationImpl(source, Notification.SET, feature, getIntValue(source),
                    tempMemory));
            counterMemory.put(source, tempMemory);
        } else {
            throw new IncQueryException(String.format(
                    "The counter of %s for feature %s reached the maximum value of int!", source, feature),
                    "Counter reached maximum value of int");
        }
    }

    private Collection<IPatternMatch> processLostMatches(Collection<IPatternMatch> signatures) throws IncQueryException {
        List<IPatternMatch> processed = new ArrayList<IPatternMatch>();
        for (IPatternMatch signature : signatures) {
            if (kind == QueryBasedFeatureKind.ITERATION) {
                ENotificationImpl notification = lostMatchIteration(signature);
                if (notification != null) {
                    notifications.add(notification);
                }
            } else {
                Object target = signature.get(targetParamName);
                InternalEObject source = (InternalEObject) signature.get(sourceParamName);
                if (target != null) {
                    if (kind == QueryBasedFeatureKind.SUM) {
                        decreaseCounter(source, (Integer) target);
                    } else {
                        if (feature.isMany()) {
                            notifications
                                    .add(new ENotificationImpl(source, Notification.REMOVE, feature, target, null));
                            removeFromManyRefMemory(source, target);
                        } else {
                            Object updateValue = updateMemory.get(source);
                            if (updateValue != null) {
                                notifications.add(new ENotificationImpl(source, Notification.SET, feature, target,
                                        updateValue));
                                setSingleRefMemory(source, updateValue);
                                updateMemory.remove(source);
                            } else {
                                notifications
                                        .add(new ENotificationImpl(source, Notification.SET, feature, target, null));
                                setSingleRefMemory(source, null);
                            }
                        }
                    }
                } else {
                    if (kind == QueryBasedFeatureKind.COUNTER) {
                        decreaseCounter(source, 1);
                    }
                }
            }
            processed.add(signature);
        }
        return processed;
    }

    /**
     * Called each time when a new match is found for Iteration kind
     * 
     * @param signature
     * @return notification to be sent, if one is necessary
     */
    protected ENotificationImpl newMatchIteration(IPatternMatch signature) {
        throw new UnsupportedOperationException("Iteration derived feature handlers must override newMatchIteration");
    }

    /**
     * Called each time when a match is lost for Iteration kind
     * 
     * @param signature
     * @return notification to be sent, if one is necessary
     */
    protected ENotificationImpl lostMatchIteration(IPatternMatch signature) {
        throw new UnsupportedOperationException("Iteration derived feature handlers must override oldMatchIteration");
    }

    @Override
    public Object getValueIteration(Object source) {
        throw new UnsupportedOperationException("Iteration derived feature handlers must override getValueIteration");
    }

    /**
     * @throws CoreException
     */
    private void decreaseCounter(InternalEObject source, int delta) throws IncQueryException {
        Integer value = counterMemory.get(source);
        if (value == null) {
            matcher.getEngine()
                    .getLogger()
                    .error("[IncqueryFeatureHandler] Space-time continuum breached (should never happen): decreasing a counter with no previous value");
        } else if (value >= delta) {
            int tempMemory = value - delta;
            int oldValue = value;
            notifications.add(new ENotificationImpl(source, Notification.SET, feature, oldValue, tempMemory));
            counterMemory.put(source, tempMemory);
        } else {
            throw new IncQueryException(String.format("The counter of %s for feature %s cannot go below zero!", source,
                    feature), "Counter cannot go below zero");
        }
    }

    private void checkUnhandledNewMatch() {
        if (!updateMemory.isEmpty()) {
            for (InternalEObject source : updateMemory.keySet()) {
                notifications.add(new ENotificationImpl(source, Notification.SET, feature, null, updateMemory
                        .get(source)));
                setSingleRefMemory(source, updateMemory.get(source));
            }
            updateMemory.clear();
        }
    }
}
