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
import java.util.Iterator;
import java.util.LinkedHashSet;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;

/**
 * @author Bergmann Gábor
 */
public class EMFContainmentHierarchyTraversal {
	Object topElement;
	Collection<EObject> containedElements = new LinkedHashSet<EObject>();
	Collection<Resource> containedResources = new LinkedHashSet<Resource>();
	
//	public static EMFContainmentHierarchyTraversal create(Notifier emfRoot) throws ViatraCompiledRuntimeException {
//		if (emfRoot instanceof ResourceSet) return new EMFContainmentHierarchyTraversal((ResourceSet)emfRoot);
//		else if (emfRoot instanceof Resource) return new EMFContainmentHierarchyTraversal((Resource)emfRoot);
//		else if (emfRoot instanceof EObject) return new EMFContainmentHierarchyTraversal((EObject)emfRoot);
//		else throw new ViatraCompiledRuntimeException(ViatraCompiledRuntimeException.INVALID_EMFROOT);
//	}
	
	public EMFContainmentHierarchyTraversal(EObject topElement) {
		this.topElement = topElement;

		containedElements.add(topElement);
		for (Iterator<EObject> iContained = topElement.eAllContents(); iContained.hasNext(); ) {
			containedElements.add(iContained.next());
		}
	}
	public EMFContainmentHierarchyTraversal(Resource resource) {
		this.topElement = resource;

		containedResources.add(resource);
		for (Iterator<EObject> iContained = resource.getAllContents(); iContained.hasNext(); ) {
			containedElements.add(iContained.next());
		}
	}
	public EMFContainmentHierarchyTraversal(ResourceSet resourceSet) {
		this.topElement = resourceSet;
		
		for (Resource resource: resourceSet.getResources()) {
			containedResources.add(resource);
			for (Iterator<EObject> iContained = resource.getAllContents(); iContained.hasNext(); ) {
				containedElements.add(iContained.next());
			}			
		}

	}
	
	@SuppressWarnings("unchecked")
	public void accept(EMFVisitor visitor) {
		for (Resource resource : containedResources) {
			visitor.visitResource(resource);
			for (EObject element : resource.getContents()) visitor.visitTopElementInResource(resource, element);
		}
		for (EObject source : containedElements) {
			visitor.visitElement(source);
			for (EStructuralFeature feature: source.eClass().getEAllStructuralFeatures()) {
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
	}
	
	/**
	 * @param visitor
	 * @param source
	 * @param feature
	 * @param target
	 */
	private void visitFeature(EMFVisitor visitor, EObject source,
			EStructuralFeature feature, Object target) {
		if (feature instanceof EAttribute) {
			visitor.visitAttribute(source, (EAttribute)feature, target);
		} else if (feature instanceof EReference) {
			if (containedElements.contains(target)) 
				visitor.visitInternalReference(source, (EReference)feature, target);
			else
				visitor.visitExternalReference(source, (EReference)feature, target);
		}
	}

}
