/*******************************************************************************
 * Copyright (c) 2004-2008 Gabor Bergmann and Daniel Varro
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
import java.util.LinkedHashSet;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;

/**
 * Does not visit derived links.
 * @author Bergmann Gábor
 */
public class EMFContainmentHierarchyTraversal {
//	Collection<EObject> containedElements = new LinkedHashSet<EObject>();
	Collection<EObject> rootElements = new LinkedHashSet<EObject>();
	Collection<Resource> rootResources = new LinkedHashSet<Resource>();
	
//	public static EMFContainmentHierarchyTraversal create(Notifier emfRoot) throws IncQueryRuntimeException {
//		if (emfRoot instanceof ResourceSet) return new EMFContainmentHierarchyTraversal((ResourceSet)emfRoot);
//		else if (emfRoot instanceof Resource) return new EMFContainmentHierarchyTraversal((Resource)emfRoot);
//		else if (emfRoot instanceof EObject) return new EMFContainmentHierarchyTraversal((EObject)emfRoot);
//		else throw new IncQueryRuntimeException(IncQueryRuntimeException.INVALID_EMFROOT);
//	}
	
	public EMFContainmentHierarchyTraversal(EObject topElement) {
		rootElements.add(topElement);
//		containedElements.add(topElement);
//		for (Iterator<EObject> iContained = topElement.eAllContents(); iContained.hasNext(); ) {
//			containedElements.add(iContained.next());
//		}
	}
	public EMFContainmentHierarchyTraversal(Resource resource) {
		rootResources.add(resource);
//		for (Iterator<EObject> iContained = resource.getAllContents(); iContained.hasNext(); ) {
//			containedElements.add(iContained.next());
//		}
	}
	public EMFContainmentHierarchyTraversal(ResourceSet resourceSet) {
		for (Resource resource: resourceSet.getResources()) {
			rootResources.add(resource);
//			for (Iterator<EObject> iContained = resource.getAllContents(); iContained.hasNext(); ) {
//				containedElements.add(iContained.next());
//			}			
		}

	}
	public EMFContainmentHierarchyTraversal(ResourceSet resourceSet, Collection<Resource> additionalResources) {
		this(resourceSet);
		rootResources.addAll(additionalResources);
	}
	
	@SuppressWarnings("unchecked")
	public void accept(EMFVisitor visitor) {
		for (Resource resource : rootResources) {
			visitor.visitResource(resource);
			for (EObject element : resource.getContents()) {
				visitor.visitTopElementInResource(resource, element);
				visitObject(visitor, element);
			}
		}
		for (EObject source : rootElements) {
			visitObject(visitor, source);
		}
	}
	/**
	 * @param visitor
	 * @param source
	 */
	private void visitObject(EMFVisitor visitor, EObject source) {
		if (source == null) return;
		visitor.visitElement(source);
		for (EStructuralFeature feature: source.eClass().getEAllStructuralFeatures()) {
			if (feature.isDerived()) continue;
			if (feature.isMany()) {
				Collection<? extends Object> targets = (Collection<? extends Object>) source.eGet(feature);
				for (Object target : targets) {
					visitFeature(visitor, source, feature, target);	
				}
			} else {
				Object target = source.eGet(feature);
				visitFeature(visitor, source, feature, target);
			}
		}
	}
	
	/**
	 * @param visitor
	 * @param source
	 * @param feature
	 * @param target
	 */
	private void visitFeature(EMFVisitor visitor, EObject source, EStructuralFeature feature, Object target) {
		if (feature instanceof EAttribute) {
			visitor.visitAttribute(source, (EAttribute)feature, target);
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
