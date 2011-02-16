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

package hu.bme.mit.incquery.core.codegen;

import hu.bme.mit.incquery.core.codegen.internal.APIGenerator;
import hu.bme.mit.incquery.core.codegen.internal.ExtensionContributionsGenerator;
import hu.bme.mit.incquery.core.codegen.internal.ModulesLoader;
import hu.bme.mit.incquery.core.codegen.internal.PatternBuilderSourceGenerator;
import hu.bme.mit.incquery.core.codegen.internal.PatternsCollector;
import hu.bme.mit.incquery.core.project.IncQueryNature;
import incquerygenmodel.IncQueryGenmodel;

import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
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
		clean(monitor);
		buildAfterClean(monitor);
	}

	public void clean(IProgressMonitor monitor) throws CodeGenerationException {
		IProject project = getProject();
		IFolder folder = project.getFolder(IncQueryNature.SRCGEN_DIR);
		try {
			folder.refreshLocal(IResource.DEPTH_INFINITE, monitor);
			for (IResource res : folder.members()) {
				res.delete(true, monitor);
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

}
