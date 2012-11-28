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
package org.eclipse.incquery.runtime.base.api;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;

/**
 * Interface for observing insertion / deletion of EClass instances. 
 * Note that: EClass may be omitted 
 * 
 * @author Tamas Szabo
 *
 */
public interface InstanceListener {

	/**
	 * Called when the given instance appeared under the given Notifier instance.
	 * 
	 * @param clazz the EClass of the instance
	 * @param instance the EObject instance
	 */
	public void instanceInserted(EClass clazz, EObject instance);

	/**
	 * Called when the given instance disappeared under the given Notifier instance.
	 * 
	 * @param clazz the EClass of the instance
	 * @param instance the EObject instance
	 */
	public void instanceDeleted(EClass clazz, EObject instance);
}
