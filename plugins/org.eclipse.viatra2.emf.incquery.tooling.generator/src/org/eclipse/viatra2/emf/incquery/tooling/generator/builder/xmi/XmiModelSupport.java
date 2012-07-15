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

package org.eclipse.viatra2.emf.incquery.tooling.generator.builder.xmi;

import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.viatra2.emf.incquery.runtime.api.IncQueryEngine;
import org.eclipse.viatra2.emf.incquery.runtime.util.XmiModelUtil;
import org.eclipse.viatra2.patternlanguage.eMFPatternLanguage.PatternModel;
import org.eclipse.xtext.builder.IXtextBuilderParticipant.IBuildContext;
import org.eclipse.xtext.resource.IContainer;
import org.eclipse.xtext.resource.IResourceDescription;
import org.eclipse.xtext.resource.IResourceDescription.Delta;
import org.eclipse.xtext.resource.IResourceDescriptions;
import org.eclipse.xtext.resource.IResourceServiceProvider;
import org.eclipse.xtext.resource.impl.ResourceDescriptionsProvider;

import com.google.inject.Inject;

/**
 * Xmi Model Support for the BuilderParticipant. 
 * Builds an XMI model from an {@link IBuildContext}.
 * Gathers all relevant resources that is accessible from the classpath.
 * 
 * @author Mark Czotter 
 *
 */
public class XmiModelSupport {

	@Inject 
	private XmiModelBuilder xmiModelBuilder;
	
	@Inject
	private ResourceDescriptionsProvider resourceDescriptionsProvider;
	
	@Inject
	private IResourceServiceProvider resourceServiceProvider;
	
	@Inject
	private IContainer.Manager containerManager;
	
	/**
	 * Builds a global XMI model with a {@link XmiModelBuilder} builder. Before
	 * the actual build, finds all relevant eiq resources, so the XMI build is
	 * performed on all currently available {@link PatternModel}.
	 * 
	 * @param baseDelta
	 * @param context
	 * @param monitor
	 * @throws CoreException 
	 */
	public void build(Delta baseDelta, IBuildContext context, IProgressMonitor monitor) {
		// Normal CleanUp and codegen done on every delta, do XMI Model build
		try {
			monitor.beginTask("Building XMI model", 1);
			internalBuild(baseDelta, context, monitor);
		} catch (Exception e) {
			IncQueryEngine.getDefaultLogger().error("Exception during XMI Model Building Phase", e);
		} finally {
			monitor.worked(1);
		}
	}
	
	private void internalBuild(Delta baseDelta, IBuildContext context, IProgressMonitor monitor) throws CoreException {
		Resource deltaResource = context.getResourceSet().getResource(baseDelta.getUri(), true);
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
		xmiModelBuilder.build(context.getResourceSet(), getXmiModelPath(context.getBuiltProject()));
	}

	private String getXmiModelPath(IProject project) throws CoreException {
		IFolder folder = project.getFolder(XmiModelUtil.XMI_OUTPUT_FOLDER);
		IFile file = folder.getFile(XmiModelUtil.GLOBAL_EIQ_FILENAME);
		if (!folder.exists()) {
			folder.create(IResource.DEPTH_INFINITE, false, null);
		}
		if (file.exists()) {
			file.delete(true, null);
		}
		return file.getFullPath().toString();
	}
	
}
