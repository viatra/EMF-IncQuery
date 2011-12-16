package org.eclipse.viatra2.emf.incquery.databinding.tooling;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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
import org.eclipse.viatra2.gtasm.support.helper.GTASMHelper;
import org.eclipse.viatra2.gtasmmodel.gtasm.metamodel.asm.core.GTASMElement;
import org.eclipse.viatra2.gtasmmodel.gtasm.metamodel.asm.core.RuntimeAnnotation;
import org.eclipse.viatra2.gtasmmodel.gtasm.metamodel.asm.core.RuntimeAnnotationElement;
import org.eclipse.viatra2.gtasmmodel.gtasm.metamodel.gt.GTPattern;
import org.osgi.framework.Bundle;

public class DatabindingAdapterGenerator {
 
	static final String activatorTemplateFile = "Activator.java.template";
	static final String databindingTemplateFile = "DatabindingAdapter.java.template";

	final String activatorTemplate, databindingTemplate;

	IProject databindingProject, incQueryProject;

	Map<GTPattern, GTPatternJavaData> gtPatternJavaRepresentations;

	public DatabindingAdapterGenerator(Map<GTPattern, GTPatternJavaData> gtPatternJavaRepresentations, IProject sampleProject, IProject incQueryProject) throws CodeGenerationException {
		super();
		this.databindingProject = sampleProject;
		this.incQueryProject = incQueryProject;
		this.gtPatternJavaRepresentations = gtPatternJavaRepresentations;

		Bundle thisBundle = DatabindingToolingActivator.context.getBundle();
		this.activatorTemplate = CodegenSupport.loadTemplate(activatorTemplateFile, thisBundle);
		this.databindingTemplate = CodegenSupport.loadTemplate(databindingTemplateFile, thisBundle);
	}

	public void generateActivator(String projectID, IProgressMonitor monitor)
			throws CodeGenerationException {
		Map<String, String> substitutions = new HashMap<String, String>();

		substitutions.put("java-package", IncQueryNature.GENERATED_DATABINDING_PACKAGEROOT);
		substitutions.put("java-project-id", projectID);

		String generatedCode = CodegenSupport.processTemplate(
				activatorTemplate, substitutions);

		try {
			IFolder rootFolder = databindingProject
					.getFolder(IncQueryNature.GENERATED_DATABINDING_DIR);
			rootFolder.create(true, true, monitor);

			IFile file = rootFolder.getFile("Activator.java");

			CodegenSupport.printStringToFile(monitor, generatedCode, file);
		} 
		catch (UnsupportedEncodingException e) {
			throw new CodeGenerationException("Error writing generated command handlers to file.", e);
		} 
		catch (CoreException e) {
			throw new CodeGenerationException("Error writing the Activator file of the databinding project", e);
		}
	}

	public Map<String, DatabindingAdapterData> generateHandlersForPatternMatcherCalls(
			IProgressMonitor monitor) throws CodeGenerationException {
		Map<String, String> substitutions = new HashMap<String, String>();
		Map<String, DatabindingAdapterData> databindableMatchers = new HashMap<String, DatabindingAdapterData>();

		for (Map.Entry<GTPattern, GTPatternJavaData> entry : gtPatternJavaRepresentations.entrySet()) {
			GTPattern pattern = entry.getKey();
			GTPatternJavaData patternData = entry.getValue();

			substitutions.put("pattern-name", pattern.getName());

			substitutions.put("java-signature-package",	patternData.getSignaturePackage());
			substitutions.put("java-signature", patternData.getSignatureName());

			String className = getDatabindableMatcherName(pattern);
			substitutions.put("java-class-name", className);

			CodegenSupport.PackageLocationFinder packageLocation = getHandlerPackage(
					pattern, monitor);

			String javaPackageName = packageLocation.getJavaPackageName();
			substitutions.put("databinding-package", javaPackageName);

			String generatedCode = null;
			String message = null;
			String parameterMap = "";
			
			// process annotation
			Map<String, String> patternUIAnnotation = GTASMHelper.extractLowerCaseRuntimeAnnotation(pattern, "@PatternUI");
			Set<Map<String, String>> observableValueAnnotation = extractLowerCaseRuntimeAnnotation(pattern, "@ObservableValue");

			if (patternUIAnnotation != null) {
				message = patternUIAnnotation.get("message");
				if (message == null) continue;
			}
			
			if (observableValueAnnotation != null) {
				int i = 1;
				for (Map<String,String> map : observableValueAnnotation) {
					parameterMap += "\t\tparameterMap.put(\""+map.get("name")+"\", \""+map.get("expression")+"\");";
					if (i != observableValueAnnotation.size()) {
						parameterMap += "\n";
					}
					i++;
				}
			}
			
			if (!parameterMap.matches("")) {
				substitutions.put("parameterMap", parameterMap);
				generatedCode = CodegenSupport.processTemplate(databindingTemplate, substitutions);			
				databindableMatchers.put(className, new DatabindingAdapterData(javaPackageName, className, 
						pattern.getName(), message, false, patternData.getMatcherPackage()+"$Factory"));

				IFile file = packageLocation.getFolder().getFile(className + ".java");

				try {
					CodegenSupport.printStringToFile(monitor, generatedCode, file);
				} 
				catch (UnsupportedEncodingException e) {
					throw new CodeGenerationException("Error writing generated command constraints to file.",e);
				} 
				catch (CoreException e) {
					throw new CodeGenerationException("Error writing source code file for generated command constraint of " + pattern.getFqn(), e);
				}
			}
			else {
				databindableMatchers.put(className, new DatabindingAdapterData(javaPackageName, className, 
						pattern.getName(), message, true, patternData.getMatcherPackage()+"$Factory"));
			}
		}

		return databindableMatchers;
	}

	protected String getDatabindableMatcherName(GTPattern pattern) {
		String pName = pattern.getName().substring(1);
		return "Databinding"+ Character.toUpperCase(pattern.getName().charAt(0)) + pName+ "Adapter";
	}

	protected CodegenSupport.PackageLocationFinder getHandlerPackage(
			GTPattern pattern, IProgressMonitor monitor)
			throws CodeGenerationException {
		IPath pathRoot = databindingProject.getFolder(
				IncQueryNature.GENERATED_DATABINDING_DIR).getFullPath();
		String packageNameRoot = IncQueryNature.GENERATED_DATABINDING_PACKAGEROOT;
		return new CodegenSupport.PackageLocationFinder(pattern, pathRoot,
				packageNameRoot, monitor);
	}
	
	private Set<Map<String, String>> extractLowerCaseRuntimeAnnotation(GTASMElement element, String annotationName) {
		Set<Map<String, String>> result = new HashSet<Map<String, String>>();
		
		for (RuntimeAnnotation annotation : element.getRuntimeAnnotations()) {
			if (annotation.getAnnotationName().toLowerCase().equals(annotationName.toLowerCase())) {
				Map<String, String> map = new HashMap<String, String>();
				for (RuntimeAnnotationElement annotationElement : annotation.getElements()) {
					map.put(annotationElement.getKey().toLowerCase(), annotationElement.getValue());
				}
				result.add(map);
			}
		}
		return result;
	}
}
