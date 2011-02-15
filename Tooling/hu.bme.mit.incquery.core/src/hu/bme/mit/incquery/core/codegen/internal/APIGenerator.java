/*******************************************************************************
 * Copyright (c) 2004-2010 Gabor Bergmann, Akos Horvath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Akos Horvath and Gabor Bergmann - initial API and implementation
 *******************************************************************************/

package hu.bme.mit.incquery.core.codegen.internal;

import hu.bme.mit.incquery.core.IncQueryPlugin;
import hu.bme.mit.incquery.core.codegen.CodeGenerationException;
import hu.bme.mit.incquery.core.project.IncQueryNature;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.common.util.EList;
import org.eclipse.pde.core.project.IBundleProjectDescription;
import org.eclipse.pde.core.project.IBundleProjectService;
import org.eclipse.pde.core.project.IPackageExportDescription;
import org.eclipse.pde.internal.core.project.PackageExportDescription;
import org.eclipse.viatra2.gtasmmodel.gtasm.metamodel.gt.GTPattern;
import org.eclipse.viatra2.gtasmmodel.gtasm.metamodel.gt.PatternVariable;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

/**
 * Generates the DSM specific Interfaces for the GT Patterns
 * @author Akos Horvath and Bergmann Gabor
 *
 */
public class APIGenerator {
	
	static final String patternMatcherAPITemplateFile = 
		"GeneratedPatternMatcherAPI.java.template";
	static final String patternSpecificMethodsTemplateFile = 
		"PatternSpecificMatcherMethods.javafragment.template";
	static final String nullaryPatternSpecificMethodsTemplateFile = 
		"PatternSpecificMatcherMethodsNullary.javafragment.template";
	
	final String patternMatcherAPITemplate, 
		patternSpecificMethodsTemplate, 
		nullaryPatternSpecificMethodsTemplate;
	
	IProject project;
	
	final DTOGenerator dtoGenerator;

	/**
	 * @param project
	 * @throws CodeGenerationException 
	 */
	public APIGenerator(IProject project) throws CodeGenerationException {
		super();
		this.project = project;
		this.patternMatcherAPITemplate = CodegenSupport.loadTemplate(patternMatcherAPITemplateFile);
		this.patternSpecificMethodsTemplate = CodegenSupport.loadTemplate(patternSpecificMethodsTemplateFile);
		this.nullaryPatternSpecificMethodsTemplate = CodegenSupport.loadTemplate(nullaryPatternSpecificMethodsTemplateFile);
		this.dtoGenerator = new DTOGenerator(project,this);
	}
	
	/**Emits generated source code for DSM specific pattern matcher classes for the given patterns.
	 * @param patterns The paterns who's API classes are emitted
	 * @param monitor Progress monitor for the file operations
	 * @throws CodeGenerationException
	 */
	public void generateApiForPatterns(Set<GTPattern> patterns, IProgressMonitor monitor) throws CodeGenerationException {
		Map<String, String> substitutions = new HashMap<String, String>();
		Set<String> packagestoExport = new HashSet<String>();
		
		for(GTPattern pattern: patterns)
			{
			substitutions.put("pattern-name", pattern.getName());
			substitutions.put("pattern-fqn", pattern.getFqn());
			substitutions.put("pattern-varnum", ""+pattern.getSymParameters().size());
			substitutions.put("module-file", pattern.getNamespace().getModule().getFileName());
			substitutions.put("posmap-block", PatternBuilderSourceGenerator.genPosMapper(pattern));
			
			//pattern input specific parameters
			String[] strs = generateInputSpecificParameters(pattern);
			substitutions.put("pattern-params", strs[0]);
			substitutions.put("pattern-params-value", strs[1]);
			substitutions.put("pattern-params-boolean", strs[2]);
			substitutions.put("tuple-params", strs[3]);
			substitutions.put("signature-params", strs[4]);
			substitutions.put("quoted-params", strs[5]);
			substitutions.put("javadoc-params", strs[6]);
			
			//DTO specific parameters
			
			substitutions.put("pattern-dto", dtoGenerator.getSignatureName(pattern));
			
			
			String className = getMatcherName(pattern);
			substitutions.put("java-class", className);
			
			CodegenSupport.PackageLocationFinder packageLocation = getAPIPackage(pattern, monitor);
			
			String javaPackageName = packageLocation.getJavaPackageName();
			substitutions.put("java-package",javaPackageName);
			
			// Parameter-specific method fragments
			if(pattern.getSymParameters().size() != 0)
				substitutions.put("parameter-specific-methods", 
						CodegenSupport.processTemplate(patternSpecificMethodsTemplate, substitutions));
				else substitutions.put("parameter-specific-methods", 
						CodegenSupport.processTemplate(nullaryPatternSpecificMethodsTemplate, substitutions));
			
			//DTO java package
			CodegenSupport.PackageLocationFinder dtoPackage = dtoGenerator.getSignaturePackage(pattern, monitor);
			substitutions.put("java-dtoimport", dtoPackage.getJavaPackageName()+"."+dtoGenerator.getSignatureName(pattern));
			packagestoExport.add(dtoPackage.getJavaPackageName());
			
			packagestoExport.add(javaPackageName);
			
			String generatedCode = CodegenSupport.processTemplate(patternMatcherAPITemplate, substitutions);
						
			IFile file = packageLocation.getFolder().getFile(className+ ".java");
			
			try {
				CodegenSupport.printStringToFile(monitor, generatedCode, file);
			} catch (UnsupportedEncodingException e) {
				throw new CodeGenerationException("Error writing generated pattern matcher matchers to file.", e);
			} catch (CoreException e) {
				throw new CodeGenerationException(
						"Error writing source code file for generated pattern matchers of "
						+ pattern.getFqn(), e);
			}

			//DTO generation
			dtoGenerator.generateDTOForPattern(pattern, monitor);
			}
		
		setManifestExportPackage(packagestoExport, monitor);
	}

	/** Generates the given pattern's symbolic parameter specific inputs for the templates
	 * @param pattern The GT pattern who's domain specific matcher class needs to be generated
	 * @return
	 * string[0] is the parameter list of the implementing method
	 * string[1] is the list of the symbolics parameters with a ',' delimeter character
	 * string[2] a specific set of operations to set the fixed boolean array for the RetePatternMatcher
	 * string[3] the tuple based input parameter for the DTO's constructor
	 * string[4] signature-params
	 * string[5] quoted-params
	 * string[6] javadoc-params
	 */
	private String[] generateInputSpecificParameters(GTPattern pattern) {
	
		String[] strings = {"","","","", "", "", ""};
		//inits th3e Strings :-)
		
		EList<PatternVariable> patternVars = pattern.getSymParameters();
		
		for(int i= 0; i<patternVars.size(); i++)
			{
			PatternVariable patternVar = patternVars.get(i);
			String varName = patternVar.getName();
			strings[0] += getElementType(patternVar)+" "+varName;
			strings[1] += varName;
			strings[2] += varName+"!= null?true:false";
			strings[3] += "t.get("+i+")";
			strings[4] += "signature["+i+"]";
			strings[5] += "\""+varName+"\"";
			strings[6] += "\t * @param " + varName 
				+ " the fixed value of pattern parameter " 
				+ varName + ", or null if not bound.";
			
			if(i < patternVars.size()-1)
				{strings[0] += ", ";
				 strings[1] += ", ";
				 strings[2] += ", ";
				 strings[3] += ", ";
				 strings[4] += ", ";
				 strings[5] += ", ";
				 strings[6] += "\n";
				}
			}
		
		return strings;
	}
	
	/** Returns the domain specific type of the given pattern variable
	 * @param patternVar the pattern variable
	 * @return At this moment it gives back only Object TODO finish
	 */
	protected String getElementType(PatternVariable patternVar) {
		return "Object";
	}
	
	/** Returns the java package location of the patterns DTO
	 * @param pattern 
	 * @param monitor
	 * @return
	 * @throws CodeGenerationException
	 */
	protected CodegenSupport.PackageLocationFinder getAPIPackage(GTPattern pattern, IProgressMonitor monitor) throws CodeGenerationException {
		IPath pathRoot = project.getFolder(IncQueryNature.GENERATED_MATCHERS_DIR).getFullPath();
		String packageNameRoot = IncQueryNature.GENERATED_MATCHERS_PACKAGEROOT;
		
		return
			new CodegenSupport.PackageLocationFinder(pattern, pathRoot, packageNameRoot, monitor);
	}
	
	/** Sets the exported packages in the MANIFEST.ML file of the project 
	 * @param packages the packages of the API
	 * @param monitor
	 * @throws CodeGenerationException
	 */
	private void setManifestExportPackage(Set<String> packages, IProgressMonitor monitor) throws CodeGenerationException {
		
		BundleContext context = IncQueryPlugin.plugin.context;
		ServiceReference ref = context
				.getServiceReference(IBundleProjectService.class.getName());
		IBundleProjectService service = (IBundleProjectService) context
				.getService(ref);
		IBundleProjectDescription bundleDesc;
		try {
			bundleDesc = service.getDescription(project);
			
			IPackageExportDescription[] oldExports = bundleDesc.getPackageExports();
			ArrayList<IPackageExportDescription> newExports = new ArrayList<IPackageExportDescription>();
			if(oldExports != null)
				newExports.addAll(Arrays.asList(oldExports));
			
			for(String packageFqn: packages) {
			IPackageExportDescription pckDesc = service.newPackageExport(packageFqn, null, true, null);
			newExports.add(pckDesc);
			}
		
			bundleDesc.setPackageExports(newExports.toArray(new PackageExportDescription[0]));
			
			bundleDesc.apply(monitor);
		} catch (CoreException e) {
			throw new CodeGenerationException("Error while modifying the Runtime Export package attributes", e);
		} finally {
			context.ungetService(ref);
		}
	}
	
	protected String getMatcherName(GTPattern pattern) {
		String pName = pattern.getName().substring(1);
		return Character.toUpperCase(pattern.getName().charAt(0))+pName +"Matcher";
		
	}
}
