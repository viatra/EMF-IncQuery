package org.eclipse.viatra2.patternlanguage.ui.validation;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.emf.codegen.ecore.genmodel.GenModel;
import org.eclipse.emf.codegen.ecore.genmodel.GenPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.viatra2.emf.incquery.core.project.ProjectGenerationHelper;
import org.eclipse.viatra2.emf.incquery.tooling.generator.genmodel.IEiqGenmodelProvider;
import org.eclipse.viatra2.patternlanguage.eMFPatternLanguage.EMFPatternLanguagePackage;
import org.eclipse.viatra2.patternlanguage.eMFPatternLanguage.PackageImport;
import org.eclipse.viatra2.patternlanguage.validation.EMFIssueCodes;
import org.eclipse.viatra2.patternlanguage.validation.EMFPatternLanguageJavaValidator;
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
