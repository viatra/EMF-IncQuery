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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.util.EcoreEList;
import org.eclipse.viatra2.emf.incquery.runtime.api.IMatcherFactory;
import org.eclipse.viatra2.emf.incquery.runtime.api.IPatternMatch;
import org.eclipse.viatra2.emf.incquery.runtime.api.IncQueryEngine;
import org.eclipse.viatra2.emf.incquery.runtime.api.IncQueryMatcher;
import org.eclipse.viatra2.emf.incquery.runtime.exception.IncQueryException;
import org.eclipse.viatra2.emf.incquery.runtime.extensibility.MatcherFactoryRegistry;


/**
 * @author Abel Hegedus
 */
public final class IncqueryFeatureHelper {

	//private static final Map<EObject, Map<EStructuralFeature, IncqueryFeatureHandler>> sourceMap = new HashMap<EObject, Map<EStructuralFeature, IncqueryFeatureHandler>>();
	// temporal registry for features being built
	private static final Map<EObject, List<EStructuralFeature>> SOURCEMAP = new HashMap<EObject, List<EStructuralFeature>>();
	//private static final Map<EObject, Map<EStructuralFeature,IncQueryMatcher<IPatternMatch>>> sourceMap = new HashMap<EObject, Map<EStructuralFeature,IncQueryMatcher<IPatternMatch>>>();
	private static final Map<EObject, Map<EStructuralFeature,Integer>> HANDLER_REQUEST_COUNTER = new HashMap<EObject, Map<EStructuralFeature,Integer>>();
	//private static boolean matcherInitialization = false;

	private static final Map<Notifier, Map<EStructuralFeature,WeakReference<IncqueryDerivedFeature>>> FEATURE_MAP = new WeakHashMap<Notifier, Map<EStructuralFeature,WeakReference<IncqueryDerivedFeature>>>();
	
	/**
	 * 
	 */
	private IncqueryFeatureHelper() {
	}
	
	/*public static IncqueryFeatureHandler getHandler(
			EObject source, EStructuralFeature feature) {

		if (sourceMap.containsKey(source)) {
			Map<EStructuralFeature, IncqueryFeatureHandler> featureMap = sourceMap
					.get(source);
			if (featureMap.containsKey(feature)) {
				return featureMap.get(feature);
			}
		}

		return null;
	}*/

	public static IncqueryFeatureHandler createHandler(
			EObject source, EStructuralFeature feature,
			IMatcherFactory<IncQueryMatcher<IPatternMatch>> matcherFactory, String sourceParamName,
			String targetParamName, FeatureKind kind) {

		//Map<EStructuralFeature, IncqueryFeatureHandler> featureMap;
		//if (sourceMap.containsKey(source)) {
		//	featureMap = sourceMap.get(source);
			// if(featureMap.containsKey(feature)) {
			// overwriting handler
			// } else {
			// }
		//} else {
		//	featureMap = new HashMap<EStructuralFeature, IncqueryFeatureHandler>();
		//	sourceMap.put(source, featureMap);
		//}
		//createFeatureHandler(source, feature, matcherFactory, sourceParamName, targetParamName, featureMap, kind);
		return createFeatureHandler(source, feature, matcherFactory, sourceParamName, targetParamName, kind, true);
		//return featureMap.get(feature);
	}

	/**
	 * @param source
	 * @param feature
	 * @param matcherFactory
	 * @param sourceParamName
	 * @param targetParamName
	 * @param FEATURE_MAP
	 */
	//private static void createFeatureHandler(EObject source, EStructuralFeature feature,
	private static IncqueryFeatureHandler createFeatureHandler(EObject source, EStructuralFeature feature,
			IMatcherFactory<?> matcherFactory, String sourceParamName,
			String targetParamName,
			/*Map<EStructuralFeature, IncqueryFeatureHandler> featureMap,*/ FeatureKind kind, boolean keepCache) {
		try {
			List<EStructuralFeature> featureList = null;
			//Map<EStructuralFeature, IncQueryMatcher<IPatternMatch>> featureList = null;
			Map<EStructuralFeature,Integer> counter = null;
			if(SOURCEMAP.containsKey(source)) {
				featureList = SOURCEMAP.get(source);
			} else {
				featureList = new ArrayList<EStructuralFeature>();
				//featureList = new HashMap<EStructuralFeature,IncQueryMatcher<IPatternMatch>>();
				SOURCEMAP.put(source, featureList);
			}
			if(HANDLER_REQUEST_COUNTER.containsKey(source)) {
				counter = HANDLER_REQUEST_COUNTER.get(source);
			} else {
				counter = new HashMap<EStructuralFeature,Integer>();
				HANDLER_REQUEST_COUNTER.put(source, counter);
			}
			if(counter.containsKey(feature)) {
				Integer count = counter.get(feature);
				counter.put(feature, count+1);
			} else {
				counter.put(feature, 1);
			}
			IncQueryMatcher<?> matcher = null;
			if (!featureList.contains(feature)){
			//if (!featureList.containsKey(feature)){
				featureList.add(feature);
				//featureList.put(feature,handler);
				
				if(matcherFactory == null) {
					throw new IncQueryException(
							String.format("EMF-IncQuery can not provide the value of derived feature %s as no matcher factory was given.", feature),
							"No matcher factory for derived feature handler!");
				}
				matcher = prepareMatcherForSource(source, feature, matcherFactory);
			}
			//matcher = featureList.get(feature);
			IncqueryFeatureHandler handler = null;
			if(matcher != null) {
				featureList.remove(feature);
				handler = new IncqueryFeatureHandler(
						(InternalEObject) source, feature, matcher, sourceParamName, targetParamName, kind, keepCache);
			}
			//featureList.put(feature, handler);
			//featureList.remove(feature);
			if(featureList.isEmpty()) {
				SOURCEMAP.remove(source);
			}
			if(counter.get(feature) == 1) {
				//matcher.getEngine().getLogger().logWarning("Starting handler for feature " + feature);
				handler.startMonitoring();
				counter.remove(feature);
				return handler;
			} else {
				counter.put(feature, counter.get(feature)-1);
				//return null;
			}
			//}
		} catch (IncQueryException e) {
			IncQueryEngine.getDefaultLogger().error("Handler initialization failed", e);
		}
		return null;
	}

  private static IncQueryMatcher<?> prepareMatcherForSource(EObject source, EStructuralFeature feature,
      IMatcherFactory<?> matcherFactory) throws IncQueryException {
    IncQueryMatcher<?> matcher;
    Resource eResource = source.eResource();
    if(eResource != null) {
    	ResourceSet resourceSet = eResource.getResourceSet();
    	if(resourceSet != null) {
    		matcher = matcherFactory.getMatcher(resourceSet);
    	} else {
    		matcher = matcherFactory.getMatcher(eResource);
    		matcher.getEngine().getLogger().debug(String.format("Matcher for derived feature %1$s of %2$s initialized on resource.", feature, source));
    	}
    } else {
    	matcher = matcherFactory.getMatcher(source);
    	matcher.getEngine().getLogger().debug(String.format("Matcher for derived feature %1$s of %2$s initialized on %2$s.", feature, source));
    }
    if(matcher == null) {
    	throw new IncQueryException(
    			String.format("Could not create matcher for pattern %s defining feature %s.", matcherFactory.getPattern(), feature),
    			"Derived feature handler could not initiate matcher.");
    }/* else {
    	if(!featureList.containsKey(feature)) {
    		featureList.put(feature, matcher);
    	}
    }*/
    return matcher;
  }
  
  private static Notifier prepareNotifierForSource(EObject source) {
    if(source != null) {
      Resource eResource = source.eResource();
      if(eResource != null) {
        ResourceSet resourceSet = eResource.getResourceSet();
        if(resourceSet != null) {
          return resourceSet;
        } else {
          return eResource;
        }
      }
    }
    return source;
  }

	@SuppressWarnings("rawtypes")
  public static EList getManyReferenceValueForHandler(IncqueryFeatureHandler handler, InternalEObject source, EStructuralFeature feature) {
		if(handler != null) {
			return handler.getManyReferenceValueAsEList();
		} else {
			if(source != null && feature != null) {
			  return new EcoreEList.UnmodifiableEList(source,	feature, 0, null);
			}
		}
		return null;
	}
	
	public static Object getSingleReferenceValueForHandler(IncqueryFeatureHandler handler, InternalEObject source, EStructuralFeature feature) {
    if(handler != null) {
      return handler.getSingleReferenceValue();
    }
    return null;
  }
	
	public static int getIntValueForHandler(IncqueryFeatureHandler handler, InternalEObject source, EStructuralFeature feature) {
	  if(handler != null) {
      return handler.getIntValue();
    }
    return 0;
	}
	
	public static Object getIterationValueForHandler(IncqueryFeatureHandler handler, InternalEObject source, EStructuralFeature feature) {
	  if(handler != null) {
	    return handler.getValueIteration();
	  }
	  return null;
	}
	
	

	/**
	 * @param source
	 * @param feature
	 * @param patternFQN
	 * @param sourceParamName
	 * @param targetParamName
	 * @param kind
	 * @return
	 */
	public static IncqueryFeatureHandler createHandler(EObject source, EStructuralFeature feature, String patternFQN,
			String sourceParamName, String targetParamName, FeatureKind kind) {
		return createHandler(source, feature, patternFQN, sourceParamName, targetParamName, kind, true);
	}
	
	public static IncqueryFeatureHandler createHandler(EObject source, EStructuralFeature feature, String patternFQN,
			String sourceParamName, String targetParamName, FeatureKind kind, boolean keepCache) {
		IMatcherFactory<?> matcherFactory = MatcherFactoryRegistry.getMatcherFactory(patternFQN);
		if(matcherFactory != null) {
			return createFeatureHandler(source, feature, matcherFactory, sourceParamName, targetParamName, kind, keepCache);
		}
		return null;
	}
	
	public static IncqueryDerivedFeature getIncqueryDerivedFeature(Notifier notifier, EStructuralFeature feature, String patternFQN,
      String sourceParamName, String targetParamName, FeatureKind kind, boolean keepCache) {
	  
	  Map<EStructuralFeature, WeakReference<IncqueryDerivedFeature>> features = FEATURE_MAP.get(notifier);
	  if(features == null) {
	    features = new HashMap<EStructuralFeature, WeakReference<IncqueryDerivedFeature>>();
	    FEATURE_MAP.put(notifier, features);
	  }
	  WeakReference<IncqueryDerivedFeature> weakReference = features.get(feature);
	  
    IncqueryDerivedFeature derivedFeature = weakReference == null?null:weakReference.get();
	  if(derivedFeature != null) {
	    return derivedFeature;
	  }
	  
	  derivedFeature = new IncqueryDerivedFeature(feature, kind, keepCache);
	  features.put(feature, new WeakReference<IncqueryDerivedFeature>(derivedFeature));
	  
	  IMatcherFactory<?> matcherFactory = MatcherFactoryRegistry.getMatcherFactory(patternFQN);
    if(matcherFactory != null) {
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
	
	public static IncqueryDerivedFeature getIncqueryDerivedFeature(Notifier notifier, EObject source, EStructuralFeature feature, String patternFQN,
      String sourceParamName, String targetParamName, FeatureKind kind) {
	  return getIncqueryDerivedFeature(notifier, feature, patternFQN, sourceParamName, targetParamName, kind, true);
	}
	
	public static IncqueryDerivedFeature getIncqueryDerivedFeature(EObject source, EStructuralFeature feature, String patternFQN,
      String sourceParamName, String targetParamName, FeatureKind kind, boolean keepCache, boolean useSourceAsNotifier) {
	  Notifier notifier = source;
	  if(!useSourceAsNotifier) {
	    notifier = prepareNotifierForSource(source);
	  }
    return getIncqueryDerivedFeature(notifier, feature, patternFQN, sourceParamName, targetParamName, kind, keepCache);
  }
	
	/*public static IncqueryDerivedFeature getIncqueryDerivedFeature(EObject source, EStructuralFeature feature, String patternFQN,
      String sourceParamName, String targetParamName, FeatureKind kind, boolean useSourceAsNotifier) {
	  return getIncqueryDerivedFeature(source, feature, patternFQN, sourceParamName, targetParamName, kind, true, useSourceAsNotifier);
	}*/
	
	public static IncqueryDerivedFeature getIncqueryDerivedFeature(EObject source, EStructuralFeature feature, String patternFQN,
      String sourceParamName, String targetParamName, FeatureKind kind) {
    return getIncqueryDerivedFeature(source, feature, patternFQN, sourceParamName, targetParamName, kind, true, false);
  }
}
