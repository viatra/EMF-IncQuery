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

package org.eclipse.viatra2.emf.incquery.gui.wizards.internal.operations;

import java.io.IOException;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.emf.codegen.ecore.genmodel.GenModel;
import org.eclipse.emf.common.util.EList;
import org.eclipse.ui.actions.WorkspaceModifyOperation;
import org.eclipse.viatra2.emf.incquery.core.project.IncQueryNature;
import org.eclipse.viatra2.emf.incquery.gui.IncQueryGUIPlugin;
import org.eclipse.viatra2.emf.incquery.tooling.generator.generatorModel.GeneratorModelFactory;
import org.eclipse.viatra2.emf.incquery.tooling.generator.generatorModel.GeneratorModelReference;
import org.eclipse.viatra2.emf.incquery.tooling.generator.generatorModel.IncQueryGeneratorModel;
import org.eclipse.viatra2.emf.incquery.tooling.generator.genmodel.IEiqGenmodelProvider;
import org.eclipse.xtext.ui.resource.IResourceSetProvider;
import org.eclipse.xtext.util.StringInputStream;

public class CreateGenmodelOperation extends
		WorkspaceModifyOperation {
	private final IProject project;
	private List<GenModel> genmodels;
	private IEiqGenmodelProvider genmodelProvider;
	private IResourceSetProvider resourceSetProvider;

	public CreateGenmodelOperation(IProject project,
			List<GenModel> genmodels, IEiqGenmodelProvider genmodelProvider, IResourceSetProvider resourceSetProvider) {
		this.project = project;
		this.genmodels = genmodels;
		this.genmodelProvider = genmodelProvider;
		this.resourceSetProvider = resourceSetProvider;
	}

	protected void execute(IProgressMonitor monitor)
			throws CoreException {
		try {
			IncQueryGeneratorModel generatorModel = genmodelProvider
					.getGeneratorModel(project,
							resourceSetProvider.get(project));
			EList<GeneratorModelReference> genmodelRefs = generatorModel
					.getGenmodels();
			for (GenModel ecoreGenmodel : genmodels) {
				GeneratorModelReference ref = GeneratorModelFactory.eINSTANCE
						.createGeneratorModelReference();
				ref.setGenmodel(ecoreGenmodel);
				genmodelRefs.add(ref);
			}
			if (genmodelRefs.isEmpty()) {
				IFile file = project.getFile(IncQueryNature.IQGENMODEL);
				file.create(new StringInputStream(""), false, new SubProgressMonitor(monitor, 1));
			} else {
				genmodelProvider.saveGeneratorModel(project, generatorModel);
			}
		} catch (IOException e) {
			throw new CoreException(new Status(IStatus.ERROR,
					IncQueryGUIPlugin.PLUGIN_ID,
					"Cannot create generator model: " + e.getMessage(), e));
		}
	}
}