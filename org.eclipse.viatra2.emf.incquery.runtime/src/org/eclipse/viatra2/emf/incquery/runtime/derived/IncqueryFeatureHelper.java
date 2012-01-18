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

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.viatra2.emf.incquery.runtime.api.IMatcherFactory;
import org.eclipse.viatra2.emf.incquery.runtime.api.IPatternSignature;
import org.eclipse.viatra2.emf.incquery.runtime.api.IncQueryMatcher;
import org.eclipse.viatra2.emf.incquery.runtime.derived.IncqueryFeatureHandler.FeatureKind;
import org.eclipse.viatra2.emf.incquery.runtime.exception.IncQueryRuntimeException;


/**
 * @author Abel Hegedus
 * TODO remove sourceMap (memory leak) 
 */
public class IncqueryFeatureHelper {

	//private static final Map<EObject, Map<EStructuralFeature, IncqueryFeatureHandler>> sourceMap = new HashMap<EObject, Map<EStructuralFeature, IncqueryFeatureHandler>>();

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
			IMatcherFactory matcherFactory, String sourceParamName,
			String targetParamName, FeatureKind kind) {

		//Map<EStructuralFeature, IncqueryFeatureHandler> featureMap;
		//if (sourceMap.containsKey(source)) {
		//	featureMap = sourceMap.get(source);
			// if(featureMap.containsKey(feature)) {
			// overwriting handler
			// TODO !!!
			// } else {
			// }
		//} else {
		//	featureMap = new HashMap<EStructuralFeature, IncqueryFeatureHandler>();
		//	sourceMap.put(source, featureMap);
		//}
		//createFeatureHandler(source, feature, matcherFactory, sourceParamName, targetParamName, featureMap, kind);
		return createFeatureHandler(source, feature, matcherFactory, sourceParamName, targetParamName, kind);
		//return featureMap.get(feature);
	}

	/**
	 * @param source
	 * @param feature
	 * @param matcherFactory
	 * @param sourceParamName
	 * @param targetParamName
	 * @param featureMap
	 */
	//private static void createFeatureHandler(EObject source, EStructuralFeature feature,
	private static IncqueryFeatureHandler createFeatureHandler(EObject source, EStructuralFeature feature,
			IMatcherFactory matcherFactory, String sourceParamName,
			String targetParamName,
			/*Map<EStructuralFeature, IncqueryFeatureHandler> featureMap,*/ FeatureKind kind) {
		try {
			IncQueryMatcher<IPatternSignature> matcher = matcherFactory.getMatcher(source);
			IncqueryFeatureHandler handler = new IncqueryFeatureHandler(
					(InternalEObject) source, feature, matcher, sourceParamName, targetParamName, kind);
			handler.startMonitoring();
			//featureMap.put(feature, handler);
			return handler;
		} catch (IncQueryRuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

}
