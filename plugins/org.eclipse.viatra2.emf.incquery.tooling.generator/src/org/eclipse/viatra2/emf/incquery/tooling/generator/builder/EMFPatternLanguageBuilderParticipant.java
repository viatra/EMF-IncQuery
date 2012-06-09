package org.eclipse.viatra2.emf.incquery.tooling.generator.builder;

import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.pde.core.plugin.IPluginExtension;
import org.eclipse.viatra2.emf.incquery.core.project.ProjectGenerationHelper;
import org.eclipse.viatra2.emf.incquery.runtime.api.IncQueryEngine;
import org.eclipse.viatra2.emf.incquery.tooling.generator.ExtensionGenerator;
import org.eclipse.viatra2.emf.incquery.tooling.generator.GenerateMatcherFactoryExtension;
import org.eclipse.viatra2.emf.incquery.tooling.generator.builder.xmi.XmiModelSupport;
import org.eclipse.viatra2.emf.incquery.tooling.generator.fragments.IGenerationFragment;
import org.eclipse.viatra2.emf.incquery.tooling.generator.fragments.IGenerationFragmentProvider;
import org.eclipse.viatra2.emf.incquery.tooling.generator.util.EMFPatternLanguageJvmModelInferrerUtil;
import org.eclipse.viatra2.patternlanguage.core.helper.CorePatternLanguageHelper;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Pattern;
import org.eclipse.xtext.builder.BuilderParticipant;
import org.eclipse.xtext.builder.EclipseResourceFileSystemAccess2;
import org.eclipse.xtext.generator.IGenerator;
import org.eclipse.xtext.resource.IResourceDescription;
import org.eclipse.xtext.resource.IResourceDescription.Delta;

import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.google.inject.Injector;

/**
 * @author Mark Czotter
 */
public class EMFPatternLanguageBuilderParticipant extends BuilderParticipant {

	@Inject
	private Injector injector;
	
	@Inject
	private IGenerator generator;
	
	@Inject
	private IGenerationFragmentProvider fragmentProvider;
	
	@Inject
	private EMFPatternLanguageJvmModelInferrerUtil util;
	
	@Inject
	private XmiModelSupport xmiModelSupport;
	
	@Inject
	private EnsurePluginSupport ensureSupport;
	
	@Inject
	private CleanSupport cleanSupport;
	
	@Inject
	private EclipseResourceSupport eclipseResourceSupport;
	
	@Inject 
	private GenerateMatcherFactoryExtension matcherFactoryExtensionGenerator;
	
	@Override
	public void build(final IBuildContext context, IProgressMonitor monitor)
			throws CoreException {
		if (!isEnabled(context)) {
			return;
		}
        final List<IResourceDescription.Delta> relevantDeltas = getRelevantDeltas(context);
        if (relevantDeltas.isEmpty()) {
            return;
        }
		// monitor handling
		if (monitor.isCanceled()) {
			throw new OperationCanceledException();
		}
		SubMonitor progress = SubMonitor.convert(monitor, 5);
		final IProject modelProject = context.getBuiltProject();
		modelProject.refreshLocal(IResource.DEPTH_INFINITE, progress.newChild(1));
		if (context.getBuildType() == BuildType.CLEAN || context.getBuildType() == BuildType.RECOVERY) {
			cleanSupport.fullClean(context, progress.newChild(1));
			// invoke clean build on main project src-gen
			super.build(context, progress.newChild(1));
			if (context.getBuildType() == BuildType.CLEAN) {
				// work 2 unit if clean build is performed (xmi build, and ensure)
				progress.worked(2);
				return;
			}
		} else {
			ensureSupport.clean();
			cleanSupport.normalClean(context, relevantDeltas, progress.newChild(1));
		}
		super.build(context, progress.newChild(1));
		// normal cleanUp and codegen done on every delta, do XMI Model build
		xmiModelSupport.build(relevantDeltas.get(0), context, progress.newChild(1));
		// normal code generation done, extensions, packages ready to add to the plug-ins
		ensureSupport.ensure(modelProject, progress.newChild(1));
	}
	
	@Override
	protected void handleChangedContents(Delta delta, IBuildContext context,
			EclipseResourceFileSystemAccess2 fileSystemAccess)
			throws CoreException {
		// TODO: we will run out of memory here if the number of deltas is large enough
		Resource deltaResource = context.getResourceSet().getResource(delta.getUri(), true);
		if (shouldGenerate(deltaResource, context)) {
			try {
				// do inferred jvm model to code transformation
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
		ExtensionGenerator generator = new ExtensionGenerator();
		generator.setProject(project);
		TreeIterator<EObject> it = deltaResource.getAllContents();
		while (it.hasNext()) {
			EObject obj = it.next();
			if (obj instanceof Pattern && !CorePatternLanguageHelper.isPrivate((Pattern)obj)) {
				Iterable<IPluginExtension> extensionContribution = matcherFactoryExtensionGenerator.extensionContribution((Pattern)obj, generator);
				ensureSupport.appendAllExtension(project, extensionContribution);
				executeGeneratorFragments(context.getBuiltProject(), (Pattern) obj);
				ensureSupport.exportPackage(project, util.getPackageName((Pattern) obj));
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
			try {
				injector.injectMembers(fragment);
				executeGeneratorFragment(fragment, modelProject, pattern);
			} catch (Exception e) {
				String msg = String.format("Exception when executing generation for '%s' in fragment '%s'", CorePatternLanguageHelper.getFullyQualifiedName(pattern), fragment.getClass().getCanonicalName());
				IncQueryEngine.getDefaultLogger().logError(msg, e);
			}
		}
	}
	
	private void executeGeneratorFragment(IGenerationFragment fragment,
			IProject modelProject, Pattern pattern) throws CoreException {
		IProject targetProject = createOrGetTargetProject(modelProject,
				fragment);
		EclipseResourceFileSystemAccess2 fsa = eclipseResourceSupport.createProjectFileSystemAccess(targetProject);
		fragment.generateFiles(pattern, fsa);
		// Generating Eclipse extensions
		ExtensionGenerator generator = new ExtensionGenerator();
		generator.setProject(targetProject);
		Iterable<IPluginExtension> extensionContribution = fragment.extensionContribution(pattern, generator);
		// Gathering all registered extensions together to avoid unnecessary plugin.xml modifications
		// Both for performance and for avoiding race conditions
		ensureSupport.appendAllExtension(targetProject, extensionContribution);		
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
		String modelProjectName = ProjectGenerationHelper.getBundleSymbolicName(modelProject);;
		List<String> dependencies = Lists.asList(modelProjectName, fragment.getProjectDependencies());
		if (postfix == null || postfix.isEmpty()) {
			ProjectGenerationHelper.ensureBundleDependencies(modelProject,
					dependencies);
			return modelProject;
		} else {
			IProject targetProject = fragmentProvider.getFragmentProject(modelProject, fragment);
			if (!targetProject.exists()) {
				ProjectGenerationHelper.initializePluginProject(targetProject, dependencies);
			} else {
				ProjectGenerationHelper.ensureBundleDependencies(targetProject, dependencies);
			}
			return targetProject;
		}
	}
	
}
