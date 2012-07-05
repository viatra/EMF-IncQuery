/*******************************************************************************
 * Copyright (c) 2010-2012, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Tamas Szabo, Gabor Bergmann - initial API and implementation
 *******************************************************************************/

package org.eclipse.viatra2.emf.incquery.base.core;


import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Callable;

import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.viatra2.emf.incquery.base.api.ParameterizedNavigationHelper;
import org.eclipse.viatra2.emf.incquery.base.comprehension.EMFModelComprehension;
import org.eclipse.viatra2.emf.incquery.base.exception.IncQueryBaseException;

public class ParameterizedNavigationHelperImpl extends NavigationHelperImpl implements ParameterizedNavigationHelper {
	
	public ParameterizedNavigationHelperImpl(Notifier notifier) throws IncQueryBaseException {
		super(notifier, NavigationHelperType.REGISTER);
	}
	
	/**
	 * Feature registration and model traversal is delayed while true
	 */
	protected boolean delayTraversals = false;
	/**
	 * Classes to be registered once the coalescing period is over
	 */
	protected Set<EClass> delayedClasses;
	/**
	 * EStructuralFeatures to be registered once the coalescing period is over
	 */
	protected Set<EStructuralFeature> delayedFeatures;
	/**
	 * EDataTypes to be registered once the coalescing period is over
	 */
	protected Set<EDataType> delayedDataTypes;
	
	private Set<EClass> noClass() { return Collections.emptySet(); };
	private Set<EDataType> noDataType() { return Collections.emptySet(); };
	private Set<EStructuralFeature> noFeature() { return Collections.emptySet(); };
	
	
	<T> Set<T> setMinus(Set<T> a, Set<T> b) {
		Set<T> result = new HashSet<T>(a);
		result.removeAll(b);
		return result;
	}
	
	@Override
	public void registerEStructuralFeatures(Set<EStructuralFeature> features) {
		if (features != null) {
			if (delayTraversals) {
				delayedFeatures.addAll(features);
			} else {
				features = setMinus(features, observedFeatures);

				observedFeatures.addAll(features);
				final NavigationHelperVisitor visitor = 
						new NavigationHelperVisitor.TraversingVisitor(this, features, noClass(), noClass(), noDataType());
				traverse(visitor);
			}
		}
	}



	@Override
	public void unregisterEStructuralFeatures(Set<EStructuralFeature> features) {
		if (features != null) {
			observedFeatures.removeAll(features);
			delayedFeatures.removeAll(features);
			for (EStructuralFeature f : features) {
				for (Object key : contentAdapter.featureMap.keySet()) {
					contentAdapter.featureMap.get(key).remove(f);
					// TODO proper notification
				}
			}
		}
	}

	@Override
	public void registerEClasses(Set<EClass> classes) {
		if (classes != null) {
			if (delayTraversals) {
				delayedClasses.addAll(classes);
			} else {
				classes = setMinus(classes, directlyObservedClasses);
				
				final HashSet<EClass> oldClasses = new HashSet<EClass>(directlyObservedClasses);
				startObservingClasses(classes);		
				final NavigationHelperVisitor visitor = 
						new NavigationHelperVisitor.TraversingVisitor(this, noFeature(), classes, oldClasses, noDataType());
				traverse(visitor);
			}
		}
	}
	/**
	 * @param classes
	 */
	protected void startObservingClasses(Set<EClass> classes) {
		directlyObservedClasses.addAll(classes);
		getAllObservedClasses().addAll(classes);
		for (EClass eClass : classes) {
			final Set<EClass> subTypes = NavigationHelperContentAdapter.subTypeMap.get(eClass);
			if (subTypes != null) {
				allObservedClasses.addAll(subTypes);
			}					
		}
	}

	@Override
	public void unregisterEClasses(Set<EClass> classes) {
		if (classes != null) {
			directlyObservedClasses.removeAll(classes);
			allObservedClasses = null;
			delayedClasses.removeAll(classes);
			for (EClass c : classes) {
				contentAdapter.instanceMap.remove(c);
			}
		}
	}

	@Override
	public void registerEDataTypes(Set<EDataType> dataTypes) {
		if (dataTypes != null) {
			if (delayTraversals) {
				delayedDataTypes.addAll(dataTypes);
			} else {
				dataTypes = setMinus(dataTypes, observedDataTypes);
				
				observedDataTypes.addAll(dataTypes);
				final NavigationHelperVisitor visitor = 
						new NavigationHelperVisitor.TraversingVisitor(this, noFeature(), noClass(), noClass(), dataTypes);
				traverse(visitor);

			}
		}
	}

	@Override
	public void unregisterEDataTypes(Set<EDataType> dataTypes) {
		if (dataTypes != null) {
			observedDataTypes.removeAll(dataTypes);
			delayedDataTypes.removeAll(dataTypes);
			for (EDataType dt : dataTypes) {
				contentAdapter.dataTypeMap.remove(dt);
			}
		}
	}
	
	@Override
	public <V> V coalesceTraversals(Callable<V> callable) throws InvocationTargetException {
		if(delayTraversals) 
			throw new UnsupportedOperationException("Coalescing EMF model traversals in EMF-IncQuery base is not reentrant.");
		
		delayedClasses = new HashSet<EClass>();
		delayedFeatures = new HashSet<EStructuralFeature>();
		delayedDataTypes = new HashSet<EDataType>(); 
		
		V result = null;
		try {
			try {
				delayTraversals = true;
				result = callable.call();
			} finally {
				delayTraversals = false;
				
				delayedFeatures = setMinus(delayedFeatures, observedFeatures);
				delayedClasses = setMinus(delayedClasses, directlyObservedClasses);
				delayedDataTypes = setMinus(delayedDataTypes, observedDataTypes);
				
				boolean classesWarrantTraversal = !setMinus(delayedClasses, getAllObservedClasses()).isEmpty();

				if (!delayedClasses.isEmpty() || !delayedFeatures.isEmpty() || !delayedDataTypes.isEmpty()) {
					final HashSet<EClass> oldClasses = new HashSet<EClass>(directlyObservedClasses);
					startObservingClasses(delayedClasses);
					observedFeatures.addAll(delayedFeatures);
					observedDataTypes.addAll(delayedDataTypes);
					
					// make copies and clean original accumulators, for the rare case that a coalesced 
					// 	traversal is invoked during visitation, e.g. by a derived feature implementation
					final HashSet<EClass> toGatherClasses = new HashSet<EClass>(delayedClasses);
					final HashSet<EStructuralFeature> toGatherFeatures = new HashSet<EStructuralFeature>(delayedFeatures);
					final HashSet<EDataType> toGatherDataTypes = new HashSet<EDataType>(delayedDataTypes);
					delayedFeatures.clear();
					delayedClasses.clear();
					delayedDataTypes.clear();
					
					if (classesWarrantTraversal || !toGatherFeatures.isEmpty() || !toGatherDataTypes.isEmpty()) {
						final NavigationHelperVisitor visitor = 
								new NavigationHelperVisitor.TraversingVisitor(this, toGatherFeatures, toGatherClasses, oldClasses, toGatherDataTypes);
						traverse(visitor);
					}
				}
			}
		} catch (Exception e) {
			throw new InvocationTargetException(e);
		}
		return result;
	}
	
	private void traverse(final NavigationHelperVisitor visitor) {
		EMFModelComprehension.visitModel(visitor, notifier);
		for (Notifier additional : additionalRoots) {
			EMFModelComprehension.visitModel(visitor, additional);		
		}
		runAfterUpdateCallbacks();
	}
	
}
