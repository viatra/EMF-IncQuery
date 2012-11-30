/*******************************************************************************
 * Copyright (c) 2010-2012, Tamas Szabo, Zoltan Ujhelyi, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Tamas Szabo, Zoltan Ujhelyi - initial API and implementation
 *******************************************************************************/

package org.eclipse.incquery.tooling.ui.wizards;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.emf.codegen.ecore.genmodel.GenModel;
import org.eclipse.incquery.tooling.core.generator.genmodel.IEiqGenmodelProvider;
import org.eclipse.incquery.tooling.core.project.IncQueryNature;
import org.eclipse.incquery.tooling.ui.wizards.internal.operations.CompositeWorkspaceModifyOperation;
import org.eclipse.incquery.tooling.ui.wizards.internal.operations.CreateGenmodelOperation;
import org.eclipse.incquery.tooling.ui.wizards.internal.operations.EnsureProjectDependencies;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.actions.WorkspaceModifyOperation;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.wizards.newresource.BasicNewProjectResourceWizard;
import org.eclipse.xtext.ui.resource.IResourceSetProvider;

import com.google.inject.Inject;

public class NewGenmodelWizard extends Wizard implements INewWizard {

    private IWorkbench workbench;
    private IStructuredSelection selection;
    private SelectIncQueryProjectPage projectPage;
    private NewEiqGenmodelPage genmodelPage;

    @Inject
    private IEiqGenmodelProvider genmodelProvider;
    @Inject
    private IResourceSetProvider resourceSetProvider;
    @Inject
    private Logger logger;

    @Override
    public void addPages() {
        projectPage = new SelectIncQueryProjectPage("Select EMF-IncQuery project", selection, logger);
        addPage(projectPage);
        genmodelPage = new NewEiqGenmodelPage(false);
        addPage(genmodelPage);
    }

    @Override
    public void init(IWorkbench workbench, IStructuredSelection selection) {
        this.workbench = workbench;
        this.selection = selection;

    }

    @Override
    public boolean performFinish() {
        IProject project = projectPage.getSelectedProject();

        WorkspaceModifyOperation op = null;
        List<String> genmodelDependencies = new ArrayList<String>();
        for (GenModel model : genmodelPage.getSelectedGenmodels()) {
            String modelPluginID = model.getModelPluginID();
            if (!genmodelDependencies.contains(modelPluginID)) {
                genmodelDependencies.add(modelPluginID);
            }
        }
        WorkspaceModifyOperation projectOp = new EnsureProjectDependencies(project, genmodelDependencies);
        WorkspaceModifyOperation genmodelOp = new CreateGenmodelOperation(project, genmodelPage.getSelectedGenmodels(),
                genmodelProvider, resourceSetProvider);
        op = new CompositeWorkspaceModifyOperation(new WorkspaceModifyOperation[] { projectOp, genmodelOp },
                "Creating generator model");

        try {
            getContainer().run(true, true, op);
        } catch (InterruptedException e) {
            return false;
        } catch (InvocationTargetException e) {
            Throwable realException = e.getTargetException();
            logger.error("Cannot initialize EMF-IncQuery generator model " + realException.getMessage(), realException);
            MessageDialog.openError(getShell(), "Error", realException.getMessage());
            return false;
        }

        IFile genmodelFile = (IFile) project.findMember(IncQueryNature.IQGENMODEL);
        BasicNewProjectResourceWizard.selectAndReveal(genmodelFile, workbench.getActiveWorkbenchWindow());

        IWorkbenchPage page = workbench.getActiveWorkbenchWindow().getActivePage();

        try {
            page.openEditor(new FileEditorInput(genmodelFile),
                    workbench.getEditorRegistry().getDefaultEditor(genmodelFile.getName()).getId());
        } catch (PartInitException e) {
            logger.error("Cannot open EMF-IncQuery generator model", e);
        }
        return true;
    }

}
