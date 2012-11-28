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

package org.eclipse.incquery.runtime.internal;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.incquery.runtime.api.IncQueryEngine;
import org.eclipse.incquery.runtime.base.api.DataTypeListener;
import org.eclipse.incquery.runtime.base.api.FeatureListener;
import org.eclipse.incquery.runtime.base.api.InstanceListener;
import org.eclipse.incquery.runtime.base.api.NavigationHelper;
import org.eclipse.incquery.runtime.rete.boundary.IManipulationListener;
import org.eclipse.incquery.runtime.rete.boundary.PredicateEvaluatorNode;
import org.eclipse.incquery.runtime.rete.boundary.ReteBoundary;
import org.eclipse.incquery.runtime.rete.matcher.ReteEngine;
import org.eclipse.incquery.runtime.rete.network.Direction;

/**
 * A listener binding as Rete boundary to an eiqBase index
 * @author Bergmann Gábor
 *
 */
public class BaseIndexListener implements FeatureListener, InstanceListener, DataTypeListener, IManipulationListener {
	private final ReteBoundary<?> boundary;
	private final NavigationHelper baseIndex;
	
	/** 
	 * This reference is vital, to avoid the premature GC of the engine while the EMF model is still reachable.
	 * Retention path: EMF model -> IQBase -> BaseIndexListener -> IQEngine 
	 */
	@SuppressWarnings("unused")
	private final IncQueryEngine iqEngine;
	
	private final Set<EClass> classes = new HashSet<EClass>();
	private final Set<EDataType> dataTypes = new HashSet<EDataType>();
	private final Set<EStructuralFeature> features = new HashSet<EStructuralFeature>();
	
	/**
	 * @param boundary
	 */
	public BaseIndexListener(IncQueryEngine iqEngine, ReteEngine<?> engine, NavigationHelper baseIndex) {
		super();
		this.iqEngine = iqEngine;
		this.boundary = engine.getBoundary();
		this.baseIndex = baseIndex;
		engine.addDisconnectable(this);
		
	}
	
	public void ensure(EClass eClass) {
		if (classes.add(eClass)) {
			final Set<EClass> newClasses = Collections.singleton(eClass);
			if (!baseIndex.isInWildcardMode()) 
				baseIndex.registerEClasses(newClasses);
			baseIndex.registerInstanceListener(newClasses, this);
		}
	}
	public void ensure(EDataType eDataType) {
		if (dataTypes.add(eDataType)) {
			final Set<EDataType> newDataTypes = Collections.singleton(eDataType);
			if (!baseIndex.isInWildcardMode()) 
				baseIndex.registerEDataTypes(newDataTypes);
			baseIndex.registerDataTypeListener(newDataTypes, this);
		}
	}
	public void ensure(EStructuralFeature feature) {
		if (features.add(feature)) {
			final Set<EStructuralFeature> newFeatures = Collections.singleton(feature);
			if (!baseIndex.isInWildcardMode()) 
				baseIndex.registerEStructuralFeatures(newFeatures);
			baseIndex.registerFeatureListener(newFeatures, this);
		}
	}
	

	@Override
	public void instanceInserted(EClass clazz, EObject instance) {
		boundary.updateUnary(Direction.INSERT, instance, clazz);
		boundary.updateInstantiation(Direction.INSERT, clazz, instance);
	}

	@Override
	public void instanceDeleted(EClass clazz, EObject instance) {
		boundary.updateUnary(Direction.REVOKE, instance, clazz);
		boundary.updateInstantiation(Direction.REVOKE, clazz, instance);
	}
	
	@Override
	public void dataTypeInstanceInserted(EDataType type, Object instance) {
		boundary.updateUnary(Direction.INSERT, instance, type);
		boundary.updateInstantiation(Direction.INSERT, type, instance);
	}
	
	@Override
	public void dataTypeInstanceDeleted(EDataType type, Object instance) {
		boundary.updateUnary(Direction.REVOKE, instance, type);
		boundary.updateInstantiation(Direction.REVOKE, type, instance);
	}
	
	@Override
	public void featureInserted(EObject host, EStructuralFeature feature, Object value) {
		boundary.updateBinaryEdge(Direction.INSERT, host, value, feature);
	}

	@Override
	public void featureDeleted(EObject host, EStructuralFeature feature, Object value) {
		boundary.updateBinaryEdge(Direction.REVOKE, host, value, feature);
	}

	
	@Override
	public void registerSensitiveTerm(Object element,
			PredicateEvaluatorNode termEvaluatorNode) {
	}
	
	@Override
	public void unregisterSensitiveTerm(Object element,
			PredicateEvaluatorNode termEvaluatorNode) {
	}

	@Override
	public void disconnect() {
		baseIndex.unregisterFeatureListener(features, this); features.clear();
		baseIndex.unregisterInstanceListener(classes, this); classes.clear();
		baseIndex.unregisterDataTypeListener(dataTypes, this); dataTypes.clear();
	}
	
	
}
