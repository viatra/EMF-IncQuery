/*******************************************************************************
 * Copyright (c) 2010-2012, Mark Czotter, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Mark Czotter - initial API and implementation
 *******************************************************************************/

package org.eclipse.viatra2.emf.incquery.tooling.generator.util

import com.google.inject.Inject
import org.apache.log4j.Logger
import org.eclipse.core.resources.IWorkspaceRoot
import org.eclipse.core.runtime.Path
import org.eclipse.emf.ecore.EObject
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Pattern
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.PatternModel
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Variable
import org.eclipse.xtend2.lib.StringConcatenation
import org.eclipse.xtext.common.types.JvmTypeReference
import org.eclipse.xtext.nodemodel.util.NodeModelUtils
import org.eclipse.xtext.xbase.compiler.TypeReferenceSerializer
import org.eclipse.xtext.xbase.compiler.output.ITreeAppendable
import org.eclipse.xtext.xbase.typing.ITypeProvider
import org.eclipse.jdt.core.JavaConventions
import org.eclipse.jdt.core.JavaCore
import org.eclipse.core.runtime.IStatus
import java.util.regex.Matcher

/**
 * Utility class for the EMFPatternLanguageJvmModelInferrer.
 * 
 * @author Mark Czotter
 */
class EMFPatternLanguageJvmModelInferrerUtil {
	
	@Inject extension EMFJvmTypesBuilder
	Logger logger = Logger::getLogger(getClass())
	private String MULTILINE_COMMENT_PATTERN = "(/\\*([^*]|[\\r\\n]|(\\*+([^*/]|[\\r\\n])))*\\*+/)"
	@Inject	IWorkspaceRoot workspaceRoot
	@Inject ITypeProvider typeProvider
	@Inject TypeReferenceSerializer typeReferenceSerializer
	
	def bundleName(Pattern pattern) {
		val project = workspaceRoot.getFile(
				new Path(pattern.eResource.getURI().toPlatformString(true)))
				.getProject();
		project.name
	}
	
	/**
	 * This method returns the pattern name. 
	 * If the pattern name contains the package (any dot), 
	 * then removes all segment except the last one.
	 */
	def realPatternName(Pattern pattern) {
		var name = pattern.name
		if (name.contains(".")) {
			return name.substring(name.lastIndexOf(".")+1)
		}
		return name
	}
	
	def modelFileName(EObject object) {
		val name = object.eResource?.URI.trimFileExtension.lastSegment
		val status = JavaConventions::validateJavaTypeName(name, JavaCore::VERSION_1_6, JavaCore::VERSION_1_6)
		if (status.severity == IStatus::ERROR) {
			throw new IllegalArgumentException("The file name " + name + " is not a valid Java type name. Please, rename the file!")
		}
		name
	}
	/**
	 * Returns the MatcherFactoryClass name based on the Pattern's name
	 */
	def matcherFactoryClassName(Pattern pattern) {
		var name = pattern.name
		if (name.contains(".")) {
			name = pattern.realPatternName
		}
		name.toFirstUpper+"MatcherFactory"
	}
	
	/**
	 * Returns the IMatcherFactoryProvider class name based on the Pattern's name
	 */
	def matcherFactoryProviderClassName(Pattern pattern) {
		"Provider"
	}	
	/**
	 * Returns the IMatcherFactoryProvider class name based on the Pattern's name
	 */
	def matcherFactoryHolderClassName(Pattern pattern) {
		"LazyHolder"
	}	

	/**
	 * Returns the MatcherClass name based on the Pattern's name
	 */
   	def matcherClassName(Pattern pattern) {
   		var name = pattern.name
		if (name.contains(".")) {
			name = pattern.realPatternName
		}
   		name.toFirstUpper+"Matcher"
   	}

	/**
	 * Returns the MatchClass name based on the Pattern's name
	 */   	 
   	def matchClassName(Pattern pattern) {
   		var name = pattern.name
		if (name.contains(".")) {
			name = pattern.realPatternName
		}
   		name.toFirstUpper+"Match"
   	}
   	
	/**
	 * Returns the ProcessorClass name based on the Pattern's name
	 */   	
   	def processorClassName(Pattern pattern) {
   		var name = pattern.name
		if (name.contains(".")) {
			name = pattern.realPatternName
		}
   		name.toFirstUpper+"Processor"
   	}
   	
   	/**
	 * Returns the EvaluatorClass name based on the Pattern's name
	 */   	
   	def evaluatorClassName(Pattern pattern) {
   		var name = pattern.name
		if (name.contains(".")) {
			name = pattern.realPatternName
		}
   		name.toFirstUpper+"Evaluator"
   	}
   	
   	/**
   	 * Returns field name for Variable
   	 */
   	def fieldName(Variable variable) {
   		"f"+variable?.name.toFirstUpper
   	}
   	
   	/**
   	 * Returns parameter name for Variable
   	 */
   	def parameterName(Variable variable) {
   		"p"+variable?.name?.toFirstUpper
   	}
   	
   	def positionConstant(Variable variable) {
   		"POSITION_"+variable?.name?.toUpperCase;
   	}
   	
   	/**
   	 * Returns correct getter method name for variable.
   	 * For variable with name 'class' returns getValueOfClass, otherwise returns <code>get#variable.name.toFirstUpper#</code>.
   	 */
   	def getterMethodName(Variable variable) {
   		if (variable.name == "class") {
   			return "getValueOfClass"
   		} else {
   			return "get" + variable?.name?.toFirstUpper
   		}
   	}
   	
   	/**
   	 * Returns correct setter method name for variable.
   	 * Currently returns <code>set#variable.name.toFirstUpper#</code>.
   	 */
   	def setterMethodName(Variable variable) {
   		"set" + variable?.name?.toFirstUpper
   	}
   	
	/**
	 * Calls the typeProvider. 
	 * See the XBaseUsageCrossReferencer class, possible solution for local variable usage
	 * TODO: improve type calculation 
	 * @return JvmTypeReference pointing the EClass that defines the Variable's type.
	 * @see ITypeProvider
	 * @see EMFPatternTypeProvider
	 */
   	def JvmTypeReference calculateType(Variable variable) {
   		typeProvider.getTypeForIdentifiable(variable)
   	}
   	
   	/**
   	 * Serializes the EObject into Java String variable.
   	 */
   	def serializeToJava(EObject eObject) {
		val parseString = eObject.serialize
		if (parseString.nullOrEmpty) {
			return "";
		}
		val splits = parseString.split("[\r\n]+")
		val stringRep = '''String patternString = ""''' as StringConcatenation
	  	stringRep.newLine
	  	for (s : splits) {
	  		// Extra space needed before and after every line, 
	  		// otherwise parser parses the entire string (or part of it) as package name).
	  		stringRep.append("+\" " + s + " \"")
	  		stringRep.newLine
		}
	  	stringRep.append(";")
	  	return stringRep   		
  	}
  	
  	/**
  	 * Serializes the input for Javadoc
  	 */
  	def serializeToJavadoc(Pattern pattern) {
  		var javadocString = pattern.serialize
  		if (javadocString.nullOrEmpty) {
  			return "Serialization error, check Log"
  		}
  		javadocString = javadocString.replaceAll(java::util::regex::Pattern::quote("\\\""),Matcher::quoteReplacement("\""))
  		javadocString = javadocString.replaceAll("@","{@literal @}")
  		javadocString = javadocString.replaceAll("<","{@literal <}")
  		javadocString = javadocString.replaceAll(">","{@literal >}")
  		return javadocString.trim
  	}
  	
  	/**
  	 * Serializes EObject to a String representation. Escapes only the double qoutes.
  	 */
  	def private serialize(EObject eObject) {
  		try {
  			// This call sometimes causes ConcurrentModificationException
//			val serializedObject = serializer.serialize(eObject)
			// Another way to serialize the eObject, uses the current node model
			// simple getText returns the currently text, that parsed by the editor 
			val eObjectNode = NodeModelUtils::getNode(eObject)
			if (eObjectNode != null) {
				return escape(eObjectNode.text)	
			}
			// getTokenText returns the string without hidden tokens
//			NodeModelUtils::getTokenText(NodeModelUtils::getNode(eObject)).replaceAll("\"", "\\\\\"")
		} catch (Exception e) {
			if (logger != null) {
				logger.error("Error when serializing " + eObject.eClass.name, e)	
			}
		}
		return null
  	}
  	
  	def private escape(String escapable) {
  		if (escapable == null) return null
  		// escape double quotes
  		var escapedString = escapable.replaceAll("\"", "\\\\\"")
  		// escape javadoc comments to single space
  		// FIXME need a better replacement, or better way to do this
  		escapedString = escapedString.replaceAll(MULTILINE_COMMENT_PATTERN, " ")
  		return escapedString
  	}
  	
  	/**
  	 * Returns the packageName: PatternModel.packageName + Pattern.name, packageName is ignored, when nullOrEmpty.
  	 */
  	def getPackageName(Pattern pattern) {
  		var packageName = (pattern.eContainer as PatternModel).packageName
	   	if (packageName.nullOrEmpty) {
	   		packageName = ""
	   	} else {
	   		packageName = packageName + "."
	   	}
	   	return (packageName + pattern.name).toLowerCase
  	}
	
	def getPackagePath(Pattern pattern) {
		pattern.packageName.replace(".","/")
	}
	
	def referClass(ITreeAppendable appendable, EObject ctx, Class<?> clazz, JvmTypeReference... typeArgs) {
//		val type = ctx.newTypeRef(clazz, typeArgs).type
//		appendable.append(type)
		//'''«type.simpleName»'''
		appendable.serialize(ctx.newTypeRef(clazz, typeArgs), ctx)
	}
	
	def serialize(ITreeAppendable appendable, JvmTypeReference ref, EObject ctx) {
		typeReferenceSerializer.serialize(ref, ctx, appendable)		
	}

}
