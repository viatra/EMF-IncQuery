package org.eclipse.viatra2.patternlanguage.ui.builder;

import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.Sets.newLinkedHashSet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.pde.core.plugin.IPluginExtension;
import org.eclipse.viatra2.emf.incquery.core.project.ProjectGenerationHelper;
import org.eclipse.viatra2.emf.incquery.tooling.generator.ExtensionGenerator;
import org.eclipse.viatra2.emf.incquery.tooling.generator.GenerateMatcherFactoryExtension;
import org.eclipse.viatra2.emf.incquery.tooling.generator.fragments.IGenerationFragment;
import org.eclipse.viatra2.emf.incquery.tooling.generator.fragments.IGenerationFragmentProvider;
import org.eclipse.viatra2.emf.incquery.tooling.generator.util.EMFPatternLanguageJvmModelInferrerUtil;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Pattern;
import org.eclipse.xtext.builder.BuilderParticipant;
import org.eclipse.xtext.builder.EclipseOutputConfigurationProvider;
import org.eclipse.xtext.builder.DerivedResourceMarkers.GeneratorIdProvider;
import org.eclipse.xtext.builder.EclipseResourceFileSystemAccess2;
import org.eclipse.xtext.generator.IDerivedResourceMarkers;
import org.eclipse.xtext.generator.IGenerator;
import org.eclipse.xtext.generator.OutputConfiguration;
import org.eclipse.xtext.resource.IResourceDescription;
import org.eclipse.xtext.resource.IResourceDescription.Delta;
import org.eclipse.xtext.xbase.lib.Pair;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Provider;

public class EMFPatternLanguageBuilderParticipant extends BuilderParticipant {

	@Inject
	private Injector injector;
	
	@Inject
	private IGenerator generator;
	
	@Inject
	private Provider<EclipseResourceFileSystemAccess2> fileSystemAccessProvider;
	
	@Inject
	private IDerivedResourceMarkers derivedResourceMarkers;
	
	@Inject
 	private GeneratorIdProvider generatorIdProvider;
	
	@Inject
	private IGenerationFragmentProvider fragmentProvider;
	
	@Inject
	private EMFPatternLanguageJvmModelInferrerUtil util;
	
	@Inject
	private IWorkspaceRoot workspaceRoot;
	
	@Inject
	private EclipseOutputConfigurationProvider outputConfigurationProvider;
	
	private Multimap<IProject, IPluginExtension> extensionMap = ArrayListMultimap.create();
	private Multimap<IProject, Pair<String, String>> removableExtensionMap = ArrayListMultimap.create();
	
	@Override
	public void build(final IBuildContext context, IProgressMonitor monitor)
			throws CoreException {
		if (!isEnabled(context)) {
			return;
		}
		
        final List<IResourceDescription.Delta> deltas = getRelevantDeltas(context);
        if (deltas.isEmpty()) {
            return;
        }

		final int numberOfDeltas = deltas.size();
		
		// monitor handling
		if (monitor.isCanceled()) {
			throw new OperationCanceledException();
		}
			
		// do clean build
		if (context.getBuildType() == BuildType.CLEAN || context.getBuildType() == BuildType.RECOVERY) {
			// TODO add clean logic for all added extension (first: matcherfactory, validation.constraint)
			// TODO add clean logic for all exported packages
			// TODO remove Global XMI model from queries folder
			if (context.getBuildType() == BuildType.CLEAN) {
				return;
			}
		}
		super.build(context, monitor);
	}
	
	@Override
	protected void handleChangedContents(Delta delta, IBuildContext context,
			EclipseResourceFileSystemAccess2 fileSystemAccess)
			throws CoreException {
		// TODO: we will run out of memory here if the number of deltas is large enough
		Resource deltaResource = context.getResourceSet().getResource(delta.getUri(), true);
		if (shouldGenerate(deltaResource, context)) {
			try {
				// do infered jvm model to code transformation
				generator.doGenerate(deltaResource, fileSystemAccess);
				doPostGenerate(deltaResource, context);				 
			} catch (RuntimeException e) {
				if (e.getCause() instanceof CoreException) {
					throw (CoreException) e.getCause();
				}
				throw e;
			}
		}
	}
	
	/**
	 * Do stuff like export packages, add extensions.
	 * @param deltaResource
	 * @param context
	 * @throws CoreException
	 */
	private void doPostGenerate(Resource deltaResource,
			IBuildContext context) throws CoreException {
		final IProject project = context.getBuiltProject();
		GenerateMatcherFactoryExtension matcherFactoryExtensionGenerator = new GenerateMatcherFactoryExtension();
		injector.injectMembers(matcherFactoryExtensionGenerator);
		ExtensionGenerator generator = new ExtensionGenerator();
		generator.setProject(project);
		ArrayList<String> packageNames = new ArrayList<String>();
		TreeIterator<EObject> it = deltaResource.getAllContents();
		while (it.hasNext()) {
			EObject obj = it.next();
			if (obj instanceof Pattern) {
				Iterable<IPluginExtension> extensionContribution = matcherFactoryExtensionGenerator.extensionContribution((Pattern)obj, generator);
				extensionMap.putAll(project, extensionContribution);
				executeGeneratorFragments(context.getBuiltProject(), (Pattern) obj);
				packageNames.add(util.getPackageName((Pattern) obj));
			}
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
			fragment.generateFiles(pattern, fsa);
			// Generating Eclipse extensions
			ExtensionGenerator generator = new ExtensionGenerator();
			generator.setProject(targetProject);
			Iterable<IPluginExtension> extensionContribution = fragment.extensionContribution(pattern, generator);
			// Gathering all registered extensions together to avoid unnecessary plugin.xml modifications
			// Both for performance and for avoiding race conditions
			extensionMap.putAll(targetProject, extensionContribution);
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
	
	/**
	 * Calculates a file system access component for the selected target project. This is required for code generation API.
	 * @param targetProject
	 * @return an initialized file system access component for the 
	 */
	private EclipseResourceFileSystemAccess2 createProjectFileSystemAccess(
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
	
}
