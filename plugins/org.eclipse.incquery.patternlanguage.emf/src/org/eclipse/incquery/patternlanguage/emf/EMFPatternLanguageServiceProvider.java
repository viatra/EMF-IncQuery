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
package org.eclipse.incquery.patternlanguage.emf;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.incquery.patternlanguage.emf.eMFPatternLanguage.EMFPatternLanguagePackage;
import org.eclipse.incquery.patternlanguage.patternLanguage.PatternLanguagePackage;
import org.eclipse.xtext.resource.IGlobalServiceProvider.ResourceServiceProviderImpl;
import org.eclipse.xtext.resource.IResourceServiceProvider;
import org.eclipse.xtext.resource.IResourceServiceProvider.Registry;

import com.google.inject.Inject;

/**
 * @author Zoltan Ujhelyi
 * 
 */
public class EMFPatternLanguageServiceProvider extends ResourceServiceProviderImpl {

    private final IResourceServiceProvider serviceProvider;

    @Inject
    public EMFPatternLanguageServiceProvider(Registry registry, IResourceServiceProvider serviceProvider) {
        super(registry, serviceProvider);
        this.serviceProvider = serviceProvider;
    }

    @Override
    public <T> T findService(EObject eObject, Class<T> serviceClazz) {
        Resource res = eObject.eResource();
        String nsURI = eObject.eClass().getEPackage().getNsURI();
        if (res == null
                && (nsURI.equals(PatternLanguagePackage.eNS_URI) || nsURI.equals(EMFPatternLanguagePackage.eNS_URI))) {
            T service = serviceProvider.get(serviceClazz);
            return service;
        } else {
            return super.findService(eObject, serviceClazz);
        }
    }
}
