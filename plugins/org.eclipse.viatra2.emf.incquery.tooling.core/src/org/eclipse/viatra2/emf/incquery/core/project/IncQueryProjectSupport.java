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

package org.eclipse.viatra2.emf.incquery.core.project;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Path;
import org.eclipse.pde.core.project.IBundleProjectDescription;
import org.eclipse.pde.core.project.IBundleProjectService;
import org.eclipse.viatra2.emf.incquery.core.IncQueryPlugin;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import com.google.common.collect.ImmutableList;

/**
 * @author Zoltan Ujhelyi, Istvan Rath
 * 
 *         Support methods for creating a project
 */
public class IncQueryProjectSupport {

	
	private IncQueryProjectSupport() {}
	/**
	 * Creates a new IncQuery project: a plug-in project with src and src-gen
	 * folders and specific dependencies.
	 * 
	 * @param description
	 * @param proj
	 * @param monitor
	 * @throws CoreException
	 * @throws OperationCanceledException
	 */
	public static void createProject(IProjectDescription description,
			IProject proj, IProgressMonitor monitor) throws CoreException {
		ImmutableList<String> dependencies = ImmutableList.of(
				"org.eclipse.pde.core", "org.eclipse.emf.ecore",
				"org.eclipse.emf.transaction",
				"org.eclipse.viatra2.emf.incquery.runtime",
				"org.eclipse.xtext.xbase.lib");
		BundleContext context = null;
		ServiceReference<IBundleProjectService> ref = null;

		try {

			monitor.beginTask("", 2000);
			/* Creating plug-in information */
			context = IncQueryPlugin.plugin.context;
			ref = context.getServiceReference(IBundleProjectService.class);
			final IBundleProjectService service = context.getService(ref);
			IBundleProjectDescription bundleDesc = service.getDescription(proj);
			IPath[] additionalBinIncludes = new IPath[] {new Path("plugin.xml"), new Path("queries/")};
			ProjectGenerationHelper.fillProjectMetadata(proj, dependencies, service, bundleDesc, additionalBinIncludes);
			bundleDesc.apply(monitor);
			// Adding IncQuery-specific natures
			ProjectGenerationHelper.addNatures(proj, new String[] {
					IncQueryNature.NATURE_ID,
					"org.eclipse.xtext.ui.shared.xtextNature" }, monitor);
		} finally {
			monitor.done();
			if (context != null && ref != null) {
				context.ungetService(ref);
			}
		}
	}

}
