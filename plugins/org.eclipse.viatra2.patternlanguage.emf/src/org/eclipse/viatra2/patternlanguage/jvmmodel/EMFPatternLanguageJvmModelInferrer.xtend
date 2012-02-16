/*******************************************************************************
 * Copyright (c) 2011 Zoltan Ujhelyi and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Zoltan Ujhelyi - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra2.patternlanguage.jvmmodel

import com.google.inject.Inject
import java.util.Arrays
import org.eclipse.viatra2.emf.incquery.runtime.api.GenericPatternMatcher
import org.eclipse.viatra2.emf.incquery.runtime.api.IPatternMatch
import org.eclipse.viatra2.emf.incquery.runtime.api.impl.BasePatternMatch
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Pattern
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.PatternModel
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Variable
import org.eclipse.viatra2.patternlanguage.eMFPatternLanguage.ClassType
import org.eclipse.viatra2.patternlanguage.eMFPatternLanguage.EClassConstraint
import org.eclipse.xtext.common.types.JvmDeclaredType
import org.eclipse.xtext.common.types.JvmTypeReference
import org.eclipse.xtext.common.types.JvmVisibility
import org.eclipse.xtext.util.IAcceptor
import org.eclipse.xtext.xbase.compiler.ImportManager
import org.eclipse.xtext.xbase.jvmmodel.AbstractModelInferrer

/**
 * <p>Infers a JVM model from the source model.</p> 
 *
 * <p>The JVM model should contain all elements that would appear in the Java code 
 * which is generated from the source model. Other models link against the JVM model rather than the source model.</p>
 * 
 * @author Mark Czotter     
 */
class EMFPatternLanguageJvmModelInferrer extends AbstractModelInferrer {

    /**
     * convenience API to build and initialize JvmTypes and their members.
     */
	@Inject extension org.eclipse.viatra2.patternlanguage.jvmmodel.EMFJvmTypesBuilder
   	
	/**
	 * Is called for each Pattern instance in a resource.
	 * 
	 * @param element - the model to create one or more JvmDeclaredTypes from.
	 * @param acceptor - each created JvmDeclaredType without a container should be passed to the acceptor in order get attached to the
	 *                   current resource.
	 * @param isPreLinkingPhase - whether the method is called in a pre linking phase, i.e. when the global index isn't fully updated. You
	 *        must not rely on linking using the index if iPrelinkingPhase is <code>true</code>
	 */
   	def dispatch void infer(Pattern pattern, IAcceptor<JvmDeclaredType> acceptor, boolean isPrelinkingPhase) {
   		val mainPackageName = (pattern.eContainer as PatternModel).packageName
   		// infer Match class
   		val matchClass = inferMatchClass(pattern, isPrelinkingPhase, mainPackageName)
   		
   		// infer a Matcher class
   		val matcherClass = inferMatcherClass(pattern, isPrelinkingPhase, mainPackageName)
   		
   		// accept new classes
   		acceptor.accept(matchClass)
   		acceptor.accept(matcherClass)
   	}
   	
   	def JvmDeclaredType inferMatchClass(Pattern pattern, boolean isPrelinkingPhase, String mainPackageName) {
   		return pattern.toClass(pattern.matchClassName) [
   			packageName = mainPackageName
   			superTypes += pattern.newTypeRef(typeof (org.eclipse.viatra2.emf.incquery.runtime.api.impl.BasePatternMatch))
   			superTypes += pattern.newTypeRef(typeof (org.eclipse.viatra2.emf.incquery.runtime.api.IPatternMatch))
   			
   			// add fields
   			for (Variable variable : pattern.parameters) {
   				members += pattern.toField(variable.fieldName, variable.calculateType)
   			}

   			members += pattern.toField("parameterNames", pattern.newTypeRef(typeof (String)).addArrayTypeDimension) [
   				it.setInitializer(['''{«FOR variable : pattern.parameters SEPARATOR ', '»"«variable.name»"«ENDFOR»}'''])
   			]
   			
   			// add constructor
   			members += pattern.toConstructor(pattern.matchClassName) [
   				it.visibility = JvmVisibility::PUBLIC
   				for (Variable variable : pattern.parameters) {
   					val javaType = variable.calculateType
   					it.parameters += variable.toParameter(variable.name, javaType)
   				}
   				it.body = ['''
   					«FOR variable : pattern.parameters»
   					this.«variable.fieldName» = «variable.name»;
   					«ENDFOR»
   				''']
   			]
   			
   			// add methods
   			members += pattern.toMethod("patternName", pattern.newTypeRef(typeof(String))) [
   				annotations += pattern.toAnnotation(typeof (Override))
   				it.body = ['''
   					return "«pattern.name»";
   				''']
   			]
   			
   			// add getters
   			members += pattern.toMethod("get", pattern.newTypeRef(typeof (Object))) [
   				annotations += pattern.toAnnotation(typeof (Override))
   				it.parameters += pattern.toParameter("parameterName", pattern.newTypeRef(typeof (String)))
   				it.body = ['''
   					«FOR variable : pattern.parameters»
   					if ("«variable.name»".equals(parameterName)) return this.«variable.fieldName»;
   					«ENDFOR»
   					return null;
   				''']
   			]
   			for (Variable variable : pattern.parameters) {
   				members += pattern.toMethod("get" + variable.name.toFirstUpper, variable.calculateType) [
   					it.body = ['''
   						return this.«variable.fieldName»;
   					''']
   				]
   			}
   			
   			// add setters
   			members += pattern.toMethod("set", pattern.newTypeRef(typeof (boolean))) [
   				annotations += pattern.toAnnotation(typeof (Override))
   				it.parameters += pattern.toParameter("parameterName", pattern.newTypeRef(typeof (String)))
   				it.parameters += pattern.toParameter("newValue", pattern.newTypeRef(typeof (Object)))
   				it.body = ['''
   					«FOR variable : pattern.parameters»
   					if ("«variable.name»".equals(parameterName) && newValue instanceof «variable.calculateType.simpleName») {
   						this.«variable.fieldName» = («variable.calculateType.simpleName») newValue;
   						return true;
   					}
   					«ENDFOR»
   					return false;
   				''']
   			]
   			for (Variable variable : pattern.parameters) {
   				members += pattern.toMethod("set" + variable.name.toFirstUpper, null) [
   					it.parameters += pattern.toParameter(variable.name, variable.calculateType)
   					it.body = ['''
   						this.«variable.fieldName» = «variable.name»;
   					''']
   				]
   			}
   			
   			// add extra methods like equals, hashcode, toArray, parameterNames
   			members += pattern.toMethod("parameterNames", pattern.newTypeRef(typeof (String)).addArrayTypeDimension) [
   				annotations += pattern.toAnnotation(typeof (Override))
   				it.body = ['''
   					return parameterNames;
   				''']
   			]
   			
   			members += pattern.toMethod("toArray", pattern.newTypeRef(typeof (Object)).addArrayTypeDimension) [
   				annotations += pattern.toAnnotation(typeof (Override))
   				it.body = ['''
   					return new Object[]{«FOR variable : pattern.parameters SEPARATOR ', '»«variable.fieldName»«ENDFOR»};
   				''']
   			]
			
			members += pattern.toMethod("prettyPrint", pattern.newTypeRef(typeof (String))) [
				annotations += pattern.toAnnotation(typeof (Override))
				it.body = ['''
					StringBuilder result = new StringBuilder();
					«FOR variable : pattern.parameters»
					result.append("\"«variable.name»\"=" + prettyPrintValue(«variable.fieldName») + "\n");
					«ENDFOR»
					return result.toString();
				''']
			]
			
			members += pattern.toMethod("hashCode", pattern.newTypeRef(typeof (int))) [
				annotations += pattern.toAnnotation(typeof (Override))
				it.body = ['''
					final int prime = 31;
					int result = 1;
					«FOR variable : pattern.parameters»
					result = prime * result + ((«variable.fieldName» == null) ? 0 : «variable.fieldName».hashCode()); 
					«ENDFOR»
					return result; 
				''']
			]
			
			members += pattern.toMethod("equals", pattern.newTypeRef(typeof (boolean))) [
				annotations += pattern.toAnnotation(typeof (Override))
				parameters += pattern.toParameter("obj", pattern.newTypeRef(typeof (Object)))
				it.body = [it | pattern.equalsMethodBody(it)]
			]
   			
   		]
   	}
   	
   	def inferMatcherClass(Pattern pattern, boolean isPrelinkingPhase, String mainPackageName) {
   		return pattern.toClass(pattern.matcherClassName) [
   			packageName = mainPackageName 
   			superTypes += pattern.newTypeRef(typeof(GenericPatternMatcher))
   			
   			
   			//Adding type-safe matcher calls
   			members += pattern.toMethod("getAllMatches", pattern.newTypeRef(typeof(String))) [
   				for (parameter : pattern.parameters){
   					val javaType = pattern.newTypeRef(typeof(Object))
					it.parameters += parameter.toParameter(parameter.name, javaType)				
   				}
   				
   				it.body = ['''
   					return "Hello «pattern.name»";
   				''']
   			]
   		]
   	}
   	
   	def CharSequence equalsMethodBody(Pattern pattern, ImportManager importManager) {
   		importManager.addImportFor(pattern.newTypeRef(typeof (Arrays)).type)
   		return '''
					if (this == obj)
						return true;
					if (obj == null)
						return false;
					if (!(obj instanceof IPatternMatch))
						return false;
					IPatternMatch otherSig  = (IPatternMatch) obj;
					if (!patternName().equals(otherSig.patternName()))
						return false;
					if (!«pattern.matchClassName».class.equals(obj.getClass()))
						return Arrays.deepEquals(toArray(), otherSig.toArray());
					«pattern.matchClassName» other = («pattern.matchClassName») obj;
					«FOR variable : pattern.parameters»
					if («variable.fieldName» == null) {if (other.«variable.fieldName» != null) return false;}
					else if (!«variable.fieldName».equals(other.«variable.fieldName»)) return false;
					«ENDFOR»
					return true;
				'''
   	}

   	def matcherClassName(Pattern pattern) {
   		pattern.name.toFirstUpper+"Matcher"
   	}
   	
   	def matchClassName(Pattern pattern) {
   		pattern.name.toFirstUpper+"Match"
   	} 		
   	
   	def fieldName(Variable variable) {
   		"f"+variable.name.toFirstUpper
   	}
   	
   	// Type calculation: first try
   	// See the XBaseUsageCrossReferencer class, possible solution for local variable usage
	// TODO: Find out how to get the type for variable 
   	def JvmTypeReference calculateType(Variable variable) {
   		if (variable.type != null && !variable.type.typename.nullOrEmpty) {
   			return variable.newTypeRef(variable.type.typename)
   		} else {
   			if (variable.eContainer() instanceof Pattern) {
   				val pattern = variable.eContainer() as Pattern;
   				for (body : pattern.bodies) {
   					for (constraint : body.constraints) {
   						if (constraint instanceof EClassConstraint) {
   							val entityType = (constraint as EClassConstraint).type
   							val variableRef = (constraint as EClassConstraint).getVar
   							if (variableRef.variable == variable || variableRef.getVar.equals(variable.name)) {
   								if (entityType instanceof ClassType) {
   									val clazz = (entityType as ClassType).classname.instanceClass
   									val typeref = variable.newTypeRef(clazz)
   									if (typeref != null) {
   										return typeref
   									}
   								}
   							}
   						}
   					}
   				}
   			}
   			return variable.newTypeRef(typeof(Object))
   		}
   	}
}
