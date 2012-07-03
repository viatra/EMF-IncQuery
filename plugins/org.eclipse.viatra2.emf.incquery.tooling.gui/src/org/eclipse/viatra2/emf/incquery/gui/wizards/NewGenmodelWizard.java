package org.eclipse.viatra2.emf.incquery.gui.wizards;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.emf.codegen.ecore.genmodel.GenModel;
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
import org.eclipse.viatra2.emf.incquery.core.project.IncQueryNature;
import org.eclipse.viatra2.emf.incquery.gui.wizards.internal.operations.CompositeWorkspaceModifyOperation;
import org.eclipse.viatra2.emf.incquery.gui.wizards.internal.operations.CreateGenmodelOperation;
import org.eclipse.viatra2.emf.incquery.gui.wizards.internal.operations.EnsureProjectDependencies;
import org.eclipse.viatra2.emf.incquery.tooling.generator.genmodel.IEiqGenmodelProvider;
import org.eclipse.xtext.ui.resource.IResourceSetProvider;

import com.google.inject.Inject;

public class NewGenmodelWizard extends Wizard implements INewWizard {

	private IWorkbench workbench;
	private IStructuredSelection selection;
	private SelectIncQueryProjectPage projectPage;
	private NewEiqGenmodelPage genmodelPage;

	@Inject
	IEiqGenmodelProvider genmodelProvider;
	@Inject
	IResourceSetProvider resourceSetProvider;

	@Override
	public void addPages() {
		projectPage = new SelectIncQueryProjectPage(
				"Select EMF-IncQuery project", selection, genmodelProvider);
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
		WorkspaceModifyOperation projectOp = new EnsureProjectDependencies(
				project, genmodelDependencies);
		WorkspaceModifyOperation genmodelOp = new CreateGenmodelOperation(
				project, genmodelPage.getSelectedGenmodels(), genmodelProvider,
				resourceSetProvider);
		op = new CompositeWorkspaceModifyOperation(
				new WorkspaceModifyOperation[] { projectOp, genmodelOp },
				"Creating generator model");

		try {
			getContainer().run(true, true, op);
		} catch (InterruptedException e) {
			return false;
		} catch (InvocationTargetException e) {
			// TODO real error logging!
			Throwable realException = e.getTargetException();
			realException.printStackTrace();
			MessageDialog.openError(getShell(), "Error",
					realException.getMessage());
			return false;
		}

		IFile genmodelFile = (IFile) project.findMember(IncQueryNature.IQGENMODEL);
		BasicNewProjectResourceWizard.selectAndReveal(genmodelFile,
				workbench.getActiveWorkbenchWindow());

		IWorkbenchPage page = workbench.getActiveWorkbenchWindow().getActivePage();
		
		try {
			page.openEditor(new FileEditorInput(genmodelFile), workbench.getEditorRegistry().getDefaultEditor(genmodelFile.getName()).getId());
		} catch (PartInitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
	}

}
