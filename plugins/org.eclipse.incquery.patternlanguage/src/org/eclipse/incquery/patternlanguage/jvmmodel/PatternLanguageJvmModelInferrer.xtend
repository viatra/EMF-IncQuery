/*******************************************************************************
 * Copyright (c) 2010-2012, Zoltan Ujhelyi, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Zoltan Ujhelyi - initial API and implementation
 *******************************************************************************/
 package org.eclipse.incquery.patternlanguage.jvmmodel

import org.eclipse.incquery.patternlanguage.patternLanguage.PatternModel
import org.eclipse.xtext.common.types.JvmDeclaredType
import org.eclipse.xtext.util.IAcceptor
import org.eclipse.xtext.xbase.jvmmodel.AbstractModelInferrer

/**
 * <p>Infers a JVM model from the source model.</p> 
 *
 * <p>The JVM model should contain all elements that would appear in the Java code 
 * which is generated from the source model. Other models link against the JVM model rather than the source model.</p>     
 */
class PatternLanguageJvmModelInferrer extends AbstractModelInferrer {

    /**
     * convenience API to build and initialize JvmTypes and their members.
     */
	//@Inject extension JvmTypesBuilder

	/**
	 * Is called for each instance of the first argument's type contained in a resource.
	 * 
	 * @param element - the model to create one or more JvmDeclaredTypes from.
	 * @param acceptor - each created JvmDeclaredType without a container should be passed to the acceptor in order get attached to the
	 *                   current resource.
	 * @param isPreLinkingPhase - whether the method is called in a pre linking phase, i.e. when the global index isn't fully updated. You
	 *        must not rely on linking using the index if iPrelinkingPhase is <code>true</code>
	 */
   	def dispatch void infer(PatternModel element, IAcceptor<JvmDeclaredType> acceptor, boolean isPrelinkingPhase) {
   		
   		// Here you explain how your model is mapped to Java elements, by writing the actual translation code.
   		// An example based on the initial hellow world example could look like this:
   		
//   		acceptor.accept(element.toClass("my.company.greeting.MyGreetings") [
//   			for (greeting : element.greetings) {
//   				members += greeting.toMethod(greeting.name, greeting.newTypeRef(typeof(String))) [
//   					it.body ['''
//   						return "Hello «greeting.name»";
//   					''']
//   				]
//   			}
//   		])
   	}
}
