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
package org.eclipse.viatra2.emf.incquery.tooling.generator.ui

import com.google.inject.Inject
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Pattern
import org.eclipse.xtext.common.types.JvmDeclaredType
import org.eclipse.xtext.common.types.util.TypeReferences
import org.eclipse.xtext.util.IAcceptor
import org.eclipse.xtext.xbase.jvmmodel.AbstractModelInferrer
import org.eclipse.viatra2.emf.incquery.tooling.generator.util.EMFJvmTypesBuilder
import org.eclipse.viatra2.emf.incquery.tooling.generator.util.EMFPatternLanguageJvmModelInferrerUtil
import org.eclipse.viatra2.emf.incquery.runtime.api.GenericPatternMatcher
import org.eclipse.xtext.common.types.JvmGenericType
import com.google.common.collect.Iterables
import com.google.common.collect.Lists
import com.google.common.collect.ImmutableList
import org.eclipse.xtext.xbase.jvmmodel.JvmTypesBuilder

/**
 * <p>Infers a JVM model from the source model.</p> 
 *
 * <p>The JVM model should contain all elements that would appear in the Java code 
 * which is generated from the source model. Other models link against the JVM model rather than the source model.</p>
 * 
 * @author Mark Czotter     
 */
class SampleUIJvmModelInferrer {

    /**
     * convenience API to build and initialize JvmTypes and their members.
     */
	@Inject extension JvmTypesBuilder
	@Inject extension EMFPatternLanguageJvmModelInferrerUtil
	@Inject extension TypeReferences types
	
   	def Iterable<JvmGenericType> infer(Pattern pattern) {
		var type = pattern.toClass(pattern.name.toFirstUpper + "Matcher") [
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
		return ImmutableList::of(type)
   	}
   	
}
