package org.eclipse.viatra2.emf.incquery.tooling.generator.ui;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.emf.codegen.ecore.genmodel.GenModel;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.viatra2.emf.incquery.core.project.ProjectGenerationHelper;
import org.eclipse.viatra2.emf.incquery.tooling.generator.generatorModel.GeneratorModelReference;
import org.eclipse.viatra2.emf.incquery.tooling.generator.validation.GeneratorModelJavaValidator;
import org.eclipse.xtext.common.types.access.jdt.IJavaProjectProvider;
import org.eclipse.xtext.validation.Check;

import com.google.inject.Inject;

public class GenmodelProjectBasedValidation extends GeneratorModelJavaValidator {

	public final static String GENMODEL_DEPENDENCY = "org.eclipse.viatra2.emf.incquery.tooling.generator.ui."
			+ "genmodel_dependency";

	@Inject
	IJavaProjectProvider projectProvider;

	@Check
	public void checkGenmodelDependencies(GeneratorModelReference ref) {
		Resource res = ref.eResource();
		if (res != null && projectProvider != null) {
			IProject project = projectProvider.getJavaProject(
					res.getResourceSet()).getProject();
			final GenModel genmodel = ref.getGenmodel();
			if (genmodel != null) {
				String modelPluginID = genmodel.getModelPluginID();
				try {
					if (!ProjectGenerationHelper.checkBundleDependency(project,
							modelPluginID)) {
						error(String.format(
								"To refer elements from the Generator Model %s the bundle %s must be added as dependency",
								genmodel.eResource().getURI().toString(),
								modelPluginID), ref, null, GENMODEL_DEPENDENCY,
								modelPluginID);
					}
				} catch (CoreException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
}
