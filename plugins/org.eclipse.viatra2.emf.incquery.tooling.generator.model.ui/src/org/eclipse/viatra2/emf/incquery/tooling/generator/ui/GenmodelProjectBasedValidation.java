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
package org.eclipse.viatra2.emf.incquery.tooling.generator.ui;

import org.apache.log4j.Logger;
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

	public static final String GENMODEL_DEPENDENCY = "org.eclipse.viatra2.emf.incquery.tooling.generator.ui."
			+ "genmodel_dependency";

	@Inject
	private IJavaProjectProvider projectProvider;
	@Inject
	private Logger logger;

	@Check
	public void checkGenmodelDependencies(GeneratorModelReference ref) {
		Resource res = ref.eResource();
		if (res != null && projectProvider != null) {
			IProject project = projectProvider.getJavaProject(
					res.getResourceSet()).getProject();
			final GenModel genmodel = ref.getGenmodel();
			if (genmodel != null) {
				checkExistingDependency(ref, project, genmodel);
			}
		}
	}

	private void checkExistingDependency(final GeneratorModelReference ref,
			IProject project, final GenModel genmodel) {
		String modelPluginID = genmodel.getModelPluginID();
		try {
			if (modelPluginID != null
					&& !modelPluginID.isEmpty()
					&& !ProjectGenerationHelper.checkBundleDependency(project,
							modelPluginID)) {
				error(String
						.format("To refer elements from the Generator Model %s the bundle %s must be added as dependency",
								genmodel.eResource().getURI().toString(),
								modelPluginID),
						ref, null, GENMODEL_DEPENDENCY, modelPluginID);
			}
		} catch (CoreException e) {
			logger.error("Error checking project: ", e);
		}
	}
}
