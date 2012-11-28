/*******************************************************************************
 * Copyright (c) 2010-2012, Zoltan Ujhelyi, Mark Czotter, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Zoltan Ujhelyi, Mark Czotter - initial API and implementation
 *******************************************************************************/

package org.eclipse.incquery.tooling.core.generator.fragments;

import org.eclipse.core.resources.IProject;
import org.eclipse.incquery.patternlanguage.emf.core.patternLanguage.Pattern;

/**
 * An interface for collecting code generation fragments for specific patterns.
 * The concrete value is injected using the {@link GeneratorModule}-based
 * injectors.
 * 
 * @author Zoltan Ujhelyi
 * 
 */
public interface IGenerationFragmentProvider {

	/**
	 * Collects the generation fragments applicable for a selected pattern.
	 * @param pattern
	 * @return a non-null collection of code generation fragments. May be empty.
	 */
	public Iterable<IGenerationFragment> getFragmentsForPattern(Pattern pattern);
	
	/**
	 * Collects all {@link IGenerationFragment}.
	 * @return a non-null collection of code generation fragments.
	 */
	public Iterable<IGenerationFragment> getAllFragments();
	
	/**
	 * Returns the fragment project for the {@link IGenerationFragment} based on the modelProject.
	 * @param modelProject
	 * @param fragment
	 * @return
	 */
	public IProject getFragmentProject(IProject modelProject, IGenerationFragment fragment);
}
