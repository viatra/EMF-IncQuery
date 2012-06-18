package org.eclipse.viatra2.emf.incquery.tooling.generator.genmodel;

import java.util.Collections;
import java.util.Iterator;

import org.eclipse.core.resources.IProject;
import org.eclipse.emf.codegen.ecore.genmodel.GenPackage;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.viatra2.emf.incquery.core.project.ProjectGenerationHelper;
import org.eclipse.viatra2.emf.incquery.tooling.generator.generatorModel.GeneratorModelReference;
import org.eclipse.viatra2.emf.incquery.tooling.generator.generatorModel.IncQueryGeneratorModel;
import org.eclipse.viatra2.patternlanguage.scoping.MetamodelProviderService;
import org.eclipse.xtext.common.types.access.jdt.IJavaProjectProvider;
import org.eclipse.xtext.naming.IQualifiedNameConverter;
import org.eclipse.xtext.naming.QualifiedName;
import org.eclipse.xtext.resource.EObjectDescription;
import org.eclipse.xtext.resource.IEObjectDescription;
import org.eclipse.xtext.scoping.IScope;
import org.eclipse.xtext.scoping.impl.SimpleScope;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.inject.Inject;

public class GenModelMetamodelProviderService extends MetamodelProviderService {

	@Inject
	IJavaProjectProvider projectProvider;
	@Inject
	IQualifiedNameConverter qualifiedNameConverter;
	
	@Override
	public IScope getAllMetamodelObjects(EObject ctx) {
		Resource res = ctx.eResource();
		Iterable<IEObjectDescription> referencedPackages = Lists.newArrayList();
		if (res != null && projectProvider != null) {
			ResourceSet set = res.getResourceSet();
			IJavaProject javaProject = projectProvider.getJavaProject(set);
			IProject project = javaProject.getProject();
			IncQueryGeneratorModel generatorModel = ProjectGenerationHelper.getGeneratorModel(project, set);
			for (GeneratorModelReference ref : generatorModel.getGenmodels()) {
				
				Iterable<IEObjectDescription> packages = Iterables.transform(ref.getGenmodel().getGenPackages(), new Function<GenPackage, IEObjectDescription>() {
					public IEObjectDescription apply(GenPackage from) {
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
		}
		return new SimpleScope(super.getAllMetamodelObjects(ctx), referencedPackages);
	}

	@Override
	public EPackage loadEPackage(final String packageUri, ResourceSet set) {
		if (set != null && projectProvider != null) {
			IJavaProject javaProject = projectProvider.getJavaProject(set);
			IProject project = javaProject.getProject();
			IncQueryGeneratorModel generatorModel = ProjectGenerationHelper.getGeneratorModel(project, set);
			for (GeneratorModelReference ref : generatorModel.getGenmodels()) {
				
				Iterable<GenPackage> genPackages = 
				Iterables.filter(ref.getGenmodel().getGenPackages(), new Predicate<GenPackage>() {
					public boolean apply(GenPackage genPackage) {
						return packageUri.equals(genPackage.getEcorePackage().getNsURI());
					}
				});
				Iterator<GenPackage> iterator = genPackages.iterator();
				if (iterator.hasNext()) {
					GenPackage genPackage = iterator.next();
					return genPackage.getEcorePackage();
				}
			}
		}
		return super.loadEPackage(packageUri, set);
	}

}
