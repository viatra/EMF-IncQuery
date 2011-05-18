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

package org.eclipse.viatra2.emf.incquery.gui.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.viatra2.emf.incquery.core.codegen.ProjectGenerator;
import org.eclipse.viatra2.emf.incquery.core.genmodel.GenModelHelper;
import org.eclipse.viatra2.emf.incquery.gui.IncQueryGUIPlugin;
import org.eclipse.viatra2.emf.incquery.model.incquerygenmodel.IncQueryGenmodel;

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
		if (!selection.isEmpty()) {
			CodeGenerationJob job = new CodeGenerationJob("IncQuery code generation", selection);
			job.setUser(true);
			job.schedule();
		}
		return null;
	}
	
	private class CodeGenerationJob extends WorkspaceJob{

		public CodeGenerationJob(String name, IStructuredSelection selection) {
			super(name);
			this.selection = selection;
		}
		IStructuredSelection selection;
		
		@Override
		public IStatus runInWorkspace(IProgressMonitor monitor) {
			monitor.beginTask("Generating IncQuery source code", 2);
			Object firstElement = selection.getFirstElement();
			try {
				monitor.subTask("Loading IncQuery genmodel file");
				IProject project;
				IncQueryGenmodel iqGen;
				if (firstElement instanceof IProject) {
					project = (IProject) firstElement;
					iqGen = GenModelHelper.parseGenModel(project);
				} else {
					IFile iFile = (IFile)firstElement;
					project = iFile.getProject();
					iqGen = GenModelHelper.parseGenModel(iFile);
				}
				monitor.worked(1);
				if (iqGen!=null) {
					monitor.subTask("Invoking code generator");
					new ProjectGenerator(project, iqGen).fullBuild(monitor);
					monitor.worked(2);
				} else {
					return new Status(Status.ERROR, IncQueryGUIPlugin.PLUGIN_ID, 
							"Error loading IncQuery genmodel file");
				}
			} catch (Exception e) {
				return reportException(e);
			} finally {
				monitor.done();
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
		Status status = new Status(Status.ERROR, IncQueryGUIPlugin.PLUGIN_ID, errorMessage, e);
		return status;
	}

}
