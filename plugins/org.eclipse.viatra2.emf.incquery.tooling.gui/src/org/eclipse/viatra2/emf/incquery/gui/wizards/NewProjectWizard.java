/*******************************************************************************
 * Copyright (c) 2010-2012, Zoltan Ujhelyi, Tamas Szabo, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Zoltan Ujhelyi, Tamas Szabo - initial API and implementation
 *******************************************************************************/

package org.eclipse.viatra2.emf.incquery.gui.wizards;

import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.emf.codegen.ecore.genmodel.GenModel;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.actions.WorkspaceModifyOperation;
import org.eclipse.ui.dialogs.WizardNewProjectCreationPage;
import org.eclipse.ui.wizards.newresource.BasicNewProjectResourceWizard;
import org.eclipse.viatra2.emf.incquery.gui.wizards.internal.operations.CompositeWorkspaceModifyOperation;
import org.eclipse.viatra2.emf.incquery.gui.wizards.internal.operations.CreateGenmodelOperation;
import org.eclipse.viatra2.emf.incquery.gui.wizards.internal.operations.CreateProjectOperation;
import org.eclipse.viatra2.emf.incquery.tooling.generator.genmodel.IEiqGenmodelProvider;
import org.eclipse.xtext.ui.resource.IResourceSetProvider;

import com.google.common.collect.ImmutableList;
import com.google.inject.Inject;

/**
 * A wizard class for initializing an EMF IncQuery project.
 * 
 * @author Zoltan Ujhelyi
 * 
 */
public class NewProjectWizard extends Wizard implements INewWizard {

	private WizardNewProjectCreationPage projectCreationPage;
	private NewEiqGenmodelPage genmodelPage;
	private IProject project;
	private IWorkbench workbench;
	private IWorkspace workspace;

	@Inject
	private IEiqGenmodelProvider genmodelProvider;
	@Inject
	private IResourceSetProvider resourceSetProvider;
	@Inject
	private Logger logger;
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.wizard.Wizard#addPages()
	 */
	@Override
	public void addPages() {
		projectCreationPage = new WizardNewProjectCreationPage(
				"NewIncQueryProject");
		projectCreationPage.setTitle("New EMF IncQuery Project");
		projectCreationPage
				.setDescription("Create a new EMF IncQuery project.");
		addPage(projectCreationPage);
		genmodelPage = new NewEiqGenmodelPage(true);
		addPage(genmodelPage);
	}

	public void init(IWorkbench workbench, IStructuredSelection selection) {
		this.workbench = workbench;
		workspace = ResourcesPlugin.getWorkspace();
	}

	@Override
	public boolean performFinish() {
		if (project != null) {
			return true;
		}
		final IProject projectHandle = projectCreationPage.getProjectHandle();
		if (projectHandle.exists()) {
			return false;
		}
		URI projectURI = (!projectCreationPage.useDefaults()) ? projectCreationPage
				.getLocationURI() : null;
		final IProjectDescription description = workspace
				.newProjectDescription(projectHandle.getName());
		description.setLocationURI(projectURI);

		WorkspaceModifyOperation op = null;  
		if (genmodelPage.isCreateGenmodelChecked()) {
			List<String> genmodelDependencies = new ArrayList<String>();
			for (GenModel model : genmodelPage.getSelectedGenmodels()) {
				String modelPluginID = model.getModelPluginID();
				if (!genmodelDependencies.contains(modelPluginID)) {
					genmodelDependencies.add(modelPluginID);
				}
			}
			WorkspaceModifyOperation projectOp = new CreateProjectOperation(
					projectHandle, description, genmodelDependencies);
			WorkspaceModifyOperation genmodelOp = new CreateGenmodelOperation(
					projectHandle, genmodelPage.getSelectedGenmodels(),
					genmodelProvider, resourceSetProvider);
			op = new CompositeWorkspaceModifyOperation(
					new WorkspaceModifyOperation[] { projectOp, genmodelOp },
					"Creating project");
		} else {
			op = new CreateProjectOperation(projectHandle, description, ImmutableList.<String>of());
		}

		try {
			getContainer().run(true, true, op);
		} catch (InterruptedException e) {
			return false;
		} catch (InvocationTargetException e) {
			//Removing project if it is partially created
			if (projectHandle.exists()) {
				try {
					projectHandle.delete(true, new NullProgressMonitor());
				} catch (CoreException e1) {
					logger.error("Cannot remove partially created EMF-IncQuery project.", e1);
				}
			}
			Throwable realException = e.getTargetException();
			logger.error("Cannot create EMF-IncQuery project: " + realException.getMessage(), realException);
			MessageDialog.openError(getShell(), "Error",
					realException.getMessage());
			return false;
		}

		project = projectHandle;

		BasicNewProjectResourceWizard.selectAndReveal(project,
				workbench.getActiveWorkbenchWindow());

		return true;

	}

}
