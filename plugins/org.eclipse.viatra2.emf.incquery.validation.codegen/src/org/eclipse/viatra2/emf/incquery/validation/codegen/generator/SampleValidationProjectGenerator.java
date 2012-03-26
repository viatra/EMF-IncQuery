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

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.emf.codegen.ecore.genmodel.GenPackage;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.pde.core.project.IBundleProjectDescription;
import org.eclipse.viatra2.emf.incquery.core.codegen.CodeGenerationException;
import org.eclipse.viatra2.emf.incquery.core.codegen.SampleProjectGenerator;
import org.eclipse.viatra2.emf.incquery.core.codegen.util.GTPatternJavaData;
import org.eclipse.viatra2.emf.incquery.core.codegen.util.PatternsCollector;
import org.eclipse.viatra2.emf.incquery.model.incquerygenmodel.EcoreModel;
import org.eclipse.viatra2.emf.incquery.model.incquerygenmodel.IncQueryGenmodel;
import org.eclipse.viatra2.emf.incquery.validation.codegen.internal.ExtensionsforSampleValidationProjectGenerator;
import org.eclipse.viatra2.emf.incquery.validation.codegen.internal.SampleConstraintGenerator;
import org.eclipse.viatra2.emf.incquery.validation.codegen.internal.SampleConstraintGenerator.ConstraintData;
import org.eclipse.viatra2.emf.incquery.validation.codegen.project.SampleValidationProjectSupport;
import org.eclipse.viatra2.framework.FrameworkException;
import org.eclipse.viatra2.framework.FrameworkManagerException;
import org.eclipse.viatra2.framework.IFramework;
import org.eclipse.viatra2.gtasm.support.helper.GTASMHelper;
import org.eclipse.viatra2.gtasmmodel.gtasm.metamodel.gt.GTPattern;

/**
 * 
 * @author Abel Hegedus
 *
 */
public class SampleValidationProjectGenerator extends SampleProjectGenerator {
	
	public SampleValidationProjectGenerator(IProject incQueryProject,IncQueryGenmodel iqGen, IProgressMonitor monitor) throws CodeGenerationException {
		super(incQueryProject, iqGen, monitor);
		projectNameFragment = ".validation.sample";
	}

	public void buildAfterClean(IProgressMonitor monitor) throws CodeGenerationException {
		try {
			IFramework framework = modulesLoader.loadFramework(incQueryProject);
			try {
				modulesLoader.loadAllModules(framework);
				Set<GTPattern> patterns = new PatternsCollector(framework).getCollectedPatterns();
				Map<GTPattern, GTPatternJavaData> gtPatternJavaRepresentations = generateGTPatternJavaData(patterns, monitor);
				SampleConstraintGenerator constraintGenerator = new SampleConstraintGenerator(gtPatternJavaRepresentations, getProject(), incQueryProject);
				constraintGenerator.generateActivator(sampleProject.getName() , monitor);
				Map<String,ConstraintData> constraints = constraintGenerator.generateHandlersForPatternMatcherCalls(monitor);
				ExtensionsforSampleValidationProjectGenerator extensionGenerator = new ExtensionsforSampleValidationProjectGenerator(sampleProject,iqGen);
				extensionGenerator.contributeToExtensionPoint(constraints, getEditorId2DomainMap(iqGen), monitor);
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

	private Map<String,String> getEditorId2DomainMap(IncQueryGenmodel incqueryGenmodel) {
		//incqueryGenmodel.getEcoreModel().get(0).getModels().getGenPackages().get(0).getFileExtension();
		
		// TODO: works only when the editor file extension is an GenPackage's file extension who has at least one classifiers
		Map<String,String> editorIDs = new HashMap<String, String>(); 
		
		for(EcoreModel ecoreModel :incqueryGenmodel.getEcoreModel()){
			for(EObject genp : ecoreModel.getModels().eContents()){
				if(genp instanceof GenPackage){
					editorIDs.put(((GenPackage) genp).getQualifiedEditorClassName()+"ID", ((GenPackage) genp).getNSName());
				}
			}
			
			/*for(GenPackage genPackage: ecoreModel.getModels().getAllGenPackagesWithClassifiers()) 
			{
				editorIDs.add(genPackage.getFileExtension());
			}*/
		}
		
		// FIXME remove constant addition and find another solution to incorporate editor IDs
		//editorIDs.put("org.eclipse.papyrus.core.papyrusEditor", "papyrusGMF");
		
		return editorIDs;
	}

	private Map<GTPattern, GTPatternJavaData> generateGTPatternJavaData(
			Set<GTPattern> patterns,
			IProgressMonitor monitor) throws CodeGenerationException {
		Map<GTPattern, GTPatternJavaData>  datas = new HashMap<GTPattern, GTPatternJavaData>();
		
		for(GTPattern pattern: patterns) {
			
			Map<String, String> annotation = 
				GTASMHelper.extractLowerCaseRuntimeAnnotation(pattern, "@Constraint");
			if (annotation != null){
				genereteGTPatternJavaData(monitor, datas, pattern);
			}
		}
		return datas;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.viatra2.emf.incquery.core.codegen.SampleProjectGenerator#createProject(org.eclipse.pde.core.project.IBundleProjectDescription, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	protected void createProject(IBundleProjectDescription bundleDesc, IProgressMonitor monitor) throws CoreException {
		sampleProject = SampleValidationProjectSupport.createProject(monitor, bundleDesc.getSymbolicName(),
				incQueryProject.getName());
	}
}
