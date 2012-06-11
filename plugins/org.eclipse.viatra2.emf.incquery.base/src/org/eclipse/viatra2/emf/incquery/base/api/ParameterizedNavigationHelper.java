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

package org.eclipse.viatra2.emf.incquery.base.api;

import java.util.Set;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;

/**
 * A {@link ParameterizedNavigationHelper} works the same way as a {@link NavigationHelper} but 
 * it allows to manually register and unregister those EAttributes, EReferences and EClasses whose 
 * notifications will be handled incrementally.
 * 
 * @author Tamas Szabo
 *
 */
public interface ParameterizedNavigationHelper extends NavigationHelper {
	
	/**
	 * The notifications of the given features will be processed by the helper together with the others previously registered.
	 * Note that registering new features requires to visit the whole attached model.
	 * 
	 * @param features the set of features to observ
	 */
	public void registerEStructuralFeatures(Set<EStructuralFeature> features);
	
	/**
	 * The notifications of the given features will be ignored; however
	 * the ones belonging to the previously registered features are still processed.
	 * Note that when re-registering features that were previously observed the whole attached model needs to be visited again.
	 * 
	 * @param features the set of features whose notification will be ignored
	 */
	public void unregisterEStructuralFeatures(Set<EStructuralFeature> features);
	
	/**
	 * The notifications of the given classes will be processed by the helper together with others previously registered.
	 * Note that registering new classes requires to visit the whole attached model.
	 * 
	 * @param classes the set of classes to observ
	 */
	public void registerEClasses(Set<EClass> classes);
	
	/**
	 * The notifications of the given classes will be ignored; however
	 * the ones belonging to the previously registered classes are still processed.
	 * Note that when re-registering classes that were previously observed the whole attached model needs to be visited again.
	 * 
	 * @param classes the set of classes whose notification will be ignored
	 */
	public void unregisterEClasses(Set<EClass> classes);
}
