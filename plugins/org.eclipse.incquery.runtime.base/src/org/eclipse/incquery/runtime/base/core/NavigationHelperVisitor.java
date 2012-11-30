/*******************************************************************************
 * Copyright (c) 2010-2012, Tamas Szabo, Gabor Bergmann, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Tamas Szabo, Gabor Bergmann - initial API and implementation
 *******************************************************************************/

package org.eclipse.incquery.runtime.base.core;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.incquery.runtime.base.comprehension.EMFVisitor;

public abstract class NavigationHelperVisitor extends EMFVisitor {

    /**
     * A visitor for processing a single change event. Does not traverse the model. Uses all the observed types.
     */
    public static class ChangeVisitor extends NavigationHelperVisitor {
        // local copies to save actual state, in case visitor has to be saved for later due unresolvable proxies
        private final boolean wildcardMode;
        private final HashSet<EClass> allObservedClasses;
        private final HashSet<EDataType> observedDataTypes;
        private final HashSet<EStructuralFeature> observedFeatures;

        public ChangeVisitor(NavigationHelperImpl navigationHelper, boolean isInsertion) {
            super(navigationHelper, isInsertion, false);
            wildcardMode = navigationHelper.isInWildcardMode();
            allObservedClasses = new HashSet<EClass>(navigationHelper.getAllObservedClasses());
            observedDataTypes = new HashSet<EDataType>(navigationHelper.getObservedDataTypes());
            observedFeatures = new HashSet<EStructuralFeature>(navigationHelper.getObservedFeatures());
        }

        @Override
        protected boolean observesClass(EClass eClass) {
            return wildcardMode || allObservedClasses.contains(eClass);
        }

        @Override
        protected boolean observesDataType(EDataType type) {
            return wildcardMode || observedDataTypes.contains(type);
        }

        @Override
        protected boolean observesFeature(EStructuralFeature feature) {
            return wildcardMode || observedFeatures.contains(feature);
        }
    }

    /**
     * A visitor for a single-pass traversal of the whole model, processing only the given types and inserting them.
     */
    public static class TraversingVisitor extends NavigationHelperVisitor {
        private final boolean wildcardMode;
        Set<EStructuralFeature> features;
        Set<EClass> newClasses;
        Set<EClass> oldClasses; // if decends from an old class, no need to add!
        Map<EClass, Boolean> classObservationMap; // true for a class even if only a supertype is included in classes;
        Set<EDataType> dataTypes;

        public TraversingVisitor(NavigationHelperImpl navigationHelper, Set<EStructuralFeature> features,
                Set<EClass> newClasses, Set<EClass> oldClasses, Set<EDataType> dataTypes) {
            super(navigationHelper, true, true);
            wildcardMode = navigationHelper.isInWildcardMode();
            this.features = features;
            this.newClasses = newClasses;
            this.oldClasses = oldClasses;
            this.classObservationMap = new HashMap<EClass, Boolean>();
            this.dataTypes = dataTypes;
        }

        @Override
        protected boolean observesClass(EClass eClass) {
            if (navigationHelper.isInWildcardMode())
                return true;
            Boolean observed = classObservationMap.get(eClass);
            if (observed == null) {
                final EList<EClass> eAllSuperTypes = eClass.getEAllSuperTypes();
                final boolean overApprox = newClasses.contains(eClass)
                        || newClasses.contains(NavigationHelperContentAdapter.eObjectClass)
                        || !Collections.disjoint(eAllSuperTypes, newClasses);
                observed = overApprox && !oldClasses.contains(eClass)
                        && !oldClasses.contains(NavigationHelperContentAdapter.eObjectClass)
                        && Collections.disjoint(eAllSuperTypes, oldClasses);
                classObservationMap.put(eClass, observed);
            }
            return observed;
        }

        @Override
        protected boolean observesDataType(EDataType type) {
            return wildcardMode || dataTypes.contains(type);
        }

        @Override
        protected boolean observesFeature(EStructuralFeature feature) {
            return wildcardMode || features.contains(feature);
        }

    }

    protected NavigationHelperImpl navigationHelper;
    private NavigationHelperContentAdapter store;
    boolean isInsertion;
    boolean descendHierarchy;

    NavigationHelperVisitor(NavigationHelperImpl navigationHelper, boolean isInsertion, boolean descendHierarchy) {
        super();
        this.navigationHelper = navigationHelper;
        this.store = navigationHelper.getContentAdapter();
        this.isInsertion = isInsertion;
        this.descendHierarchy = descendHierarchy;
    }

    // public void visitModel(Notifier emfRoot, Set<EStructuralFeature> features, Set<EClass> classes, Set<EDataType>
    // dataTypes) {
    // if (emfRoot instanceof EObject) {
    // visitEObjectRoot((EObject) emfRoot, features, classes, dataTypes);
    // }
    // else if (emfRoot instanceof Resource) {
    // visitResourceRoot((Resource) emfRoot, features, classes, dataTypes);
    // }
    // else if (emfRoot instanceof ResourceSet) {
    // visitResourceSetRoot((ResourceSet) emfRoot, features, classes, dataTypes);
    // }
    // }
    //
    // private void visitResourceSetRoot(ResourceSet resourceSet, Set<EStructuralFeature> features, Set<EClass> classes,
    // Set<EDataType> dataTypes) {
    // for (Resource r : resourceSet.getResources()) {
    // visitResourceRoot(r, features, classes, dataTypes);
    // }
    // }
    //
    // private void visitResourceRoot(Resource resource, Set<EStructuralFeature> features, Set<EClass> classes,
    // Set<EDataType> dataTypes) {
    // for (EObject obj : resource.getContents()) {
    // visitEObjectRoot(obj, features, classes, dataTypes);
    // }
    // }
    //
    // private void visitEObjectRoot(EObject root, Set<EStructuralFeature> features, Set<EClass> classes, Set<EDataType>
    // dataTypes) {
    // visitObject(root, features, classes, dataTypes);
    // TreeIterator<EObject> it = root.eAllContents();
    //
    // while (it.hasNext()) {
    // visitObject(it.next(), features, classes, dataTypes);
    // }
    // }

    @Override
    public boolean pruneSubtrees(EObject source) {
        return !descendHierarchy;
    }

    @Override
    public boolean pruneSubtrees(Resource source) {
        return !descendHierarchy;
    }

    @Override
    public boolean pruneFeature(EStructuralFeature feature) {
        if (observesFeature(feature))
            return false;
        if (feature instanceof EAttribute && observesDataType(((EAttribute) feature).getEAttributeType()))
            return false;
        if (isInsertion && navigationHelper.isExpansionAllowed() && feature instanceof EReference
                && !((EReference) feature).isContainment())
            return false;
        return true;
    }

    protected abstract boolean observesFeature(EStructuralFeature feature);

    protected abstract boolean observesDataType(EDataType type);

    protected abstract boolean observesClass(EClass eClass);

    @Override
    public void visitElement(EObject source) {
        EClass eClass = source.eClass();
        if (eClass.eIsProxy())
            eClass = (EClass) EcoreUtil.resolve(eClass, source);

        store.maintainTypeHierarchy(eClass);
        if (observesClass(eClass)) {
            if (isInsertion) {
                store.insertInstanceTuple(eClass, source);
            } else {
                store.removeInstanceTuple(eClass, source);
            }
        }
    }

    @Override
    public void visitAttribute(EObject source, EAttribute feature, Object target) {
        final EDataType eAttributeType = feature.getEAttributeType();
        if (observesFeature(feature)) {
            if (isInsertion) {
                store.insertFeatureTuple(feature, target, source);
            } else {
                store.removeFeatureTuple(feature, target, source);
            }
        }
        if (observesDataType(eAttributeType))
            store.dataTypeInstanceUpdate(eAttributeType, target, isInsertion);
    };

    @Override
    public void visitInternalContainment(EObject source, EReference feature, EObject target) {
        visitReference(source, feature, target);
    }

    @Override
    public void visitNonContainmentReference(EObject source, EReference feature, EObject target) {
        visitReference(source, feature, target);
        if (isInsertion)
            navigationHelper.considerForExpansion(target);
    };

    private void visitReference(EObject source, EReference feature, EObject target) {
        if (observesFeature(feature)) {
            if (isInsertion)
                store.insertFeatureTuple(feature, target, source);
            else
                store.removeFeatureTuple(feature, target, source);
        }
    }

    @Override
    public void visitUnresolvableProxyFeature(EObject source, EReference reference, EObject target) {
        store.suspendVisitorOnUnresolvableFeature(this, source, reference, target, isInsertion);
    }

    @Override
    public void visitUnresolvableProxyObject(EObject source) {
        store.suspendVisitorOnUnresolvableObject(this, source, isInsertion);
    }

    @Override
    public boolean forceProxyResolution() {
        return isInsertion;
    }

    // private void visitObject(EObject obj, Set<EStructuralFeature> features, Set<EClass> classes, Set<EDataType>
    // dataTypes) {
    // if (obj != null) {
    //
    // if (classes != null) {
    // if (navigationHelper.isInWildcardMode() || classes.contains(obj.eClass())) {
    // store.insertInstanceTuple(obj.eClass(), obj);
    // }
    // }
    //
    // if (features != null) {
    // for (EReference ref : obj.eClass().getEAllReferences()) {
    // if (navigationHelper.isInWildcardMode() || features.contains(ref)) {
    // Object o = obj.eGet(ref);
    //
    // if (o != null) {
    // if (o instanceof EObjectEList<?>) {
    // @SuppressWarnings("unchecked")
    // EObjectEList<EObject> list = (EObjectEList<EObject>) o;
    // Iterator<EObject> it = list.iterator();
    // while (it.hasNext()) {
    // store.insertFeatureTuple(ref, it.next(), obj);
    // }
    // } else {
    // store.insertFeatureTuple(ref, o, obj);
    // }
    // }
    // }
    // }
    //
    // visitObjectForEAttribute(obj, features, dataTypes, true);
    // }
    // }
    // }

    // // visit all the attributes of the given EObject and insert them to the
    // // cache
    // public void visitObjectForEAttribute(EObject obj, Set<EStructuralFeature> features, Set<EDataType> dataTypes,
    // boolean isInsertion) {
    // if (obj != null) {
    // for (EAttribute attr : obj.eClass().getEAllAttributes()) {
    // final EDataType eAttributeType = attr.getEAttributeType();
    // if (navigationHelper.isInWildcardMode() || features.contains(attr) || dataTypes.contains(eAttributeType)) {
    // if (attr.isMany()) {
    // Collection<Object> values = (Collection<Object>) obj.eGet(attr);
    // for (Object value : values) {
    // visitAttributeValue(obj, features, dataTypes,
    // isInsertion, attr, eAttributeType, value);
    // }
    // } else {
    // Object value = obj.eGet(attr);
    // if (value != null) {
    // visitAttributeValue(obj, features, dataTypes,
    // isInsertion, attr, eAttributeType, value);
    // }
    // }
    // }
    // }
    // }
    // }

}
