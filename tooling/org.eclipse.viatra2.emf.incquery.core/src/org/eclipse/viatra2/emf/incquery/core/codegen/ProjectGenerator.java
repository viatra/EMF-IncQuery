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

package org.eclipse.viatra2.emf.incquery.core.codegen;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.viatra2.emf.incquery.core.codegen.internal.APIGenerator;
import org.eclipse.viatra2.emf.incquery.core.codegen.internal.ExtensionContributionsGenerator;
import org.eclipse.viatra2.emf.incquery.core.codegen.internal.PatternBuilderSourceGenerator;
import org.eclipse.viatra2.emf.incquery.core.codegen.util.ModulesLoader;
import org.eclipse.viatra2.emf.incquery.core.codegen.util.PatternsCollector;
import org.eclipse.viatra2.emf.incquery.core.project.IncQueryNature;
import org.eclipse.viatra2.emf.incquery.model.incquerygenmodel.IncQueryGenmodel;
import org.eclipse.viatra2.framework.FrameworkException;
import org.eclipse.viatra2.framework.FrameworkManagerException;
import org.eclipse.viatra2.framework.IFramework;
import org.eclipse.viatra2.gtasmmodel.gtasm.metamodel.gt.GTPattern;

/**
 * @author Bergmann Gábor
 *
 */
public class ProjectGenerator {
	IProject project;
	IncQueryGenmodel iqGen;
	
	PatternBuilderSourceGenerator patternBuilderSourceGenerator;
	ExtensionContributionsGenerator extensionContributionsGenerator;
	APIGenerator apiGenerator;
	ModulesLoader modulesLoader;
	
	public ProjectGenerator(IProject project, IncQueryGenmodel iqGen) throws CodeGenerationException {
		super();
		this.project = project;
		this.iqGen = iqGen;
		
		this.patternBuilderSourceGenerator = new PatternBuilderSourceGenerator(getProject(), iqGen);
		this.extensionContributionsGenerator = new ExtensionContributionsGenerator(getProject());
		this.apiGenerator = new APIGenerator(getProject());
		this.modulesLoader = new ModulesLoader(project);
	}

	public void fullBuild(IProgressMonitor monitor) throws CodeGenerationException {
		monitor.subTask("Removing existing code");
		clean(monitor);
		monitor.subTask("Generating Java code");
		buildAfterClean(monitor);
	}

	public void clean(final IProgressMonitor monitor) throws CodeGenerationException {
		IProject project = getProject();
		IFolder folder = project.getFolder(IncQueryNature.SRCGEN_DIR);
		try {
			folder.refreshLocal(IResource.DEPTH_INFINITE, monitor);
			CollectDeletedElement visitor = new CollectDeletedElement();
			folder.accept(visitor);
			for (IResource res : visitor.toDelete) {
				res.delete(false, new SubProgressMonitor(monitor, 1));
			}
		} catch (CoreException e) {
			throw new CodeGenerationException("Error during cleanup before code EMF-IncQuery code generation.", e);
		}
	}

	public void buildAfterClean(IProgressMonitor monitor) throws CodeGenerationException {
		try {
			IFramework framework = modulesLoader.loadFramework(project);
			try {
				modulesLoader.loadAllModules(framework);
				Set<GTPattern> patterns = new PatternsCollector(framework).getCollectedPatterns();
				Map<GTPattern, String> generatedPatternBuilderClasses = 
					patternBuilderSourceGenerator.generatePatternBuilders(framework, patterns, monitor);
				extensionContributionsGenerator.contributeToExtensionPoint(generatedPatternBuilderClasses, monitor);
				apiGenerator.generateApiForPatterns(patterns, monitor);
			} finally {
				modulesLoader.disposeFramework(framework);
			}		
		} catch (CoreException e) {
			throw new CodeGenerationException("Error during EMF-IncQuery code generation. ", e);
		} catch (FrameworkException e) {
			throw new CodeGenerationException("Error during EMF-IncQuery code generation. ", e);
		} catch (FrameworkManagerException e) {
			throw new CodeGenerationException("Error during EMF-IncQuery code generation. ", e);
		}
	}

	public IProject getProject() {
		return project;
	}

	private class CollectDeletedElement implements IResourceVisitor {
		List<IResource> toDelete = new ArrayList<IResource>();
		@Override
		public boolean visit(IResource resource) throws CoreException {
			if (resource instanceof IFile) {
				if ("java".equalsIgnoreCase(((IFile)resource).getFileExtension())) {
					toDelete.add(resource);
					return false;
				}
			}
			return true;
		}
	};
}
