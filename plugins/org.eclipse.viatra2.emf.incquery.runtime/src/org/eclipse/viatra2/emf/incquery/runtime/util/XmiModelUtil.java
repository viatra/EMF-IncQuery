package org.eclipse.viatra2.emf.incquery.runtime.util;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.viatra2.emf.incquery.runtime.IncQueryRuntimePlugin;

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
		ResourceSet set = IncQueryRuntimePlugin.getDefault().getInjector().getInstance(ResourceSet.class);
		Resource globalXmiResource = set.getResource(getGlobalEiqModelUri(bundleName), true);;
		return globalXmiResource;
	}
	
	/**
	 * Creates a platformplugin URI from bundleName and default location of the global EIQ model file path
	 * @param bundleName
	 * @return
	 */
	public static URI getGlobalEiqModelUri(String bundleName) {
		return URI.createPlatformResourceURI(String.format("%s/%s/%s",
				bundleName, XMI_OUTPUT_FOLDER, GLOBAL_EIQ_FILENAME), true);
	}
	
}
