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

package org.eclipse.incquery.tooling.core.generator.jvmmodel

import com.google.inject.Inject
import org.eclipse.incquery.runtime.api.IncQueryEngine
import org.eclipse.incquery.runtime.api.impl.BaseGeneratedMatcherFactory
import org.eclipse.incquery.runtime.exception.IncQueryException
import org.eclipse.incquery.runtime.extensibility.IMatcherFactoryProvider
import org.eclipse.incquery.tooling.core.generator.util.EMFJvmTypesBuilder
import org.eclipse.incquery.tooling.core.generator.util.EMFPatternLanguageJvmModelInferrerUtil
import org.eclipse.incquery.patternlanguage.helper.CorePatternLanguageHelper
import org.eclipse.incquery.patternlanguage.patternLanguage.Pattern
import org.eclipse.xtext.common.types.JvmDeclaredType
import org.eclipse.xtext.common.types.JvmTypeReference
import org.eclipse.xtext.common.types.JvmVisibility
import org.eclipse.xtext.common.types.util.TypeReferences

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
  		matcherFactoryClass.inferMatcherFactoryInnerClasses(pattern)
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
				try {
					return «pattern.matcherFactoryHolderClassName».INSTANCE;
				} catch (''') referClass(pattern, typeof(ExceptionInInitializerError)) append(" ") append(''' 
				err) {
					processInitializerError(err);
					throw err;
				}
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
  		//matcherFactoryClass.members += pattern.toField("INSTANCE", types.createTypeRef(matcherFactoryClass)/*pattern.newTypeRef("volatile " + matcherFactoryClass.simpleName)*/) [
		//	it.visibility = JvmVisibility::PRIVATE
		//	it.setStatic(true)
		//	it.setInitializer([append('''null''')]);
		//]
  	}
  	
 	/**
   	 * Infers inner class for MatcherFactory class based on the input 'pattern'.
   	 */
  	def inferMatcherFactoryInnerClasses(JvmDeclaredType matcherFactoryClass, Pattern pattern) {
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
   		matcherFactoryClass.members += pattern.toClass(pattern.matcherFactoryHolderClassName) [
			it.visibility = JvmVisibility::PRIVATE
			it.setStatic(true)
			it.members += pattern.toField("INSTANCE", types.createTypeRef(matcherFactoryClass)/*pattern.newTypeRef("volatile " + matcherFactoryClass.simpleName)*/) [
				it.setFinal(true)
				it.setStatic(true)
				it.setInitializer([append('''make()''')]);
			]
			it.members += pattern.toMethod("make", types.createTypeRef(matcherFactoryClass)) [
				it.visibility = JvmVisibility::PUBLIC
				it.setStatic(true)
				it.setBody([append('''
					try {
						return new «pattern.matcherFactoryClassName»();
					} catch (''') referClass(pattern, typeof(IncQueryException)) append(" ") append(''' 
					ex) {
						throw new ''') referClass(pattern, typeof(RuntimeException)) append('''
						(ex);
					}
				''')])			
			]		
		]
 	}
	
}