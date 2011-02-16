/*******************************************************************************
 * Copyright (c) 2004-2010 Gabor Bergmann and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Gabor Bergmann - initial API and implementation
 *******************************************************************************/

package org.eclipse.viatra2.emf.incquery.core.codegen.internal;


import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.pde.core.plugin.IExtensions;
import org.eclipse.pde.core.plugin.IExtensionsModelFactory;
import org.eclipse.pde.core.plugin.IPluginElement;
import org.eclipse.pde.core.plugin.IPluginExtension;
import org.eclipse.pde.core.plugin.IPluginExtensionPoint;
import org.eclipse.pde.core.plugin.IPluginModel;
import org.eclipse.pde.internal.core.PDECore;
import org.eclipse.pde.internal.core.bundle.WorkspaceBundleModel;
import org.eclipse.pde.internal.core.plugin.WorkspacePluginModel;
import org.eclipse.pde.internal.core.project.PDEProject;
import org.eclipse.viatra2.emf.incquery.core.codegen.CodeGenerationException;
import org.eclipse.viatra2.gtasmmodel.gtasm.metamodel.gt.GTPattern;

/**
 * @author Bergmann Gábor
 *
 */
public class ExtensionContributionsGenerator {
	IProject project;

	/**
	 * @param project
	 * @throws CodeGenerationException 
	 */
	public ExtensionContributionsGenerator(IProject project) throws CodeGenerationException {
		super();
		this.project = project;
	}
	
	
	@SuppressWarnings("restriction")
	public void contributeToExtensionPoint(Map<GTPattern, String> contributionClassNames, IProgressMonitor monitor) 
		throws CodeGenerationException {
		IFile manifest = PDEProject.getManifest(project);
		IFile pluginXml = PDEProject.getPluginXml(project);
		IPluginModel plugin = (IPluginModel)PDECore.getDefault().getModelManager().findModel(project);
		WorkspaceBundleModel bModel = new WorkspaceBundleModel(manifest);
		bModel.setEditable(true);
		WorkspacePluginModel fModel = new WorkspacePluginModel(pluginXml, true);
		fModel.setEditable(true);
		try {
			String extensionID = project.getName() + ".generatedContribution";
			fModel.load();
			//Storing a write-only plugin.xml model
			IExtensions extensions = fModel.getExtensions();
			//Storing a read-only plugin.xml model
			IExtensions readExtension = plugin.getExtensions();
			for (IPluginExtension extension : readExtension.getExtensions()) {
				String id = extension.getId();
				//The second contentEquals is needed as the returned id contains the project name twice 
				if (id==null || !(id.contentEquals(extensionID) || id.contentEquals(project.getName() + "." + extensionID))) {
					extensions.add(extension);
				}
			}
			for (IPluginExtensionPoint point : readExtension.getExtensionPoints()) {
				extensions.add(point);
			}
			IExtensionsModelFactory factory = fModel.getFactory();
			IPluginExtension contribExtension = factory.createExtension();
			contribExtension.setId(extensionID);
			contribExtension.setPoint("org.eclipse.viatra2.emf.incquery.codegen.patternmatcher.builder");
			for (Map.Entry<GTPattern, String> entry : contributionClassNames.entrySet()) {
				IPluginElement builderElement = factory.createElement(contribExtension);
				builderElement.setName("pattern-builder");
				builderElement.setAttribute("build-class", entry.getValue());
				builderElement.setAttribute("pattern-fqn", entry.getKey().getFqn());
				contribExtension.add(builderElement);
			}
			extensions.add(contribExtension);
			contribExtension.setInTheModel(true);
			fModel.save();
		} catch (CoreException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

}
