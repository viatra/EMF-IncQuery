package org.eclipse.viatra2.emf.incquery.runtime.util;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.viatra2.emf.incquery.runtime.IncQueryRuntimePlugin;

public class XmiModelUtil {

	public static final String XMI_OUTPUT_FOLDER = "queries";
	public static final String GLOBAL_EIQ_FILENAME = "globalEiqModel.xmi";
	
	/**
	 * Returns the global EIQ resource (XMI), that is hosted in the given bundle.
	 * If no resource is found, null is returned.
	 * @param bundleName, cant be null
	 * @return
	 */
	public static Resource getGlobalXmiResource(String bundleName) {
		Resource globalXmiResource = null;
		ResourceSet set = IncQueryRuntimePlugin.getDefault().getInjector().getInstance(ResourceSet.class);
		try { 
			globalXmiResource = set.getResource(getGlobalEiqModelUri(bundleName), true);
		} catch (Exception e) {
			System.err.println("Exception during Global XMi Resource load: " + e.getMessage());
			globalXmiResource = null;
		}
		return globalXmiResource;
	}
	
	/**
	 * Creates a platformplugin URI from bundleName and default location of the global EIQ model file path
	 * @param bundleName
	 * @return
	 */
	private static URI getGlobalEiqModelUri(String bundleName) {
		return URI.createPlatformResourceURI(String.format("%s/%s/%s",
				bundleName, XMI_OUTPUT_FOLDER, GLOBAL_EIQ_FILENAME), true);
	}
	
}
