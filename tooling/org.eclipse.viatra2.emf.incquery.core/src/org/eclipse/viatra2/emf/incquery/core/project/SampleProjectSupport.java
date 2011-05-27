/*******************************************************************************
 * Copyright (c) 2004-2010 Akos Horvath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Akos Horvath - initial API and implementation
 *******************************************************************************/


package org.eclipse.viatra2.emf.incquery.core.project;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.pde.core.project.IBundleProjectDescription;
import org.eclipse.pde.core.project.IBundleProjectService;
import org.eclipse.pde.core.project.IRequiredBundleDescription;
import org.eclipse.viatra2.emf.incquery.core.IncQueryPlugin;
import org.eclipse.viatra2.framework.FrameworkManager;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.Version;

public class SampleProjectSupport {

	/**
	 * <p>
	 * Creates a new IncQuery Sample UI project
	 * </p>
	 * @param monitor
	 * @param correspondingIncQueryProjectID
	 * @param incQueryProjectName
	 * @return the created IncQuery sample project
	 * @throws CoreException
	 * @throws OperationCanceledException
	 */
	public static IProject createProject(IProgressMonitor monitor,
			String correspondingIncQueryProjectID,
			String incQueryProjectName) throws CoreException,
			OperationCanceledException {
		BundleContext context = null;
		ServiceReference ref = null;

		try {
			monitor.beginTask("", 2000);

			IWorkspace workspace = ResourcesPlugin.getWorkspace();
			IWorkspaceRoot root = workspace.getRoot();
			IProject proj = root.getProject(incQueryProjectName+".ui.sample");

			if(proj.exists())
				{
				proj.delete(true, true, monitor);
				}

			proj.create(new SubProgressMonitor(monitor, 1000));
			if (monitor.isCanceled()) {
				throw new OperationCanceledException();
			}

			proj.open(IResource.BACKGROUND_REFRESH, new SubProgressMonitor(
					monitor, 1000));
			/* Adding project nature */
			IProjectDescription desc = proj.getDescription();
			List<String> newNatures = new ArrayList<String>();
			newNatures.addAll(Arrays.asList(desc.getNatureIds()));
			newNatures.add(JavaCore.NATURE_ID);
			newNatures.add("org.eclipse.pde.PluginNature");
			desc.setNatureIds(newNatures.toArray(new String[] {}));
			proj.setDescription(desc, monitor);
			/* Creating plug-in information */
			context = IncQueryPlugin.plugin.context;
			ref = context
					.getServiceReference(IBundleProjectService.class.getName());
			IBundleProjectService service = (IBundleProjectService) context
					.getService(ref);
			IBundleProjectDescription bundleDesc = service.getDescription(proj);
			bundleDesc.setBundleName(desc.getName());
			bundleDesc.setBundleVersion(new Version(0, 0, 1, "qualifier"));
			bundleDesc.setSingleton(true);
			bundleDesc.setTargetVersion(IBundleProjectDescription.VERSION_3_6);
			bundleDesc.setSymbolicName(desc.getName());
			bundleDesc.setExtensionRegistry(true);
			bundleDesc.setActivator("handlers.Activator");
			// Adding dependencies
			IRequiredBundleDescription[] reqBundles = new IRequiredBundleDescription[] {
					service.newRequiredBundle("org.eclipse.pde.core", null, false, false),
					service.newRequiredBundle("org.eclipse.emf.ecore", null, false, false),
					service.newRequiredBundle("org.eclipse.ui", null, false, false),
					service.newRequiredBundle("org.eclipse.core.runtime", null, false, false),
					service.newRequiredBundle(correspondingIncQueryProjectID, null, false, true)};
			bundleDesc.setRequiredBundles(reqBundles);
			bundleDesc.apply(monitor);
			context.ungetService(ref);
			/* Creating Java folders */
			final List<IClasspathEntry> classpathEntries = new ArrayList<IClasspathEntry>();
			final IJavaProject javaProject = JavaCore.create(proj);
			final IFolder srcContainer = proj.getFolder("src");
			srcContainer.create(true, true, monitor);
			final IClasspathEntry srcClasspathEntry = JavaCore
					.newSourceEntry(srcContainer.getFullPath());
			classpathEntries.add(srcClasspathEntry);
//			final IFolder srcGenContainer = proj.getFolder("src-gen");
//			srcGenContainer.create(true, true, monitor);
//			final IClasspathEntry srcGenClasspathEntry = JavaCore
//					.newSourceEntry(srcGenContainer.getFullPath());
//			classpathEntries.add(srcGenClasspathEntry);
			// Plug-in classpath
			classpathEntries.add(JavaCore.newContainerEntry(new Path(
					"org.eclipse.pde.core.requiredPlugins")));
			classpathEntries.add(JavaRuntime.getDefaultJREContainerEntry());
			javaProject.setRawClasspath(classpathEntries
					.toArray(new IClasspathEntry[classpathEntries.size()]),
					monitor);

			/* TODO UGLY: Add the build.properties to the project */
			File buildProp = FrameworkManager.getFileFromBundle(
					IncQueryNature.BUNDLE_ID, IncQueryNature.SOURCE_BUILD_PROPERTIES);
			addFileToProject(proj, new Path("build.properties"), new FileInputStream(buildProp), monitor);

		return proj;
		} catch (IOException ioe) {
			IStatus status = new Status(IStatus.ERROR,
					IncQueryNature.BUNDLE_ID, IStatus.ERROR,
					ioe.getLocalizedMessage(), ioe);
			throw new CoreException(status);
		} finally {
			monitor.done();
			if(context != null && ref != null)
				context.ungetService(ref);
		}

	}

	/**
	 * Adds a file to a container.
	 * @param container the container to add the file to
	 * @param path the path of the newly created file
	 * @param contentStream the file will be filled with this stream's contents
	 * @param monitor
	 * @throws CoreException
	 */
	private static void addFileToProject(IContainer container, Path path,
			InputStream contentStream, IProgressMonitor monitor)
			throws CoreException {
		final IFile file = container.getFile(path);

		if (file.exists()) {
			file.setContents(contentStream, true, true, monitor);
		} else {
			file.create(contentStream, true, monitor);
		}
	}
}
