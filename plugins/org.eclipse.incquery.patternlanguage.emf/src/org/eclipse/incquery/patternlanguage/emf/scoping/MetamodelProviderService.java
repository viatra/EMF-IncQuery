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
package org.eclipse.incquery.patternlanguage.emf.scoping;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.incquery.patternlanguage.emf.EcoreGenmodelRegistry;
import org.eclipse.xtext.naming.IQualifiedNameConverter;
import org.eclipse.xtext.naming.QualifiedName;
import org.eclipse.xtext.resource.EObjectDescription;
import org.eclipse.xtext.resource.IEObjectDescription;
import org.eclipse.xtext.scoping.IScope;
import org.eclipse.xtext.scoping.impl.SimpleScope;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.inject.Inject;

public class MetamodelProviderService implements IMetamodelProvider {

	@Inject
	private Logger logger;
	
	@Inject
	private IQualifiedNameConverter qualifiedNameConverter;
	
	private EcoreGenmodelRegistry genmodelRegistry; 

	protected EcoreGenmodelRegistry getGenmodelRegistry() {
		if (genmodelRegistry == null)
			genmodelRegistry = new EcoreGenmodelRegistry(logger);
		return genmodelRegistry;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.viatra2.patternlanguage.scoping.IMetamodelProvider#
	 * getAllMetamodelObjects()
	 */
	@Override
	public IScope getAllMetamodelObjects(EObject context) {
		final Map<String, EPackage> metamodelMap = getMetamodelMap();
		Set<String> packageURIs = new HashSet<String>(
				metamodelMap.keySet());
		Iterable<IEObjectDescription> metamodels = Iterables.transform(packageURIs,
				new Function<String, IEObjectDescription>() {
					public IEObjectDescription apply(String from) {
						EPackage ePackage = metamodelMap.get(from);
						// InternalEObject proxyPackage = (InternalEObject)
						// EcoreFactory.eINSTANCE.createEPackage();
						// proxyPackage.eSetProxyURI(URI.createURI(from));
						QualifiedName qualifiedName = qualifiedNameConverter
								.toQualifiedName(from);
						// return EObjectDescription.create(qualifiedName,
						// proxyPackage,
						// Collections.singletonMap("nsURI", "true"));
						return EObjectDescription.create(qualifiedName,
								ePackage,
								Collections.singletonMap("nsURI", "true"));
					}
				});
		return new SimpleScope(IScope.NULLSCOPE, metamodels);
	}

	protected Map<String, EPackage> getMetamodelMap(){
		Map<String, EPackage> packageMap = Maps.newHashMap();
		Set<String> nsURISet = Sets.newHashSet(EPackage.Registry.INSTANCE.keySet());
		for (String key : nsURISet) {
			packageMap.put(key, EPackage.Registry.INSTANCE.getEPackage(key));
		}
		return packageMap;

	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.viatra2.patternlanguage.scoping.IMetamodelProvider#getPackage
	 * (java.lang.String)
	 */
	@Override
	public EPackage loadEPackage(String packageUri, ResourceSet resourceSet) {
		if (EPackage.Registry.INSTANCE.containsKey(packageUri)) {
			return EPackage.Registry.INSTANCE.getEPackage(packageUri);
		}
		URI uri = null;
		try {
			 uri = URI.createURI(packageUri);
			if (uri.fragment() == null) {
				Resource resource = resourceSet.getResource(uri, true);
				return (EPackage) resource.getContents().get(0);
			}
			return (EPackage) resourceSet.getEObject(uri, true);
		} catch(RuntimeException ex) {
			if (uri != null && uri.isPlatformResource()) {
				String platformString = uri.toPlatformString(true);
				URI platformPluginURI = URI.createPlatformPluginURI(platformString, true);
				return loadEPackage(platformPluginURI.toString(), resourceSet);
			}
			logger.trace("Cannot load package with URI '" + packageUri + "'", ex);
			return null;
		}
	}

	@Override
	public boolean isGeneratedCodeAvailable(EPackage ePackage, ResourceSet set) {
		return getGenmodelRegistry().findGenPackage(ePackage.getNsURI(), set) != null;
	}
}
