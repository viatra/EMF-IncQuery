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

package org.eclipse.viatra2.compiled.emf.runtime.internal;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.resource.Resource;

/**
 * @author Bergmann Gábor
 *
 */
public class EMFVisitor {

	/**
	 * @param resource
	 * @param element
	 */
	public  void visitTopElementInResource(Resource resource, EObject element) {}

	/**
	 * @param resource
	 */
	public  void visitResource(Resource resource) {}

	/**
	 * @param source
	 */
	public  void visitElement(EObject source) {}

	/**
	 * @param source
	 * @param feature
	 * @param target
	 */
	public  void visitExternalReference(EObject source, EReference feature, Object target) {}

	/**
	 * @param source
	 * @param feature
	 * @param target
	 */
	public  void visitInternalReference(EObject source, EReference feature, Object target) {}

	/**
	 * @param current
	 * @param feature
	 * @param value
	 */
	public  void visitAttribute(EObject source, EAttribute feature, Object target) {}

}