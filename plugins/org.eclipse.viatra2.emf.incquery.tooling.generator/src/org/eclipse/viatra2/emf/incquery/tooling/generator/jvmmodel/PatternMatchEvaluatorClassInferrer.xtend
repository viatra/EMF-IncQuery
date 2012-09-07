/*******************************************************************************
 * Copyright (c) 2010-2012, Andras Okros, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Andras Okros - initial API and implementation
 *******************************************************************************/

package org.eclipse.viatra2.emf.incquery.tooling.generator.jvmmodel

import com.google.inject.Inject
import java.util.ArrayList
import java.util.List
import java.util.Map
import org.eclipse.viatra2.emf.incquery.runtime.api.IMatchChecker
import org.eclipse.viatra2.emf.incquery.tooling.generator.util.EMFJvmTypesBuilder
import org.eclipse.viatra2.emf.incquery.tooling.generator.util.EMFPatternLanguageJvmModelInferrerUtil
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.tuple.Tuple
import org.eclipse.viatra2.patternlanguage.core.helper.CorePatternLanguageHelper
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.CheckConstraint
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Pattern
import org.eclipse.xtext.common.types.JvmDeclaredType
import org.eclipse.xtext.common.types.JvmTypeReference
import org.eclipse.xtext.common.types.util.Primitives
import org.eclipse.xtext.xbase.XExpression
import org.eclipse.xtext.xbase.typing.ITypeProvider
import org.eclipse.xtext.common.types.JvmVisibility
import org.eclipse.xtext.common.types.JvmPrimitiveType

/**
 * {@link IMatchChecker} implementation inferer.
 */
class PatternMatchEvaluatorClassInferrer {

	@Inject extension EMFJvmTypesBuilder
	@Inject extension EMFPatternLanguageJvmModelInferrerUtil
	@Inject extension JavadocInferrer
	@Inject extension ITypeProvider
	@Inject extension Primitives

	/**
	 * Infers the {@link IMatchChecker} implementation class from a {@link Pattern}.
	 */
	def List<JvmDeclaredType> inferEvaluatorClass(Pattern pattern, boolean isPrelinkingPhase, String checkerPackageName, JvmTypeReference matchClassRef) {
		val List<JvmDeclaredType> result = new ArrayList<JvmDeclaredType>()
		var int patternBodyNumber = 0
		for (patternBody:pattern.bodies) {
			patternBodyNumber = patternBodyNumber + 1
			var int checkConstraintNumber = 0
  			for (constraint:patternBody.constraints) {
  				if (constraint instanceof CheckConstraint) {
  					checkConstraintNumber = checkConstraintNumber + 1
  					val String postFix = patternBodyNumber + "_" + checkConstraintNumber
  					val XExpression xExpression = (constraint as CheckConstraint).expression
					val checkerClass = pattern.toClass(pattern.evaluatorClassName + postFix ) [
  						it.packageName = checkerPackageName
  						it.documentation = pattern.javadocEvaluatorClass.toString
  						it.superTypes += pattern.newTypeRef(typeof(IMatchChecker))
  					]
  					//Forcing a boolean return type for check expression
  					//Results in less misleading error messages
  					//checkerClass.inferEvaluatorClassMethods(pattern, xExpression)
  					checkerClass.inferEvaluatorClassMethods(pattern, xExpression, pattern.newTypeRef(typeof(Boolean)))
  					result.add(checkerClass)
  				}
  			}
  		}
  		return result
  	}
  	
	/**
   	 * Infers methods for checker class based on the input 'pattern'.
   	 */  	
  	def inferEvaluatorClassMethods(JvmDeclaredType checkerClass, Pattern pattern, XExpression xExpression) {
  		val type = getType(xExpression)
  		inferEvaluatorClassMethods(checkerClass, pattern, xExpression, type)
  	}
  	
	/**
   	 * Infers methods for checker class based on the input 'pattern'.
   	 */  	
  	def inferEvaluatorClassMethods(JvmDeclaredType checkerClass, Pattern pattern, XExpression xExpression, JvmTypeReference type) {
  		checkerClass.members += pattern.toMethod("evaluateXExpressionGenerated", asWrapperTypeIfPrimitive(type)) [
  			it.visibility = JvmVisibility::PRIVATE
			for (variable : CorePatternLanguageHelper::getReferencedPatternVariablesOfXExpression(xExpression)){
				it.parameters += variable.toParameter(variable.name, variable.calculateType)
			}
			it.documentation = pattern.javadocEvaluatorClassGeneratedMethod.toString
			it.setBody(xExpression)
		]

		checkerClass.members += pattern.toMethod("evaluateXExpression", asWrapperTypeIfPrimitive(getType(xExpression))) [
			it.parameters += pattern.toParameter("tuple", pattern.newTypeRef(typeof (Tuple)))
			it.parameters += pattern.toParameter("tupleNameMap", pattern.newTypeRef(typeof (Map), pattern.newTypeRef(typeof (String)), pattern.newTypeRef(typeof (Integer))))
			it.documentation = pattern.javadocEvaluatorClassWrapperMethod.toString
			it.setBody([append('''
				«FOR variable : CorePatternLanguageHelper::getReferencedPatternVariablesOfXExpression(xExpression)»int «variable.name»Position = tupleNameMap.get("«variable.name»");
				«variable.calculateType.qualifiedName» «variable.name» = («variable.calculateType.qualifiedName») tuple.get(«variable.name»Position);
				«ENDFOR»
				return evaluateXExpressionGenerated(«FOR variable : CorePatternLanguageHelper::getReferencedPatternVariablesOfXExpression(xExpression) SEPARATOR ', '»«variable.name»«ENDFOR»);''')
	   		])
		]
  	}
	
}