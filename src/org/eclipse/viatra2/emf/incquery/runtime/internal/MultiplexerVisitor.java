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
import org.eclipse.emf.ecore.resource.Resource;

/**
 * Multiplexes between a collection of EMF visitors.
 * @author Bergmann Gábor
 *
 */
public class MultiplexerVisitor extends EMFVisitor {
	
	private Collection<EMFVisitor> visitors;

	/**
	 * @param visitors
	 */
	public MultiplexerVisitor(Collection<EMFVisitor> visitors) {
		super();
		this.visitors = visitors;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.viatra2.emf.incquery.runtime.internal.EMFVisitor#visitTopElementInResource(org.eclipse.emf.ecore.resource.Resource, org.eclipse.emf.ecore.EObject)
	 */
	@Override
	public void visitTopElementInResource(Resource resource, EObject element) {
		for (EMFVisitor visitor : visitors) visitor.visitTopElementInResource(resource, element);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.viatra2.emf.incquery.runtime.internal.EMFVisitor#visitResource(org.eclipse.emf.ecore.resource.Resource)
	 */
	@Override
	public void visitResource(Resource resource) {
		for (EMFVisitor visitor : visitors) visitor.visitResource(resource);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.viatra2.emf.incquery.runtime.internal.EMFVisitor#visitElement(org.eclipse.emf.ecore.EObject)
	 */
	@Override
	public void visitElement(EObject source) {
		for (EMFVisitor visitor : visitors) visitor.visitElement(source);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.viatra2.emf.incquery.runtime.internal.EMFVisitor#visitExternalReference(org.eclipse.emf.ecore.EObject, org.eclipse.emf.ecore.EReference, java.lang.Object)
	 */
	@Override
	public void visitExternalReference(EObject source, EReference feature, EObject target) {
		for (EMFVisitor visitor : visitors) visitor.visitExternalReference(source, feature, target);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.viatra2.emf.incquery.runtime.internal.EMFVisitor#visitInternalReference(org.eclipse.emf.ecore.EObject, org.eclipse.emf.ecore.EReference, java.lang.Object)
	 */
	@Override
	public void visitInternalReference(EObject source, EReference feature, EObject target) {
		for (EMFVisitor visitor : visitors) visitor.visitInternalReference(source, feature, target);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.viatra2.emf.incquery.runtime.internal.EMFVisitor#visitAttribute(org.eclipse.emf.ecore.EObject, org.eclipse.emf.ecore.EAttribute, java.lang.Object)
	 */
	@Override
	public void visitAttribute(EObject source, EAttribute feature, Object target) {
		for (EMFVisitor visitor : visitors) visitor.visitAttribute(source, feature, target);
	}

}
