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
import org.eclipse.viatra2.emf.incquery.runtime.api.IMatcherFactory
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Pattern
import org.eclipse.xtext.common.types.JvmDeclaredType
import org.eclipse.xtext.common.types.JvmVisibility
import org.eclipse.xtext.common.types.util.TypeReferences
import org.eclipse.xtext.util.IAcceptor
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
	@Inject extension EMFJvmTypesBuilder
	@Inject extension EMFPatternLanguageJvmModelInferrerUtil
	@Inject extension PatternMatchClassInferrer
	@Inject extension PatternMatcherClassInferrer
	@Inject extension PatternMatcherFactoryClassInferrer
	@Inject extension PatternMatchProcessorClassInferrer
	@Inject extension TypeReferences types
	
	/**
	 * Is called for each Pattern instance in a resource.
	 * 
	 * @param pattern - the model to create one or more JvmDeclaredTypes from.
	 * @param acceptor - each created JvmDeclaredType without a container should be passed to the acceptor in order get attached to the
	 *                   current resource.
	 * @param isPreLinkingPhase - whether the method is called in a pre linking phase, i.e. when the global index isn't fully updated. You
	 *        must not rely on linking using the index if iPrelinkingPhase is <code>true</code>
	 */
   	def dispatch void infer(Pattern pattern, IAcceptor<JvmDeclaredType> acceptor, boolean isPrelinkingPhase) {
   		if (pattern.name.nullOrEmpty) return;
	   	val packageName = pattern.getPackageName
	   	// infer Match class
	   	val matchClass = pattern.inferMatchClass(isPrelinkingPhase, packageName)
	   	val matchClassRef = types.createTypeRef(matchClass)
	   	// infer a Matcher class
	   	val matcherClass = pattern.inferMatcherClass(isPrelinkingPhase, packageName, matchClassRef)
	   	val matcherClassRef = types.createTypeRef(matcherClass)
	   	// infer MatcherFactory class
	   	val matcherFactoryClass = pattern.inferMatcherFactoryClass(isPrelinkingPhase, packageName, matchClassRef, matcherClassRef)
	   	// infer Processor class
	   	val processorClass = pattern.inferProcessorClass(isPrelinkingPhase, packageName, matchClassRef)
	   	// add Factory field to Matcher class
	   	matcherClass.members += pattern.toField("FACTORY", pattern.newTypeRef(typeof (IMatcherFactory), cloneWithProxies(matchClassRef), cloneWithProxies(matcherClassRef))) [
	   		it.visibility = JvmVisibility::PUBLIC
	   		it.setStatic(true)
			it.setFinal(true)
			it.setInitializer([''' new «matcherFactoryClass.simpleName»()'''])
	   	]
	   	// accept new classes
	   	acceptor.accept(matchClass)
	   	acceptor.accept(matcherClass)
	   	acceptor.accept(matcherFactoryClass)
	   	acceptor.accept(processorClass)
   	}
   	
}
