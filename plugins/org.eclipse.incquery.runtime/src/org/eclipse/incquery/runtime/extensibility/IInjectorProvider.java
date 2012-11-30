/*******************************************************************************
 * Copyright (c) 2010-2012, Zoltan Ujhelyi, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Zoltan Ujhelyi - initial API and implementation
 *******************************************************************************/
package org.eclipse.incquery.runtime.extensibility;

import com.google.inject.Injector;

/**
 * Interface for providing external Guice modules for EMF-IncQuery
 * 
 * @author Zoltan Ujhelyi
 * 
 */
public interface IInjectorProvider {

    public Injector getInjector();
}
