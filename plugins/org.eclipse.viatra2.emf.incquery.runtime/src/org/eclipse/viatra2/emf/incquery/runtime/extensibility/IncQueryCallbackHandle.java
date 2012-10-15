/*******************************************************************************
 * Copyright (c) 2010-2012, Bergmann Gabor, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Bergmann Gabor - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra2.emf.incquery.runtime.extensibility;

/**
 * Handle interface for removal of EMF-IncQuery matcher callbacks.
 * 
 * @author Bergmann Gabor
 *
 */
public interface IncQueryCallbackHandle {
	public void removeCallback();
}
