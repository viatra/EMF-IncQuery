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

import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.emf.codegen.ecore.genmodel.GenModel;
import org.eclipse.emf.codegen.ecore.genmodel.GenPackage;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;

import com.google.common.collect.Maps;

public class EcoreGenmodelRegistry {

    private static final String EPACKAGE_EXTENSION_ID = "org.eclipse.emf.ecore.generated_package";
    private static final String GENMODEL_ATTRIBUTE = "genModel";
    private static final String URI_ATTRIBUTE = "uri";
    private Map<String, String> genmodelUriMap = Maps.newHashMap();
    private Map<String, GenPackage> genpackageMap = Maps.newHashMap();
    private Logger logger;

    public EcoreGenmodelRegistry(Logger logger) {
        this.logger = logger;

        if (Platform.getExtensionRegistry() == null) {
            return;
        }
        IConfigurationElement[] packages = Platform.getExtensionRegistry().getConfigurationElementsFor(
                EPACKAGE_EXTENSION_ID);
        for (IConfigurationElement packageExtension : packages) {
            if (packageExtension.isValid()) {
                String genmodelUri = packageExtension.getAttribute(GENMODEL_ATTRIBUTE);
                if (genmodelUri != null && !genmodelUri.isEmpty()) {
                    String uri = packageExtension.getAttribute(URI_ATTRIBUTE);
                    if (URI.createURI(genmodelUri).isRelative()) {
                        genmodelUriMap.put(uri, String.format("platform:/plugin/%s/%s", packageExtension
                                .getContributor().getName(), genmodelUri));
                    } else {
                        genmodelUriMap.put(uri, genmodelUri);
                    }
                }
            }
        }
    }

    public GenPackage findGenPackage(String nsURI, ResourceSet set) {
        if (!genpackageMap.containsKey(nsURI)) {
            if (!genmodelUriMap.containsKey(nsURI)) {
                return null;
            }
            GenPackage genPackage = loadGenPackage(nsURI, genmodelUriMap.get(nsURI), set);
            if (genPackage != null) {
                genpackageMap.put(nsURI, genPackage);
            }
            return genPackage;
        }
        return genpackageMap.get(nsURI);
    }

    private GenPackage loadGenPackage(String nsURI, String genmodelUri, ResourceSet set) {
        try {
            URI uri = URI.createURI(genmodelUri);
            if (uri.isRelative()) {
                uri = URI.createPlatformPluginURI(genmodelUri, true);
            }
            Resource resource = set.getResource(uri, true);
            TreeIterator<EObject> it = resource.getAllContents();
            while (it.hasNext()) {
                EObject object = it.next();
                if (object instanceof GenPackage) {
                    if (((GenPackage) object).getNSURI().equals(nsURI)) {
                        return (GenPackage) object;
                    } else if (object instanceof GenModel) {
                        it.prune();
                    }
                }
            }
        } catch (RuntimeException ex) {
            logger.error("Error while retrieving genmodel of EPackage " + nsURI + " from location: " + genmodelUri, ex);
        }
        return null;
    }
}
