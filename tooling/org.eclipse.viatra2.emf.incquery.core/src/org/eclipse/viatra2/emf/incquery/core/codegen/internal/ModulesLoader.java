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

package org.eclipse.viatra2.emf.incquery.core.codegen.internal;


import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.viatra2.emf.incquery.core.project.IncQueryNature;
import org.eclipse.viatra2.errors.VPMRuntimeException;
import org.eclipse.viatra2.errors.info.ErrorInformation;
import org.eclipse.viatra2.errors.reporting.IErrorReporter;
import org.eclipse.viatra2.framework.FrameworkException;
import org.eclipse.viatra2.framework.FrameworkManager;
import org.eclipse.viatra2.framework.FrameworkManagerException;
import org.eclipse.viatra2.framework.IFramework;
import org.eclipse.viatra2.gtasmmodel.gtasm.metamodel.asm.definitions.Machine;
import org.eclipse.viatra2.lpgparser.loader.VTCLParseController;
import org.eclipse.viatra2.lpgparser.loader.VTCLParserManager;

/**
 * @author Bergmann Gábor
 */
public class ModulesLoader {
	private IProject project;

	/**
	 * @param project
	 */
	public ModulesLoader(IProject project) {
		super();
		this.project = project;
	}

	public IFramework loadFramework(IProject project)
			throws FrameworkManagerException {
		FrameworkManager fwManager = FrameworkManager.getInstance();
		IFramework framework = fwManager.createFramework(project
				.findMember(new Path(IncQueryNature.TARGET_VPML), false)
				.getLocation().toOSString());
		return framework;
	}

	public IFramework loadAllModules(IFramework framework)
			throws CoreException, FrameworkException {
		/*
		 * IFolder folder = project.getFolder(IncQueryNature.MACHINES_DIR); for
		 * (IResource res : folder.members()) { if (res instanceof IFile) {
		 * IFile file = (IFile) res; if (file.getFileExtension().toLowerCase()
		 * .contentEquals("module")) { loadSerializedModule(file, framework); }
		 * } }
		 */
		IFolder folder = project.getFolder(IncQueryNature.VTCL_DIR);
		for (IResource res : folder.members()) {
			if (res instanceof IFile) {
				IFile file = (IFile) res;
				if (file.getFileExtension().toLowerCase().contentEquals("vtcl")) {
					loadVTCLModule(file, framework);
				} else if (file.getFileExtension().toLowerCase().contentEquals("module")) {
					loadSerializedModule(file, framework);
				}
			}
		}
		return framework;
	}

	private void loadSerializedModule(IFile file, IFramework framework)
			throws FrameworkException {
		framework.loadMachine(file.getLocation().toOSString(),
				"org.eclipse.viatra2.frameworkgui.loaders.XMIModuleLoader");
	}

	private void loadVTCLModule(IFile file, IFramework framework)
			throws FrameworkException {
		// framework.loadMachine(
		// file.getLocation().toOSString(),
		// "org.eclipse.viatra2.lpgparser.loader.LPGVTCLLoader");
		try {
			//TODO this functionality should be available as API
			String fileName = file.getLocation().toOSString();

			IErrorReporter markerManager = (IErrorReporter) file
					.getAdapter(IErrorReporter.class);
			markerManager.deleteMarkers(IMarker.PROBLEM);

			// Initialize parsing
			VTCLParserManager parserManager = (VTCLParserManager) framework
					.getVTCLParserManager();
			VTCLParseController fParseController = parserManager
					.lookupAndCreateParseController(fileName);
			fParseController.parseAndBuild();
			// Refresh marker annotations
			for (ErrorInformation error : fParseController.getErrors()) {
				markerManager.reportError(error);
			}
			Machine machine = fParseController.getMachine();
			if (machine == null)
				throw new FrameworkException("Cannot load VTCL file: "
						+ file.getFullPath().toOSString());
			framework.addMachine(machine.getFqn(), machine);
		} catch (VPMRuntimeException e) {
			throw new FrameworkException(e.getMessage(), e);
		}
	}

	public void disposeFramework(IFramework framework)
			throws FrameworkManagerException {
		FrameworkManager.getInstance().disposeFramework(framework.getId());
	}

}
