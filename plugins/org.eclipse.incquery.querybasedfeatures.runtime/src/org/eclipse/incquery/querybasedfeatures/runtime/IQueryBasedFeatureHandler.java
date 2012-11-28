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

import java.util.List;

import org.eclipse.emf.common.util.EList;

/**
 * @author Abel Hegedus
 *
 */
public interface IQueryBasedFeatureHandler {

    public abstract Object getValue(Object source);

    public abstract int getIntValue(Object source);

    public abstract Object getSingleReferenceValue(Object source);

    public abstract List<?> getManyReferenceValue(Object source);

    public abstract EList getManyReferenceValueAsEList(Object source);

    /**
     * Called when getValue method is called for Iteration kind
     * 
     * @return the value of the feature
     */
    public abstract Object getValueIteration(Object source);

}