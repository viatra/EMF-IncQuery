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
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Pattern
import org.eclipse.xtext.naming.IQualifiedNameProvider
import org.eclipse.viatra2.emf.incquery.tooling.generator.util.EMFPatternLanguageJvmModelInferrerUtil
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Variable

class JavadocInferrer {
	
	@Inject extension EMFPatternLanguageJvmModelInferrerUtil
	@Inject extension IQualifiedNameProvider
	
	/**
   	 * Infers javadoc for Match class based on the input 'pattern'.
   	 */
   	def javadocMatchClass(Pattern pattern) '''
		Pattern-specific match representation of the «pattern.fullyQualifiedName» pattern, 
		to be used in conjunction with «pattern.matcherClassName».
		
		<p>Class fields correspond to parameters of the pattern. Fields with value null are considered unassigned.
		Each instance is a (possibly partial) substitution of pattern parameters, 
		usable to represent a match of the pattern in the result of a query, 
		or to specify the bound (fixed) input parameters when issuing a query.
		
		@see «pattern.matcherClassName»
		@see «pattern.processorClassName»
   	'''
	
	def javadocMatcherClass(Pattern pattern) '''		
		Generated pattern matcher API of the «pattern.fullyQualifiedName» pattern, 
		providing pattern-specific query methods.
		
		«pattern.serializeToJavadoc»
		
		@see «pattern.matchClassName»
		@see «pattern.matcherFactoryClassName»
		@see «pattern.processorClassName»
   	'''
   	
   	def javadocMatcherFactoryClass(Pattern pattern) '''
	 	A pattern-specific matcher factory that can instantiate «pattern.matcherClassName» in a type-safe way.
	 	
	 	@see «pattern.matcherClassName»
	 	@see «pattern.matchClassName»
   	'''
   	
   	def javadocProcessorClass(Pattern pattern) '''
		A match processor tailored for the «pattern.fullyQualifiedName» pattern.
		
		Clients should derive an (anonymous) class that implements the abstract process().
	'''
   	
   	def javadocMatcherConstructorNotifier(Pattern pattern) '''
		Initializes the pattern matcher over a given EMF model root (recommended: Resource or ResourceSet). 
		If a pattern matcher is already constructed with the same root, only a lightweight reference is created.
		The scope of pattern matching will be the given EMF model root and below (see FAQ for more precise definition).
		The match set will be incrementally refreshed upon updates from this scope.
		@param emfRoot the root of the EMF containment hierarchy where the pattern matcher will operate. Recommended: Resource or ResourceSet.
		@throws IncQueryException if an error occurs during pattern matcher creation
	'''
	
	def javadocMatcherConstructorEngine(Pattern pattern) '''
		Initializes the pattern matcher within an existing EMF-IncQuery engine. 
		If the pattern matcher is already constructed in the engine, only a lightweight reference is created.
		The match set will be incrementally refreshed upon updates.
		@param engine the existing EMF-IncQuery engine in which this matcher will be created.
		@throws IncQueryException if an error occurs during pattern matcher creation
	'''
	
	def javadocGetAllMatchesMethod(Pattern pattern) '''
		Returns the set of all matches of the pattern that conform to the given fixed values of some parameters.
		«FOR p : pattern.parameters»
		@param «p.parameterName» the fixed value of pattern parameter «p.name», or null if not bound.
		«ENDFOR»
		@return matches represented as a «pattern.matchClassName» object.
	'''
	
	def javadocGetOneArbitraryMatchMethod(Pattern pattern) '''
		Returns an arbitrarily chosen match of the pattern that conforms to the given fixed values of some parameters.
		Neither determinism nor randomness of selection is guaranteed.
		«FOR p : pattern.parameters»
		@param «p.parameterName» the fixed value of pattern parameter «p.name», or null if not bound.
		«ENDFOR»
		@return a match represented as a «pattern.matchClassName» object, or null if no match is found.
	'''
	
	def javadocHasMatchMethod(Pattern pattern) '''
		Indicates whether the given combination of specified pattern parameters constitute a valid pattern match,
		under any possible substitution of the unspecified parameters (if any).
		«FOR p : pattern.parameters»
		@param «p.parameterName» the fixed value of pattern parameter «p.name», or null if not bound.
		«ENDFOR»
		@return true if the input is a valid (partial) match of the pattern.
	'''
	
	def javadocHasMatchMethodNoParameter(Pattern pattern) '''
		Indicates whether the (parameterless) pattern matches or not. 
		@return true if the pattern has a valid match.
	'''
	
	def javadocCountMatchesMethod(Pattern pattern) '''
		Returns the number of all matches of the pattern that conform to the given fixed values of some parameters.
		«FOR p : pattern.parameters»
		@param «p.parameterName» the fixed value of pattern parameter «p.name», or null if not bound.
		«ENDFOR»
		@return the number of pattern matches found.
	'''
	
	def javadocForEachMatchMethod(Pattern pattern) '''
		Executes the given processor on each match of the pattern that conforms to the given fixed values of some parameters.
		«FOR p : pattern.parameters»
		@param «p.parameterName» the fixed value of pattern parameter «p.name», or null if not bound.
		«ENDFOR»
		@param processor the action that will process each pattern match.
	'''
	
	def javadocForOneArbitraryMatchMethod(Pattern pattern) '''
		Executes the given processor on an arbitrarily chosen match of the pattern that conforms to the given fixed values of some parameters.  
		Neither determinism nor randomness of selection is guaranteed.
		«FOR p : pattern.parameters»
		@param «p.parameterName» the fixed value of pattern parameter «p.name», or null if not bound.
		«ENDFOR»
		@param processor the action that will process the selected match. 
		@return true if the pattern has at least one match with the given parameter values, false if the processor was not invoked
	'''
	
	def javadocProcessMethod(Pattern pattern) '''
		Defines the action that is to be executed on each match.
		«FOR p : pattern.parameters»
		@param «p.parameterName» the value of pattern parameter «p.name» in the currently processed match 
		«ENDFOR»
	'''
	
	def javadocNewFilteredDeltaMonitorMethod(Pattern pattern) '''
		Registers a new filtered delta monitor on this pattern matcher.
		The DeltaMonitor can be used to track changes (delta) in the set of filtered pattern matches from now on, considering those matches only that conform to the given fixed values of some parameters. 
		It can also be reset to track changes from a later point in time, 
		and changes can even be acknowledged on an individual basis. 
		See {@link DeltaMonitor} for details.
		@param fillAtStart if true, all current matches are reported as new match events; if false, the delta monitor starts empty.
		«FOR p : pattern.parameters»
		@param «p.parameterName» the fixed value of pattern parameter «p.name», or null if not bound.
		«ENDFOR»
		@return the delta monitor.
	'''
	
	def javadocGetAllValuesOfMethod(Variable parameter) '''
		Retrieve the set of values that occur in matches for «parameter.name».
		@return the Set of all values, null if no parameter with the given name exists, empty set if there are no matches
	'''
}