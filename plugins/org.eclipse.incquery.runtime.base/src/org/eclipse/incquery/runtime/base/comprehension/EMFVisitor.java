/*******************************************************************************
 * Copyright (c) 2004-2010 Gabor Bergmann and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *  Gabor Bergmann - initial API and implementation
 *******************************************************************************/

package org.eclipse.incquery.runtime.base.comprehension;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;

/**
 * Use EMFModelComprehension to visit an EMF model.
 * 
 * @author Bergmann Gábor
 * 
 */
// FIXME:
// - handle boundary of active emfRoot subtree
// - more efficient traversal
public class EMFVisitor {

    /**
     * @param resource
     * @param element
     */
    public void visitTopElementInResource(Resource resource, EObject element) {
    }

    /**
     * @param resource
     */
    public void visitResource(Resource resource) {
    }

    /**
     * @param source
     */
    public void visitElement(EObject source) {
    }

    // /**
    // * @param source
    // * @param feature
    // * @param target
    // */
    // public void visitExternalReference(EObject source, EReference feature, EObject target) {}

    /**
     * @param source
     * @param feature
     * @param target
     */
    public void visitNonContainmentReference(EObject source, EReference feature, EObject target) {
    }

    /**
     * @param source
     * @param feature
     * @param target
     */
    public void visitInternalContainment(EObject source, EReference feature, EObject target) {
    }

    /**
     * @param current
     * @param feature
     * @param value
     */
    public void visitAttribute(EObject source, EAttribute feature, Object target) {
    }

    /**
     * Returns true if the given feature should not be traversed (interesting esp. if multi-valued)
     */
    public boolean pruneFeature(EStructuralFeature feature) {
        return false;
    }

    /**
     * Returns true if the contents of an object should be pruned (and not explored by the visitor)
     */
    public boolean pruneSubtrees(EObject source) {
        return false;
    }

    /**
     * Returns true if the contents of a resource should be pruned (and not explored by the visitor)
     */
    public boolean pruneSubtrees(Resource source) {
        return false;
    }

    public void visitUnresolvableProxyObject(EObject source) {
    }

    public void visitUnresolvableProxyFeature(EObject source, EReference feature, EObject target) {
    }

    /**
     * @return
     */
    public boolean forceProxyResolution() {
        return true;
    }

}