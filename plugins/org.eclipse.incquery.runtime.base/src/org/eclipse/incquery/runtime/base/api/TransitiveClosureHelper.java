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

import org.eclipse.emf.ecore.EObject;
import org.eclipse.incquery.runtime.base.itc.igraph.ITcDataSource;

/**
 * The class can be used to compute the transitive closure of a given emf model, 
 * where the nodes will be the objects in the model and the edges will be represented by the references between them.
 * One must provide the set of references that the helper should treat as edges when creating an instance with the factory: 
 * only the notifications about these references will be handled.
 * 
 * @author Tamas Szabo
 *
 */
public interface TransitiveClosureHelper extends ITcDataSource<EObject> {

}
