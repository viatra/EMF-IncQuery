package org.eclipse.viatra2.patternlanguage.scoping;

import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.xtext.resource.IEObjectDescription;

public interface IMetamodelProvider {

	/**
	 * Returns a set of all available EPackages wrapped into
	 * {@link IEObjectDescription} for the use of scoping
	 * 
	 * @return
	 */
	Iterable<IEObjectDescription> getAllMetamodelObjects();

	/**
	 * Loads an EMF package from the nsURI or resource URI of the model, and
	 * uses the resource set given as the second parameter.
	 * 
	 * @param uri
	 * @param resourceSet
	 * @return the loaded EMF EPackage
	 */
	EPackage loadEPackage(String uri, ResourceSet resourceSet);

}