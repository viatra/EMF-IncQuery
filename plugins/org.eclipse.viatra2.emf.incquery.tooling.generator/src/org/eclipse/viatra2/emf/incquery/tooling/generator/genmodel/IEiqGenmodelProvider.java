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
package org.eclipse.viatra2.emf.incquery.tooling.generator.genmodel;

import java.io.IOException;

import org.eclipse.core.resources.IProject;
import org.eclipse.emf.codegen.ecore.genmodel.GenModel;
import org.eclipse.emf.codegen.ecore.genmodel.GenPackage;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.viatra2.emf.incquery.tooling.generator.generatorModel.IncQueryGeneratorModel;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Pattern;

/**
 * Helper interface for accessing eiq genmodels
 * 
 */
public interface IEiqGenmodelProvider {

	/**
	 * Gets the generator model for a selected IncQuery-related context object
	 * (e.g. a {@link Pattern}). If the project of the pattern has no generator
	 * model specified, this method returns an empty generator model. The
	 * genmodel will be placed into the {@link ResourceSet} of the Pattern
	 * object.
	 * 
	 * @param pattern
	 * @return the loaded generator model
	 * @throws IllegalArgumentException
	 *             if the parameter pattern is not serialized into a
	 *             {@link ResourceSet} that is not linked to an IncQuery project
	 */
	IncQueryGeneratorModel getGeneratorModel(EObject context)
			throws IllegalArgumentException;

	/**
	 * Gets the generator model for a selected IncQuery project. If the project
	 * has no generator model specified, this method returns an empty generator
	 * model. The genmodel will be placed into the specified resource set
	 * 
	 * @param project
	 * @param set
	 * @return the loaded generator model
	 */
	IncQueryGeneratorModel getGeneratorModel(IProject project, ResourceSet set);
	
	/**
	 * Saves the changes to the generator model instance in the selected
	 * project. The provider assumes that the genmodel was instantiated by using
	 * the {@link #getGeneratorModel(EObject)} or the
	 * {@link #getGeneratorModel(IProject, ResourceSet)} methods.
	 * 
	 * @throws IOException 
	 */
	void saveGeneratorModel(IProject project, IncQueryGeneratorModel generatorModel) throws IOException;

	/**
	 * Tries to find the EMF {@link GenModel} for a selected {@link EPackage}.
	 * The context object is used for determining the actual project.
	 * 
	 * @param ePackage
	 * @return the corresponding {@link GenPackage} for the selected
	 *         {@link EPackage}
	 */
	GenPackage findGenPackage(EObject context, EPackage ePackage);
	
	/**
	 * Tries to find the EMF {@link GenModel} for a selected {@link EPackage}.
	 * The resource set is expected to be the one Xtext assigns for a Java
	 * project.
	 * 
	 * @param packageNsUri
	 * @return the corresponding {@link GenPackage} for the selected
	 *         {@link EPackage}
	 */
	GenPackage findGenPackage(ResourceSet set, final String packageNsUri);
	/**
	 * Tries to find the EMF {@link GenModel} for a selected {@link EPackage}.
	 * The resource set is expected to be the one Xtext assigns for a Java
	 * project.
	 * 
	 * @param packageNsUri
	 * @return the corresponding {@link GenPackage} for the selected
	 *         {@link EPackage}
	 */
	GenPackage findGenPackage(ResourceSet set, final EPackage ePackage);
	
	/**
	 * Tries to find the EMF {@link GenModel} for a selected {@link EPackage}.
	 * The context object is used for determining the actual project.
	 * 
	 * @param packageNsUri
	 * @return the corresponding {@link GenPackage} for the selected
	 *         {@link EPackage}
	 */
	GenPackage findGenPackage(EObject ctx, final String packageNsUri);

}
