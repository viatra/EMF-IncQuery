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

package org.eclipse.viatra2.emf.incquery.validation.codegen.internal;

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
import org.eclipse.viatra2.emf.incquery.model.incquerygenmodel.IncQueryGenmodel;
import org.eclipse.viatra2.emf.incquery.validation.codegen.internal.SampleConstraintGenerator.ConstraintData;

/**
 * Generates the extensions for the sample projects
 * @author Akos Horvath
 *
 */
public class ExtensionsforSampleValidationProjectGenerator {

	IProject project;
	IncQueryGenmodel iqGen;
	
	/**
	 * @param project
	 * @throws CodeGenerationException
	 */
	public ExtensionsforSampleValidationProjectGenerator(IProject project, IncQueryGenmodel iqGen)
			throws CodeGenerationException {
		super();
		this.iqGen = iqGen;
		this.project = project;
	}


	/*private IPluginElement addIFileFilter(IExtensionsModelFactory factory,
			IPluginExtension contribExtension, String editorId)
			throws CoreException {
		// Creates the root for the filter
		IPluginElement builderElement = factory.createElement(contribExtension);
		builderElement.setName("visibleWhen");
		//builderElement.setAttribute("checkEnabled", "false");
		// Creates the with element
		IPluginElement builderElementWith = factory
				.createElement(builderElement);
		builderElementWith.setName("with");
		builderElementWith.setAttribute("variable", "activeEditorId");
		IPluginElement builderElementEquals = factory.createElement(builderElementWith);
		builderElementEquals.setName("equals");
		builderElementEquals.setAttribute("value", editorId);
		builderElementWith.add(builderElementEquals);
		builderElement.add(builderElementWith);
		
		builderElementWith = factory.createElement(builderElement);
		builderElementWith.setName("with");
		builderElementWith.setAttribute("variable", "selection");
		builderElement.add(builderElementWith);

		// creates the iterate
		IPluginElement builderElementCount = factory
				.createElement(builderElement);
		builderElementCount.setName("count");
		builderElementCount.setAttribute("value", "+");
		builderElementWith.add(builderElementCount);
		*/
		// creates the adapt
		/*IPluginElement builderElementAdapt = factory
				.createElement(contribExtension);
		builderElementAdapt.setName("adapt");
		builderElementAdapt.setAttribute("type",
				"org.eclipse.core.resources.IFile");
		builderElementCount.add(builderElementAdapt);*/

		// creates the test
		/*IPluginElement builderElementTest = factory
				.createElement(contribExtension);
		builderElementTest.setName("test");
		builderElementTest.setAttribute("value", "*." + fileExtension);
		builderElementTest.setAttribute("property",
				"org.eclipse.core.resources.name");
		builderElementAdapt.add(builderElementTest);*/
/*
		return builderElement;
	}
*/
	/*private IPluginElement createMenuContribution(
			IExtensionsModelFactory factory,
			IPluginExtension contribExtensionMenuContrib, String editorID,
			String menuID)
			throws CoreException {

		IPluginElement builderElement = factory
				.createElement(contribExtensionMenuContrib);
		// Creates the Project Explorer menu Contribution
		builderElement.setName("menuContribution");
		//builderElement.setAttribute("allPopups", "false");
		builderElement.setAttribute("locationURI", menuID);
		// Creates the menu itself
		//IPluginElement builderElementMenu = factory
		//		.createElement(contribExtensionMenuContrib);
		//builderElementMenu.setAttribute("label", "Initialize Validation (powered by EMF-INCQuery)");
		//builderElementMenu.setName("menu");

		//builderElement.add(builderElementMenu);
		//for (Map.Entry<String, ConstraintData> entry : handlers
		//		.entrySet()) {
		IPluginElement builderElementCommand = factory
				.createElement(builderElement);
		builderElementCommand.setName("command");
		builderElementCommand.setAttribute("commandId",
				"org.eclipse.viatra2.emf.incquery.validation.commands.initializevalidation");
		builderElementCommand.setAttribute("label", "Initialize Validation (powered by EMF-INCQuery)");
		builderElementCommand.setAttribute("style", "push");
		builderElementCommand.add(this.addIFileFilter(factory,
				contribExtensionMenuContrib, editorID));
		
		builderElement.add(builderElementCommand);
		//}
		return builderElement;
	}*/
	
	private IPluginElement createEditorContribution(IExtensionsModelFactory factory,
			IPluginExtension contribExtensionEditorContrib, String editorID, String domain) throws CoreException {
		
		IPluginElement builderEditorContributionElement = factory
			.createElement(contribExtensionEditorContrib);
		builderEditorContributionElement.setName("editorContribution");
		builderEditorContributionElement.setAttribute("id", project.getName() + "." + domain + ".editorContribution");
		builderEditorContributionElement.setAttribute("targetID", editorID);
		
		IPluginElement builderActionElement = factory.createElement(builderEditorContributionElement);
		builderActionElement.setName("action");
		builderActionElement.setAttribute("class", "org.eclipse.viatra2.emf.incquery.validation.ui.actions.InitValidationActionDelegate");
		builderActionElement.setAttribute("id", project.getName() + "." + domain + ".action");
		builderActionElement.setAttribute("label", "EMF-IncQuery Validation for " + domain);
		builderActionElement.setAttribute("style", "push");
		builderActionElement.setAttribute("toolbarPath", "file/export");
		
		builderEditorContributionElement.add(builderActionElement);
		
		return builderEditorContributionElement;
	}

	@SuppressWarnings("restriction")
	public void contributeToExtensionPoint(Map<String, ConstraintData> constraints,
			Map<String,String> editor2domain, IProgressMonitor monitor)
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
			IExtensionsModelFactory factory = fModel.getFactory();
			//IPluginExtension contribExtension = factory.createExtension();

			// Generates the commands
			//contribExtension.setId(extensionID);
			//contribExtension.setPoint("org.eclipse.ui.menus");
			/*for (Map.Entry<String,ConstraintData> entry : handlers
					.entrySet()) {
				IPluginElement builderElement = factory
						.createElement(contribExtension);
				builderElement.setName("command");
				builderElement.setAttribute("description",
						"Invokes the EMF-IncQuery engine to return all matches of the "
								+ entry.getValue().getPatternName()
								+ " pattern");
				builderElement.setAttribute("id", entry.getKey() + "ID");
				if(entry.getValue().getConstraintName().endsWith("Counter")){
					builderElement.setAttribute("name", "Get number of matches of the "
							+ entry.getValue().getPatternName() + " pattern");
				} else {
					builderElement.setAttribute("name", "Get all matches of the "
							+ entry.getValue().getPatternName() + " pattern");
				}
				contribExtension.add(builderElement);
			}*/
			//extensions.add(contribExtension);
			//contribExtension.setInTheModel(true);

			// Generates the menu contributions
			/*IPluginExtension contribExtensionMenuContrib = factory
			.createExtension();
			contribExtensionMenuContrib.setId("context-menus");
			contribExtensionMenuContrib.setPoint("org.eclipse.ui.menus");
			IPluginElement papyrusMenuElement = this.createMenuContribution(factory, contribExtensionMenuContrib,
					"org.eclipse.papyrus.core.papyrusEditor",
					"popup:org.eclipse.gmf.runtime.diagram.ui.DiagramEditorContextMenu");
			contribExtensionMenuContrib.add(papyrusMenuElement);
			extensions.add(contribExtensionMenuContrib);
			*/
			// Generates the menu contributions
			IPluginExtension contribExtensionEditorContrib = factory
					.createExtension();
			//contribExtensionMenuContrib.setId("context-menus");
			//contribExtensionMenuContrib.setPoint("org.eclipse.ui.menus");
			contribExtensionEditorContrib.setPoint("org.eclipse.ui.editorActions");

			//registers the different file extensions
			for(String editorId: editor2domain.keySet())
			{
				IPluginElement menuElement = createEditorContribution(factory, contribExtensionEditorContrib, editorId, editor2domain.get(editorId)); 
					//this.createMenuContribution(factory, contribExtensionMenuContrib, editorId,
					//	"popup:org.eclipse.gmf.runtime.diagram.ui.DiagramEditorContextMenu");
				contribExtensionEditorContrib.add(menuElement);
	
				/*menuElement = this.createMenuContribution(factory,
						contribExtensionMenuContrib, fileExtension,
						"popup:org.eclipse.ui.views.ResourceNavigator", handlers);
				contribExtensionMenuContrib.add(menuElement);
	
				menuElement = this.createMenuContribution(factory,
						contribExtensionMenuContrib, fileExtension,
						"popup:org.eclipse.jdt.ui.PackageExplorer", handlers);
				contribExtensionMenuContrib.add(menuElement);*/
			}

			extensions.add(contribExtensionEditorContrib);
			contribExtensionEditorContrib.setInTheModel(true);
			
			// generates the handlers
			IPluginExtension contribExtensionConstraint = factory
					.createExtension();
			contribExtensionConstraint.setId("");
			contribExtensionConstraint.setPoint("org.eclipse.viatra2.emf.incquery.validation.constraint");
			for (Map.Entry<String, ConstraintData> entry : constraints
					.entrySet()) {
				IPluginElement builderElementConstraint = factory
						.createElement(contribExtensionConstraint);
				builderElementConstraint.setName("constraint");
				builderElementConstraint.setAttribute("class", entry.getValue()
						.getConstraintPackage() + "." + entry.getKey());
				builderElementConstraint.setAttribute("name", entry.getKey());
				contribExtensionConstraint.add(builderElementConstraint);
			}
			extensions.add(contribExtensionConstraint);
			contribExtensionConstraint.setInTheModel(true);
			fModel.save();
		} catch (CoreException e1) {
			throw new CodeGenerationException(
					"Error during EMF-IncQuery Sample Validation project generation. ", e1);
		}
	}

}
