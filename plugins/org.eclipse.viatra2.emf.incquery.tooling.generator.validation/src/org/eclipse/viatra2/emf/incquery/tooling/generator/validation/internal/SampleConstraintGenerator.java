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


package org.eclipse.viatra2.emf.incquery.tooling.generator.validation.internal;


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
import org.eclipse.viatra2.emf.incquery.core.codegen.util.CodegenSupport;
import org.eclipse.viatra2.emf.incquery.core.codegen.util.GTPatternJavaData;
import org.eclipse.viatra2.emf.incquery.core.project.IncQueryNature;
import org.eclipse.viatra2.emf.incquery.tooling.generator.validation.ValidationCodegenPlugin;
import org.eclipse.viatra2.gtasm.support.helper.GTASMHelper;
import org.eclipse.viatra2.gtasmmodel.gtasm.metamodel.gt.GTPattern;
import org.osgi.framework.Bundle;

public class SampleConstraintGenerator {

	static final String activatorTemplateFile =
		"Activator.java.template";
	static final String constraintTemplateFile =
		"Constraint.java.template";

	final String activatorTemplate,constraintTemplate;

	IProject sampleProject, incQueryProject;

	Map<GTPattern, GTPatternJavaData> gtPatternJavaRepresentations;

	/**
	 * @param gtPatternJavaRepresentations
	 * @param sampleProject
	 * @param incQueryProject
	 * @throws CodeGenerationException
	 */
	public SampleConstraintGenerator(Map<GTPattern, GTPatternJavaData> gtPatternJavaRepresentations, IProject sampleProject, IProject incQueryProject) throws CodeGenerationException {
		super();
		this.sampleProject = sampleProject;
		this.incQueryProject = incQueryProject;
		this.gtPatternJavaRepresentations = gtPatternJavaRepresentations;

		Bundle thisBundle = ValidationCodegenPlugin.plugin.context.getBundle();
		this.activatorTemplate = CodegenSupport.loadTemplate(activatorTemplateFile, thisBundle);
		this.constraintTemplate = CodegenSupport.loadTemplate(constraintTemplateFile, thisBundle);
	}


	public void generateActivator(String projectID, IProgressMonitor monitor) throws CodeGenerationException {
		Map<String, String> substitutions = new HashMap<String, String>();

		substitutions.put("java-package", IncQueryNature.GENERATED_HANDLER_PACKAGEROOT);
		substitutions.put("java-project-id", projectID);

		String generatedCode = CodegenSupport.processTemplate(activatorTemplate, substitutions);


		try {
			IFolder rootFolder = sampleProject.getFolder(IncQueryNature.GENERATED_HANDLER_DIR);
			if(!rootFolder.exists()) {
				rootFolder.create(true, true, monitor);
			}//String packageNameRoot = IncQueryNature.GENERATED_HANDLER_PACKAGEROOT;

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
	public Map<String,ConstraintData> generateHandlersForPatternMatcherCalls(IProgressMonitor monitor) throws CodeGenerationException {
		Map<String, String> substitutions = new HashMap<String, String>();
		Map<String,ConstraintData> constraints = new HashMap<String, SampleConstraintGenerator.ConstraintData>();
		for(Map.Entry<GTPattern, GTPatternJavaData> entry: gtPatternJavaRepresentations.entrySet())
			{
			GTPattern pattern = entry.getKey();
			GTPatternJavaData patternData = entry.getValue();

			substitutions.put("pattern-name", pattern.getName());

			substitutions.put("java-matcher-package", patternData.getMatcherPackage());
			substitutions.put("java-signature-package", patternData.getSignaturePackage());
			substitutions.put("java-matcher", patternData.getMatcherName());
			substitutions.put("java-signature", patternData.getSignatureName());

			String className = getConstraintName(pattern);
			substitutions.put("java-class-name", className);

			CodegenSupport.PackageLocationFinder packageLocation =
				getHandlerPackage(pattern, monitor);

			String javaPackageName = packageLocation.getJavaPackageName();
			substitutions.put("constraint-package", javaPackageName);

			String generatedCode = null;
			
			// process annotation
			
			Map<String, String> annotation = 
				GTASMHelper.extractLowerCaseRuntimeAnnotation(pattern, "@Constraint");
			if (annotation != null){
				String constraintMode = annotation.get("mode");
				String locationName = annotation.get("location");
				if(locationName != null && !locationName.equals("")){
					substitutions.put("location-param-name", locationName);
				}
				String message = annotation.get("message");
				if(message != null && !message.equals("")){
					substitutions.put("message-string", message);
				}
				// counter pattern
				if ("problem".equals(constraintMode) || "warning".equals(constraintMode)){
					className = getConstraintName(pattern);
					substitutions.put("java-class-name", className);
					generatedCode = CodegenSupport.processTemplate(constraintTemplate, substitutions);
				//} //else if("list".equals(machinePatternMode)){
				 //	generatedCode = CodegenSupport.processTemplate(handlerTemplate, substitutions);
				} else 
					continue; // constraints and warnings are handled in validation
				//}
			} 
			// no annotation means default handler was generated
			if(generatedCode == null){
				//generatedCode = CodegenSupport.processTemplate(handlerTemplate, substitutions);
				continue;
			}
			
			constraints.put(className, new ConstraintData(javaPackageName, className, pattern.getName()));
			
			IFile file = packageLocation.getFolder().getFile(className+ ".java");

			try {
				CodegenSupport.printStringToFile(monitor, generatedCode, file);
			} catch (UnsupportedEncodingException e) {
				throw new CodeGenerationException("Error writing generated command constraints to file.", e);
			} catch (CoreException e) {
				throw new CodeGenerationException(
						"Error writing source code file for generated command constraint of "
						+ pattern.getFqn(), e);
			}

			}

		return constraints;
	}
	
	/** Returns the name of the Java class representing the pattern's command constraint
	 * @param pattern
	 * @return
	 */
	protected String getConstraintName(GTPattern pattern) {
		String pName = pattern.getName().substring(1);
		return Character.toUpperCase(pattern.getName().charAt(0))+pName+"Constraint";
	}

	protected CodegenSupport.PackageLocationFinder getHandlerPackage(GTPattern pattern, IProgressMonitor monitor) throws CodeGenerationException {
		IPath pathRoot = sampleProject.getFolder(IncQueryNature.GENERATED_HANDLER_DIR).getFullPath();
		String packageNameRoot = IncQueryNature.GENERATED_HANDLER_PACKAGEROOT;

		return
			new CodegenSupport.PackageLocationFinder(pattern, pathRoot, packageNameRoot, monitor);
	}

	public static class ConstraintData{

		private String constraintPackage, constraintName, patternName;

		public ConstraintData(String constraintPackage, String constraintName, String patternName) {
			this.constraintName = constraintName;
			this.constraintPackage = constraintPackage;
			this.patternName = patternName;
		}

		public String getConstraintPackage() {
			return constraintPackage;
		}

		public void setConstraintPackage(String constraintPackage) {
			this.constraintPackage = constraintPackage;
		}

		public String getConstraintName() {
			return constraintName;
		}

		public void setConstraintName(String constraintName) {
			this.constraintName = constraintName;
		}

		public String getPatternName() {
			return patternName;
		}

		public void setPatternName(String patternName) {
			this.patternName = patternName;
		}

	}

}
