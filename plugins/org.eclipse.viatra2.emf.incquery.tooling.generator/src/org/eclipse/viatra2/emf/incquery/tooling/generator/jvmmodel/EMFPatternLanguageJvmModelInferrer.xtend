/*******************************************************************************
 * Copyright (c) 2011 Zoltan Ujhelyi, Mark Czotter, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Zoltan Ujhelyi, Mark Czotter - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra2.emf.incquery.tooling.generator.jvmmodel

import com.google.inject.Inject
import org.apache.log4j.Logger
import org.eclipse.viatra2.emf.incquery.runtime.api.IMatcherFactory
import org.eclipse.viatra2.emf.incquery.runtime.exception.IncQueryException
import org.eclipse.viatra2.emf.incquery.tooling.generator.builder.GeneratorIssueCodes
import org.eclipse.viatra2.emf.incquery.tooling.generator.builder.IErrorFeedback
import org.eclipse.viatra2.emf.incquery.tooling.generator.util.EMFJvmTypesBuilder
import org.eclipse.viatra2.emf.incquery.tooling.generator.util.EMFPatternLanguageJvmModelInferrerUtil
import org.eclipse.viatra2.patternlanguage.core.helper.CorePatternLanguageHelper
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Pattern
import org.eclipse.viatra2.patternlanguage.eMFPatternLanguage.PatternModel
import org.eclipse.xtext.common.types.JvmVisibility
import org.eclipse.xtext.common.types.util.TypeReferences
import org.eclipse.xtext.diagnostics.Severity
import org.eclipse.xtext.xbase.jvmmodel.AbstractModelInferrer
import org.eclipse.xtext.xbase.jvmmodel.IJvmDeclaredTypeAcceptor
import org.eclipse.xtext.xbase.jvmmodel.IJvmModelAssociator

/**
 * <p>Infers a JVM model from the source model.</p> 
 *
 * <p>The JVM model should contain all elements that would appear in the Java code 
 * which is generated from the source model. Other models link against the JVM model rather than the source model.</p>
 * 
 * @author Mark Czotter
 */
class EMFPatternLanguageJvmModelInferrer extends AbstractModelInferrer {

	@Inject
	private Logger logger;
	@Inject
	private IErrorFeedback errorFeedback;
    /**
     * convenience API to build and initialize JvmTypes and their members.
     */
	@Inject extension EMFJvmTypesBuilder
	@Inject extension EMFPatternLanguageJvmModelInferrerUtil
	@Inject extension PatternMatchClassInferrer
	@Inject extension PatternMatcherClassInferrer
	@Inject extension PatternMatcherFactoryClassInferrer
	@Inject extension PatternMatchProcessorClassInferrer
	@Inject extension PatternGroupClassInferrer
	@Inject extension JavadocInferrer	
	@Inject extension TypeReferences types
	@Inject extension IJvmModelAssociator associator
	
	/**
	 * Is called for each Pattern instance in a resource.
	 * 
	 * @param pattern - the model to create one or more JvmDeclaredTypes from.
	 * @param acceptor - each created JvmDeclaredType without a container should be passed to the acceptor in order get attached to the
	 *                   current resource.
	 * @param isPreLinkingPhase - whether the method is called in a pre linking phase, i.e. when the global index isn't fully updated. You
	 *        must not rely on linking using the index if iPrelinkingPhase is <code>true</code>
	 */
   	def dispatch void infer(Pattern pattern, IJvmDeclaredTypeAcceptor acceptor, boolean isPrelinkingPhase) {
   		if (pattern.name.nullOrEmpty) return;
   		if (CorePatternLanguageHelper::isPrivate(pattern)) {
   			return;
   		}
   		logger.debug("Inferring Jvm Model for " + pattern.name);
	   	try {
	   		val packageName = pattern.getPackageName
			// infer Match class
		   	val matchClass = pattern.inferMatchClass(isPrelinkingPhase, packageName)
		   	val matchClassRef = types.createTypeRef(matchClass)
		   	// infer a Matcher class
		   	val matcherClass = pattern.inferMatcherClass(isPrelinkingPhase, packageName, matchClassRef)
		   	val matcherClassRef = types.createTypeRef(matcherClass)
		   	// infer MatcherFactory class
		   	val matcherFactoryClass = pattern.inferMatcherFactoryClass(isPrelinkingPhase, packageName, matchClassRef, matcherClassRef)
		   	val matcherFactoryClassRef = types.createTypeRef(matcherFactoryClass)
		   	/*val matcherFactoryProviderClass = matcherFactoryClass.members.findFirst([it instanceof JvmDeclaredType]) as JvmDeclaredType*/
		   	// infer Processor class
		   	val processorClass = pattern.inferProcessorClass(isPrelinkingPhase, packageName, matchClassRef)
		   	// add Factory field to Matcher class
		   	matcherClass.members += pattern.toMethod("factory", pattern.newTypeRef(typeof(IMatcherFactory), cloneWithProxies(matcherClassRef))) [
		   		it.visibility = JvmVisibility::PUBLIC
		   		it.setStatic(true)
				it.documentation = pattern.javadocFactoryMethod.toString
				it.exceptions += pattern.newTypeRef(typeof (IncQueryException))
				it.setBody([append('''
					return ''') serialize(matcherFactoryClassRef, pattern) append('''.instance();''')
				])
			]
		   	
		   	associator.associatePrimary(pattern, matcherClass)
		   	// accept new classes
		   	acceptor.accept(matchClass)
		   	acceptor.accept(matcherClass)
		   	acceptor.accept(matcherFactoryClass)
		   	acceptor.accept(processorClass)
		   	/*acceptor.accept(matcherFactoryProviderClass)*/
	   	} catch(Exception e) {
	   		logger.error("Exception during Jvm Model Infer for: " + pattern, e)
	   	}
   	}
   	
   	/**
	 * Is called for each Pattern instance in a resource.
	 * 
	 * @param pattern - the model to create one or more JvmDeclaredTypes from.
	 * @param acceptor - each created JvmDeclaredType without a container should be passed to the acceptor in order get attached to the
	 *                   current resource.
	 * @param isPreLinkingPhase - whether the method is called in a pre linking phase, i.e. when the global index isn't fully updated. You
	 *        must not rely on linking using the index if iPrelinkingPhase is <code>true</code>
	 */
   	def dispatch void infer(PatternModel model, IJvmDeclaredTypeAcceptor acceptor, boolean isPrelinkingPhase) {
	   	try {
	   		logger.debug("Inferring Jvm Model for Pattern model " + model.modelFileName);
   			val groupClass = model.inferPatternGroup
   			model.associatePrimary(groupClass)
   			acceptor.accept(groupClass)
   		} catch (IllegalArgumentException e){
   			errorFeedback.reportErrorNoLocation(model, e.message, GeneratorIssueCodes::INVALID_PATTERN_MODEL_CODE, Severity::ERROR, IErrorFeedback::JVMINFERENCE_ERROR_TYPE)
   		} catch(Exception e) {
	   		logger.error("Exception during Jvm Model Infer for pattern model: " + model, e)
	   	}
   	}
}
