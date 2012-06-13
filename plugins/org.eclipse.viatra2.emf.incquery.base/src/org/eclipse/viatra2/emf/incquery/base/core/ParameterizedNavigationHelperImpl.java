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


import java.util.Set;

import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.viatra2.emf.incquery.base.api.ParameterizedNavigationHelper;
import org.eclipse.viatra2.emf.incquery.base.exception.IncQueryBaseException;

public class ParameterizedNavigationHelperImpl extends NavigationHelperImpl implements ParameterizedNavigationHelper {

	public ParameterizedNavigationHelperImpl(Notifier notifier) throws IncQueryBaseException {
		super(notifier, NavigationHelperType.REGISTER);
	}
	
	@Override
	public void registerEStructuralFeatures(Set<EStructuralFeature> features) {
		if (features != null) {
			observedFeatures.addAll(features);
			visitor.visitModel(notifier, features, null);
		}
	}

	@Override
	public void unregisterEStructuralFeatures(Set<EStructuralFeature> features) {
		if (features != null) {
			observedFeatures.removeAll(features);

			for (EStructuralFeature f : features) {
				if (f instanceof EAttribute) {
					for (Object key : contentAdapter.getAttrMap().keySet()) {
						contentAdapter.getAttrMap().get(key).remove(f);
					}
				}
				if (f instanceof EReference) {
					for (EObject key : contentAdapter.getRefMap().keySet()) {
						contentAdapter.getRefMap().get(key).remove(f);
					}
				}
			}
		}
	}

	@Override
	public void registerEClasses(Set<EClass> classes) {
		if (classes != null) {
			observedClasses.addAll(classes);
			visitor.visitModel(notifier, null, classes);
		}
	}

	@Override
	public void unregisterEClasses(Set<EClass> classes) {
		if (classes != null) {
			observedClasses.removeAll(classes);
		 
			for (EClass c : classes) {
				contentAdapter.getInstanceMap().remove(c);
			}
		}
	}
}
