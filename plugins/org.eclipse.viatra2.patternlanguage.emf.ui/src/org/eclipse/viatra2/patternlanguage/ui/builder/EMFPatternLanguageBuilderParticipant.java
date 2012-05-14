package org.eclipse.viatra2.patternlanguage.ui.builder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.pde.core.plugin.IPluginExtension;
import org.eclipse.viatra2.emf.incquery.core.project.ProjectGenerationHelper;
import org.eclipse.viatra2.emf.incquery.runtime.IExtensions;
import org.eclipse.viatra2.emf.incquery.runtime.util.XmiModelUtil;
import org.eclipse.viatra2.emf.incquery.tooling.generator.ExtensionGenerator;
import org.eclipse.viatra2.emf.incquery.tooling.generator.GenerateMatcherFactoryExtension;
import org.eclipse.viatra2.emf.incquery.tooling.generator.fragments.IGenerationFragment;
import org.eclipse.viatra2.emf.incquery.tooling.generator.fragments.IGenerationFragmentProvider;
import org.eclipse.viatra2.emf.incquery.tooling.generator.util.EMFPatternLanguageJvmModelInferrerUtil;
import org.eclipse.viatra2.emf.incquery.tooling.generator.util.XmiModelBuilder;
import org.eclipse.viatra2.patternlanguage.core.helper.CorePatternLanguageHelper;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Pattern;
import org.eclipse.viatra2.patternlanguage.eMFPatternLanguage.PatternModel;
import org.eclipse.xtext.builder.BuilderParticipant;
import org.eclipse.xtext.builder.EclipseOutputConfigurationProvider;
import org.eclipse.xtext.builder.EclipseResourceFileSystemAccess2;
import org.eclipse.xtext.generator.IGenerator;
import org.eclipse.xtext.generator.OutputConfiguration;
import org.eclipse.xtext.resource.IContainer;
import org.eclipse.xtext.resource.IResourceDescription;
import org.eclipse.xtext.resource.IResourceDescription.Delta;
import org.eclipse.xtext.resource.IResourceDescriptions;
import org.eclipse.xtext.resource.IResourceServiceProvider;
import org.eclipse.xtext.resource.impl.ResourceDescriptionsProvider;
import org.eclipse.xtext.xbase.lib.Functions;
import org.eclipse.xtext.xbase.lib.IterableExtensions;
import org.eclipse.xtext.xbase.lib.Pair;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.inject.Inject;
import com.google.inject.Injector;

/**
 * @author Mark Czotter
 */
public class EMFPatternLanguageBuilderParticipant extends BuilderParticipant {

	private Logger logger = Logger.getLogger(getClass());
	
	@Inject
	private Injector injector;
	
	@Inject
	private IGenerator generator;
	
	@Inject
	private IGenerationFragmentProvider fragmentProvider;
	
	@Inject
	private EMFPatternLanguageJvmModelInferrerUtil util;
	
	@Inject
	private IWorkspaceRoot workspaceRoot;
	
	@Inject
	private EclipseOutputConfigurationProvider outputConfigurationProvider;
	
	@Inject
	private XmiModelBuilder xmiModelBuilder;
	
	@Inject
	private ResourceDescriptionsProvider resourceDescriptionsProvider;
	
	@Inject
	private IContainer.Manager containerManager;
	
	@Inject
	private IResourceServiceProvider resourceServiceProvider;
	
	private Multimap<IProject, String> exportedPackageMap = ArrayListMultimap.create();
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

//		final int numberOfDeltas = deltas.size();
		
		// monitor handling
		if (monitor.isCanceled()) {
			throw new OperationCanceledException();
		}
		final IProject project = context.getBuiltProject();
		project.refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
		// do Clean build cleanUp
		if (context.getBuildType() == BuildType.CLEAN || context.getBuildType() == BuildType.RECOVERY) {
			ProjectGenerationHelper.removeAllExtension(project, IExtensions.MATCHERFACTORY_EXTENSION_POINT_ID);
			IProject validationProject = workspaceRoot.getProject(project.getName() + ".validation");
			if (validationProject.exists()) {
				ProjectGenerationHelper.removeAllExtension(validationProject, "org.eclipse.viatra2.emf.incquery.validation.runtime.constraint");				
			}
			// TODO add clean logic for remaining extensions
			removeExportedPackages(project);
			removeXmiModel(project);
			if (context.getBuildType() == BuildType.CLEAN) {
				return;
			}
		} else {
			// clear maps for new build
			exportedPackageMap.clear();
			extensionMap.clear();
			removableExtensionMap.clear();
			if (getGlobalXmiFile(project).exists()) {
				try {
					Resource globalXmiModel = XmiModelUtil.getGlobalXmiResource(project.getName());
					for (Delta delta : deltas) {
						Resource deltaResource = context.getResourceSet().getResource(delta.getUri(), true);
						if (delta.getNew() != null && shouldGenerate(deltaResource, context)) {
							cleanUp(project, deltaResource, globalXmiModel);
						}
					}
				} catch (Exception e) {
					logger.error("Exception during cleanUp Phase!", e);
				}
			}
		}
		super.build(context, monitor);
		// Normal CleanUp done on every delta, do XMI Model build 
		try {
			IProgressMonitor xmiBuildMonitor = new SubProgressMonitor(monitor, 1);
			xmiBuildMonitor.beginTask("Building XMI model", 1);
			buildXmiModel(context);	
			xmiBuildMonitor.done();
		} catch (Exception e) {
			logger.error("Exception during XMI Model Building Phase", e);
		}
		// ensure exported package and extensions
		for (IProject proj : exportedPackageMap.keySet()) {
			// ensure package exports per project
			ProjectGenerationHelper.ensurePackageExports(project, exportedPackageMap.get(proj));
		}
		
		// Loading extensions to the generated projects
		// if new contributed extensions exists remove the removables from the 
		// contributed extensions, so the truly removed extensions remain in the removedExtensions
		if (!extensionMap.isEmpty()) {
			// iterate over the contributed extensions, remove the removables
			for (IProject proj : extensionMap.keySet()) {
				Iterable<IPluginExtension> extensions = extensionMap.get(proj);
				Collection<Pair<String, String>> removableExtensions = removableExtensionMap.get(proj);
				if (!removableExtensions.isEmpty()) {
					// not remove a removable if exist in the current extension map
					for (final IPluginExtension ext : extensions) {
						Pair<String, String> found = IterableExtensions.findFirst(removableExtensions, new Functions.Function1<Pair<String, String>, Boolean>() {
							@Override
							public Boolean apply(Pair<String, String> p) {
								return (p.getKey().equals(ext.getId())) 
										&& (p.getValue().equals(ext.getPoint()));
							}
						});
						removableExtensions.remove(found);
					}					
				}
				ProjectGenerationHelper.ensureExtensions(proj, extensions, removableExtensions);
			}
			// iterate over the remaining removables, remove all prev. extension from the projects
			for (IProject proj : removableExtensionMap.keySet()) {
				if (!extensionMap.containsKey(proj)) {
					Iterable<Pair<String, String>> removableExtensions = removableExtensionMap.get(proj);
					Iterable<IPluginExtension> extensions = Lists.newArrayList();
					ProjectGenerationHelper.ensureExtensions(proj, extensions, removableExtensions);
				}
			}
		} else {
			// if no contributed extensions (like no pattern in the eiq file)
			// remove all previous extension
			for (IProject proj : removableExtensionMap.keySet()) {
				Iterable<Pair<String, String>> removableExtensions = removableExtensionMap.get(proj);
				Iterable<IPluginExtension> extensions = Lists.newArrayList();
				ProjectGenerationHelper.ensureExtensions(proj, extensions, removableExtensions);
			}
		}
	}
	
	/**
	 * Removes all packages, based on the Xmi Model.
	 * @param project
	 * @throws CoreException
	 */
	private void removeExportedPackages(IProject project) throws CoreException {
		if (getGlobalXmiFile(project).exists()) {
			ArrayList<String> packageNames = new ArrayList<String>();
			Resource globalXmiModel = XmiModelUtil.getGlobalXmiResource(project.getName());
			Iterator<EObject> iter = globalXmiModel.getAllContents();
			while(iter.hasNext()) {
				EObject obj = iter.next();
				if (obj instanceof Pattern) {
					packageNames.add(util.getPackageName((Pattern) obj));
				}
			}
			ProjectGenerationHelper.removePackageExports(project, packageNames);
		}		
	}

	/**
	 * Returns an {@link IFile} on the path 'queries/globalEiqModel.xmi' in the project.
	 * @param project
	 * @return
	 */
	private IFile getGlobalXmiFile(IProject project) {
		String xmiModelPath = String.format("%s/%s", XmiModelUtil.XMI_OUTPUT_FOLDER, XmiModelUtil.GLOBAL_EIQ_FILENAME);
		return project.getFile(new Path(xmiModelPath));
	}
	
	/**
	 * Full cleanUp for current deltaResource based on the Global XMI model saved before. 
	 * @param project
	 * @param deltaResource 
	 * @param globalXmiModel
	 * @throws CoreException
	 */
	private void cleanUp(IProject project, Resource deltaResource, Resource globalXmiModel) throws CoreException {
		// do the clean up based on the previous model
		ArrayList<String> packageNames = new ArrayList<String>();
		TreeIterator<EObject> it = globalXmiModel.getAllContents();
		while (it.hasNext()) {
			EObject obj = it.next();
			if (obj instanceof Pattern) {
				Pattern pattern = (Pattern) obj;
				if (pattern.getFileName().equals(
						deltaResource.getURI().toString())) {
					// add package name for removal
					packageNames.add(util.getPackageName(pattern));
					// clean up code and extensions in the modelProject
					executeCleanUpOnModelProject(project, pattern);
					// clean up code and extensions for all fragments
					executeCleanUpOnFragments(project, pattern);
				}
			}
		}
		// remove previously exported packages
		ProjectGenerationHelper.removePackageExports(project, packageNames);
	}
	
	/**
	 * Executes Normal Build cleanUp on the current Built Project
	 * (modelProject). Removes all code generated previously for the
	 * {@link Pattern}, and marks current {@link Pattern} related extensions for
	 * removal.
	 * 
	 * @param modelProject
	 * @param pattern
	 * @throws CoreException
	 */
	private void executeCleanUpOnModelProject(IProject modelProject, Pattern pattern) throws CoreException {
		EclipseResourceFileSystemAccess2 fsa = createProjectFileSystemAccess(modelProject);
		fsa.deleteFile(util.getPackagePath(pattern) + "/" + util.matchClassName(pattern) + ".java");
		fsa.deleteFile(util.getPackagePath(pattern) + "/" + util.matcherClassName(pattern) + ".java");
		fsa.deleteFile(util.getPackagePath(pattern) + "/" + util.matcherFactoryClassName(pattern) + ".java");
		fsa.deleteFile(util.getPackagePath(pattern) + "/" + util.processorClassName(pattern) + ".java");
		// only the extension id and point name is needed for removal
		String extensionId = CorePatternLanguageHelper.getFullyQualifiedName(pattern);
		removableExtensionMap.put(modelProject, Pair.of(extensionId, IExtensions.MATCHERFACTORY_EXTENSION_POINT_ID));
	}
	
	/**
	 * Executes Normal Build cleanUp on every {@link IGenerationFragment} registered to the current {@link Pattern}. Marks current {@link Pattern} related extensions for
	 * removal.
	 * @param modelProject
	 * @param pattern
	 * @throws CoreException
	 */
	private void executeCleanUpOnFragments(IProject modelProject, Pattern pattern) throws CoreException {
		for (IGenerationFragment fragment : fragmentProvider
				.getFragmentsForPattern(pattern)) {
			injector.injectMembers(fragment);
			IProject targetProject = createOrGetTargetProject(modelProject,
					fragment);
			EclipseResourceFileSystemAccess2 fsa = createProjectFileSystemAccess(targetProject);
			fragment.cleanUp(pattern, fsa);
			removableExtensionMap.putAll(targetProject, fragment.removeExtension(pattern));
		}
	}
	
	/**
	 * Deletes the Global XMI model file from the queries folder.
	 * @param project
	 * @throws CoreException
	 */
	private void removeXmiModel(IProject project) throws CoreException {
		String xmiModelPath = String.format("%s/%s", XmiModelUtil.XMI_OUTPUT_FOLDER, XmiModelUtil.GLOBAL_EIQ_FILENAME);
		IFile file = project.getFile(new Path(xmiModelPath));
		if (file.exists()) {
			file.delete(IResource.KEEP_HISTORY, new NullProgressMonitor());
		}
	}

	/**
	 * Builds a global XMI model with a {@link XmiModelBuilder} builder.
	 * Before the actual build, finds all relevant eiq resources, so the XMI build is performed on all currently available {@link PatternModel}.
	 * @param context
	 */
	private void buildXmiModel(IBuildContext context) {
		Delta delta = getRelevantDeltas(context).get(0);
		Resource deltaResource = context.getResourceSet().getResource(delta.getUri(), true);
		// create a resourcedescription for the input, 
		// this way we can find all relevant EIQ file in the context of this input.
		IResourceDescriptions index = resourceDescriptionsProvider.createResourceDescriptions();
		IResourceDescription resDesc = index.getResourceDescription(deltaResource.getURI());
		List<IContainer> visibleContainers = containerManager.getVisibleContainers(resDesc, index);
		// load all visible resource to the resourceset of the input resource
		for (IContainer container : visibleContainers) {
			for (IResourceDescription rd : container.getResourceDescriptions()) {
				if (resourceServiceProvider.canHandle(rd.getURI())) {
					context.getResourceSet().getResource(rd.getURI(), true);
				}
			}
		}
		xmiModelBuilder.build(context.getResourceSet(), context.getBuiltProject());
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
	 * From all {@link Pattern} instance in the current deltaResource, computes
	 * various additions to the modelProject, and executes the provided
	 * fragments. Various contribution: package export, MatcherFactory extension, validation constraint stuff.
	 * 
	 * 
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
		TreeIterator<EObject> it = deltaResource.getAllContents();
		while (it.hasNext()) {
			EObject obj = it.next();
			if (obj instanceof Pattern) {
				Iterable<IPluginExtension> extensionContribution = matcherFactoryExtensionGenerator.extensionContribution((Pattern)obj, generator);
				extensionMap.putAll(project, extensionContribution);
				executeGeneratorFragments(context.getBuiltProject(), (Pattern) obj);
				exportedPackageMap.put(project, util.getPackageName((Pattern) obj));
			}
		}
	}

	/**
	 * Executes all {@link IGenerationFragment} provided for the current {@link Pattern}.
	 * @param modelProject
	 * @param pattern
	 * @throws CoreException
	 */
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
	
	/**
	 * Creates or finds {@link IProject} associated with the
	 * {@link IGenerationFragment}. If the project exist dependencies ensured
	 * based on the {@link IGenerationFragment} contribution. If the project not
	 * exist, it will be initialized.
	 * 
	 * @param modelProject
	 * @param fragment
	 * @return
	 * @throws CoreException
	 */
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
