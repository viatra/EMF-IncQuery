/*******************************************************************************
 * Copyright (c) 2004-2010 Zoltan Ujhelyi and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Zoltan Ujhelyi - initial API and implementation
 *    Abel Hegedus   - modified for validation project generation
 *******************************************************************************/


package org.eclipse.viatra2.emf.incquery.validation.tooling.ui;

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
import org.eclipse.viatra2.emf.incquery.core.codegen.SampleProjectGenerator;
import org.eclipse.viatra2.emf.incquery.core.genmodel.GenModelHelper;
import org.eclipse.viatra2.emf.incquery.model.incquerygenmodel.IncQueryGenmodel;
import org.eclipse.viatra2.emf.incquery.validation.codegen.generator.SampleValidationProjectGenerator;
import org.eclipse.viatra2.emf.incquery.validation.tooling.ValidationToolingPlugin;

/**
 * The command handler for generating an Validation example from the IncQuery Genmodel.
 * @author Zoltan Ujhelyi, Abel Hegedus
 *
 */
public class GenerateSampleProjectHandler extends AbstractHandler {

	/* (non-Javadoc)
	 * @see org.eclipse.core.commands.IHandler#execute(org.eclipse.core.commands.ExecutionEvent)
	 */
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IStructuredSelection selection = (IStructuredSelection) HandlerUtil.getCurrentSelection(event);
		CodeGenerationJob job = new CodeGenerationJob("IncQuery sample validation project generation", selection);
		job.setUser(true);
		job.schedule();
		return null;
	}

	private class CodeGenerationJob extends Job{

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
			} else {
				iFile = (IFile)firstElement;
				incQueryProject = iFile.getProject();
			}
			IncQueryGenmodel iqGen = GenModelHelper.parseGenModel(iFile);

			try {
				new SampleValidationProjectGenerator(incQueryProject, iqGen, monitor).fullBuild(monitor);
			} catch (CodeGenerationException e) {
				return reportException(e);
			} catch (RuntimeException e) {
				return reportException(e);
			}
			return Status.OK_STATUS;
		}

	}

	/**
	 * @param e
	 */
	private Status reportException(Exception e) {
		String errorMessage = "An error occurred during EMF-IncQuery code generation. "
							//+ "See also error log. "
							+ "\n Error message: " + e.getMessage()
							+ "\n Error class: " + e.getClass().getCanonicalName()
							+ "\n\t (see Error Log for further details.)";
		Status status = new Status(Status.ERROR, ValidationToolingPlugin.PLUGIN_ID, errorMessage, e);
		//Activator.log(status);
		return status;
	}


}
