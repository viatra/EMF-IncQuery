package org.eclipse.viatra2.emf.incquery.tooling.generator;

import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.viatra2.emf.incquery.core.project.ProjectGenerationHelper;
import org.eclipse.viatra2.emf.incquery.tooling.generator.fragments.IGenerationFragment;
import org.eclipse.viatra2.emf.incquery.tooling.generator.fragments.IGenerationFragmentProvider;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Pattern;
import org.eclipse.xtext.builder.EclipseResourceFileSystemAccess2;
import org.eclipse.xtext.generator.IFileSystemAccess;
import org.eclipse.xtext.xbase.compiler.JvmModelGenerator;

import com.google.common.collect.Lists;
import com.google.inject.Inject;

/**
 * A custom generator for EMF-IncQuery projects that is based on the JVM Model
 * Inferrers, but allows extensions based on an injected
 * {@link IGenerationFragmentProvider} instance.
 * 
 * @author Zoltan Ujhelyi
 * 
 */
public class IncQueryGenerator extends JvmModelGenerator {

	Logger logger = Logger.getLogger(getClass());
	
	@Inject
	IGenerationFragmentProvider fragmentProvider;
	@Inject
	IWorkspaceRoot workspaceRoot;

	@Override
	public void doGenerate(Resource input, IFileSystemAccess fsa) {
		super.doGenerate(input, fsa);
		try {
			IProject project = workspaceRoot.getFile(
					new Path(input.getURI().toPlatformString(true)))
					.getProject();
			TreeIterator<EObject> it = input.getAllContents();
			while (it.hasNext()) {
				EObject obj = it.next();
				if (obj instanceof Pattern) {
					executeGeneratorFragments(project, (Pattern) obj);
				}
			}
		} catch (CoreException e) {
			logger.error("Error during code generation", e);
		}
	}

	private void executeGeneratorFragments(IProject modelProject, Pattern obj)
			throws CoreException {
		for (IGenerationFragment fragment : fragmentProvider
				.getFragmentsForPattern(obj)) {
			System.out.println(obj.getName() + ": "
					+ fragment.getClass().getCanonicalName());
			EclipseResourceFileSystemAccess2 fsa = new EclipseResourceFileSystemAccess2();
			IProject targetProject = createOrGetTargetProject(modelProject,
					fragment);
			fsa.setProject(targetProject);
		}
	}

	private IProject createOrGetTargetProject(IProject modelProject,
			IGenerationFragment fragment) throws CoreException {
		String postfix = fragment.getProjectPostfix();
		List<String> dependencies = Lists.asList(modelProject.getName(), fragment.getProjectDependencies());
		if (postfix == null || postfix.isEmpty()) {
			ProjectGenerationHelper.ensureBundleDependencies(modelProject,
					dependencies);
			return modelProject;
		} else {
			String projectName = String.format("%s.%s", modelProject.getName(),
					postfix);
			IProject targetProject = workspaceRoot.getProject(projectName);
			if (!targetProject.exists()) {
				ProjectGenerationHelper.initializePluginProject(targetProject, dependencies);
			} else {
				ProjectGenerationHelper.ensureBundleDependencies(targetProject, dependencies);
			}
			return targetProject;
		}
	}

}
