/*******************************************************************************
 * Copyright (c) 2004-2010 Akos Horvath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Akos Horvath - initial API and implementation
 *******************************************************************************/


package org.eclipse.viatra2.emf.incquery.core.codegen;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.emf.codegen.ecore.genmodel.GenPackage;
import org.eclipse.pde.core.project.IBundleProjectDescription;
import org.eclipse.pde.core.project.IBundleProjectService;
import org.eclipse.viatra2.emf.incquery.core.IncQueryPlugin;
import org.eclipse.viatra2.emf.incquery.core.codegen.internal.ExtensionsforSampleProjectGenerator;
import org.eclipse.viatra2.emf.incquery.core.codegen.internal.SampleHandlerGenerator;
import org.eclipse.viatra2.emf.incquery.core.codegen.internal.SampleHandlerGenerator.HandlerData;
import org.eclipse.viatra2.emf.incquery.core.codegen.util.CodegenSupport;
import org.eclipse.viatra2.emf.incquery.core.codegen.util.GTPatternJavaData;
import org.eclipse.viatra2.emf.incquery.core.codegen.util.ModulesLoader;
import org.eclipse.viatra2.emf.incquery.core.codegen.util.PatternsCollector;
import org.eclipse.viatra2.emf.incquery.core.project.IncQueryNature;
import org.eclipse.viatra2.emf.incquery.core.project.ProjectGenerationHelper;
import org.eclipse.viatra2.emf.incquery.core.project.SampleProjectSupport;
import org.eclipse.viatra2.emf.incquery.model.incquerygenmodel.EcoreModel;
import org.eclipse.viatra2.emf.incquery.model.incquerygenmodel.IncQueryGenmodel;
import org.eclipse.viatra2.framework.FrameworkException;
import org.eclipse.viatra2.framework.FrameworkManagerException;
import org.eclipse.viatra2.framework.IFramework;
import org.eclipse.viatra2.gtasmmodel.gtasm.metamodel.gt.GTPattern;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

public class SampleProjectGenerator {
	
	protected IProject incQueryProject, sampleProject;
	protected IncQueryGenmodel iqGen;
	protected ModulesLoader modulesLoader;
	protected String projectNameFragment;
	
	//SampleProjectGenerator sampleProjectGeneartor;
	
	public SampleProjectGenerator(IProject incQueryProject,IncQueryGenmodel iqGen, IProgressMonitor monitor) throws CodeGenerationException {
		super();
		this.incQueryProject = incQueryProject;
		this.iqGen = iqGen;
		this.modulesLoader = new ModulesLoader(incQueryProject);
		this.projectNameFragment = ".ui.sample";
	}

	public void fullBuild(IProgressMonitor monitor) throws CodeGenerationException {
		monitor.subTask("Crating project if non-existent");
		buildProject(monitor);
		monitor.subTask("Removing existing code");
		clean(monitor);
		monitor.subTask("Generating Java code");
		buildAfterClean(monitor);
	}

	private void buildProject(IProgressMonitor monitor) throws CodeGenerationException {

		BundleContext context = null;
		ServiceReference ref = null;

		try {
			context = IncQueryPlugin.plugin.context;
			ref = context.getServiceReference(IBundleProjectService.class.getName());
			IBundleProjectService service = (IBundleProjectService) context.getService(ref);
			IBundleProjectDescription bundleDesc = service.getDescription(incQueryProject);
			sampleProject = SampleProjectSupport.checkforExistingProject(incQueryProject.getName(),projectNameFragment);
			if(sampleProject == null) {
				createProject(bundleDesc, monitor);
			}
		} catch (OperationCanceledException e) {
			throw new CodeGenerationException("Error during project creation before EMF-IncQuery Sample project generation.", e);
		} catch (CoreException e) {
			throw new CodeGenerationException("Error during project creation before EMF-IncQuery Sample project generation.", e);
		} finally {
			if (context != null && ref != null)
				context.ungetService(ref);
		}
	}

	/**
	 * @param bundleDesc
	 * @param monitor
	 * @throws CoreException
	 */
	protected void createProject(IBundleProjectDescription bundleDesc, IProgressMonitor monitor) throws CoreException {
		sampleProject = SampleProjectSupport.createProject(monitor, bundleDesc.getBundleName(),
			incQueryProject.getName());
	}

	public void clean(IProgressMonitor monitor) throws CodeGenerationException {
		IProject project = getProject();
		IFolder folder = project.getFolder(IncQueryNature.SRCGEN_DIR);
		try {
			ProjectGenerationHelper.deleteJavaFiles(folder, monitor);
		} catch (CoreException e) {
			throw new CodeGenerationException("Error during cleanup before code EMF-IncQuery Sample project generation.", e);
		}
	}

	public void buildAfterClean(IProgressMonitor monitor) throws CodeGenerationException {
		try {
			IFramework framework = modulesLoader.loadFramework(incQueryProject);
			try {
				modulesLoader.loadAllModules(framework);
				Set<GTPattern> patterns = new PatternsCollector(framework).getCollectedPatterns();
				Map<GTPattern, GTPatternJavaData> gtPatternJavaRepresentations = generateGTPatternJavaData(patterns, monitor);
				SampleHandlerGenerator handlerGenerator = new SampleHandlerGenerator(gtPatternJavaRepresentations,
						getProject(), incQueryProject);
				handlerGenerator.generateActivator(sampleProject.getName(), monitor);
				Map<String, HandlerData> handlers = handlerGenerator.generateHandlersForPatternMatcherCalls(monitor);
				ExtensionsforSampleProjectGenerator extensionGenerator = new ExtensionsforSampleProjectGenerator(sampleProject);
				extensionGenerator.contributeToExtensionPoint(handlers, getFileExtension(iqGen), monitor);
			} finally {
				modulesLoader.disposeFramework(framework);
			}
		} catch (OperationCanceledException e) {
			throw new CodeGenerationException("Error during EMF-IncQuery Sample project generation. ", e);
		} catch (CoreException e) {
			throw new CodeGenerationException("Error during EMF-IncQuery Sample project generation. ", e);
		} catch (FrameworkException e) {
			throw new CodeGenerationException("Error during EMF-IncQuery Sample project generation. ", e);
		} catch (FrameworkManagerException e) {
			throw new CodeGenerationException("Error during EMF-IncQuery Sample project generation. ", e);
		}
	}

//		
//		String editorID = incqueryGenmodel.getEcoreModel().get(0).getModels().getEditorPluginID();
//		String modelName = incqueryGenmodel.getEcoreModel().get(0).getModels().getModelName();
//		return editorID+"."+modelName+"EditorID";
//	}

	private Collection<String> getFileExtension(IncQueryGenmodel incqueryGenmodel) {
		//incqueryGenmodel.getEcoreModel().get(0).getModels().getGenPackages().get(0).getFileExtension();

		// TODO: works only when the editor file extension is an GenPackage's file extension who has at leat one classifiers
		List<String> fileExtensions = new ArrayList<String>(); 
		
		for(EcoreModel ecoreModel :incqueryGenmodel.getEcoreModel())
			for(GenPackage genPackage: ecoreModel.getModels().getAllGenPackagesWithClassifiers()) 
			{
				fileExtensions.add(genPackage.getFileExtension());
			}
		return fileExtensions;
	}

	private Map<GTPattern, GTPatternJavaData> generateGTPatternJavaData(
			Set<GTPattern> patterns,
			IProgressMonitor monitor) throws CodeGenerationException {
		Map<GTPattern, GTPatternJavaData>  datas = new HashMap<GTPattern, GTPatternJavaData>();
		
		for(GTPattern pattern: patterns) {
			genereteGTPatternJavaData(monitor, datas, pattern);
			
		}
		return datas;
	}

	/**
	 * @param monitor
	 * @param datas
	 * @param pattern
	 * @throws CodeGenerationException
	 */
	protected void genereteGTPatternJavaData(IProgressMonitor monitor, Map<GTPattern, GTPatternJavaData> datas,
			GTPattern pattern) throws CodeGenerationException {
		GTPatternJavaData data = new GTPatternJavaData();
		data.setPatternName(pattern.getName());
		
		//mathcer
		IPath pathRoot = incQueryProject.getFolder(IncQueryNature.GENERATED_MATCHERS_DIR).getFullPath();
		String packageNameRoot = IncQueryNature.GENERATED_MATCHERS_PACKAGEROOT;
		CodegenSupport.PackageLocationFinder matcherPLF= new CodegenSupport.PackageLocationFinder(pattern, pathRoot, packageNameRoot, monitor);
		//sets the matcher and the matcher's package
		data.setMatcherName(getMatcherName(pattern));
		data.setMatcherPackage(matcherPLF.getJavaPackageName()+"."+getMatcherName(pattern));
		
		//singature
		pathRoot = incQueryProject.getFolder(IncQueryNature.GENERATED_DTO_DIR).getFullPath();
		packageNameRoot = IncQueryNature.GENERATED_DTO_PACKAGEROOT;
		CodegenSupport.PackageLocationFinder signaturePLF= new CodegenSupport.PackageLocationFinder(pattern, pathRoot, packageNameRoot, monitor);
		//sets the matcher and the matcher's package
		data.setSignatureName(getSignatureName(pattern));
		data.setSignaturePackage(signaturePLF.getJavaPackageName()+"."+getSignatureName(pattern));
		
		datas.put(pattern, data);
	}
	
	protected String getMatcherName(GTPattern pattern) {
		String pName = pattern.getName().substring(1);
		return Character.toUpperCase(pattern.getName().charAt(0))+pName +"Matcher";
		
	}
	
	protected String getSignatureName(GTPattern pattern) {
		String pName = pattern.getName().substring(1);
		return Character.toUpperCase(pattern.getName().charAt(0))+pName +"Signature";
		
	}

	protected IProject getProject() {
		return sampleProject;
	}
}
