/*******************************************************************************
 * Copyright (c) 2010-2012, Mark Czotter, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Mark Czotter - initial API and implementation
 *******************************************************************************/

package org.eclipse.incquery.tooling.core.generator.builder;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.eclipse.incquery.patternlanguage.emf.IResourceSetPreparer;
import org.eclipse.incquery.patternlanguage.helper.CorePatternLanguageHelper;
import org.eclipse.incquery.patternlanguage.patternLanguage.Pattern;
import org.eclipse.incquery.runtime.IExtensions;
import org.eclipse.incquery.runtime.exception.IncQueryException;
import org.eclipse.incquery.runtime.util.XmiModelUtil;
import org.eclipse.incquery.runtime.util.XmiModelUtilRunningOptionEnum;
import org.eclipse.incquery.tooling.core.generator.GenerateMatcherFactoryExtension;
import org.eclipse.incquery.tooling.core.generator.GenerateXExpressionEvaluatorExtension;
import org.eclipse.incquery.tooling.core.generator.fragments.IGenerationFragment;
import org.eclipse.incquery.tooling.core.generator.fragments.IGenerationFragmentProvider;
import org.eclipse.incquery.tooling.core.generator.util.EMFPatternLanguageJvmModelInferrerUtil;
import org.eclipse.incquery.tooling.core.generator.util.XMIResourceURIHandler;
import org.eclipse.incquery.tooling.core.project.ProjectGenerationHelper;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.xtext.builder.EclipseResourceFileSystemAccess2;
import org.eclipse.xtext.builder.IXtextBuilderParticipant.IBuildContext;
import org.eclipse.xtext.generator.IFileSystemAccess;
import org.eclipse.xtext.generator.OutputConfiguration;
import org.eclipse.xtext.resource.IResourceDescription.Delta;
import org.eclipse.xtext.resource.XtextResourceSet;
import org.eclipse.xtext.xbase.lib.Pair;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.inject.Inject;
import com.google.inject.Injector;

/**
 * Clean phase support for BuilderParticipant.
 * 
 * @author Mark Czotter
 * 
 */
public class CleanSupport {

    private final class PrepareResourceSetWithLoader implements IResourceSetPreparer {

        private final IProject project;

        public PrepareResourceSetWithLoader(IProject project) {
            this.project = project;
        }

        @Override
        public void prepareResourceSet(ResourceSet set) {
            Map<String, Object> options = Maps.newHashMap();
            XMIResourceURIHandler xmiResourceURIHandler = new XMIResourceURIHandler(set);
            injector.injectMembers(xmiResourceURIHandler);
            options.put(XMLResource.OPTION_URI_HANDLER, xmiResourceURIHandler);
            set.getLoadOptions().putAll(options);
            if (set instanceof XtextResourceSet) {
                ((XtextResourceSet) set).setClasspathURIContext(JavaCore.create(project));
            }
        }
    }

    @Inject
    private Injector injector;

    @Inject
    private IGenerationFragmentProvider fragmentProvider;

    @Inject
    private EclipseResourceSupport eclipseResourceSupport;

    @Inject
    private EMFPatternLanguageJvmModelInferrerUtil jvmInferrerUtil;

    @Inject
    private EnsurePluginSupport ensureSupport;

    @Inject
    private IErrorFeedback errorFeedback;

    @Inject
    private Logger logger;

    /**
     * Performs a full clean on the currently built project and all related fragments.
     * 
     * @param context
     * @param monitor
     */
    public void fullClean(IBuildContext context, IProgressMonitor monitor) {
        try {
            internalFullClean(context, monitor);
        } catch (Exception e) {
            logger.error("Exception during Full Clean!", e);
        } finally {
            monitor.worked(1);
        }
    }

    private void internalFullClean(IBuildContext context, IProgressMonitor monitor) throws CoreException,
            IncQueryException {
        IProject modelProject = context.getBuiltProject();
        // clean all fragments
        cleanAllFragment(modelProject);
        // clean current model project
        List<Pair<String, String>> removableExtensions = new ArrayList<Pair<String, String>>();
        removableExtensions.addAll(GenerateMatcherFactoryExtension.getRemovableExtensionIdentifiers());
        removableExtensions.addAll(GenerateXExpressionEvaluatorExtension.getRemovableExtensionIdentifiers());
        ProjectGenerationHelper.removeAllExtension(modelProject, removableExtensions);
        removeExportedPackages(modelProject);
        removeXmiModel(modelProject);
    }

    /**
     * Performs full Clean on every registered {@link IGenerationFragment}.
     * 
     * @param modelProject
     * @throws CoreException
     */
    private void cleanAllFragment(IProject modelProject) throws CoreException {
        for (IGenerationFragment fragment : fragmentProvider.getAllFragments()) {
            try {
                cleanFragment(modelProject, fragment);
            } catch (Exception e) {
                logger.error("Exception during full Clean on " + fragment.getClass().getCanonicalName(), e);
            }
        }
    }

    private void cleanFragment(IProject modelProject, IGenerationFragment fragment) throws CoreException {
        IProject fragmentProject = fragmentProvider.getFragmentProject(modelProject, fragment);
        if (fragmentProject.exists()) {
            fragmentProject.refreshLocal(IResource.DEPTH_INFINITE, null);
            // full clean on output directories
            EclipseResourceFileSystemAccess2 fsa = eclipseResourceSupport
                    .createProjectFileSystemAccess(fragmentProject);
            for (OutputConfiguration config : fsa.getOutputConfigurations().values()) {
                cleanFragmentFolder(fragmentProject, config);
            }
            // clean all removable extensions
            ProjectGenerationHelper.removeAllExtension(fragmentProject, fragment.getRemovableExtensions());
            // removing all fragment-related markers
            errorFeedback.clearMarkers(fragmentProject, IErrorFeedback.FRAGMENT_ERROR_TYPE);
        }
    }

    private void cleanFragmentFolder(IProject fragmentProject, OutputConfiguration config) throws CoreException {
        IFolder folder = fragmentProject.getFolder(config.getOutputDirectory());
        if (folder.exists()) {
            for (IResource resource : folder.members()) {
                resource.delete(IResource.KEEP_HISTORY, new NullProgressMonitor());
            }
        }
    }

    /**
     * Removes all packages, based on the Xmi Model.
     * 
     * @param project
     * @throws CoreException
     * @throws IncQueryException
     */
    private void removeExportedPackages(IProject project) throws CoreException, IncQueryException {
        if (getGlobalXmiFile(project).exists()) {
            ArrayList<String> packageNames = new ArrayList<String>();
            Resource globalXmiModel = XmiModelUtil.getGlobalXmiResource(XmiModelUtilRunningOptionEnum.JUST_RESOURCE,
                    project.getName(), new PrepareResourceSetWithLoader(project));
            Iterator<EObject> iter = globalXmiModel.getAllContents();
            while (iter.hasNext()) {
                EObject obj = iter.next();
                if (obj instanceof Pattern) {
                    packageNames.add(jvmInferrerUtil.getPackageName((Pattern) obj));
                }
            }
            ProjectGenerationHelper.removePackageExports(project, packageNames);
        }
    }

    /**
     * Deletes the Global XMI model file from the queries folder.
     * 
     * @param project
     * @throws CoreException
     */
    private void removeXmiModel(IProject project) throws CoreException {
        IFile file = project.getFile(new Path(XmiModelUtil.getGlobalXmiFilePath()));
        if (file != null && file.exists()) {
            file.delete(IResource.KEEP_HISTORY, new NullProgressMonitor());
        }
    }

    /**
     * Performs a normal Clean on the currently built project and all related fragments.
     * 
     * @param context
     * @param relevantDeltas
     * @param monitor
     */
    public void normalClean(IBuildContext context, List<Delta> relevantDeltas, IProgressMonitor monitor) {
        try {
            internalNormalClean(context, relevantDeltas, monitor);
        } catch (Exception e) {
            logger.error("Exception during Normal Clean!", e);
        } finally {
            monitor.worked(1);
        }
    }

    private void internalNormalClean(IBuildContext context, List<Delta> relevantDeltas, IProgressMonitor monitor)
            throws CoreException, IncQueryException {
        IProject modelProject = context.getBuiltProject();
        if (getGlobalXmiFile(modelProject).exists()) {
            Resource globalXmiModel = XmiModelUtil.getGlobalXmiResource(XmiModelUtilRunningOptionEnum.JUST_RESOURCE,
                    modelProject.getName(), new PrepareResourceSetWithLoader(modelProject));
            for (Delta delta : relevantDeltas) {
                Resource deltaResource = context.getResourceSet().getResource(delta.getUri(), true);
                if (delta.getNew() != null /* && shouldGenerate(deltaResource, context) */) {
                    cleanUpDelta(modelProject, deltaResource, globalXmiModel);
                }
            }
        }
    }

    /**
     * Full cleanUp for current deltaResource based on the Global XMI model saved before.
     * 
     * @param project
     * @param deltaResource
     * @param globalXmiModel
     * @throws CoreException
     */
    private void cleanUpDelta(IProject project, Resource deltaResource, Resource globalXmiModel) throws CoreException {
        // do the clean up based on the previous model
        ArrayList<String> packageNames = new ArrayList<String>();
        TreeIterator<EObject> it = globalXmiModel.getAllContents();
        while (it.hasNext()) {
            EObject obj = it.next();
            if (obj instanceof Pattern) {
                Pattern pattern = (Pattern) obj;
                if (pattern.getFileName().equals(deltaResource.getURI().toString())) {
                    // add package name for removal
                    packageNames.add(jvmInferrerUtil.getPackageName(pattern));
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
     * Executes Normal Build cleanUp on the current Built Project (modelProject). Removes all code generated previously
     * for the {@link Pattern}, and marks current {@link Pattern} related extensions for removal.
     * 
     * @param modelProject
     * @param pattern
     * @throws CoreException
     */
    private void executeCleanUpOnModelProject(IProject modelProject, Pattern pattern) throws CoreException {
        EclipseResourceFileSystemAccess2 fsa = eclipseResourceSupport.createProjectFileSystemAccess(modelProject);
        List<String> classPackagePaths = getPathsForJvmInferredClasses(pattern);
        String outputDir = fsa.getOutputConfigurations().get(IFileSystemAccess.DEFAULT_OUTPUT).getOutputDirectory();
        for (String classPackagePath : classPackagePaths) {
            try {
                fsa.deleteFile(classPackagePath);
            } catch (Exception e) {
                String msg = String.format("Java file cannot be deleted through IFileSystemAccess: %s",
                        classPackagePath);
                logger.warn(msg, e);
                IFile classFile = modelProject.getFile(new Path(outputDir + "/" + classPackagePath));
                if (classFile != null && classFile.exists()) {
                    classFile.delete(IResource.KEEP_HISTORY, null);
                }
            }
        }
        // only the extension id and point name is needed for removal
        String extensionId = CorePatternLanguageHelper.getFullyQualifiedName(pattern);
        ensureSupport
                .removeExtension(modelProject, Pair.of(extensionId, IExtensions.MATCHERFACTORY_EXTENSION_POINT_ID));
        ensureSupport.removeExtension(modelProject,
                Pair.of(extensionId, IExtensions.XEXPRESSIONEVALUATOR_EXTENSION_POINT_ID));
    }

    private List<String> getPathsForJvmInferredClasses(Pattern pattern) {
        final String packageName = jvmInferrerUtil.getPackagePath(pattern);
        List<String> classNames = Lists.newArrayList(jvmInferrerUtil.matchClassName(pattern),
                jvmInferrerUtil.matcherClassName(pattern), jvmInferrerUtil.matcherFactoryClassName(pattern),
                jvmInferrerUtil.processorClassName(pattern));
        return Lists.transform(classNames, new Function<String, String>() {
            @Override
            public String apply(String input) {
                return String.format("%s/%s.java", packageName, input);
            }
        });
    }

    /**
     * Executes Normal Build cleanUp on every {@link IGenerationFragment} registered to the current {@link Pattern}.
     * Marks current {@link Pattern} related extensions for removal. If the {@link IProject} related to
     * {@link IGenerationFragment} does not exist, clean up skipped for the fragment.
     * 
     * @param modelProject
     * @param pattern
     * @throws CoreException
     */
    private void executeCleanUpOnFragments(IProject modelProject, Pattern pattern) throws CoreException {
        for (IGenerationFragment fragment : fragmentProvider.getFragmentsForPattern(pattern)) {
            try {
                injector.injectMembers(fragment);
                // clean if the project still exist
                IProject targetProject = fragmentProvider.getFragmentProject(modelProject, fragment);
                if (targetProject.exists()) {
                    targetProject.refreshLocal(IResource.DEPTH_INFINITE, null);
                    EclipseResourceFileSystemAccess2 fsa = eclipseResourceSupport
                            .createProjectFileSystemAccess(targetProject);
                    fragment.cleanUp(pattern, fsa);
                    ensureSupport.removeAllExtension(targetProject, fragment.removeExtension(pattern));
                    // removing all fragment-related markers
                    errorFeedback.clearMarkers(targetProject, IErrorFeedback.FRAGMENT_ERROR_TYPE);
                }
            } catch (Exception e) {
                String msg = String.format("Exception when executing clean for '%s' in fragment '%s'",
                        CorePatternLanguageHelper.getFullyQualifiedName(pattern), fragment.getClass()
                                .getCanonicalName());
                logger.error(msg, e);
            }
        }
    }

    /**
     * Returns an {@link IFile} on the path 'queries/globalEiqModel.xmi' in the project.
     * 
     * @param project
     * @return
     */
    private IFile getGlobalXmiFile(IProject project) {
        return project.getFile(new Path(XmiModelUtil.getGlobalXmiFilePath()));
    }

}
