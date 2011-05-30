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


package org.eclipse.viatra2.emf.incquery.validation.codegen.project;


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
import org.eclipse.viatra2.emf.incquery.core.project.IncQueryNature;
import org.eclipse.viatra2.emf.incquery.core.project.ProjectGenerationHelper;
import org.eclipse.viatra2.emf.incquery.validation.codegen.ValidationCodegenPlugin;
import org.eclipse.viatra2.framework.FrameworkManager;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.Version;

public class SampleValidationProjectSupport {

	/**
	 * <p>
	 * Creates a new IncQuery project together with its default folder
	 * structure:
	 * </p>
	 * <ul>
	 * <li>models
	 * <ul>
	 * <li>vtcl</li>
	 * </ul>
	 * </li>
	 * </ul>
	 * <p>
	 * It also creates a VPM model file in the models folder.
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

			String validationProjectName = incQueryProjectName+".validation.sample";
			IProject proj = ProjectGenerationHelper
			.initializeProject(validationProjectName,
					ProjectGenerationHelper.generatedNatures, monitor);
			IProjectDescription desc = proj.getDescription();
			/* Creating plug-in information */
			context = ValidationCodegenPlugin.plugin.context;
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
					service.newRequiredBundle("org.eclipse.viatra2.emf.incquery.validation", null, false, true),
					service.newRequiredBundle(correspondingIncQueryProjectID, null, false, true)};
			bundleDesc.setRequiredBundles(reqBundles);
			bundleDesc.apply(monitor);
			context.ungetService(ref);
			/* Creating Java folders */
			ProjectGenerationHelper.initializeClasspath(proj, monitor, ProjectGenerationHelper.singleSourceFolder);
			ProjectGenerationHelper.initializeBuildProperties(proj, monitor);
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
}
