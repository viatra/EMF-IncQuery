/*******************************************************************************
 * Copyright (c) 2010-2012, Mark Czotter, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Mark Czotter - initial API and implementation
 *   Andras Okros - extending the logic with separated plugin and resource calls
 *******************************************************************************/
package org.eclipse.incquery.runtime.util;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.URIConverter;
import org.eclipse.incquery.patternlanguage.emf.IResourceSetPreparer;
import org.eclipse.incquery.runtime.exception.IncQueryException;
import org.eclipse.incquery.runtime.internal.XtextInjectorProvider;
import org.eclipse.xtext.common.types.access.ClasspathTypeProviderFactory;

import com.google.inject.Injector;

/**
 * Utility class for loading Global XMI model on path queries/globalEiqModel.xmi.
 */
public class XmiModelUtil {

    public static final String XMI_OUTPUT_FOLDER = "queries";

    public static final String GLOBAL_EIQ_FILENAME = "globalEiqModel.xmi";

    /**
     * Returns the global EIQ resource (XMI), that is hosted in the given bundle. If something happened during model
     * load, an exception is thrown.
     * 
     * @param bundleName
     * @param optionEnum
     *            the option whether to use Resource or Plugin or both type of URIs
     * @return the global xmi resource
     * @throws IncQueryException
     *             if the XMI store is unavailable
     * @see {@link ResourceSet#getResource(URI, boolean)}
     */
    public static Resource getGlobalXmiResource(XmiModelUtilRunningOptionEnum optionEnum, String bundleName)
            throws IncQueryException {
        return getGlobalXmiResource(optionEnum, bundleName, null);
    }

    /**
     * Returns the global EIQ resource (XMI), that is hosted in the given bundle. If something happened during model
     * load, an exception is thrown.
     * 
     * @param bundleName
     * @param loadParameters
     *            parameters for the resource loading
     * @param optionEnum
     *            the option whether to use Resource or Plugin or both type of URIs
     * @return the global xmi resource
     * @throws IncQueryException
     *             if the XMI store is unavailable
     * @see {@link ResourceSet#getResource(URI, boolean)}
     */
    public static Resource getGlobalXmiResource(XmiModelUtilRunningOptionEnum optionEnum, String bundleName,
            IResourceSetPreparer preparer) throws IncQueryException {
        try {
            ResourceSet set = prepareXtextResource();
            if (preparer != null) {
                preparer.prepareResourceSet(set);
            }
            final URI globalXmiResourceURI = getGlobalXmiResourceURI(optionEnum, bundleName);
            return set.getResource(globalXmiResourceURI, true);
        } catch (Exception ex) {
            if (ex instanceof IncQueryException)
                throw (IncQueryException) ex;
            else
                throw new IncQueryException(
                        "An error occured while trying to load the generated patterns stored in bundle " + bundleName,
                        "Error loading generated patterns", ex);
        }
    }

    /**
     * Prepares an Xtext resource set with the registered injector of the EMF-IncQuery plugin
     * 
     * @return the Xtext injected resource set
     */
    public static ResourceSet prepareXtextResource() {
        Injector injector = XtextInjectorProvider.INSTANCE.getInjector();
        return prepareXtextResource(injector);
    }

    /**
     * Prepares an Xtext resource set with the given injector
     * 
     * @return the injected resource set
     */
    public static ResourceSet prepareXtextResource(Injector injector) {
        ResourceSet set = injector.getInstance(ResourceSet.class);
        ClasspathTypeProviderFactory cptf = injector.getInstance(ClasspathTypeProviderFactory.class);
        cptf.createTypeProvider(set);
        return set;
    }

    /**
     * Returns the URI for global XMI Model located at path queries/globalEiqModel.xmi in the given bundle/project.
     * First tries to resolve the path with platformResource (in workspace), if not found, uses the platformPluginURI
     * (in bundles), if not found in here either, throws {@link IncQueryException}.
     * 
     * @param bundleName
     *            workspace project name or platform bundle name.
     * @param optionEnum
     *            the option whether to use Resource or Plugin or both type of URIs
     * @return the EMF resource URI pointing to the XMI store of generated pattern
     * @throws IncQueryException
     *             when the global XMI model is not found in bundle/project
     */
    public static URI getGlobalXmiResourceURI(XmiModelUtilRunningOptionEnum optionEnum, String bundleName)
            throws IncQueryException {
        URI resourceURI = resolvePlatformURI(optionEnum,
                String.format("%s/%s/%s", bundleName, XMI_OUTPUT_FOLDER, GLOBAL_EIQ_FILENAME));
        if (resourceURI != null) {
            return resourceURI;
        }
        throw new IncQueryException(String.format("EMF-IncQuery pattern storage %s not found in bundle/project: %s",
                GLOBAL_EIQ_FILENAME, bundleName), "Missing " + GLOBAL_EIQ_FILENAME);
    }

    /**
     * Returns the globalXmiModel path.
     * 
     * @return
     */
    public static String getGlobalXmiFilePath() {
        return String.format("%s/%s", XmiModelUtil.XMI_OUTPUT_FOLDER, XmiModelUtil.GLOBAL_EIQ_FILENAME);
    }

    /**
     * First tries to resolve the path with platformResource (in workspace), if not found, uses the platformPluginURI
     * (in bundles).
     * 
     * @param platformURI
     *            the URI to resolve
     * @param optionEnum
     *            the option whether to use Resource or Plugin or both type of URIs
     * @return the EMF URI for the given platform URI
     */
    public static URI resolvePlatformURI(XmiModelUtilRunningOptionEnum optionEnum, String platformURI) {
        URI uri = resolvePlatformResourceURI(optionEnum, platformURI);
        if (uri != null) {
            return uri;
        }
        uri = resolvePlatformPluginURI(optionEnum, platformURI);
        if (uri != null) {
            return uri;
        }
        return null;
    }

    private static URI resolvePlatformResourceURI(XmiModelUtilRunningOptionEnum optionEnum, String platformURI) {
        if (XmiModelUtilRunningOptionEnum.BOTH.equals(optionEnum)
                || XmiModelUtilRunningOptionEnum.JUST_RESOURCE.equals(optionEnum)) {
            URI resourceURI = URI.createPlatformResourceURI(platformURI, true);
            if (URIConverter.INSTANCE.exists(resourceURI, null)) {
                return resourceURI;
            }
        }
        return null;
    }

    private static URI resolvePlatformPluginURI(XmiModelUtilRunningOptionEnum optionEnum, String platformURI) {
        if (XmiModelUtilRunningOptionEnum.BOTH.equals(optionEnum)
                || XmiModelUtilRunningOptionEnum.JUST_PLUGIN.equals(optionEnum)) {
            URI pluginURI = URI.createPlatformPluginURI(platformURI, true);
            if (URIConverter.INSTANCE.exists(pluginURI, null)) {
                return pluginURI;
            }
        }
        return null;
    }

}
