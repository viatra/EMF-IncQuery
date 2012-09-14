/*******************************************************************************
 * Copyright (c) 2010-2012, Mark Czotter, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Mark Czotter - initial API and implementation
 *******************************************************************************/

package org.eclipse.viatra2.emf.incquery.tooling.generator.builder;

import java.util.Collection;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.pde.core.plugin.IPluginExtension;
import org.eclipse.viatra2.emf.incquery.core.project.ProjectGenerationHelper;
import org.eclipse.viatra2.emf.incquery.tooling.generator.fragments.IGenerationFragment;
import org.eclipse.viatra2.emf.incquery.tooling.generator.fragments.IGenerationFragmentProvider;
import org.eclipse.xtext.xbase.lib.Functions;
import org.eclipse.xtext.xbase.lib.IterableExtensions;
import org.eclipse.xtext.xbase.lib.Pair;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * Ensure support for BuilderParticipant.
 * 
 * @author Mark Czotter
 * 
 */
@Singleton
public class EnsurePluginSupport {
	
	@Inject
	private IGenerationFragmentProvider fragmentProvider;
	
	@Inject
	private Logger logger;
	
	private Multimap<IProject, String> exportedPackageMap = ArrayListMultimap.create();
	private Multimap<IProject, IPluginExtension> appendableExtensionMap = ArrayListMultimap.create();
	private Multimap<IProject, Pair<String, String>> removableExtensionMap = ArrayListMultimap.create();
	private Multimap<IProject, String> modelBundleIds = HashMultimap.create();
	
	public void appendExtension(IProject project, IPluginExtension extension) {
		appendableExtensionMap.put(project, extension);
	}
	
	public void appendAllExtension(IProject project, Iterable<IPluginExtension> extensions) {
		appendableExtensionMap.putAll(project, extensions);
	}
	
	public void removeExtension(IProject project, Pair<String, String> extension) {
		removableExtensionMap.put(project, extension);
	}
	
	public void removeAllExtension(IProject targetProject,
			Iterable<Pair<String, String>> extensions) {
		removableExtensionMap.putAll(targetProject, extensions);
	}
	
	public void exportPackage(IProject project, String packageName) {
		exportedPackageMap.put(project, packageName);
	}
	
	/**
	 * Adds a bundle id to the projects bundle collection. The implementation
	 * manages multiple additions by storing only a single element for each id.
	 * 
	 * @param project
	 * @param bundleId
	 */
	public void addModelBundleId(IProject project, String bundleId) {
		modelBundleIds.put(project, bundleId);
	}
	
	public void clean() {
		exportedPackageMap.clear();
		appendableExtensionMap.clear();
		removableExtensionMap.clear();
		modelBundleIds.clear();
	}

	/**
	 * The ensure phase performs changes to the plugin.xml and MANIFEST.MF descriptors.
	 * @param modelProject 
	 * @param monitor
	 * @throws CoreException 
	 */
	public void ensure(IProject modelProject, IProgressMonitor monitor) {
		// normal code generation done, extensions, packages ready to add to the plug-ins
		try {
			internalEnsure(modelProject, monitor);
		} catch (Exception e) {
			logger.error("Exception during Extension/Package ensure Phase", e);
		} finally {
			monitor.worked(1);
		}
	}
	
	public Collection<String> getModelBundleDependencies(IProject project) {
		return modelBundleIds.get(project);
	}
	
	private void internalEnsure(IProject modelProject, IProgressMonitor monitor) throws CoreException {
		// ensure exported package and extensions
		ensurePackages(monitor);
		ensureExtensions(monitor);
		ensureSourceFolders(modelProject, monitor);
	}

	private void ensurePackages(IProgressMonitor monitor) throws CoreException {
		for (IProject proj : exportedPackageMap.keySet()) {
			// ensure package exports per project
			ProjectGenerationHelper.ensurePackageExports(proj, exportedPackageMap.get(proj));
		}		
	}
	
	private void ensureExtensions(IProgressMonitor monitor) throws CoreException {
		// Loading extensions to the generated projects
		// if new contributed extensions exists remove the removables from the 
		// contributed extensions, so the truly removed extensions remain in the removedExtensions
		if (!appendableExtensionMap.isEmpty()) {
			// iterate over the contributed extensions, remove the removables
			for (IProject proj : appendableExtensionMap.keySet()) {
				Iterable<IPluginExtension> extensions = appendableExtensionMap.get(proj);
				Collection<Pair<String, String>> removableExtensions = removableExtensionMap.get(proj);
				if (!removableExtensions.isEmpty()) {
					removeSameExtensions(removableExtensions, extensions);	
				}
				ProjectGenerationHelper.ensureExtensions(proj, extensions,
						removableExtensions);
			}
			// iterate over the remaining removables, remove all prev. extension from the projects
			for (IProject proj : removableExtensionMap.keySet()) {
				if (!appendableExtensionMap.containsKey(proj)) {
					Iterable<Pair<String, String>> removableExtensions = removableExtensionMap.get(proj);
					Iterable<IPluginExtension> extensions = Lists.newArrayList();
					ProjectGenerationHelper.ensureExtensions(proj, extensions,
							removableExtensions);
				}
			}
		} else {
			// if no contributed extensions (like no pattern in the eiq file)
			// remove all previous extension
			for (IProject proj : removableExtensionMap.keySet()) {
				Iterable<Pair<String, String>> removableExtensions = removableExtensionMap.get(proj);
				Iterable<IPluginExtension> extensions = Lists.newArrayList();
				ProjectGenerationHelper.ensureExtensions(proj, extensions,
						removableExtensions);
			}
		}
	}

	private void removeSameExtensions(Collection<Pair<String, String>> removeFrom,
			Iterable<IPluginExtension> searchList) {
		// not remove a removable if exist in the current extension map
		for (final IPluginExtension ext : searchList) {
			Pair<String, String> found = IterableExtensions.findFirst(removeFrom, new Functions.Function1<Pair<String, String>, Boolean>() {
				@Override
				public Boolean apply(Pair<String, String> p) {
					return (p.getKey().equals(ext.getId())) 
							&& (p.getValue().equals(ext.getPoint()));
				}
			});
			removeFrom.remove(found);
		}		
	}

	private void ensureSourceFolders(IProject modelProject, IProgressMonitor monitor) throws CoreException {
		// ensure classpath entries on the projects
		ProjectGenerationHelper.ensureSourceFolders(modelProject, monitor);
		for (IGenerationFragment fragment : fragmentProvider.getAllFragments()) {
			IProject fragmentProject = fragmentProvider.getFragmentProject(modelProject, fragment);
			if (fragmentProject.exists()) {
				ProjectGenerationHelper.ensureSourceFolders(fragmentProject, monitor);
			}
		}
	}

}
