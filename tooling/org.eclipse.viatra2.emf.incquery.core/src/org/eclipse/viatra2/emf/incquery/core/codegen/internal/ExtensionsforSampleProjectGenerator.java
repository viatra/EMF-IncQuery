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

package org.eclipse.viatra2.emf.incquery.core.codegen.internal;

import java.util.Collection;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.pde.core.plugin.IExtensions;
import org.eclipse.pde.core.plugin.IExtensionsModelFactory;
import org.eclipse.pde.core.plugin.IPluginElement;
import org.eclipse.pde.core.plugin.IPluginExtension;
import org.eclipse.pde.internal.core.bundle.WorkspaceBundleModel;
import org.eclipse.pde.internal.core.plugin.WorkspacePluginModel;
import org.eclipse.pde.internal.core.project.PDEProject;
import org.eclipse.viatra2.emf.incquery.core.codegen.CodeGenerationException;
import org.eclipse.viatra2.emf.incquery.core.codegen.internal.SampleHandlerGenerator.HandlerData;

/**
 * Generates the extensions for the sample projects
 * @author Akos Horvath
 *
 */
public class ExtensionsforSampleProjectGenerator {

	IProject project;

	/**
	 * @param project
	 * @throws CodeGenerationException
	 */
	public ExtensionsforSampleProjectGenerator(IProject project)
			throws CodeGenerationException {
		super();
		this.project = project;
	}

	private IPluginElement addIFileFilter(IExtensionsModelFactory factory,
			IPluginExtension contribExtension, String fileExtension)
			throws CoreException {
		// Creates the root for the filter
		IPluginElement builderElement = factory.createElement(contribExtension);
		builderElement.setName("visibleWhen");
		builderElement.setAttribute("checkEnabled", "false");
		// Creates the with element
		IPluginElement builderElementWith = factory
				.createElement(contribExtension);
		builderElementWith.setName("with");
		builderElementWith.setAttribute("variable", "selection");
		builderElement.add(builderElementWith);

		// creates the iterate
		IPluginElement builderElementIterate = factory
				.createElement(contribExtension);
		builderElementIterate.setName("iterate");
		builderElementIterate.setAttribute("ifEmpty", "false");
		builderElementWith.add(builderElementIterate);

		// creates the adapt
		IPluginElement builderElementAdapt = factory
				.createElement(contribExtension);
		builderElementAdapt.setName("adapt");
		builderElementAdapt.setAttribute("type",
				"org.eclipse.core.resources.IFile");
		builderElementIterate.add(builderElementAdapt);

		// creates the test
		IPluginElement builderElementTest = factory
				.createElement(contribExtension);
		builderElementTest.setName("test");
		builderElementTest.setAttribute("value", "*." + fileExtension);
		builderElementTest.setAttribute("property",
				"org.eclipse.core.resources.name");
		builderElementAdapt.add(builderElementTest);

		return builderElement;
	}

	private IPluginElement createMenuContribution(
			IExtensionsModelFactory factory,
			IPluginExtension contribExtensionMenuContrib, String fileExtension,
			String menuID, Map<String, HandlerData> handlers)
			throws CoreException {

		IPluginElement builderElement = factory
				.createElement(contribExtensionMenuContrib);
		// Creates the Project Explorer menu Contribution
		builderElement.setName("menuContribution");
		builderElement.setAttribute("allPopups", "false");
		builderElement.setAttribute("locationURI", menuID);
		// Creates the menu itslef
		IPluginElement builderElementMenu = factory
				.createElement(contribExtensionMenuContrib);
		builderElementMenu.setAttribute("label", "Sample EMF-IncQuery Queries");
		builderElementMenu.setName("menu");

		builderElement.add(builderElementMenu);
		for (Map.Entry<String, SampleHandlerGenerator.HandlerData> entry : handlers
				.entrySet()) {
			IPluginElement builderElementCommand = factory
					.createElement(contribExtensionMenuContrib);
			builderElementCommand.setName("command");
			builderElementCommand.setAttribute("commandId", entry.getKey()
					+ "ID");
			builderElementCommand.setAttribute("style", "push");
			builderElementCommand.add(this.addIFileFilter(factory,
					contribExtensionMenuContrib, fileExtension));
			builderElementMenu.add(builderElementCommand);
		}
		return builderElement;
	}

	@SuppressWarnings("restriction")
	public void contributeToExtensionPoint(Map<String, HandlerData> handlers,
			Collection<String> fileExtensions, IProgressMonitor monitor)
			throws CodeGenerationException {
		IFile manifest = PDEProject.getManifest(project);
		IFile pluginXml = PDEProject.getPluginXml(project);
		// IPluginModel plugin =
		// (IPluginModel)PDECore.getDefault().getModelManager().findModel(project);
		WorkspaceBundleModel bModel = new WorkspaceBundleModel(manifest);
		bModel.setEditable(true);
		WorkspacePluginModel fModel = new WorkspacePluginModel(pluginXml, true);
		fModel.setEditable(true);
		try {
			fModel.load();
			IExtensions extensions = fModel.getExtensions();
			String extensionID = "";
			IExtensionsModelFactory factory = fModel.getFactory();
			IPluginExtension contribExtension = factory.createExtension();

			// Generates the commands
			contribExtension.setId(extensionID);
			contribExtension.setPoint("org.eclipse.ui.commands");
			for (Map.Entry<String, SampleHandlerGenerator.HandlerData> entry : handlers
					.entrySet()) {
				IPluginElement builderElement = factory
						.createElement(contribExtension);
				builderElement.setName("command");
				builderElement.setAttribute("description",
						"Invokes the EMF-IncQuery engine to return all matches of the \""
								+ entry.getValue().getPatternName()
								+ "\" pattern");
				builderElement.setAttribute("id", entry.getKey() + "ID");
				if(entry.getValue().getHandlerName().endsWith("Counter")){
					builderElement.setAttribute("name", "Get number of matches of the \""
							+ entry.getValue().getPatternName() + "\" pattern");
				} else {
					builderElement.setAttribute("name", "Get all matches of the \""
							+ entry.getValue().getPatternName() + "\" pattern");
				}
				contribExtension.add(builderElement);
			}
			extensions.add(contribExtension);
			contribExtension.setInTheModel(true);

			// Generates the menu contributions
			IPluginExtension contribExtensionMenuContrib = factory
					.createExtension();
			contribExtensionMenuContrib.setId(extensionID);
			contribExtensionMenuContrib.setPoint("org.eclipse.ui.menus");

			//registers the different file extensions
			for(String fileExtension: fileExtensions)
			{
				IPluginElement menuElement = this.createMenuContribution(factory,
						contribExtensionMenuContrib, fileExtension,
						"popup:org.eclipse.ui.navigator.ProjectExplorer#PopupMenu",
						handlers);
				contribExtensionMenuContrib.add(menuElement);
	
				menuElement = this.createMenuContribution(factory,
						contribExtensionMenuContrib, fileExtension,
						"popup:org.eclipse.ui.views.ResourceNavigator", handlers);
				contribExtensionMenuContrib.add(menuElement);
	
				menuElement = this.createMenuContribution(factory,
						contribExtensionMenuContrib, fileExtension,
						"popup:org.eclipse.jdt.ui.PackageExplorer", handlers);
				contribExtensionMenuContrib.add(menuElement);
			}

			extensions.add(contribExtensionMenuContrib);
			contribExtensionMenuContrib.setInTheModel(true);

			// generates the handlers
			IPluginExtension contribExtensionHandler = factory
					.createExtension();
			contribExtensionHandler.setId(extensionID);
			contribExtensionHandler.setPoint("org.eclipse.ui.handlers");
			for (Map.Entry<String, SampleHandlerGenerator.HandlerData> entry : handlers
					.entrySet()) {
				IPluginElement builderElementHandler = factory
						.createElement(contribExtensionHandler);
				builderElementHandler.setName("handler");
				builderElementHandler.setAttribute("class", entry.getValue()
						.getHandlerPackage() + "." + entry.getKey());
				builderElementHandler.setAttribute("commandId", entry.getKey()
						+ "ID");
				contribExtensionHandler.add(builderElementHandler);
			}
			extensions.add(contribExtensionHandler);
			contribExtensionHandler.setInTheModel(true);
			fModel.save();
		} catch (CoreException e1) {
			throw new CodeGenerationException(
					"Error during EMF-IncQuery Sample project generation. ", e1);
		}
	}

}
