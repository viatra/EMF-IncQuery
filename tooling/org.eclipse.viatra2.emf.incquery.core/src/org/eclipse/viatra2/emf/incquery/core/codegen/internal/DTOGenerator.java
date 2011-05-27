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
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.common.util.EList;
import org.eclipse.viatra2.emf.incquery.core.codegen.CodeGenerationException;
import org.eclipse.viatra2.emf.incquery.core.project.IncQueryNature;
import org.eclipse.viatra2.gtasmmodel.gtasm.metamodel.gt.GTPattern;
import org.eclipse.viatra2.gtasmmodel.gtasm.metamodel.gt.PatternVariable;

/**
 * Generates the DTO objects of the patterns
 * @author Akos Horvath and Gabor Bergmann
 *
 */
public class DTOGenerator {

	static final String patternDTOTemplateFile = "DTOTemplate.java.template";

	final String patternDTOTemplate;

	private IProject project;

	private APIGenerator generator;

	/**
	 * @param project
	 * @param generator
	 * @throws CodeGenerationException
	 */
	public DTOGenerator(IProject project, APIGenerator generator) throws CodeGenerationException {
		super();
		this.project = project;
		this.generator = generator;
		this.patternDTOTemplate = CodegenSupport.loadTemplate(patternDTOTemplateFile);
	}

	/**Emits generated source code for the given pattern's DTO that is used to return the matchings of the given pattern
	 * @param pattern The patern who's DTO class is emitted
	 * @param monitor Progress monitor for the file operations
	 * @throws CodeGenerationException
	 */
	public void generateDTOForPattern(GTPattern pattern, IProgressMonitor monitor) throws CodeGenerationException {
//		if(pattern.getSymParameters().size() == 0)
//			return;

		Map<String, String> substitutions = new HashMap<String, String>();

		substitutions.put("pattern-name", pattern.getName());
		substitutions.put("pattern-fqn", pattern.getFqn());
		substitutions.put("module-file", pattern.getNamespace().getModule().getFileName());

		String className = getSignatureName(pattern);
		substitutions.put("java-class", className);

		CodegenSupport.PackageLocationFinder packageLocation =
			getSignaturePackage(pattern, monitor);

		String javaPackageName = packageLocation.getJavaPackageName();
		substitutions.put("java-package",javaPackageName);

		String[] strs = generateInputSpecificDTOAttributeTags(pattern);
		substitutions.put("java-attributes", strs[0]);
		substitutions.put("java-getters", strs[1]);
		substitutions.put("java-setters", strs[2]);
		substitutions.put("java-inputparams", strs[3]);
		substitutions.put("java-setattributes", strs[4]);
		substitutions.put("equals-override", strs[5]);
		substitutions.put("hashcode-override", strs[6]);
		substitutions.put("prettyprint-override", strs[7]);
		substitutions.put("fields-list", strs[8]);
		substitutions.put("parameters-list-quoted", strs[9]);
		substitutions.put("java-reflective-getter-lines", strs[10]);
		substitutions.put("java-reflective-setter-lines", strs[11]);

		String generatedCode;
		generatedCode = CodegenSupport.processTemplate(patternDTOTemplate, substitutions);

		try {
			IFile file = packageLocation.getFolder().getFile(className+ ".java");
			CodegenSupport.printStringToFile(monitor, generatedCode, file);
		} catch (UnsupportedEncodingException e) {
			throw new CodeGenerationException("Error writing generated pattern matcher matchers to file.", e);
		} catch (CoreException e) {
			throw new CodeGenerationException(
					"Error writing source code file for generated pattern matchers of "
					+ pattern.getFqn(), e);
		}
	}

	/** Returns the name of the Java class representing the pattern's DTO
	 * @param pattern
	 * @return
	 */
	protected String getSignatureName(GTPattern pattern) {
		return toCapitalLetter(pattern.getName())+"Signature";
	}

	/** Returns the Java package location of the patterns DTO
	 * @param pattern
	 * @param monitor
	 * @return
	 * @throws CodeGenerationException
	 */
	protected CodegenSupport.PackageLocationFinder getSignaturePackage(GTPattern pattern, IProgressMonitor monitor) throws CodeGenerationException {
		IPath pathRoot = project.getFolder(IncQueryNature.GENERATED_DTO_DIR).getFullPath();
		String packageNameRoot = IncQueryNature.GENERATED_DTO_PACKAGEROOT;

		return
			new CodegenSupport.PackageLocationFinder(pattern, pathRoot, packageNameRoot, monitor);
	}
	/** Generates the given pattern's DTO specific attributes and getter setter methods for the template
	 * @param pattern The GT pattern who's domain specific DTO class needs to be generated
	 * @return A string array containing the proper template parameters to be used
	 * string[0] is the private attibutes of the DTO
	 * string[1] is the public setter methods
	 * string[2] is the public getter methods
	 * string[3] is the input parameters for the DTO's constructor
	 * string[4] is the java code that sets the initial values of the DTO in its constructor
	 * string[5] is equals-override
	 * string[6] is hashcode-override
	 * string[7] is prettyprint-override
	 * string[8] is the comma-separated list of fields
	 * string[9] is the comma-separated list of quoted parameter names
	 * string[10] is the reflective getter java code
	 * string[11] is the reflective setter java code
	 */
	private String[] generateInputSpecificDTOAttributeTags(GTPattern pattern) {

		
		
		String[] strings = {"","","","","","","","","","","",""};
		//inits the Strings :-)

		EList<PatternVariable> patternVars = pattern.getSymParameters();

		
		
		for(int i= 0; i<patternVars.size(); i++) {
			PatternVariable patternVar = patternVars.get(i);
			
			String patternVarNamePlain = patternVar.getName();
			String patternVarNameSafe = CodegenSupport.ensureJavasafety( patternVarNamePlain );
			String patternVarNameMember = "f"+patternVarNamePlain;
			
			strings[0] += "\tprivate "+generator.getElementType(patternVar)+" "+patternVarNameMember+";"; // Java safe

			strings[1] += "\tpublic "+generator.getElementType(patternVar)+" getValueOf"+toCapitalLetter(patternVarNamePlain)
					+"(){\n\t\t return "+patternVarNameMember+";\n\t}"; // Java safe

			strings[2] += "\tpublic void setValueOf"+toCapitalLetter(patternVarNamePlain)+"("+generator.getElementType(patternVar)+" "
					+patternVarNameSafe+")" + "{\n\t\t this."+patternVarNameMember+"="+patternVarNameSafe+";\n\t}";

			strings[3] += generator.getElementType(patternVar)+" "+patternVarNameSafe;
			strings[4] += "\t\tthis."+patternVarNameMember+" = "+patternVarNameSafe+";";
			strings[5] += "\t\tif (" + patternVarNameMember + " == null) "
						+ "{if (other." + patternVarNameMember + " != null) return false;}\n"
						+ "\t\telse if (!" + patternVarNameMember + ".equals(other." + patternVarNameMember+ "))"
						+ " return false;";
			strings[6] += "\t\tresult = prime * result + ((" + patternVarNameMember	+ " == null) ? 0 : " + patternVarNameMember + ".hashCode());";
			strings[7] += "\t\tresult.append(\"" + (i==0? "" : ", ") 
				+ "\\\""+patternVarNamePlain+"\\\"=\" + printValue(" + patternVarNameMember  +"));";
			strings[8] += patternVarNameMember;
			strings[9] += "\"" + patternVarNamePlain + "\"";
			strings[10] += "\t\tif (\"" + patternVarNamePlain 
				+ "\".equals(parameterName)) return " + patternVarNameMember + ";";
			strings[11] += "\t\tif (\"" + patternVarNamePlain + "\".equals(parameterName)) {\n\t\t\t"
				+ patternVarNameMember + " = newValue;\n\t\t\treturn true;\n\t\t}";


			if(i < patternVars.size()-1) {
				strings[0] += "\n";
				strings[1] += "\n";
				strings[2] += "\n";
				strings[3] += ", ";
				strings[4] += "\n";
				strings[5] += "\n";
				strings[6] += "\n";
				strings[7] += "\n";
				strings[8] += ", ";
				strings[9] += ", ";
				strings[10] += "\n";
				strings[11] += "\n";
			}
		}
//		if(patternVars.size() > 0) //check that there are at least one parameter -> if no nothing to be generated for the DTO
//		strings[7] = generateDynamicEMFNameandIDgetter(patternVars,pattern);

		return strings;
	}

	private String toCapitalLetter(String name) {
		String pName = name.substring(1);
		return Character.toUpperCase(name.charAt(0))+pName;
	}


//	private String generateDynamicEMFNameandIDgetter(EList<PatternVariable> patternVars, GTPattern pattern){
//		String _temp = "";
//
//		_temp+= "\t@Override\n\tpublic String toString(){\n";
//		_temp+="\t\tEStructuralFeature feature = null;\n";
//		_temp+="\t\tString _temp = \" A(n) "+pattern.getName()+" signature object (\"+hashCode()+\")\\n\";\n";
//
//		for(int i= 0; i<patternVars.size(); i++)
//		{
//		PatternVariable patternVar = patternVars.get(i);
//		_temp += "\t\t// Checks that "+patternVar.getName()+" has a name attribute and uses if it has \n";
//		_temp += "\t\t_temp += \"\\t"+patternVar.getName()+" = \";\n";
//
//		//toString pretty verion
//		_temp+="\t\tfeature = null;\n";
//
//		_temp += "\t\tif("+patternVar.getName() +" instanceof EObject)\n";
//		_temp += "\t\t\tfeature = ((EObject)"+patternVar.getName()+").eClass().getEStructuralFeature(\"name\");\n\n";
//
//		_temp += "\t\tif(feature != null && ((EObject)"+patternVar.getName()+").eGet(feature) != null) _temp += ((EObject)"+patternVar.getName()+").eGet(feature).toString();\n";
//		_temp += "\t\telse _temp += "+patternVar.getName()+".toString();\n";
//		_temp += "\t\t_temp += \"\\n\";\n\n";
//		}
//		_temp += "\t\treturn _temp;\n\t}";
//		return _temp;
//	}
}
