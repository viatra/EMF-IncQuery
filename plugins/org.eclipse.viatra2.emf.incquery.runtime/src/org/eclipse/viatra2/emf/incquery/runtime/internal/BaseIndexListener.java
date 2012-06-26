/*******************************************************************************
 * Copyright (c) 2004-2012 Gabor Bergmann and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Gabor Bergmann - initial API and implementation
 *******************************************************************************/

package org.eclipse.viatra2.emf.incquery.runtime.internal;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.viatra2.emf.incquery.base.api.FeatureListener;
import org.eclipse.viatra2.emf.incquery.base.api.InstanceListener;
import org.eclipse.viatra2.emf.incquery.base.api.ParameterizedNavigationHelper;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.boundary.IManipulationListener;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.boundary.PredicateEvaluatorNode;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.boundary.ReteBoundary;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.network.Direction;

/**
 * A listener binding as Rete boundary to an eiqBase index
 * @author Bergmann Gábor
 *
 */
public class BaseIndexListener implements FeatureListener, InstanceListener, IManipulationListener {
	private ReteBoundary<?> boundary;
	private ParameterizedNavigationHelper baseIndex;
	
	
	private Set<EClassifier> types = new HashSet<EClassifier>();
	private Set<EStructuralFeature> features = new HashSet<EStructuralFeature>();
	
	/**
	 * @param boundary
	 */
	public BaseIndexListener(ReteBoundary<?> boundary, ParameterizedNavigationHelper baseIndex) {
		super();
		this.boundary = boundary;
		this.baseIndex = baseIndex;
	}
	
	public void ensure(EClassifier classifier) {
		if (types.add(classifier)) {
			if (classifier instanceof EClass) {
				final Set<EClass> newClasses = Collections.singleton((EClass)classifier);
				baseIndex.registerEClasses(newClasses);
				baseIndex.registerInstanceListener(newClasses, this);
			} else throw new UnsupportedOperationException("EDatatypes not supproted yet");
		}
	}
	public void ensure(EStructuralFeature feature) {
		if (features.add(feature)) {
				final Set<EStructuralFeature> newFeatures = Collections.singleton(feature);
				baseIndex.registerEStructuralFeatures(newFeatures);
				baseIndex.registerFeatureListener(newFeatures, this);
		}
	}
	

	/* (non-Javadoc)
	 * @see org.eclipse.viatra2.emf.incquery.base.api.InstanceListener#instanceInserted(org.eclipse.emf.ecore.EClass, org.eclipse.emf.ecore.EObject)
	 */
	@Override
	public void instanceInserted(EClass clazz, EObject instance) {
		boundary.updateInstantiation(Direction.INSERT, clazz, instance);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.viatra2.emf.incquery.base.api.InstanceListener#instanceDeleted(org.eclipse.emf.ecore.EClass, org.eclipse.emf.ecore.EObject)
	 */
	@Override
	public void instanceDeleted(EClass clazz, EObject instance) {
		boundary.updateInstantiation(Direction.REVOKE, clazz, instance);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.viatra2.emf.incquery.base.api.FeatureListener#featureInserted(org.eclipse.emf.ecore.EObject, org.eclipse.emf.ecore.EStructuralFeature, java.lang.Object)
	 */
	@Override
	public void featureInserted(EObject host, EStructuralFeature feature, Object value) {
		boundary.updateBinaryEdge(Direction.INSERT, host, value, feature);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.viatra2.emf.incquery.base.api.FeatureListener#featureDeleted(org.eclipse.emf.ecore.EObject, org.eclipse.emf.ecore.EStructuralFeature, java.lang.Object)
	 */
	@Override
	public void featureDeleted(EObject host, EStructuralFeature feature, Object value) {
		boundary.updateBinaryEdge(Direction.REVOKE, host, value, feature);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.boundary.IManipulationListener#registerSensitiveTerm(java.lang.Object, org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.boundary.PredicateEvaluatorNode)
	 */
	@Override
	public void registerSensitiveTerm(Object element,
			PredicateEvaluatorNode termEvaluatorNode) {
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.boundary.IManipulationListener#unregisterSensitiveTerm(java.lang.Object, org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.boundary.PredicateEvaluatorNode)
	 */
	@Override
	public void unregisterSensitiveTerm(Object element,
			PredicateEvaluatorNode termEvaluatorNode) {
	}

	/* (non-Javadoc)
	 * @see org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.boundary.Disconnectable#disconnect()
	 */
	@Override
	public void disconnect() {
		baseIndex.unregisterFeatureListener(features, this); features.clear();
		Set<EClass> classes = new HashSet<EClass>(); for (EClassifier eClassifier : types) if (eClassifier instanceof EClass) classes.add((EClass) eClassifier);
		baseIndex.unregisterInstanceListener(classes, this); types.clear();
	}
	
}