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

package org.eclipse.incquery.patternlanguage.emf.ui.validation;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.emf.codegen.ecore.genmodel.GenModel;
import org.eclipse.emf.codegen.ecore.genmodel.GenPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.incquery.patternlanguage.emf.eMFPatternLanguage.EMFPatternLanguagePackage;
import org.eclipse.incquery.patternlanguage.emf.eMFPatternLanguage.PackageImport;
import org.eclipse.incquery.patternlanguage.emf.validation.EMFIssueCodes;
import org.eclipse.incquery.patternlanguage.emf.validation.EMFPatternLanguageJavaValidator;
import org.eclipse.incquery.tooling.core.generator.genmodel.IEiqGenmodelProvider;
import org.eclipse.incquery.tooling.core.project.ProjectGenerationHelper;
import org.eclipse.xtext.common.types.access.jdt.IJavaProjectProvider;
import org.eclipse.xtext.validation.Check;

import com.google.inject.Inject;

public class GenmodelBasedEMFPatternLanguageJavaValidator extends
		EMFPatternLanguageJavaValidator {

	@Inject
	private IEiqGenmodelProvider genmodelProvider;
	@Inject
	private IJavaProjectProvider projectProvider;
	@Inject 
	private Logger logger;
	
	@Check
	public void checkImportDependency(PackageImport importDecl) {
		Resource res = importDecl.eResource();
		if (projectProvider == null || res == null) {
			return;
		}
		IProject project = projectProvider.getJavaProject(
					res.getResourceSet()).getProject();
		GenPackage genPackage = genmodelProvider.findGenPackage(importDecl, importDecl.getEPackage());
		if (genPackage != null) {
			final GenModel genmodel = genPackage.getGenModel();
			if (genmodel != null) {
				String modelPluginID = genmodel.getModelPluginID();
				try {
					if (modelPluginID != null
							&& !modelPluginID.isEmpty()
							&& !ProjectGenerationHelper.checkBundleDependency(
									project, modelPluginID)) {
						error(String
								.format("To refer elements from the Package %s the bundle %s must be added as dependency",
										importDecl.getEPackage().getNsURI(),
										modelPluginID),
								importDecl,
								EMFPatternLanguagePackage.Literals.PACKAGE_IMPORT__EPACKAGE,
								EMFIssueCodes.IMPORT_DEPENDENCY_MISSING,
								modelPluginID);
					}
				} catch (CoreException e) {
					logger.error("Error while checking the dependencies of the import declaration", e);
				}
			}
		}
	}
}
