package org.eclipse.viatra2.emf.incquery.runtime.util;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.URIConverter;
import org.eclipse.viatra2.emf.incquery.runtime.exception.IncQueryRuntimeException;
import org.eclipse.viatra2.emf.incquery.runtime.internal.XtextInjectorProvider;
import org.eclipse.viatra2.patternlanguage.IResourceSetPreparer;
import org.eclipse.xtext.common.types.access.ClasspathTypeProviderFactory;

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
		return getGlobalXmiResource(bundleName, null);
	}

	/**
	 * Returns the global EIQ resource (XMI), that is hosted in the given bundle.
	 * If something happened during model load, an exception is thrown.
	 * @param bundleName
	 * @param loadParameters parameters for the resource loading
	 * @return the global xmi resource
	 * @see {@link ResourceSet#getResource(URI, boolean)}.
	 */
	public static Resource getGlobalXmiResource(String bundleName, IResourceSetPreparer preparer) {
		ResourceSet set = prepareXtextResource();
		if(preparer != null) {
			preparer.prepareResourceSet(set);
		}
		return set.getResource(getGlobalXmiResourceURI(bundleName), true);
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
	 * Returns the URI for global XMI Model URI located at path queries/globalEiqModel.xmi in the given bundle/project.
	 * First tries to resolve the path with platformResource (in workspace), 
	 * if not found, uses the platformPluginURI (in bundles), if not found in here either, throws {@link IncQueryRuntimeException}.
	 * @param bundleName - workspace project name or platform bundle name.
	 * @return
	 * @throws IncQueryRuntimeException - when the global XMI model is not found in bundle/project.
	 */
	public static URI getGlobalXmiResourceURI(String bundleName) {
		URI resourceURI = resolvePlatformURI(String.format("%s/%s/%s",
				bundleName, XMI_OUTPUT_FOLDER, GLOBAL_EIQ_FILENAME));
		if (resourceURI != null) {
			return resourceURI;
		}
		throw new IncQueryRuntimeException(
				String.format("EMF-IncQuery pattern storage %s not found in bundle/project: %s", GLOBAL_EIQ_FILENAME, bundleName),
				"Missing " + GLOBAL_EIQ_FILENAME);
	}

	/**
	 * Returns the globalXmiModel path.
	 * @return
	 */
	public static String getGlobalXmiFilePath() {
		return String.format("%s/%s", XmiModelUtil.XMI_OUTPUT_FOLDER, XmiModelUtil.GLOBAL_EIQ_FILENAME);
	}
	
	/**
	 * Returns the EMF URI for the given platform URI.
	 * First tries to resolve the path with platformResource (in workspace), 
	 * if not found, uses the platformPluginURI (in bundles).
	 * @param platformURI - the URI to resolve.
	 * @return
	 */
	public static URI resolvePlatformURI(String platformURI) {
		URI resourceURI = URI.createPlatformResourceURI(platformURI, true);
		if (URIConverter.INSTANCE.exists(resourceURI, null)) {
			return resourceURI;
		}
		resourceURI = URI.createPlatformPluginURI(platformURI, true);
		if (URIConverter.INSTANCE.exists(resourceURI, null)) {
			return resourceURI;
		}
		return null;
	}
}

