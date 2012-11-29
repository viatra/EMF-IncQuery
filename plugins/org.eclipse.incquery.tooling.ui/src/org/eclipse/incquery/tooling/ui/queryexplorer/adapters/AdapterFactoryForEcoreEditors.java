/*******************************************************************************
 * Copyright (c) 2010-2012, Andras Okros, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Andras Okros - initial API and implementation
 *******************************************************************************/
package org.eclipse.incquery.tooling.ui.queryexplorer.adapters;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.edit.domain.IEditingDomainProvider;

/**
 * This AdapterFactory is responsible for processing the default EMF model inputs, and return the underlying ResourceSet
 * instances from it.
 */
@SuppressWarnings("rawtypes")
public class AdapterFactoryForEcoreEditors implements IAdapterFactory {

    @Override
    public Object getAdapter(Object adaptableObject, Class adapterType) {
        if (adapterType == ResourceSet.class && adaptableObject instanceof IEditingDomainProvider) {
            IEditingDomainProvider editingDomainProvider = (IEditingDomainProvider) adaptableObject;
            return editingDomainProvider.getEditingDomain().getResourceSet();
        }
        return null;
    }

    @Override
    public Class[] getAdapterList() {
        return new Class[] { ResourceSet.class };
    }

}
