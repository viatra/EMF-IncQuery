package org.eclipse.viatra2.patternlanguage.scoping;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.eclipse.emf.codegen.ecore.genmodel.GenPackage;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.viatra2.patternlanguage.EcoreGenmodelRegistry;
import org.eclipse.xtext.naming.IQualifiedNameConverter;
import org.eclipse.xtext.naming.QualifiedName;
import org.eclipse.xtext.resource.EObjectDescription;
import org.eclipse.xtext.resource.IEObjectDescription;
import org.eclipse.xtext.scoping.IScope;
import org.eclipse.xtext.scoping.impl.SimpleScope;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.inject.Inject;

public class MetamodelProviderService implements IMetamodelProvider {

	private final static class CastToEPackage implements
			Function<Object, EPackage> {
		@Override
		public EPackage apply(Object obj) {
			Preconditions.checkArgument(obj instanceof EPackage);
			return (EPackage) obj;
		}
	}

	@Inject
	private Logger logger;
	
	@Inject
	private IQualifiedNameConverter qualifiedNameConverter;
	
	private EcoreGenmodelRegistry genmodelRegistry = new EcoreGenmodelRegistry();

	protected EcoreGenmodelRegistry getGenmodelRegistry() {
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
		return Maps.transformValues(EPackage.Registry.INSTANCE,
				new CastToEPackage());
		
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
		return genmodelRegistry.findGenPackage(ePackage.getNsURI(), set) != null;
	}
}
