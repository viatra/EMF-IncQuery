package org.eclipse.viatra2.emf.incquery.databinding.tooling;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.viatra2.emf.incquery.core.codegen.CodeGenerationException;
import org.eclipse.viatra2.emf.incquery.core.genmodel.GenModelHelper;
import org.eclipse.viatra2.emf.incquery.model.incquerygenmodel.IncQueryGenmodel;

public class GenerateDatabindingProjectHandler extends AbstractHandler {

	public Object execute(ExecutionEvent event) throws ExecutionException {
		IStructuredSelection selection = (IStructuredSelection) HandlerUtil
				.getCurrentSelection(event);
		CodeGenerationJob job = new CodeGenerationJob(
				"IncQuery databinding project generation", selection);
		job.setUser(true);
		job.schedule();
		return null;
	}

	private class CodeGenerationJob extends Job {

		public CodeGenerationJob(String name, IStructuredSelection selection) {
			super(name);
			this.selection = selection;
		}

		IStructuredSelection selection;

		@Override
		protected IStatus run(IProgressMonitor monitor) {
			Object firstElement = selection.getFirstElement();
			IProject incQueryProject;
			IFile iFile = null;
			
			if (firstElement instanceof IProject) {
				incQueryProject = (IProject) firstElement;
			} 
			else {
				iFile = (IFile) firstElement;
				incQueryProject = iFile.getProject();
			}
			
			IncQueryGenmodel iqGen = GenModelHelper.parseGenModel(iFile);

			try {
				new DatabindingProjectGenerator(incQueryProject, iqGen, monitor).fullBuild(monitor);
			} 
			catch (RuntimeException e) {
				return reportException(e);
			} 
			catch (CodeGenerationException e) {
				return reportException(e);
			}
			return Status.OK_STATUS;
		}
	}

	private Status reportException(Exception e) {
		String errorMessage = "An error occurred during EMF-IncQuery code generation. "
				+ "\n Error message: "
				+ e.getMessage()
				+ "\n Error class: "
				+ e.getClass().getCanonicalName()
				+ "\n\t (see Error Log for further details.)";
		Status status = new Status(Status.ERROR, DatabindingToolingActivator.PLUGIN_ID,
				errorMessage, e);
		return status;
	}

}
