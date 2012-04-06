package org.eclipse.viatra2.emf.incquery.tooling.generator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.pde.core.plugin.IPluginExtension;
import org.eclipse.viatra2.emf.incquery.core.project.ProjectGenerationHelper;
import org.eclipse.viatra2.emf.incquery.runtime.util.XmiModelUtil;
import org.eclipse.viatra2.emf.incquery.tooling.generator.fragments.IGenerationFragment;
import org.eclipse.viatra2.emf.incquery.tooling.generator.fragments.IGenerationFragmentProvider;
import org.eclipse.viatra2.emf.incquery.tooling.generator.util.EMFPatternLanguageJvmModelInferrerUtil;
import org.eclipse.viatra2.emf.incquery.tooling.generator.util.XmiOutputBuilder;
import org.eclipse.viatra2.patternlanguage.core.helper.CorePatternLanguageHelper;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Pattern;
import org.eclipse.xtext.builder.EclipseOutputConfigurationProvider;
import org.eclipse.xtext.builder.EclipseResourceFileSystemAccess2;
import org.eclipse.xtext.generator.IFileSystemAccess;
import org.eclipse.xtext.generator.OutputConfiguration;
import org.eclipse.xtext.resource.IContainer;
import org.eclipse.xtext.resource.IResourceDescription;
import org.eclipse.xtext.resource.IResourceDescriptions;
import org.eclipse.xtext.resource.impl.ResourceDescriptionsProvider;
import org.eclipse.xtext.xbase.compiler.JvmModelGenerator;
import org.eclipse.xtext.xbase.lib.Pair;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.inject.Inject;
import com.google.inject.Injector;

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
	@Inject
	Injector injector;
	@Inject
	EclipseOutputConfigurationProvider outputConfigurationProvider;
	@Inject
	EMFPatternLanguageJvmModelInferrerUtil util;
	
	Multimap<IProject, IPluginExtension> extensionMap = ArrayListMultimap.create();
	
	// EXPERIMENTAL
	@Inject
	ResourceDescriptionsProvider resourceDescriptionsProvider;
	@Inject
	IContainer.Manager containerManager;
	@Inject
	XmiOutputBuilder builder;
	

	@Override
	public void doGenerate(Resource input, IFileSystemAccess fsa) {
		try {
			GenerateMatcherFactoryExtension extensionGenerator = new GenerateMatcherFactoryExtension();
			injector.injectMembers(extensionGenerator);
			IProject project = workspaceRoot.getFile(
					new Path(input.getURI().toPlatformString(true)))
					.getProject();
			ExtensionGenerator generator = new ExtensionGenerator();
			generator.setProject(project);
			// clear to remove previous extensions, that remain in the map
			// this way we can avoid growing plugin.xml file.
			extensionMap.clear();
			cleanUp(project, extensionGenerator, generator);
			// create a resourcedescription for the input, 
			// this way we can find all relevant EIQ file in the context of this input.
			IResourceDescriptions index = resourceDescriptionsProvider.createResourceDescriptions();
			IResourceDescription resDesc = index.getResourceDescription(input.getURI());
			List<IContainer> visibleContainers = containerManager.getVisibleContainers(resDesc, index);
			// load all visible resource to the resourceset of the input resource
			for (IContainer container : visibleContainers) {
				for (IResourceDescription rd : container.getResourceDescriptions()) {
					input.getResourceSet().getResource(rd.getURI(), true);				
				}
			}
			// build xmi file
			builder.build(input.getResourceSet(), project);
			super.doGenerate(input, fsa);
			ArrayList<String> packageNames = new ArrayList<String>();
			TreeIterator<EObject> it = input.getAllContents();
			while (it.hasNext()) {
				EObject obj = it.next();
				if (obj instanceof Pattern) {
					Iterable<IPluginExtension> extensionContribution = extensionGenerator.extensionContribution((Pattern)obj, generator);
					extensionMap.putAll(project, extensionContribution);
					executeGeneratorFragments(project, (Pattern) obj);
					packageNames.add(util.getPackageName((Pattern) obj));
				}
			}
			// Exporting packages from the main project
			ProjectGenerationHelper.ensurePackageExports(project, packageNames);
			// Loading extensions to the generated projects
			for (IProject proj : extensionMap.keySet()) {
				ProjectGenerationHelper.ensureExtensions(proj, extensionMap.get(proj));
			}
		} catch (CoreException e) {
			logger.error("Error during code generation", e);
		}
	}

	private void cleanUp(IProject project,
			GenerateMatcherFactoryExtension extensionGenerator,
			ExtensionGenerator generator) throws CoreException {
		// load previous global xmi resource
		Resource gxr = XmiModelUtil.getGlobalXmiResource(project.getName());
		if (gxr != null) {
			// do the clean up based on the previous model
			final String point = "org.eclipse.viatra2.emf.incquery.runtime.patternmatcher";
			ArrayList<String> packageNames = new ArrayList<String>();
			ArrayList<Pair<String, String>> contributedExtensions = new ArrayList<Pair<String,String>>();
			TreeIterator<EObject> it = gxr.getAllContents();
			while (it.hasNext()) {
				EObject obj = it.next();
				if (obj instanceof Pattern) {
					packageNames.add(util.getPackageName((Pattern) obj));
					// only the extension id and point name is needed for removal
					String pointId = CorePatternLanguageHelper.getFullyQualifiedName((Pattern) obj);
					contributedExtensions.add(Pair.of(pointId, point));
				}
			}
			// remove previously exported packages
			ProjectGenerationHelper.removePackageExports(project, packageNames);
			// remove previously added extensions
			ProjectGenerationHelper.removeExtensions(project, contributedExtensions);
		}
	}

	private void executeGeneratorFragments(IProject modelProject, Pattern pattern)
			throws CoreException {
		for (IGenerationFragment fragment : fragmentProvider
				.getFragmentsForPattern(pattern)) {
			injector.injectMembers(fragment);
			IProject targetProject = createOrGetTargetProject(modelProject,
					fragment);
			EclipseResourceFileSystemAccess2 fsa = createProjectFileSystemAccess(targetProject);
//			for (JvmGenericType type : fragment.inferFiles(pattern)) {
//				internalDoGenerate(type, fsa);
//			}
			fragment.generateFiles(pattern, fsa);
			//Generating Eclipse extensions
			ExtensionGenerator generator = new ExtensionGenerator();
			generator.setProject(targetProject);
			Iterable<IPluginExtension> extensionContribution = fragment.extensionContribution(pattern, generator);
			//Gathering all registered extensions together to avoid unnecessary plugin.xml modifications
			//Both for performance and for avoiding race conditions
			extensionMap.putAll(targetProject, extensionContribution);
		}
	}

	/**
	 * Calculates a file system access component for the selected target project. This is required for code generation API.
	 * @param targetProject
	 * @return an initialized file system access component for the 
	 */
	EclipseResourceFileSystemAccess2 createProjectFileSystemAccess(
			IProject targetProject) {
		EclipseResourceFileSystemAccess2 fsa = new EclipseResourceFileSystemAccess2();
		fsa.setProject(targetProject);
		fsa.setMonitor(new NullProgressMonitor());
		Map<String, OutputConfiguration> outputs = new HashMap<String, OutputConfiguration>(); 
		for (OutputConfiguration conf : outputConfigurationProvider.getOutputConfigurations(targetProject)) {
			outputs.put(conf.getName(), conf);
		}
		fsa.setOutputConfigurations(outputs);
		fsa.setPostProcessor(new EclipseResourceFileSystemAccess2.IFileCallback() {
			
			public boolean beforeFileDeletion(IFile file) {
				return true;
			}
			
			public void afterFileUpdate(IFile file) {
				handleFileAccess(file);
			}

			public void afterFileCreation(IFile file) {
				handleFileAccess(file);
			}
			
			protected void handleFileAccess(IFile file) {
			}
			
		});
		return fsa;
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
