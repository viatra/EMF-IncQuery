/*******************************************************************************
 * Copyright (c) 2010-2012, Zoltan Ujhelyi, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Zoltan Ujhelyi - initial API and implementation
 *******************************************************************************/

package org.eclipse.viatra2.emf.incquery.core.project;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.runtime.CoreException;

/**
 * @author Zoltan Ujhelyi
 */
public class IncQueryNature implements IProjectNature {

	/**
	 * The project nature identifier used for defining the project nature of an
	 * IncQuery project.
	 */
	public static final String NATURE_ID = "org.eclipse.viatra2.emf.incquery.projectnature"; //$NON-NLS-1$
	public static final String BUNDLE_ID = "org.eclipse.viatra2.emf.incquery.core"; //$NON-NLS-1$
	public static final String MODEL_BUNDLE_ID = "org.eclipse.viatra2.emf.importer.generic"; //$NON-NLS-1$
	public static final String SRCGEN_DIR = "src-gen"; //$NON-NLS-1$
	public static final String SRC_DIR = "src"; //$NON-NLS-1$
	public static final String EXECUTION_ENVIRONMENT = "JavaSE-1.6"; //$NON_NLS-1$
	public static final String IQGENMODEL = "generator.eiqgen";
	
	private IProject project;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.resources.IProjectNature#getProject()
	 */
	public IProject getProject() {
		return project;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.core.resources.IProjectNature#setProject(org.eclipse.core
	 * .resources.IProject)
	 */
	public void setProject(IProject project) {
		this.project = project;
	}

	public void configure() throws CoreException {
	}

	public void deconfigure() throws CoreException {
	}

}
