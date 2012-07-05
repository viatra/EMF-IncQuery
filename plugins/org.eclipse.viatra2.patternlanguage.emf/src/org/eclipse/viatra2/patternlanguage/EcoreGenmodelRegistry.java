package org.eclipse.viatra2.patternlanguage;

import java.util.Map;

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
	Map<String, String> genmodelUriMap = Maps.newHashMap();
	Map<String, GenPackage> genpackageMap = Maps.newHashMap();

	public EcoreGenmodelRegistry() {
		if (Platform.getExtensionRegistry() == null) return;
		IConfigurationElement[] packages = Platform.getExtensionRegistry()
				.getConfigurationElementsFor(EPACKAGE_EXTENSION_ID);
		for (IConfigurationElement packageExtension : packages) {
			if (packageExtension.isValid()) {
				String genmodelUri = packageExtension
						.getAttribute(GENMODEL_ATTRIBUTE);
				if (genmodelUri != null && !genmodelUri.isEmpty()) {
					String uri = packageExtension.getAttribute(URI_ATTRIBUTE);
					if (URI.createURI(genmodelUri).isRelative()) {
						genmodelUriMap.put(uri, String.format("/%s/%s",
								packageExtension.getContributor().getName(),
								genmodelUri));
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
		URI uri = URI.createURI(genmodelUri);
		if (uri.isRelative()) {
			uri = URI.createPlatformPluginURI(genmodelUri, true);
		}
		Resource resource = set.getResource(uri, true);
		TreeIterator<EObject> it =  resource.getAllContents();
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
		return null;
	}
}
