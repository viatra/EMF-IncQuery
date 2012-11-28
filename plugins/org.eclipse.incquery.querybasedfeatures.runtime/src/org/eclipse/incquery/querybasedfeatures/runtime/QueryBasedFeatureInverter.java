/*******************************************************************************
 * Copyright (c) 2010-2012, Abel Hegedus, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Abel Hegedus - initial API and implementation
 *******************************************************************************/
package org.eclipse.incquery.querybasedfeatures.runtime;

public interface QueryBasedFeatureInverter<ComputedType, StorageType> {
    /**
     * Return the storage value for the computed value.
     * 
     * @param computedValue
     * @return
     */
    StorageType invert(ComputedType computedValue);

    /**
     * Validate the computed value to ensure that inverting is possible
     * @param computedValue
     * @return
     */
    ComputedType validate(ComputedType computedValue);
}