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
 
package org.eclipse.viatra2.emf.incquery.tooling.generator.jvmmodel

import com.google.inject.Inject
import java.util.HashSet
import java.util.Set
import org.eclipse.viatra2.emf.incquery.runtime.api.IMatcherFactory
import org.eclipse.viatra2.emf.incquery.runtime.api.impl.BaseGeneratedPatternGroup
import org.eclipse.viatra2.emf.incquery.tooling.generator.util.EMFJvmTypesBuilder
import org.eclipse.viatra2.emf.incquery.tooling.generator.util.EMFPatternLanguageJvmModelInferrerUtil
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.PatternModel
import org.eclipse.xtext.common.types.JvmGenericType
import org.eclipse.xtext.common.types.JvmOperation
import org.eclipse.xtext.common.types.JvmTypeReference
import org.eclipse.xtext.common.types.JvmVisibility
import org.eclipse.xtext.common.types.util.TypeReferences
import org.eclipse.xtext.xbase.jvmmodel.IJvmModelAssociations

/**
 * Model Inferrer for Pattern grouping. Infers a Group class for every PatternModel.
 * 
 * @author Mark Czotter
 */
class PatternGroupClassInferrer {
	
	@Inject extension EMFJvmTypesBuilder
	@Inject extension EMFPatternLanguageJvmModelInferrerUtil
	@Inject extension IJvmModelAssociations
	@Inject extension TypeReferences types
	
	def inferPatternGroup(PatternModel model) {
		val groupClass = model.toClass(model.groupClassName) [
			it.packageName = model.packageName
			it.final = true
			it.superTypes += model.newTypeRef(typeof (BaseGeneratedPatternGroup))
		]
		groupClass.members += model.inferGetMatcherFactoriesMethod
		groupClass
	}
	
	def String groupClassName(PatternModel model) {
		val fileName = model.eResource.URI.trimFileExtension.lastSegment 
		return "GroupOfFile" + fileName.toFirstUpper
	}
	
	def JvmOperation inferGetMatcherFactoriesMethod(PatternModel model) {
		val matcherFactoryInterfaceReference = model.newRawTypeRef(typeof (IMatcherFactory))
		val returnTypeReference = model.newTypeRef(typeof (Set), matcherFactoryInterfaceReference)
		val matcherReferences = gatherMatchers(model)
		model.toMethod("getMatcherFactories", returnTypeReference) [
			it.visibility = JvmVisibility::PROTECTED
			it.annotations += model.toAnnotation(typeof (Override))
			it.body = [
				serialize(returnTypeReference, model)
				append(''' result = new ''')
				serialize(model.newTypeRef(typeof(HashSet), matcherFactoryInterfaceReference), model)
				append('''();''')
				for (matcherRef : matcherReferences) {
					newLine
					append('''result.add(''')
					serialize(matcherRef, model)
					append('''.FACTORY);''')
				}
				newLine
				append('''return result;''')
			]
		]
	}
	
	def gatherMatchers(PatternModel model) {
		val result = new HashSet<JvmTypeReference>()
		for (pattern : model.patterns) {
			val jvmElements = pattern.jvmElements
			val matcherClass = jvmElements.findFirst([e | e instanceof JvmGenericType])	
			if (matcherClass instanceof JvmGenericType) {
				val sourceElementRef = types.createTypeRef(matcherClass as JvmGenericType)
				result.add(sourceElementRef)
			}
		}
		result
	}
	
}