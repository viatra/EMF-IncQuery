/*******************************************************************************
 * Copyright (c) 2010-2012, Gabor Bergmann, Abel Hegedus, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Gabor Bergmann, Abel Hegedus - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra2.emf.incquery.runtime.extensibility;

import org.eclipse.incquery.emf.incquery.runtime.exception.IncQueryException;
import org.eclipse.viatra2.emf.incquery.runtime.api.IMatcherFactory;

/**
 * Provides a MatcherFactory.
 * Used e.g. as a plug-in extension.
 * 
 * @author Bergmann Gabor
 *
 */
public interface IMatcherFactoryProvider<Factory extends IMatcherFactory<?>> {
	public Factory get() throws IncQueryException;
}
