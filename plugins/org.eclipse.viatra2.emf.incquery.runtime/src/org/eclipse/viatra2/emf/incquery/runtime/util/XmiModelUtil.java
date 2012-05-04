package org.eclipse.viatra2.emf.incquery.runtime.util;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.URIConverter;
import org.eclipse.viatra2.emf.incquery.runtime.IncQueryRuntimePlugin;
import org.eclipse.viatra2.emf.incquery.runtime.exception.IncQueryRuntimeException;
import org.eclipse.viatra2.emf.incquery.runtime.internal.XtextInjectorProvider;

import com.google.inject.Injector;

/**
 * Utility class for loading Global XMI model on path queries/globalEiqModel.xmi.
 * @author Mark Czotter
 *
 */
public class XmiModelUtil {

	public static final String XMI_OUTPUT_FOLDER = "queries";
	public static final String GLOBAL_EIQ_FILENAME = "globalEiqModel.xmi";
	
	/**
	 * Returns the global EIQ resource (XMI), that is hosted in the given bundle.
	 * If something happened during model load, an exception is thrown.
	 * @param bundleName
	 * @return the global xmi resource
	 * @see {@link ResourceSet#getResource(URI, boolean)}.
	 */
	public static Resource getGlobalXmiResource(String bundleName) {
		Injector injector = XtextInjectorProvider.INSTANCE.getInjector();
		ResourceSet set = injector.getInstance(ResourceSet.class);
		Resource globalXmiResource = set.getResource(getGlobalXmiResourceURI(bundleName), true);;
		return globalXmiResource;
	}
	
	/**
	 * Returns the URI for global XMI Model URI located at path queries/globalEiqModel.xmi in the given bundle/project.
	 * First tries to resolve the path with platformResource (in workspace), 
	 * if not found, uses the platformPluginURI (in bundles), if not found in here either, throws {@link IncQueryRuntimeException}.
	 * @param bundleName - workspace project name or platform bundle name.
	 * @return
	 * @throws IncQueryRuntimeException - when the global XMI model is not found in bundle/project.
	 */
	public static URI getGlobalXmiResourceURI(String bundleName) {
		URI resourceURI = URI.createPlatformResourceURI(String.format("%s/%s/%s",
				bundleName, XMI_OUTPUT_FOLDER, GLOBAL_EIQ_FILENAME), true);
		if (URIConverter.INSTANCE.exists(resourceURI, null)) {
			return resourceURI;
		}
		resourceURI = URI.createPlatformPluginURI(String.format("%s/%s/%s",
				bundleName, XMI_OUTPUT_FOLDER, GLOBAL_EIQ_FILENAME), true);
		if (URIConverter.INSTANCE.exists(resourceURI, null)) {
			return resourceURI;
		}
		throw new IncQueryRuntimeException("Global XMI resource not found in bundle/project: " + bundleName);
	}
	
}

