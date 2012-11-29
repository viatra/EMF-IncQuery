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

import java.util.HashSet;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.incquery.patternlanguage.patternLanguage.Annotation;
import org.eclipse.incquery.patternlanguage.patternLanguage.Pattern;
import org.eclipse.incquery.tooling.core.project.ProjectGenerationHelper;
import org.eclipse.xtext.xbase.lib.StringExtensions;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.google.inject.Inject;

/**
 * A provider for {@link IGenerationFragment} classes - the fragment list is populated using the
 * registered extensions for the {@value #EXTENSIONID} extension point.
 * @author Zoltan Ujhelyi
 *
 */
public class ExtensionBasedGenerationFragmentProvider implements
		IGenerationFragmentProvider {
	
	@Inject
	private Logger logger;

    static final String EXTENSIONID = "org.eclipse.incquery.tooling.core.generatorFragment";
	static final String GENERIC_ATTRIBUTE = "";
	private Multimap<String, IGenerationFragment> fragments;
	
	@Inject
	private IWorkspaceRoot workspaceRoot;

	protected void initializeFragments() {
		fragments = ArrayListMultimap.create();
		final IConfigurationElement[] config = Platform.getExtensionRegistry()
				.getConfigurationElementsFor(EXTENSIONID);
		for (IConfigurationElement e : config) {
			final String annotationName = e.getAttribute("annotation") != null ? e.getAttribute("annotation") : GENERIC_ATTRIBUTE;
			try {
				IGenerationFragment fragment = (IGenerationFragment) e.createExecutableExtension("fragment");
				fragments.put(annotationName, fragment);
			} catch (CoreException e1) {
				logger.warn("Cannot load generator fragment from " + e.getContributor().getName(), e1);
			}
		}
	}	
	
	@Override
	public Iterable<IGenerationFragment> getFragmentsForPattern(Pattern pattern) {
		if (fragments == null) {
			initializeFragments();
		}
		HashSet<IGenerationFragment> fragmentSet = new HashSet<IGenerationFragment>(fragments.get(GENERIC_ATTRIBUTE));
		for (Annotation annotation : pattern.getAnnotations()) {
			fragmentSet.addAll(fragments.get(annotation.getName()));
		}
		return fragmentSet;
	}

	@Override
	public Iterable<IGenerationFragment> getAllFragments() {
		if (fragments == null) {
			initializeFragments();
		}
		HashSet<IGenerationFragment> fragmentSet = new HashSet<IGenerationFragment>(fragments.values());
		return fragmentSet;
	}

	@Override
	public IProject getFragmentProject(IProject modelProject,
			IGenerationFragment fragment) {
		if (StringExtensions.isNullOrEmpty(fragment.getProjectPostfix())) {
			return modelProject;
		}
		String projectName = getFragmentProjectName(modelProject, fragment);
		return workspaceRoot.getProject(projectName);
	}
	
	private String getFragmentProjectName(IProject base, IGenerationFragment fragment) {
		return String.format("%s.%s",
				ProjectGenerationHelper.getBundleSymbolicName(base),
				fragment.getProjectPostfix());
	}

}
