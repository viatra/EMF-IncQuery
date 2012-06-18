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

import org.eclipse.emf.ecore.EStructuralFeature.Setting;

/**
 * Interface for observing {@link org.eclipse.emf.ecore.EStructuralFeature.Setting} insertion and deletion.
 * 
 * @author Tamas Szabo
 *
 */
public interface FeatureListener {

	/**
	 * Called when the given setting appeared under the given Notifier instance.
	 *  
	 * @param setting the setting instance
	 */
	public void featureInserted(Setting setting);

	/**
	 * Called when the given setting disappeared under the given Notifier instance.
	 *  
	 * @param setting the setting instance
	 */
	public void featureDeleted(Setting setting);
}
