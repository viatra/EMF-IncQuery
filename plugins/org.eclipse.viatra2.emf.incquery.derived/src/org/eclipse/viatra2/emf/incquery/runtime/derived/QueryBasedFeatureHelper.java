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
import org.eclipse.viatra2.emf.incquery.runtime.api.IMatcherFactory;
import org.eclipse.viatra2.emf.incquery.runtime.api.IncQueryEngine;
import org.eclipse.viatra2.emf.incquery.runtime.api.IncQueryMatcher;
import org.eclipse.viatra2.emf.incquery.runtime.exception.IncQueryException;
import org.eclipse.viatra2.emf.incquery.runtime.extensibility.MatcherFactoryRegistry;

/**
 * @author Abel Hegedus
 */
public final class QueryBasedFeatureHelper {

    private static final Map<Notifier, Map<EStructuralFeature, WeakReference<QueryBasedFeatureHandler>>> FEATURE_MAP = new WeakHashMap<Notifier, Map<EStructuralFeature, WeakReference<QueryBasedFeatureHandler>>>();

    /**
	 * 
	 */
    private QueryBasedFeatureHelper() {
    }

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
            }
        }
        return source;
    }

    public static QueryBasedFeatureHandler getQueryBasedFeatureHandler(Notifier notifier, EStructuralFeature feature,
            String patternFQN, String sourceParamName, String targetParamName, QueryBasedFeatureKind kind,
            boolean keepCache) {

        Map<EStructuralFeature, WeakReference<QueryBasedFeatureHandler>> features = FEATURE_MAP.get(notifier);
        if (features == null) {
            features = new HashMap<EStructuralFeature, WeakReference<QueryBasedFeatureHandler>>();
            FEATURE_MAP.put(notifier, features);
        }
        WeakReference<QueryBasedFeatureHandler> weakReference = features.get(feature);

        QueryBasedFeatureHandler derivedFeature = weakReference == null ? null : weakReference.get();
        if (derivedFeature != null) {
            return derivedFeature;
        }

        derivedFeature = new QueryBasedFeatureHandler(feature, kind, keepCache);
        features.put(feature, new WeakReference<QueryBasedFeatureHandler>(derivedFeature));

        IMatcherFactory<?> matcherFactory = MatcherFactoryRegistry.getMatcherFactory(patternFQN);
        if (matcherFactory != null) {
            try {
                IncQueryMatcher<?> matcher = matcherFactory.getMatcher(notifier);
                derivedFeature.initialize(matcher, sourceParamName, targetParamName);
                derivedFeature.startMonitoring();
            } catch (IncQueryException e) {
                IncQueryEngine.getDefaultLogger().error("Handler initialization failed", e);
                return null;
            }
        }

        return derivedFeature;
    }

    public static QueryBasedFeatureHandler getQueryBasedFeatureHandler(Notifier notifier, EObject source,
            EStructuralFeature feature, String patternFQN, String sourceParamName, String targetParamName,
            QueryBasedFeatureKind kind) {
        return getQueryBasedFeatureHandler(notifier, feature, patternFQN, sourceParamName, targetParamName, kind, true);
    }

    public static QueryBasedFeatureHandler getQueryBasedFeatureHandler(EObject source, EStructuralFeature feature,
            String patternFQN, String sourceParamName, String targetParamName, QueryBasedFeatureKind kind,
            boolean keepCache, boolean useSourceAsNotifier) {
        Notifier notifier = source;
        if (!useSourceAsNotifier) {
            notifier = prepareNotifierForSource(source);
        }
        return getQueryBasedFeatureHandler(notifier, feature, patternFQN, sourceParamName, targetParamName, kind,
                keepCache);
    }

    public static QueryBasedFeatureHandler getQueryBasedFeatureHandler(EObject source, EStructuralFeature feature,
            String patternFQN, String sourceParamName, String targetParamName, QueryBasedFeatureKind kind) {
        return getQueryBasedFeatureHandler(source, feature, patternFQN, sourceParamName, targetParamName, kind, true,
                false);
    }
}
