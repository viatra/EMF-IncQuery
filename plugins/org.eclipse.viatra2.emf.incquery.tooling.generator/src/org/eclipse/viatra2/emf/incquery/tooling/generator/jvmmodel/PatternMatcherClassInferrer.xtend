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
import org.eclipse.emf.common.notify.Notifier
import org.eclipse.viatra2.emf.incquery.runtime.api.EngineManager
import org.eclipse.viatra2.emf.incquery.runtime.api.IncQueryEngine
import org.eclipse.viatra2.emf.incquery.runtime.api.IncQueryMatcher
import org.eclipse.viatra2.emf.incquery.runtime.api.impl.BaseGeneratedMatcher
import org.eclipse.viatra2.emf.incquery.tooling.generator.util.EMFJvmTypesBuilder
import org.eclipse.viatra2.emf.incquery.tooling.generator.util.EMFPatternLanguageJvmModelInferrerUtil
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Pattern
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Variable
import org.eclipse.xtext.common.types.JvmDeclaredType
import org.eclipse.xtext.common.types.JvmTypeReference
import org.eclipse.xtext.common.types.JvmVisibility
import org.eclipse.viatra2.emf.incquery.runtime.exception.IncQueryException

/**
 * {@link IncQueryMatcher} implementation inferrer.
 * 
 * @author Mark Czotter
 */
class PatternMatcherClassInferrer {

	@Inject extension EMFJvmTypesBuilder
	@Inject extension EMFPatternLanguageJvmModelInferrerUtil
	@Inject extension JavadocInferrer
	@Inject extension PatternMatcherClassMethodInferrer

	/**
	 * Infers the {@link IncQueryMatcher} implementation class from a {@link Pattern}.
	 */
	def JvmDeclaredType inferMatcherClass(Pattern pattern, boolean isPrelinkingPhase, String matcherPackageName, JvmTypeReference matchClassRef) {
		val matcherClass = pattern.toClass(pattern.matcherClassName) [
   			it.packageName = matcherPackageName
   			it.documentation = pattern.javadocMatcherClass.toString
			//it.annotations += pattern.toAnnotation(typeof (SuppressWarnings), "unused")
   			it.superTypes += pattern.newTypeRef(typeof(BaseGeneratedMatcher), cloneWithProxies(matchClassRef))
   			it.superTypes += pattern.newTypeRef(typeof(IncQueryMatcher), cloneWithProxies(matchClassRef))
   		]
   		matcherClass.inferFields(pattern)
   		matcherClass.inferConstructors(pattern)
   		matcherClass.inferMethods(pattern, matchClassRef)
   		return matcherClass
   	}
   	
   	/**
   	 * Infers fields for Match class based on the input 'pattern'.
   	 */
   	def inferFields(JvmDeclaredType matchClass, Pattern pattern) {
   		for (Variable variable : pattern.parameters) {
   			matchClass.members += pattern.toField(variable.positionConstant, pattern.newTypeRef(typeof (int)))[
	 			it.setStatic(true)
	 			it.setFinal(true)
   				it.setInitializer([append('''«pattern.parameters.indexOf(variable)»''')])
   			]
   		}
   	}
   	
	/**
   	 * Infers constructors for Matcher class based on the input 'pattern'.
   	 */
   	def inferConstructors(JvmDeclaredType matcherClass, Pattern pattern) {
   		matcherClass.members += pattern.toConstructor [
   			it.simpleName = pattern.matcherClassName
			it.visibility = JvmVisibility::PUBLIC
			it.documentation = pattern.javadocMatcherConstructorNotifier.toString
			it.parameters += pattern.toParameter("emfRoot", pattern.newTypeRef(typeof (Notifier)))
			it.exceptions += pattern.newTypeRef(typeof (IncQueryException))
			it.setBody([
				append('''this(''')
				referClass(pattern, typeof(EngineManager))
				append('''.getInstance().getIncQueryEngine(emfRoot));''')
			])
		]
		
		matcherClass.members += pattern.toConstructor [
			it.simpleName = pattern.matcherClassName
			it.visibility = JvmVisibility::PUBLIC
			it.documentation = pattern.javadocMatcherConstructorEngine.toString
			it.parameters += pattern.toParameter("engine", pattern.newTypeRef(typeof (IncQueryEngine)))
			it.exceptions += pattern.newTypeRef(typeof (IncQueryException))
			it.setBody([append('''super(engine, factory());''')])
		]
   	}
   	
}