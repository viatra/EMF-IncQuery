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

package org.eclipse.viatra2.emf.incquery.base.comprehension;

import java.util.Collection;

import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.util.FeatureMap;
import org.eclipse.emf.ecore.util.FeatureMap.Entry;

/**
 * @author Bergmann Gábor
 * 
 * Does not visit derived (volatile) links, but interprets EFeatureMaps correctly.
 *
 */
public class EMFModelComprehension {

	public static boolean unvisitableDirectly(EStructuralFeature feature) {
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
	
	public static void visitModel(EMFVisitor visitor, Notifier source) {
		if (source == null) return;
		if (source instanceof EObject) {
			visitObject(visitor, (EObject) source);
		}
		else if (source instanceof Resource) {
			visitResource(visitor, (Resource) source);
		}
		else if (source instanceof ResourceSet) {
			visitResourceSet(visitor, (ResourceSet) source);
		}
	}		
	
	public static void visitResourceSet(EMFVisitor visitor, ResourceSet source) {
		if (source == null) return;
		final EList<Resource> resources = source.getResources();
		for (Resource resource : resources) {
			visitResource(visitor, resource);
		}
	}
	
	public static void visitResource(EMFVisitor visitor, Resource source) {
		if (source == null) return;
		if(visitor.pruneSubtrees(source))
			return;
		final EList<EObject> contents = source.getContents();
		for (EObject eObject : contents) {
			visitObject(visitor, eObject);
		}
	}

	
	public static void visitObject(EMFVisitor visitor, EObject source) {
		if (source == null) return;
		visitor.visitElement(source);
		for (EStructuralFeature feature: source.eClass().getEAllStructuralFeatures()) {
			if (unvisitableDirectly(feature)) continue;
			final boolean visitorPrunes = visitor.pruneFeature(feature);
			if (visitorPrunes && !unprunableFeature(visitor, source, feature)) continue;
		
			if (feature.isMany()) {
				Collection<?> targets = (Collection<?>) source.eGet(feature);
				for (Object target : targets) {
					visitFeatureInternal(visitor, source, feature, target, visitorPrunes);	
				}
			} else {
				Object target = source.eGet(feature);
				if (target != null) visitFeatureInternal(visitor, source, feature, target, visitorPrunes);
			}
		}
	}
	
	private static boolean unprunableFeature(EMFVisitor visitor, EObject source, EStructuralFeature feature) {	
		return
				(
					feature instanceof EAttribute && 
					EcorePackage.eINSTANCE.getEFeatureMapEntry().equals(
							((EAttribute)feature).getEAttributeType()
					)
				) || (
						feature instanceof EReference &&
						((EReference)feature).isContainment() &&
						!visitor.pruneSubtrees(source)
				);
	}



	public static void visitFeature(
			EMFVisitor visitor, EObject source, EStructuralFeature feature, Object target) 
	{
		if (target == null) return;
		if (unvisitableDirectly(feature)) return;	
		final boolean visitorPrunes = visitor.pruneFeature(feature);
		if (visitorPrunes && !unprunableFeature(visitor, source, feature)) return;
		
		visitFeatureInternal(visitor, source, feature, target, visitorPrunes);
	}
	
	private static void visitFeatureInternal(
			EMFVisitor visitor, EObject source, EStructuralFeature feature, Object target, boolean visitorPrunes) 
	{
		if (feature instanceof EAttribute) {
			if (!visitorPrunes) visitor.visitAttribute(source, (EAttribute)feature, target);
			if (target instanceof FeatureMap.Entry) {
				Entry entry = (FeatureMap.Entry) target;
				final EStructuralFeature emulated = entry.getEStructuralFeature();
				
				final boolean visitorPrunesEmulated = visitor.pruneFeature(emulated);
				if (visitorPrunesEmulated && !unprunableFeature(visitor, source, emulated)) return;
				
				visitFeatureInternal(visitor, source, emulated, entry.getValue(), visitorPrunesEmulated);
			}
		} else if (feature instanceof EReference) {
			EReference reference = (EReference)feature;
			EObject targetObject = (EObject)target;
			if(targetObject != null && targetObject.eIsProxy()) {
				targetObject = EcoreUtil.resolve(targetObject,source);
				//source.eGet(feature, true);
			}
			if (reference.isContainment()) {
				if (!visitorPrunes) visitor.visitInternalContainment(source, reference, targetObject);
				if (!visitor.pruneSubtrees(source)) visitObject(visitor, targetObject);
			} else {
//			if (containedElements.contains(target)) 
				if (!visitorPrunes) visitor.visitNonContainmentReference(source, reference, targetObject);
			}
//			else
//				visitor.visitExternalReference(source, reference, targetObject);
		}
		
	}

}