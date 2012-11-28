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
package org.eclipse.viatra2.emf.incquery.runtime.derived;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.incquery.runtime.api.IMatcherFactory;
import org.eclipse.incquery.runtime.api.IncQueryEngine;
import org.eclipse.incquery.runtime.api.IncQueryMatcher;
import org.eclipse.incquery.runtime.exception.IncQueryException;
import org.eclipse.incquery.runtime.extensibility.MatcherFactoryRegistry;

/**
 * Utility class for instantiating query-based feature handlers ({@link IQueryBasedFeatureHandler}).
 * 
 * @author Abel Hegedus
 */
public final class QueryBasedFeatureHelper {

    /**
     * Weak hash map for keeping the created 
     */
    private static final Map<Notifier, Map<EStructuralFeature, WeakReference<IQueryBasedFeatureHandler>>> FEATURE_MAP = new WeakHashMap<Notifier, Map<EStructuralFeature, WeakReference<IQueryBasedFeatureHandler>>>();

    /**
	 * Constructor hidden for static utility class
	 */
    private QueryBasedFeatureHelper() {
    }

    /**
     * Decide what {@link Notifier} to use as the scope of the {@link IncQueryMatcher} underlying the created {@link IQueryBasedFeatureHandler}.
     * 
     * <p> Optimally, the {@link ResourceSet} is reachable and most other matchers will use it as well.
     * 
     * <p> Otherwise, the {@link Resource} is used if the model is not inside a resource set.
     * 
     * <p> If none of the above are reachable, the container hierarchy is traversed for a top element.
     * 
     * <p> Finally, the source itself is returned.
     * 
     * @param source the source object that initializes the handler
     * @return the topmost reachable Notifier from the source
     */
    private static Notifier prepareNotifierForSource(EObject source) {
        if (source != null) {
            Resource eResource = source.eResource();
            if (eResource != null) {
                ResourceSet resourceSet = eResource.getResourceSet();
                if (resourceSet != null) {
                    return resourceSet;
                } else {
                    return eResource;
                }
            } else {
                EObject top = source;
                while(top.eContainer() != null) {
                    top = top.eContainer();
                }
                return prepareNotifierForSource(top);
            }
        }
        return source;
    }

    /**
     * Returns the {@link IQueryBasedFeatureHandler} for the given {@link EStructuralFeature} in the given {@link Notifier}.
     *  If the handler does not exist yet, it is also initialized, before being returned.
     * 
     * <p> The required matcher is initialized using the pattern fully qualified name passed as a parameter.
     * 
     * @param notifier the exact notifier to use for the handler initialization
     * @param feature the feature that is managed by the handler
     * @param patternFQN the fully qualified name of the pattern used by the handler
     * @param sourceParamName the name of the parameter in the pattern that represents the source end of the feature
     * @param targetParamName the name of the parameter in the pattern that represents the target end of the feature
     * @param kind the {@link QueryBasedFeatureKind} that is used by the handler
     * @param keepCache specifies whether the handler uses an internal cache for feature values. Only possible with single and many reference kinds
     * @return the query-based feature handler that manages the feature values
     */
    public static IQueryBasedFeatureHandler getQueryBasedFeatureHandler(Notifier notifier, EStructuralFeature feature,
            String patternFQN, String sourceParamName, String targetParamName, QueryBasedFeatureKind kind,
            boolean keepCache) {

        Map<EStructuralFeature, WeakReference<IQueryBasedFeatureHandler>> features = FEATURE_MAP.get(notifier);
        if (features == null) {
            features = new HashMap<EStructuralFeature, WeakReference<IQueryBasedFeatureHandler>>();
            FEATURE_MAP.put(notifier, features);
        }
        WeakReference<IQueryBasedFeatureHandler> weakReference = features.get(feature);

        IQueryBasedFeatureHandler derivedFeature = weakReference == null ? null : weakReference.get();
        if (derivedFeature != null) {
            return derivedFeature;
        }

        QueryBasedFeatureHandler newDerivedFeature = new QueryBasedFeatureHandler(feature, kind, keepCache);
        features.put(feature, new WeakReference<IQueryBasedFeatureHandler>(newDerivedFeature));

        IMatcherFactory<?> matcherFactory = MatcherFactoryRegistry.getMatcherFactory(patternFQN);
        if (matcherFactory != null) {
            try {
                IncQueryMatcher<?> matcher = matcherFactory.getMatcher(notifier);
                newDerivedFeature.initialize(matcher, sourceParamName, targetParamName);
                newDerivedFeature.startMonitoring();
            } catch (IncQueryException e) {
                IncQueryEngine.getDefaultLogger().error("Handler initialization failed", e);
                return null;
            }
        } else {
            IncQueryEngine
                    .getDefaultLogger()
                    .error("Handler initialization failed, matcher factory is null. Make sure to include your EMF-IncQuery project with the query definitions in the configuration.");
        }

        return newDerivedFeature;
    }


    /**
     * Returns the {@link IQueryBasedFeatureHandler} for the given {@link EStructuralFeature} in the given {@link Notifier}.
     *  If the handler does not exist yet, it is also initialized, before being returned.
     * 
     * <p> The required matcher is initialized using the pattern fully qualified name passed as a parameter.
     * 
     * <p> Calls {@link #getQueryBasedFeatureHandler(Notifier, EStructuralFeature, String, String, String, QueryBasedFeatureKind, boolean)} with keepCache = true.
     * 
     * @param notifier the exact notifier to use for the handler initialization
     * @param feature the feature that is managed by the handler
     * @param patternFQN the fully qualified name of the pattern used by the handler
     * @param sourceParamName the name of the parameter in the pattern that represents the source end of the feature
     * @param targetParamName the name of the parameter in the pattern that represents the target end of the feature
     * @param kind the {@link QueryBasedFeatureKind} that is used by the handler
     * @return the query-based feature handler that manages the feature values
     */
    public static IQueryBasedFeatureHandler getQueryBasedFeatureHandlerOnNotifier(Notifier notifier,
            EStructuralFeature feature, String patternFQN, String sourceParamName, String targetParamName,
            QueryBasedFeatureKind kind) {
        return getQueryBasedFeatureHandler(notifier, feature, patternFQN, sourceParamName, targetParamName, kind, true);
    }

    /**
     * Returns the {@link IQueryBasedFeatureHandler} for the given {@link EStructuralFeature}  on the source or the topmost {@link Notifier}
     *  reachable from the source. If the handler does not exist yet, it is also initialized, before being returned.
     * 
     * <p> The required matcher is initialized using the pattern fully qualified name passed as a parameter.
     * 
     * <p> Calls {@link #getQueryBasedFeatureHandler(Notifier, EStructuralFeature, String, String, String, QueryBasedFeatureKind, boolean)}.
     * 
     * @param source the source object used for the handler initialization (used for determining the notifier for the underlying matcher)
     * @param feature the feature that is managed by the handler
     * @param patternFQN the fully qualified name of the pattern used by the handler
     * @param sourceParamName the name of the parameter in the pattern that represents the source end of the feature
     * @param targetParamName the name of the parameter in the pattern that represents the target end of the feature
     * @param kind the {@link QueryBasedFeatureKind} that is used by the handler
     * @param keepCache specifies whether the handler uses an internal cache for feature values. Only possible with single and many reference kinds
     * @param useSourceAsNotifier if true, the source is used as the notifier for the matcher initialization
     * @return the query-based feature handler that manages the feature values
     */
    public static IQueryBasedFeatureHandler getQueryBasedFeatureHandler(EObject source, EStructuralFeature feature,
            String patternFQN, String sourceParamName, String targetParamName, QueryBasedFeatureKind kind,
            boolean keepCache, boolean useSourceAsNotifier) {
        Notifier notifier = source;
        if (!useSourceAsNotifier) {
            notifier = prepareNotifierForSource(source);
        }
        return getQueryBasedFeatureHandler(notifier, feature, patternFQN, sourceParamName, targetParamName, kind,
                keepCache);
    }

    /**
     * Returns the {@link IQueryBasedFeatureHandler} for the given {@link EStructuralFeature} on the topmost {@link Notifier}
     *  reachable from the source. If the handler does not exist yet, it is also initialized, before being returned.
     * 
     * <p> The required matcher is initialized using the pattern fully qualified name passed as a parameter.
     * 
     * <p> Calls {@link #getQueryBasedFeatureHandler(EObject, EStructuralFeature, String, String, String, QueryBasedFeatureKind, boolean, boolean)}.
     * 
     * @param source the source object used for the handler initialization (used for determining the notifier for the underlying matcher)
     * @param feature the feature that is managed by the handler
     * @param patternFQN the fully qualified name of the pattern used by the handler
     * @param sourceParamName the name of the parameter in the pattern that represents the source end of the feature
     * @param targetParamName the name of the parameter in the pattern that represents the target end of the feature
     * @param kind the {@link QueryBasedFeatureKind} that is used by the handler
     * @return the query-based feature handler that manages the feature values
     */
    public static IQueryBasedFeatureHandler getQueryBasedFeatureHandler(EObject source, EStructuralFeature feature,
            String patternFQN, String sourceParamName, String targetParamName, QueryBasedFeatureKind kind) {
        return getQueryBasedFeatureHandler(source, feature, patternFQN, sourceParamName, targetParamName, kind, true,
                false);
    }
}
