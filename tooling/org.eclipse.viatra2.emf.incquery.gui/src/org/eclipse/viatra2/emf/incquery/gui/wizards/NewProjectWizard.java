package org.eclipse.viatra2.emf.incquery.gui.wizards;


import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.actions.WorkspaceModifyOperation;
import org.eclipse.ui.dialogs.WizardNewProjectCreationPage;
import org.eclipse.ui.wizards.newresource.BasicNewProjectResourceWizard;
import org.eclipse.viatra2.emf.incquery.core.genmodel.GenModelHelper;
import org.eclipse.viatra2.emf.incquery.core.project.IncQueryNature;
import org.eclipse.viatra2.emf.incquery.core.project.IncQueryProjectSupport;
import org.eclipse.viatra2.emf.incquery.gui.IncQueryGUIPlugin;

/**
 * A wizard class for creating an Incremental Query project with an IncQuery genmodel.
 * @author Zoltan Ujhelyi
 *
 */
public class NewProjectWizard extends Wizard implements INewWizard {

	private WizardNewProjectCreationPage projectCreationPage;
	private IProject project;
	private IWorkbench workbench;

	/*
	 * (non-Javadoc)
	 *
	 * @see org.eclipse.jface.wizard.Wizard#addPages()
	 */
	@Override
	public void addPages() {
		projectCreationPage = new WizardNewProjectCreationPage(
				"NewIncQueryProject");
		projectCreationPage.setTitle("New Incremental Query Project");
		projectCreationPage
				.setDescription("Create a new Incremental Query project.");
		addPage(projectCreationPage);
		// super.addPages();
	}

	public void init(IWorkbench workbench, IStructuredSelection selection) {
		this.workbench = workbench;
		//this.selection = selection;
	}

	@Override
	public boolean performFinish() {
		if (project != null) {
			return true;
		}
		final IProject projectHandle = projectCreationPage.getProjectHandle();
		URI projectURI = (!projectCreationPage.useDefaults()) ? projectCreationPage
				.getLocationURI() : null;
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		final IProjectDescription description = workspace
				.newProjectDescription(projectHandle.getName());
		description.setLocationURI(projectURI);
		//description.setNatureIds(new String[] {IncQueryNature.NATURE_ID});

		/*
         * Just like the ExampleWizard, but this time with an operation object
         * that modifies workspaces.
         */
        WorkspaceModifyOperation op = new WorkspaceModifyOperation() {
            protected void execute(IProgressMonitor monitor)
                    throws CoreException {
                try {
                	IncQueryProjectSupport.createProject(description, projectHandle, monitor);
					GenModelHelper.createGenmodel(new Path(IncQueryNature.IC_GENMODEL), projectHandle);
				} catch (IOException e) {
					throw new CoreException(new Status(IStatus.ERROR, IncQueryGUIPlugin.PLUGIN_ID, "Error creating project", e));
				}
            }
        };

        /*
         * This isn't as robust as the code in the BasicNewProjectResourceWizard
         * class. Consider beefing this up to improve error handling.
         */
        try {
            getContainer().run(true, true, op);
        } catch (InterruptedException e) {
            return false;
        } catch (InvocationTargetException e) {
            Throwable realException = e.getTargetException();
            MessageDialog.openError(getShell(), "Error", realException
                    .getMessage());
            return false;
        }

        project = projectHandle;

        if (project == null) {
            return false;
        }

        BasicNewProjectResourceWizard.selectAndReveal(project, workbench.getActiveWorkbenchWindow());

        return true;

	}

}
