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


package org.eclipse.viatra2.emf.incquery.validation.codegen.generator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.emf.codegen.ecore.genmodel.GenPackage;
import org.eclipse.pde.core.project.IBundleProjectDescription;
import org.eclipse.pde.core.project.IBundleProjectService;
import org.eclipse.viatra2.emf.incquery.core.codegen.CodeGenerationException;
import org.eclipse.viatra2.emf.incquery.core.codegen.util.CodegenSupport;
import org.eclipse.viatra2.emf.incquery.core.codegen.util.GTPatternJavaData;
import org.eclipse.viatra2.emf.incquery.core.codegen.util.ModulesLoader;
import org.eclipse.viatra2.emf.incquery.core.codegen.util.PatternsCollector;
import org.eclipse.viatra2.emf.incquery.core.project.IncQueryNature;
import org.eclipse.viatra2.emf.incquery.core.project.SampleProjectSupport;
import org.eclipse.viatra2.emf.incquery.model.incquerygenmodel.EcoreModel;
import org.eclipse.viatra2.emf.incquery.model.incquerygenmodel.IncQueryGenmodel;
import org.eclipse.viatra2.emf.incquery.validation.codegen.ValidationCodegenPlugin;
import org.eclipse.viatra2.emf.incquery.validation.codegen.internal.ExtensionsforSampleValidationProjectGenerator;
import org.eclipse.viatra2.emf.incquery.validation.codegen.internal.SampleConstraintGenerator;
import org.eclipse.viatra2.emf.incquery.validation.codegen.internal.SampleConstraintGenerator.ConstraintData;
import org.eclipse.viatra2.emf.incquery.validation.codegen.project.SampleValidationProjectSupport;
import org.eclipse.viatra2.framework.FrameworkException;
import org.eclipse.viatra2.framework.FrameworkManagerException;
import org.eclipse.viatra2.framework.IFramework;
import org.eclipse.viatra2.gtasmmodel.gtasm.metamodel.gt.GTPattern;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

/**
 * 
 * @author Abel Hegedus
 *
 */
public class SampleValidationProjectGenerator {
	
	IProject incQueryProject, sampleProject;
	IncQueryGenmodel iqGen;
	ModulesLoader modulesLoader;
	
	//SampleProjectGenerator sampleProjectGeneartor;
	
	public SampleValidationProjectGenerator(IProject incQueryProject,IncQueryGenmodel iqGen, IProgressMonitor monitor) throws CodeGenerationException {
		super();
		this.incQueryProject = incQueryProject;
		this.iqGen = iqGen;
		this.modulesLoader = new ModulesLoader(incQueryProject);
	}

	public void fullBuild(IProgressMonitor monitor) throws CodeGenerationException {
		try {
			buildProject(monitor);
			clean(monitor);
			buildAfterClean(monitor);
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

	private void buildProject(IProgressMonitor monitor) throws OperationCanceledException, CoreException {

		BundleContext context = null;
		ServiceReference ref = null;

		try {
			context = ValidationCodegenPlugin.plugin.context;
			ref = context
			.getServiceReference(IBundleProjectService.class.getName());
			IBundleProjectService service = (IBundleProjectService) context
			.getService(ref);
			IBundleProjectDescription bundleDesc = service.getDescription(incQueryProject);
			this.sampleProject = SampleValidationProjectSupport.createProject(monitor, bundleDesc.getBundleName(),incQueryProject.getName());
		}
		finally
		{if(context != null && ref != null)
			context.ungetService(ref);}
	}

	public void clean(IProgressMonitor monitor) throws CodeGenerationException {
		IProject project = getProject();
		IFolder folder = project.getFolder(IncQueryNature.SRC_DIR);
		try {
			folder.refreshLocal(IResource.DEPTH_INFINITE, monitor);
			for (IResource res : folder.members()) {
				res.delete(true, monitor);
			}
		} catch (CoreException e) {
			throw new CodeGenerationException("Error during cleanup before code EMF-IncQuery code generation.", e);
		}
	}

	public void buildAfterClean(IProgressMonitor monitor) throws CodeGenerationException, FrameworkManagerException, CoreException, FrameworkException {
			IFramework framework = modulesLoader.loadFramework(incQueryProject);
			try {
				modulesLoader.loadAllModules(framework);
				Set<GTPattern> patterns = new PatternsCollector(framework).getCollectedPatterns();
				Map<GTPattern, GTPatternJavaData> gtPatternJavaRepresentations = generateGTPatternJavaData(patterns, monitor);
				SampleConstraintGenerator constraintGenerator = new SampleConstraintGenerator(gtPatternJavaRepresentations, getProject(), incQueryProject);
				constraintGenerator.generateActivator(sampleProject.getName() , monitor);
				Map<String,ConstraintData> constraints = constraintGenerator.generateHandlersForPatternMatcherCalls(monitor);
				ExtensionsforSampleValidationProjectGenerator extensionGenerator = new ExtensionsforSampleValidationProjectGenerator(sampleProject,iqGen);
				extensionGenerator.contributeToExtensionPoint(constraints, getEditorIds(iqGen), monitor);
							
			} finally {
				modulesLoader.disposeFramework(framework);
			}		
	}

//		
//		String editorID = incqueryGenmodel.getEcoreModel().get(0).getModels().getEditorPluginID();
//		String modelName = incqueryGenmodel.getEcoreModel().get(0).getModels().getModelName();
//		return editorID+"."+modelName+"EditorID";
//	}

	private Collection<String> getFileExtension(IncQueryGenmodel incqueryGenmodel) {
		//incqueryGenmodel.getEcoreModel().get(0).getModels().getGenPackages().get(0).getFileExtension();

		// TODO: works only when the editor file extension is an GenPackage's file extension who has at least one classifiers
		List<String> fileExtensions = new ArrayList<String>(); 
		
		for(EcoreModel ecoreModel :incqueryGenmodel.getEcoreModel())
			for(GenPackage genPackage: ecoreModel.getModels().getAllGenPackagesWithClassifiers()) 
			{
				fileExtensions.add(genPackage.getFileExtension());
			}
		return fileExtensions;
	}
	
	private Collection<String> getEditorIds(IncQueryGenmodel incqueryGenmodel) {
		//incqueryGenmodel.getEcoreModel().get(0).getModels().getGenPackages().get(0).getFileExtension();
		
		// TODO: works only when the editor file extension is an GenPackage's file extension who has at least one classifiers
		List<String> editorIDs = new ArrayList<String>(); 
		
		for(EcoreModel ecoreModel :incqueryGenmodel.getEcoreModel()){
			editorIDs.add(ecoreModel.getModels().getEditorPluginID());
			/*for(GenPackage genPackage: ecoreModel.getModels().getAllGenPackagesWithClassifiers()) 
			{
				editorIDs.add(genPackage.getFileExtension());
			}*/
		}
		
		// FIXME remove constant addition and find another solution to incorporate editor IDs
		editorIDs.add("org.eclipse.papyrus.core.papyrusEditor");
		
		return editorIDs;
	}

	private Map<GTPattern, GTPatternJavaData> generateGTPatternJavaData(
			Set<GTPattern> patterns,
			IProgressMonitor monitor) throws CodeGenerationException {
		Map<GTPattern, GTPatternJavaData>  datas = new HashMap<GTPattern, GTPatternJavaData>();
		
		for(GTPattern pattern: patterns) {
			GTPatternJavaData data = new GTPatternJavaData();
			data.setPatternName(pattern.getName());
			
			//matcher
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
		return datas;
	}
	
	private String getMatcherName(GTPattern pattern) {
		String pName = pattern.getName().substring(1);
		return Character.toUpperCase(pattern.getName().charAt(0))+pName +"Matcher";
		
	}
	
	private String getSignatureName(GTPattern pattern) {
		String pName = pattern.getName().substring(1);
		return Character.toUpperCase(pattern.getName().charAt(0))+pName +"Signature";
		
	}

	public IProject getProject() {
		return sampleProject;
	}


}
