package org.eclipse.viatra2.emf.incquery.tooling.generator.genmodel;

import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.emf.codegen.ecore.genmodel.GenPackage;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.viatra2.emf.incquery.core.project.IncQueryNature;
import org.eclipse.viatra2.emf.incquery.tooling.generator.generatorModel.GeneratorModelFactory;
import org.eclipse.viatra2.emf.incquery.tooling.generator.generatorModel.GeneratorModelReference;
import org.eclipse.viatra2.emf.incquery.tooling.generator.generatorModel.IncQueryGeneratorModel;
import org.eclipse.viatra2.patternlanguage.scoping.MetamodelProviderService;
import org.eclipse.xtext.common.types.access.jdt.IJavaProjectProvider;
import org.eclipse.xtext.naming.IQualifiedNameConverter;
import org.eclipse.xtext.naming.QualifiedName;
import org.eclipse.xtext.resource.EObjectDescription;
import org.eclipse.xtext.resource.IEObjectDescription;
import org.eclipse.xtext.scoping.IScope;
import org.eclipse.xtext.scoping.impl.FilteringScope;
import org.eclipse.xtext.scoping.impl.SimpleScope;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.inject.Inject;

public class GenModelMetamodelProviderService extends MetamodelProviderService
		implements IEiqGenmodelProvider {

	private static final class ParentScopeFilter implements
			Predicate<IEObjectDescription> {
		private final class NameTransformerFunction implements
				Function<IEObjectDescription, QualifiedName> {
			@Override
			public QualifiedName apply(IEObjectDescription desc) {
				Preconditions.checkNotNull(desc);
				
				return desc.getQualifiedName();
			}
		}

		private Iterable<IEObjectDescription> referencedPackages;

		public ParentScopeFilter(
				Iterable<IEObjectDescription> referencedPackages) {
			super();
			this.referencedPackages = referencedPackages;
		}

		public boolean apply(IEObjectDescription desc) {
			Preconditions.checkNotNull(desc);
			
			return !Iterables.contains(Iterables.transform(referencedPackages, new NameTransformerFunction
					()), desc.getQualifiedName());
		}
	}

	@Inject
	private IJavaProjectProvider projectProvider;
	@Inject
	private IQualifiedNameConverter qualifiedNameConverter;

	private URI getGenmodelURI(IProject project) {
		IFile file = project.getFile(IncQueryNature.IQGENMODEL);
		return URI.createPlatformResourceURI(file.getFullPath().toString(), false);
	}
	
	@Override
	public IScope getAllMetamodelObjects(EObject ctx) {
		Preconditions.checkNotNull(ctx, "Context is required");
		Iterable<IEObjectDescription> referencedPackages = Lists.newArrayList();
		IncQueryGeneratorModel generatorModel = getGeneratorModel(ctx);
		for (GeneratorModelReference ref : generatorModel.getGenmodels()) {

			Iterable<IEObjectDescription> packages = Iterables.transform(ref
					.getGenmodel().getGenPackages(),
					new Function<GenPackage, IEObjectDescription>() {
						public IEObjectDescription apply(GenPackage from) {
							Preconditions.checkNotNull(from);
							
							EPackage ePackage = from.getEcorePackage();
							QualifiedName qualifiedName = qualifiedNameConverter
									.toQualifiedName(ePackage.getNsURI());
							return EObjectDescription.create(qualifiedName,
									ePackage,
									Collections.singletonMap("nsURI", "true"));
						}
					});
			referencedPackages = Iterables.concat(referencedPackages, packages);
			}
		//The FilteringScope is used to ensure elements in eiq genmodel are not accidentally found in the parent version
		return new SimpleScope(new FilteringScope(
				super.getAllMetamodelObjects(ctx), new ParentScopeFilter(
						referencedPackages)), referencedPackages);
	}

	@Override
	public EPackage loadEPackage(final String packageUri, ResourceSet set) {
		GenPackage loadedPackage = findGenPackage(set, packageUri, false);
		if (loadedPackage != null) {
			return loadedPackage.getEcorePackage();
		}
		return super.loadEPackage(packageUri, set);
	}

	@Override
	public boolean isGeneratedCodeAvailable(EPackage ePackage, ResourceSet set) {
		return (findGenPackage(set, ePackage) != null)
				|| super.isGeneratedCodeAvailable(ePackage, set);
	}

	@Override
	public IncQueryGeneratorModel getGeneratorModel(EObject pattern) {
		Resource res = pattern.eResource();
		if (res != null && projectProvider != null) {
			ResourceSet set = res.getResourceSet();
			return getGeneratorModel(set);
		}
		throw new IllegalArgumentException(
				"The project of the context cannot be determined.");
	}

	public IncQueryGeneratorModel getGeneratorModel(IProject project) {
		return getGeneratorModel(project, new ResourceSetImpl());
	}
	
	public IncQueryGeneratorModel getGeneratorModel(ResourceSet set) {
		if (projectProvider != null) {
			IJavaProject javaProject = projectProvider.getJavaProject(set);
			if (javaProject != null) {
				return getGeneratorModel(javaProject.getProject(), set);
			}
		}
		return null;
	}

	@Override
	public IncQueryGeneratorModel getGeneratorModel(IProject project,
			ResourceSet set) {
		IFile file = project.getFile(IncQueryNature.IQGENMODEL);
		if (file.exists()) {
			URI uri = URI.createPlatformResourceURI(file.getFullPath().toString(), false);
			Resource resource = set.getResource(uri, true);
			if (!resource.getContents().isEmpty()) {
				return (IncQueryGeneratorModel) resource.getContents().get(0);
			}
		} 
		return GeneratorModelFactory.eINSTANCE.createIncQueryGeneratorModel();
	}

	@Override
	public void saveGeneratorModel(IProject project, IncQueryGeneratorModel generatorModel) throws IOException {
		Resource eResource = generatorModel.eResource();
		if (eResource != null) {
			eResource.save(Maps.newHashMap());
		} else {
			URI uri = getGenmodelURI(project);
			ResourceSet set = new ResourceSetImpl();
			Resource resource = set.createResource(uri);
			resource.getContents().add(generatorModel);
			resource.save(Maps.newHashMap());
		}
		
	}

	@Override
	public GenPackage findGenPackage(EObject ctx, final EPackage ePackage) {
		return findGenPackage(ctx, ePackage.getNsURI());
	}
	
	@Override
	public GenPackage findGenPackage(EObject ctx, final String packageNsUri) {
		IncQueryGeneratorModel eiqGenModel = getGeneratorModel(ctx);
		return findGenPackage(eiqGenModel, ctx.eResource().getResourceSet(), packageNsUri, true);
	}
	
	@Override
	public GenPackage findGenPackage(ResourceSet set, final EPackage ePackage) {
		return findGenPackage(set, ePackage.getNsURI());
	}
	
	@Override
	public GenPackage findGenPackage(ResourceSet set, final String packageNsUri) {
		IncQueryGeneratorModel eiqGenModel = getGeneratorModel(set);
		return findGenPackage(eiqGenModel, set, packageNsUri, true);		
	}
	
	private GenPackage findGenPackage(ResourceSet set, final String packageNsUri, boolean fallbackToPackageRegistry) {
		IncQueryGeneratorModel eiqGenModel = getGeneratorModel(set);
		return findGenPackage(eiqGenModel, set, packageNsUri, fallbackToPackageRegistry);
	}
	
	private GenPackage findGenPackage(IncQueryGeneratorModel eiqGenModel,
			ResourceSet set, final String packageNsUri,
			boolean fallbackToPackageRegistry) {
		// eiqGenModel is null if loading a pattern from the registry
		// in this case only fallback to package Registry works
		if (eiqGenModel != null) {
			Iterable<GenPackage> genPackageIterable = Lists.newArrayList();
			for (GeneratorModelReference genModel : eiqGenModel.getGenmodels()) {
				genPackageIterable = Iterables.concat(genPackageIterable,
						genModel.getGenmodel().getGenPackages());
			}
			Iterable<GenPackage> genPackages = Iterables.filter(
					genPackageIterable, new Predicate<GenPackage>() {
						public boolean apply(GenPackage genPackage) {
							Preconditions.checkNotNull(genPackage, "Checked genpackage must not be null");
							
							return packageNsUri.equals(genPackage
									.getEcorePackage().getNsURI());
						}
					});
			Iterator<GenPackage> it = genPackages.iterator();
			if (it.hasNext()) {
				return it.next();
			}
		}
		if (fallbackToPackageRegistry){
			return genmodelRegistry.findGenPackage(packageNsUri, set);
		}
		return null;
	}
	
	public boolean isGeneratorModelDefined(IProject project) {
		IFile file = project.getFile(IncQueryNature.IQGENMODEL);
		return file.exists();
	}
	
	/**
	 * Initializes and returns the IncQuery generator model for the selected project. If the model is already initialized, it returns the existing model.
	 * @param project
	 * @return
	 */
	public IncQueryGeneratorModel initializeGeneratorModel(IProject project, ResourceSet set) {
		IFile file = project.getFile(IncQueryNature.IQGENMODEL);
		if (file.exists()) {
			return getGeneratorModel(project, set);
		} else {
			URI uri = URI.createPlatformResourceURI(file.getFullPath().toString(), false);
			Resource resource = set.createResource(uri);
			IncQueryGeneratorModel model = GeneratorModelFactory.eINSTANCE.createIncQueryGeneratorModel();
			resource.getContents().add(model);
			return model;
		}
	}
}
