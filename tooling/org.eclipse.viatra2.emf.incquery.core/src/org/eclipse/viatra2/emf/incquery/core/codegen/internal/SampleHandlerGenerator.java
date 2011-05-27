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


package org.eclipse.viatra2.emf.incquery.core.codegen.internal;


import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.viatra2.emf.incquery.core.codegen.CodeGenerationException;
import org.eclipse.viatra2.emf.incquery.core.project.IncQueryNature;
import org.eclipse.viatra2.gtasm.support.helper.GTASMHelper;
import org.eclipse.viatra2.gtasmmodel.gtasm.metamodel.gt.GTPattern;

public class SampleHandlerGenerator {

	static final String handlerTemplateFile =
		"Handler.java.template";
	static final String activatorTemplateFile =
		"Activator.java.template";
	static final String counterTemplateFile =
		"Counter.java.template";

	final String handlerTemplate,
		activatorTemplate,counterTemplate;

	IProject sampleProject, incQueryProject;

	Map<GTPattern, GTPatternJavaData> gtPatternJavaRepresentations;

	/**
	 * @param gtPatternJavaRepresentations
	 * @param sampleProject
	 * @param incQueryProject
	 * @throws CodeGenerationException
	 */
	public SampleHandlerGenerator(Map<GTPattern, GTPatternJavaData> gtPatternJavaRepresentations, IProject sampleProject, IProject incQueryProject) throws CodeGenerationException {
		super();
		this.sampleProject = sampleProject;
		this.incQueryProject = incQueryProject;
		this.gtPatternJavaRepresentations = gtPatternJavaRepresentations;

		this.handlerTemplate = CodegenSupport.loadTemplate(handlerTemplateFile);
		this.activatorTemplate = CodegenSupport.loadTemplate(activatorTemplateFile);
		this.counterTemplate = CodegenSupport.loadTemplate(counterTemplateFile);
	}


	public void generateActivator(String projectID, IProgressMonitor monitor) throws CodeGenerationException {
		Map<String, String> substitutions = new HashMap<String, String>();

		substitutions.put("java-package", IncQueryNature.GENERATED_HANDLER_PACKAGEROOT);
		substitutions.put("java-project-id", projectID);

		String generatedCode = CodegenSupport.processTemplate(activatorTemplate, substitutions);


		try {
			IFolder rootFolder = sampleProject.getFolder(IncQueryNature.GENERATED_HANDLER_DIR);
			rootFolder.create(true, true, monitor);
			//String packageNameRoot = IncQueryNature.GENERATED_HANDLER_PACKAGEROOT;

			IFile file = rootFolder.getFile("Activator.java");

			CodegenSupport.printStringToFile(monitor, generatedCode, file);
		} catch (UnsupportedEncodingException e) {
			throw new CodeGenerationException("Error writing generated command handlers to file.", e);
		} catch (CoreException e) {
			throw new CodeGenerationException(
					"Error writing the Activator file of the sample ui project",
					e);
		}

	}

	/**Emits generated source code for DSM specific pattern matcher classes for the given patterns.
	 * @param monitor Progress monitor for the file operations
	 * @return the source code of handlers in a map
	 * @throws CodeGenerationException
	 */
	public Map<String,HandlerData> generateHandlersForPatternMatcherCalls(IProgressMonitor monitor) throws CodeGenerationException {
		Map<String, String> substitutions = new HashMap<String, String>();
		Map<String,HandlerData> handlers = new HashMap<String, SampleHandlerGenerator.HandlerData>();
		for(Map.Entry<GTPattern, GTPatternJavaData> entry: gtPatternJavaRepresentations.entrySet())
			{
			GTPattern pattern = entry.getKey();
			GTPatternJavaData patternData = entry.getValue();

			substitutions.put("pattern-name", pattern.getName());

			substitutions.put("java-matcher-package", patternData.getMatcherPackage());
			substitutions.put("java-signature-package", patternData.getSignaturePackage());
			substitutions.put("java-matcher", patternData.getMatcherName());
			substitutions.put("java-signature", patternData.getSignatureName());

			String className = getHandlerName(pattern);
			substitutions.put("java-class-name", className);

			CodegenSupport.PackageLocationFinder packageLocation =
				getHandlerPackage(pattern, monitor);

			String javaPackageName = packageLocation.getJavaPackageName();
			substitutions.put("handler-package", javaPackageName);

			

			String generatedCode = null;
			
			// process annotation
			String machinePatternMode = null;
			Map<String, String> annotation = 
				GTASMHelper.extractLowerCaseRuntimeAnnotation(pattern, "@GenerationMode");
			if (annotation != null){
				machinePatternMode = annotation.get("mode");
			
				// counter pattern
				if ("counter".equals(machinePatternMode)){
					className = getCounterName(pattern);
					substitutions.put("java-class-name", className);
					generatedCode = CodegenSupport.processTemplate(counterTemplate, substitutions);
				//} //else if("list".equals(machinePatternMode)){
				 //	generatedCode = CodegenSupport.processTemplate(handlerTemplate, substitutions);
				} else // if("constraint".equals(machinePatternMode)){
					continue; // constraints and warnings are handled in validation
				//}
			} 
			// no annotation means default handler
			if(generatedCode == null){
				generatedCode = CodegenSupport.processTemplate(handlerTemplate, substitutions);
			}
			
			handlers.put(className, new HandlerData(javaPackageName, className, pattern.getName()));
			
			IFile file = packageLocation.getFolder().getFile(className+ ".java");

			try {
				CodegenSupport.printStringToFile(monitor, generatedCode, file);
			} catch (UnsupportedEncodingException e) {
				throw new CodeGenerationException("Error writing generated command handlers to file.", e);
			} catch (CoreException e) {
				throw new CodeGenerationException(
						"Error writing source code file for generated command handlers of "
						+ pattern.getFqn(), e);
			}

			}

		return handlers;
	}

	/** Returns the name of the Java class representing the pattern's command handler
	 * @param pattern
	 * @return
	 */
	protected String getHandlerName(GTPattern pattern) {
		String pName = pattern.getName().substring(1);
		return Character.toUpperCase(pattern.getName().charAt(0))+pName+"Handler";
	}
	
	/** Returns the name of the Java class representing the pattern's command counter
	 * @param pattern
	 * @return
	 */
	protected String getCounterName(GTPattern pattern) {
		String pName = pattern.getName().substring(1);
		return Character.toUpperCase(pattern.getName().charAt(0))+pName+"Counter";
	}

	protected CodegenSupport.PackageLocationFinder getHandlerPackage(GTPattern pattern, IProgressMonitor monitor) throws CodeGenerationException {
		IPath pathRoot = sampleProject.getFolder(IncQueryNature.GENERATED_HANDLER_DIR).getFullPath();
		String packageNameRoot = IncQueryNature.GENERATED_HANDLER_PACKAGEROOT;

		return
			new CodegenSupport.PackageLocationFinder(pattern, pathRoot, packageNameRoot, monitor);
	}

	public static class HandlerData{

		private String handlerPackage, handlerName, patternName;

		public HandlerData(String handlerPackage, String handlerName, String patternName) {
			this.handlerName = handlerName;
			this.handlerPackage = handlerPackage;
			this.patternName = patternName;
		}

		public String getHandlerPackage() {
			return handlerPackage;
		}

		public void setHandlerPackage(String handlerPackage) {
			this.handlerPackage = handlerPackage;
		}

		public String getHandlerName() {
			return handlerName;
		}

		public void setHandlerName(String handlerName) {
			this.handlerName = handlerName;
		}

		public String getPatternName() {
			return patternName;
		}

		public void setPatternName(String patternName) {
			this.patternName = patternName;
		}
		
		
	}

}
