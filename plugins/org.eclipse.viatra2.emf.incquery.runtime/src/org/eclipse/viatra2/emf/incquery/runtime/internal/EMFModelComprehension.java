/*******************************************************************************
 * Copyright (c) 2004-2010 Gabor Bergmann and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Gabor Bergmann - initial API and implementation
 *******************************************************************************/

package org.eclipse.viatra2.emf.incquery.runtime.internal;

import java.util.Collection;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.util.FeatureMap;
import org.eclipse.emf.ecore.util.FeatureMap.Entry;
import org.eclipse.viatra2.emf.incquery.runtime.derived.WellbehavingDerivedFeatureRegistry;

/**
 * @author Bergmann Gábor
 * 
 * Does not visit derived (volatile) links, but interprets EFeatureMaps correctly.
 *
 */
public class EMFModelComprehension {

	public static boolean unvisitable(EStructuralFeature feature) {
		boolean suspect = feature.isDerived() || feature.isVolatile();
		if(suspect) {
			// override support here 
			// (e.g. if manual notifications available, or no changes expected afterwards)
			suspect = !WellbehavingDerivedFeatureRegistry.isWellbehavingFeature(feature);
			// TODO verbose flag somewhere to ease debugging (for such warnings)
			// TODO add warning about not visited subtree (containment, FeatureMap and annotation didn't define otherwise)
		}
		return suspect;
	}
	
	public static void visitObject(EMFVisitor visitor, EObject source) {
		if (source == null) return;
		visitor.visitElement(source);
		if(visitor.pruneSubtrees(source))
			return;
		for (EStructuralFeature feature: source.eClass().getEAllStructuralFeatures()) {
			if (unvisitable(feature)) continue;
			if (feature.isMany()) {
				Collection<? extends Object> targets = (Collection<? extends Object>) source.eGet(feature);
				for (Object target : targets) {
					visitFeatureInternal(visitor, source, feature, target);	
				}
			} else {
				Object target = source.eGet(feature);
				visitFeatureInternal(visitor, source, feature, target);
			}
		}
	}


	public static void visitFeature(
			EMFVisitor visitor, EObject source, EStructuralFeature feature, Object target) 
	{
		if (unvisitable(feature)) return;		
		visitFeatureInternal(visitor, source, feature, target);
	}
	
	private static void visitFeatureInternal(
			EMFVisitor visitor, EObject source, EStructuralFeature feature, Object target) 
	{
		if (feature instanceof EAttribute) {
			visitor.visitAttribute(source, (EAttribute)feature, target);
			if (target instanceof FeatureMap.Entry) {
				Entry entry = (FeatureMap.Entry) target;
				visitFeatureInternal(visitor, source, entry.getEStructuralFeature(), entry.getValue());
			}
		} else if (feature instanceof EReference) {
			EReference reference = (EReference)feature;
			EObject targetObject = (EObject)target;
			if (reference.isContainment()) {
				visitor.visitInternalContainment(source, reference, targetObject);
				visitObject(visitor, targetObject);
			} else {
//			if (containedElements.contains(target)) 
				visitor.visitNonContainmentReference(source, reference, targetObject);
			}
//			else
//				visitor.visitExternalReference(source, reference, targetObject);
		}
		
	}

}