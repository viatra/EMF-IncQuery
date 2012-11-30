/*******************************************************************************
 * Copyright (c) 2010-2012, Mark Czotter, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Mark Czotter - initial API and implementation
 *******************************************************************************/

package org.eclipse.incquery.tooling.core.generator.util;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.xmi.impl.URIHandlerImpl;

public class EMFPatternURIHandler extends URIHandlerImpl {

    private final Map<URI, EPackage> uriToEPackageMap = new HashMap<URI, EPackage>();

    public EMFPatternURIHandler(Collection<EPackage> packages) {
        for (EPackage e : packages) {
            if (e.eResource() != null) {
                uriToEPackageMap.put(e.eResource().getURI(), e);
            }
        }
    }

    @Override
    public URI deresolve(URI uri) {
        if (uri.isPlatform()) {
            String fragment = uri.fragment();
            URI fragmentRemoved = uri.trimFragment();
            EPackage p = uriToEPackageMap.get(fragmentRemoved);
            if (p != null) {
                URI newURI = URI.createURI(p.getNsURI());
                newURI = newURI.appendFragment(fragment);
                return newURI;
            }
        }
        return super.deresolve(uri);
    }

}
