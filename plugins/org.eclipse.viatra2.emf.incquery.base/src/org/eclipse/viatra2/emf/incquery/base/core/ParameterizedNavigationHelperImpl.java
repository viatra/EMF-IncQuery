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
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Callable;

import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.viatra2.emf.incquery.base.api.ParameterizedNavigationHelper;
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
	
	
	
	@Override
	public void registerEStructuralFeatures(Set<EStructuralFeature> features) {
		if (features != null) {
			if (delayTraversals) {
				delayedFeatures.addAll(features);
			} else {
				observedFeatures.addAll(features);
				visitor.visitModel(notifier, features, null);
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
				observedClasses.addAll(classes);
				visitor.visitModel(notifier, null, classes);
			}
		}
	}

	@Override
	public void unregisterEClasses(Set<EClass> classes) {
		if (classes != null) {
			observedClasses.removeAll(classes);
			delayedClasses.removeAll(classes);
			for (EClass c : classes) {
				contentAdapter.instanceMap.remove(c);
			}
		}
	}
	
	@Override
	public <V> V coalesceTraversals(Callable<V> callable) throws InvocationTargetException {
		if(delayTraversals) 
			throw new UnsupportedOperationException("Coalescing EMF model traversals in EMF-IncQuery base is not reentrant.");
		
		delayedClasses = new HashSet<EClass>();
		delayedFeatures = new HashSet<EStructuralFeature>();
		
		V result = null;
		try {
			try {
				delayTraversals = true;
				result = callable.call();
			} finally {
				delayTraversals = false;
				if (!delayedClasses.isEmpty() || !delayedFeatures.isEmpty()) {
					observedClasses.addAll(delayedClasses);
					observedFeatures.addAll(delayedFeatures);
					
					// make copies and clean original accumulators, for the rare case that a coalesced 
					// 	traversal is invoked during visitation, e.g. by a derived feature implementation
					final HashSet<EClass> toGatherClasses = new HashSet<EClass>(delayedClasses);
					final HashSet<EStructuralFeature> toGatherFeatures = new HashSet<EStructuralFeature>(delayedFeatures);
					delayedFeatures.clear();
					delayedClasses.clear();
					
					visitor.visitModel(notifier, toGatherFeatures, toGatherClasses);			
				}
			}
		} catch (Exception e) {
			throw new InvocationTargetException(e);
		}
		return result;
	}
}
