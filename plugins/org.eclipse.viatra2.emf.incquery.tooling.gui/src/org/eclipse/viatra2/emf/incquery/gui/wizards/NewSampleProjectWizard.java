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


import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.viatra2.emf.incquery.gui.IncQueryGUIPlugin;

/**
 * A project wizard for extracting the example zip file into the workspace.
 * @author Zoltan Ujhelyi
 *
 */
public class NewSampleProjectWizard extends ProjectUnzipperNewWizard {

	/**
	 * Redefining the {@link ProjectUnzipperNewWizard} constructor with the example zip file.
	 */
	public NewSampleProjectWizard() {
		super("IncQueryExampleWizard", "Create Project",
				"The project to put our example.",
				"org.eclipse.viatra2.emf.incquery.sample", FileLocator.find(
						IncQueryGUIPlugin.getDefault().getBundle(), new Path("examples/example.zip"), null));
	}

}
