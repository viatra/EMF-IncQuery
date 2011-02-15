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

package hu.bme.mit.incquery.gui.handlers;

import hu.bme.mit.incquery.core.codegen.CodeGenerationException;
import hu.bme.mit.incquery.core.codegen.ProjectGenerator;
import hu.bme.mit.incquery.core.genmodel.GenModelHelper;
import hu.bme.mit.incquery.gui.IncQueryGUIPlugin;
import incquerygenmodel.IncQueryGenmodel;

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

/**
 * @author Bergmann Gabor, Zoltan Ujhelyi
 *
 */
public class GenerateSourceCodeHandler extends AbstractHandler {

	/* (non-Javadoc)
	 * @see org.eclipse.core.commands.IHandler#execute(org.eclipse.core.commands.ExecutionEvent)
	 */
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IStructuredSelection selection = (IStructuredSelection) HandlerUtil.getCurrentSelection(event);
		CodeGenerationJob job = new CodeGenerationJob("IncQuery code generation", selection);
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
			monitor.beginTask("Generating IncQuery source code", 2);
			Object firstElement = selection.getFirstElement();
			IProject project;
			IFile iFile = null; 
			if (firstElement instanceof IProject) {
				project = (IProject) firstElement;
			} else {
				iFile = (IFile)firstElement;
				project = iFile.getProject();
			}
			try {
				monitor.subTask("Loading IncQuery genmodel file");
				IncQueryGenmodel iqGen = GenModelHelper.parseGenModel(iFile);
				monitor.worked(1);
				if (iqGen!=null) {
					monitor.subTask("Invoking code generator");
					new ProjectGenerator(project, iqGen).fullBuild(monitor);
					monitor.worked(2);
				}
				else {
					monitor.done();
					return new Status(Status.ERROR, IncQueryGUIPlugin.PLUGIN_ID, 
							"Error loading IncQuery genmodel file");
				}
			} catch (CodeGenerationException e) {
				monitor.done();
				return reportException(e);
			} catch (RuntimeException e) {
				monitor.done();
				return reportException(e);
			}
			catch (Exception e) {
				monitor.done();
				return reportException(e);
			}
			monitor.done();
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
		Status status = new Status(Status.ERROR, IncQueryGUIPlugin.PLUGIN_ID, errorMessage, e);
		//Activator.log(status);
		return status;
	}

}
