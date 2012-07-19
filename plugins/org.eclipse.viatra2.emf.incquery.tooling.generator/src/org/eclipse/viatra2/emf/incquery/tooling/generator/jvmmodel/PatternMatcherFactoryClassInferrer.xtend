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
import org.eclipse.viatra2.emf.incquery.runtime.api.IncQueryEngine
import org.eclipse.viatra2.emf.incquery.runtime.api.impl.BaseGeneratedMatcherFactory
import org.eclipse.viatra2.emf.incquery.tooling.generator.util.EMFJvmTypesBuilder
import org.eclipse.viatra2.emf.incquery.tooling.generator.util.EMFPatternLanguageJvmModelInferrerUtil
import org.eclipse.viatra2.patternlanguage.core.helper.CorePatternLanguageHelper
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Pattern
import org.eclipse.xtext.common.types.JvmDeclaredType
import org.eclipse.xtext.common.types.JvmTypeReference
import org.eclipse.xtext.common.types.JvmVisibility
import org.eclipse.viatra2.emf.incquery.runtime.exception.IncQueryException
import org.eclipse.xtext.common.types.util.TypeReferences
import org.eclipse.xtext.xbase.XExpression
import org.eclipse.viatra2.emf.incquery.runtime.extensibility.IMatcherFactoryProvider

/**
 * {@link IMatcherFactory} implementation inferrer.
 * 
 * @author Mark Czotter
 */
class PatternMatcherFactoryClassInferrer {
	
	@Inject extension EMFJvmTypesBuilder
	@Inject extension EMFPatternLanguageJvmModelInferrerUtil
	@Inject extension JavadocInferrer
	@Inject extension TypeReferences types

	/**
	 * Infers the {@link IMatcherFactory} implementation class from {@link Pattern}.
	 */		
	def JvmDeclaredType inferMatcherFactoryClass(Pattern pattern, boolean isPrelinkingPhase, String matcherFactoryPackageName, JvmTypeReference matchClassRef, JvmTypeReference matcherClassRef) {
		val matcherFactoryClass = pattern.toClass(pattern.matcherFactoryClassName) [
  			it.packageName = matcherFactoryPackageName
  			it.documentation = pattern.javadocMatcherFactoryClass.toString
  			it.superTypes += pattern.newTypeRef(typeof (BaseGeneratedMatcherFactory), cloneWithProxies(matcherClassRef))
  		]
  		matcherFactoryClass.inferMatcherFactoryMethods(pattern, matcherClassRef)
  		matcherFactoryClass.inferMatcherFactoryConstructor(pattern)
  		matcherFactoryClass.inferMatcherFactoryField(pattern)
  		matcherFactoryClass.inferMatcherFactoryInnerClass(pattern)
  		return matcherFactoryClass
  	}
  	
	/**
   	 * Infers methods for MatcherFactory class based on the input 'pattern'.
   	 */
  	def inferMatcherFactoryMethods(JvmDeclaredType matcherFactoryClass, Pattern pattern, JvmTypeReference matcherClassRef) {
   		matcherFactoryClass.members += pattern.toMethod("instance", types.createTypeRef(matcherFactoryClass)) [
			it.visibility = JvmVisibility::PUBLIC
			it.setStatic(true)
			it.exceptions += pattern.newTypeRef(typeof (IncQueryException))
			it.documentation = pattern.javadocFactoryInstanceMethod.toString
			it.setBody([append('''
				«matcherFactoryClass.simpleName» result = INSTANCE;
				if (result == null) {
					synchronized («matcherFactoryClass.simpleName».class) {
						result = INSTANCE;
						if (result == null) {
							result = INSTANCE = new «matcherFactoryClass.simpleName»();
						}
					}
				}
				return result;
			''')])
		]

  		matcherFactoryClass.members += pattern.toMethod("instantiate", cloneWithProxies(matcherClassRef)) [
			it.visibility = JvmVisibility::PROTECTED
			it.annotations += pattern.toAnnotation(typeof (Override))
			it.parameters += pattern.toParameter("engine", pattern.newTypeRef(typeof (IncQueryEngine)))
			it.exceptions += pattern.newTypeRef(typeof (IncQueryException))
			it.setBody([append('''
				return new «pattern.matcherClassName»(engine);
			''')])
		]
//		matcherFactoryClass.members += pattern.toMethod("patternString", pattern.newTypeRef(typeof (String))) [
//			it.visibility = JvmVisibility::PROTECTED
//			it.annotations += pattern.toAnnotation(typeof (Override))
//			it.body = ['''
//«««				Serialize the PatternModel
//«««				«pattern.eContainer.serializeToJava»
//«««				return patternString;  
//				throw new UnsupportedOperationException();
//			''']
//		]
		matcherFactoryClass.members += pattern.toMethod("getBundleName", pattern.newTypeRef(typeof (String))) [
			it.visibility = JvmVisibility::PROTECTED
			it.annotations += pattern.toAnnotation(typeof (Override))
			it.setBody([append('''
				return "«pattern.bundleName»";
			''')])
		]
		matcherFactoryClass.members += pattern.toMethod("patternName", pattern.newTypeRef(typeof (String))) [
			it.visibility = JvmVisibility::PROTECTED
			it.annotations += pattern.toAnnotation(typeof (Override))
			it.setBody([append('''
				return "«CorePatternLanguageHelper::getFullyQualifiedName(pattern)»";
			''')])
		]
  	}
  	
 	/**
   	 * Infers constructor for MatcherFactory class based on the input 'pattern'.
   	 */
  	def inferMatcherFactoryConstructor(JvmDeclaredType matcherFactoryClass, Pattern pattern) {
  		matcherFactoryClass.members += pattern.toConstructor [
  			it.simpleName = matcherFactoryClass.simpleName
			it.visibility = JvmVisibility::PRIVATE
			it.exceptions += pattern.newTypeRef(typeof (IncQueryException))
			it.setBody([append('''super();''')])
		]
  	}
  	
 	/**
   	 * Infers field for MatcherFactory class based on the input 'pattern'.
   	 */
  	def inferMatcherFactoryField(JvmDeclaredType matcherFactoryClass, Pattern pattern) {
  		// TODO volatile?
  		matcherFactoryClass.members += pattern.toField("INSTANCE", types.createTypeRef(matcherFactoryClass)/*pattern.newTypeRef("volatile " + matcherFactoryClass.simpleName)*/) [
			it.visibility = JvmVisibility::PRIVATE
			it.setStatic(true)
			it.setInitializer([append('''null''')]);
		]
  	}
  	
 	/**
   	 * Infers inner class for MatcherFactory class based on the input 'pattern'.
   	 */
  	def inferMatcherFactoryInnerClass(JvmDeclaredType matcherFactoryClass, Pattern pattern) {
  		matcherFactoryClass.members += pattern.toClass(pattern.matcherFactoryProviderClassName) [
			it.visibility = JvmVisibility::PUBLIC
			it.setStatic(true)
			it.superTypes += pattern.newTypeRef(typeof(IMatcherFactoryProvider), types.createTypeRef(matcherFactoryClass))
			
			it.members += pattern.toMethod("get", types.createTypeRef(matcherFactoryClass)) [
				it.visibility = JvmVisibility::PUBLIC
				it.annotations += pattern.toAnnotation(typeof (Override))
				it.exceptions += pattern.newTypeRef(typeof (IncQueryException))
				it.setBody([append('''return instance();''')])			
			]
		]
  	}
	
}