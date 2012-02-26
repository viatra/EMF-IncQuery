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
import java.util.Arrays
import java.util.Collection
import org.eclipse.viatra2.emf.incquery.runtime.api.IPatternMatch
import org.eclipse.viatra2.emf.incquery.runtime.api.IncQueryEngine
import org.eclipse.viatra2.emf.incquery.runtime.api.IncQueryMatcher
import org.eclipse.viatra2.emf.incquery.runtime.api.impl.BaseGeneratedMatcher
import org.eclipse.viatra2.emf.incquery.runtime.api.impl.BasePatternMatch
import org.eclipse.viatra2.emf.incquery.runtime.exception.IncQueryRuntimeException
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Pattern
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.PatternModel
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Variable
import org.eclipse.viatra2.patternlanguage.eMFPatternLanguage.ClassType
import org.eclipse.viatra2.patternlanguage.eMFPatternLanguage.EClassConstraint
import org.eclipse.xtext.common.types.JvmDeclaredType
import org.eclipse.xtext.common.types.JvmTypeReference
import org.eclipse.xtext.common.types.JvmVisibility
import org.eclipse.xtext.common.types.util.TypeReferences
import org.eclipse.xtext.naming.IQualifiedNameProvider
import org.eclipse.xtext.util.IAcceptor
import org.eclipse.xtext.xbase.compiler.ImportManager
import org.eclipse.xtext.xbase.jvmmodel.AbstractModelInferrer
import org.eclipse.emf.common.notify.Notifier
import org.eclipse.viatra2.emf.incquery.runtime.api.IMatcherFactory
import org.eclipse.viatra2.emf.incquery.runtime.api.IMatchProcessor
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.tuple.Tuple
import org.eclipse.viatra2.emf.incquery.runtime.api.EngineManager
import org.eclipse.viatra2.emf.incquery.runtime.api.impl.BaseGeneratedMatcherFactory
import org.eclipse.xtext.serializer.ISerializer
import org.eclipse.xtend2.lib.StringConcatenation
import org.eclipse.emf.ecore.EObject
import org.eclipse.xtext.serializer.sequencer.ISemanticSequencer
import org.eclipse.xtext.serializer.diagnostic.ISerializationDiagnostic
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Constraint

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
	@Inject extension IQualifiedNameProvider
	@Inject extension TypeReferences types
	@Inject ISerializer serializer

	/**
	 * Is called for each Pattern instance in a resource.
	 * 
	 * @param element - the model to create one or more JvmDeclaredTypes from.
	 * @param acceptor - each created JvmDeclaredType without a container should be passed to the acceptor in order get attached to the
	 *                   current resource.
	 * @param isPreLinkingPhase - whether the method is called in a pre linking phase, i.e. when the global index isn't fully updated. You
	 *        must not rely on linking using the index if iPrelinkingPhase is <code>true</code>
	 */
   	def dispatch void infer(Pattern pattern, IAcceptor<JvmDeclaredType> acceptor, boolean isPrelinkingPhase) {
   		if (pattern.name.nullOrEmpty) return;
	   	var packageName = (pattern.eContainer as PatternModel).packageName
	   	if (packageName.nullOrEmpty) {
	   		packageName = ""
	   	} else {
	   		packageName = packageName + "."
	   	}
	   	val mainPackageName = packageName + pattern.name
	   	// infer Match class
	   	val matchClass = inferMatchClass(pattern, isPrelinkingPhase, mainPackageName)
	   	val matchClassRef = types.createTypeRef(matchClass)
	   	// infer a Matcher class
	   	val matcherClass = inferMatcherClass(pattern, isPrelinkingPhase, mainPackageName, matchClassRef)
	   	val matcherClassRef = types.createTypeRef(matcherClass)
	   	// infer MatcherFactory class
	   	val matcherFactoryClass = inferMatcherFactoryClass(pattern, isPrelinkingPhase, mainPackageName, matchClassRef, matcherClassRef)
	   	// infer Processor class
	   	val processorClass = inferProcessorClass(pattern, isPrelinkingPhase, mainPackageName, matchClassRef)
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
   	   	
   	def JvmDeclaredType inferMatchClass(Pattern pattern, boolean isPrelinkingPhase, String matchPackageName) {
   		return pattern.toClass(pattern.matchClassName) [
   			it.packageName = matchPackageName
   			it.documentation = pattern.matchClassJavadoc.toString
   			it.final = true
   			it.superTypes += pattern.newTypeRef(typeof (org.eclipse.viatra2.emf.incquery.runtime.api.impl.BasePatternMatch))
   			it.superTypes += pattern.newTypeRef(typeof (org.eclipse.viatra2.emf.incquery.runtime.api.IPatternMatch))
   			// add fields
   			for (Variable variable : pattern.parameters) {
   				it.members += pattern.toField(variable.fieldName, variable.calculateType)
   			}

   			it.members += pattern.toField("parameterNames", pattern.newTypeRef(typeof (String)).addArrayTypeDimension) [
   				it.setStatic(true);
   				it.setInitializer(['''{«FOR variable : pattern.parameters SEPARATOR ', '»"«variable.name»"«ENDFOR»}'''])
   			]
   			
   			// add constructor
   			it.members += pattern.toConstructor(pattern.matchClassName) [
   				it.visibility = JvmVisibility::PUBLIC
   				for (Variable variable : pattern.parameters) {
   					val javaType = variable.calculateType
   					it.parameters += variable.toParameter(variable.name, javaType)
   				}
   				it.body = ['''
   					«FOR variable : pattern.parameters»
   					this.«variable.fieldName» = «variable.name»;
   					«ENDFOR»
   				''']
   			]
   			
   			// add methods
   			it.members += pattern.toMethod("patternName", pattern.newTypeRef(typeof(String))) [
   				it.annotations += pattern.toAnnotation(typeof (Override))
   				it.body = ['''
   					return "«pattern.fullyQualifiedName»";
   				''']
   			]
   			
   			// add getters
   			it.members += pattern.toMethod("get", pattern.newTypeRef(typeof (Object))) [
   				it.annotations += pattern.toAnnotation(typeof (Override))
   				it.parameters += pattern.toParameter("parameterName", pattern.newTypeRef(typeof (String)))
   				it.body = ['''
   					«FOR variable : pattern.parameters»
   					if ("«variable.name»".equals(parameterName)) return this.«variable.fieldName»;
   					«ENDFOR»
   					return null;
   				''']
   			]
   			for (Variable variable : pattern.parameters) {
   				it.members += pattern.toMethod("get" + variable.name.toFirstUpper, variable.calculateType) [
   					it.body = ['''
   						return this.«variable.fieldName»;
   					''']
   				]
   			}
   			
   			// add setters
   			it.members += pattern.toMethod("set", pattern.newTypeRef(typeof (boolean))) [
   				it.annotations += pattern.toAnnotation(typeof (Override))
   				it.parameters += pattern.toParameter("parameterName", pattern.newTypeRef(typeof (String)))
   				it.parameters += pattern.toParameter("newValue", pattern.newTypeRef(typeof (Object)))
   				it.body = ['''
   					«FOR variable : pattern.parameters»
   					if ("«variable.name»".equals(parameterName) && newValue instanceof «variable.calculateType.simpleName») {
   						this.«variable.fieldName» = («variable.calculateType.simpleName») newValue;
   						return true;
   					}
   					«ENDFOR»
   					return false;
   				''']
   			]
   			for (Variable variable : pattern.parameters) {
   				it.members += pattern.toMethod("set" + variable.name.toFirstUpper, null) [
   					it.parameters += pattern.toParameter(variable.name, variable.calculateType)
   					it.body = ['''
   						this.«variable.fieldName» = «variable.name»;
   					''']
   				]
   			}
   			
   			// add extra methods like equals, hashcode, toArray, parameterNames
   			it.members += pattern.toMethod("parameterNames", pattern.newTypeRef(typeof (String)).addArrayTypeDimension) [
   				it.annotations += pattern.toAnnotation(typeof (Override))
   				it.body = ['''
   					return «pattern.matchClassName».parameterNames;
   				''']
   			]
   			
   			it.members += pattern.toMethod("toArray", pattern.newTypeRef(typeof (Object)).addArrayTypeDimension) [
   				it.annotations += pattern.toAnnotation(typeof (Override))
   				it.body = ['''
   					return new Object[]{«FOR variable : pattern.parameters SEPARATOR ', '»«variable.fieldName»«ENDFOR»};
   				''']
   			]
			
			it.members += pattern.toMethod("prettyPrint", pattern.newTypeRef(typeof (String))) [
				it.annotations += pattern.toAnnotation(typeof (Override))
				it.body = ['''
					StringBuilder result = new StringBuilder();
					«FOR variable : pattern.parameters»
					result.append("\"«variable.name»\"=" + prettyPrintValue(«variable.fieldName») + "\n");
					«ENDFOR»
					return result.toString();
				''']
			]
			
			it.members += pattern.toMethod("hashCode", pattern.newTypeRef(typeof (int))) [
				it.annotations += pattern.toAnnotation(typeof (Override))
				it.body = ['''
					final int prime = 31;
					int result = 1;
					«FOR variable : pattern.parameters»
					result = prime * result + ((«variable.fieldName» == null) ? 0 : «variable.fieldName».hashCode()); 
					«ENDFOR»
					return result; 
				''']
			]
			
			it.members += pattern.toMethod("equals", pattern.newTypeRef(typeof (boolean))) [
				it.annotations += pattern.toAnnotation(typeof (Override))
				it.parameters += pattern.toParameter("obj", pattern.newTypeRef(typeof (Object)))
				it.body = [it | pattern.equalsMethodBody(it)]
			]

			it.members += pattern.toMethod("pattern", pattern.newTypeRef(typeof (Pattern))) [
				it.annotations += pattern.toAnnotation(typeof (Override))
				it.body = ['''return «pattern.matcherClassName».FACTORY.getPattern();''']
			]
   		]
   	}
   	
   	def JvmDeclaredType inferMatcherClass(Pattern pattern, boolean isPrelinkingPhase, String matcherPackageName, JvmTypeReference matchClassRef) {
   		
   		return pattern.toClass(pattern.matcherClassName) [
   			it.packageName = matcherPackageName
   			it.documentation = pattern.matcherClassJavadoc.toString
//   		it.annotations += pattern.toAnnotation(typeof (SuppressWarnings), "unused")
   			it.superTypes += pattern.newTypeRef(typeof(BaseGeneratedMatcher), cloneWithProxies(matchClassRef))
   			it.superTypes += pattern.newTypeRef(typeof(IncQueryMatcher), cloneWithProxies(matchClassRef))
   			it.members += pattern.toConstructor(pattern.matcherClassName) [
   				it.visibility = JvmVisibility::PUBLIC
				it.documentation = pattern.matcherConstructorNotifierJavadoc.toString
   				it.parameters += pattern.toParameter("notifier", pattern.newTypeRef(typeof (Notifier)))
   				it.exceptions += pattern.newTypeRef(typeof (IncQueryRuntimeException))
   				it.body = [it | pattern.matcherConstructorBodyNotifier(it)]
   			]
   			
   			it.members += pattern.toConstructor(pattern.matcherClassName) [
   				it.visibility = JvmVisibility::PUBLIC
   				it.documentation = pattern.matcherConstructorEngineJavadoc.toString
   				it.parameters += pattern.toParameter("engine", pattern.newTypeRef(typeof (IncQueryEngine)))
   				it.exceptions += pattern.newTypeRef(typeof (IncQueryRuntimeException))
   				it.body = ['''super(engine, FACTORY);''']
   			]
   			
   			// Adding type-safe matcher calls
   			// if the pattern not defines parameters, the Matcher class contains only the hasMatch method
   			if (!pattern.parameters.isEmpty) {
   				 it.members += pattern.toMethod("getAllMatches", pattern.newTypeRef(typeof(Collection), cloneWithProxies(matchClassRef))) [
	   				it.documentation = pattern.javadocGetAllMatches.toString
	   				for (parameter : pattern.parameters){
						it.parameters += parameter.toParameter(parameter.name, parameter.calculateType)				
	   				}
	   				it.body = ['''
	   					return rawGetAllMatches(new Object[]{«FOR p : pattern.parameters SEPARATOR ', '»«p.name»«ENDFOR»});
	   				''']
	   			]
	   			
	   			it.members += pattern.toMethod("getOneArbitraryMatch", cloneWithProxies(matchClassRef)) [
	   				it.documentation = pattern.javadocGetOneArbitraryMatch.toString
	   				for (parameter : pattern.parameters){
						it.parameters += parameter.toParameter(parameter.name, parameter.calculateType)				
	   				}
	   				it.body = ['''
	   					return rawGetOneArbitraryMatch(new Object[]{«FOR p : pattern.parameters SEPARATOR ', '»«p.name»«ENDFOR»});
	   				''']
	   			]
	   			
	   			it.members += pattern.toMethod("hasMatch", pattern.newTypeRef(typeof(boolean))) [
	   				it.documentation = pattern.javadocHasMatch.toString
	   				for (parameter : pattern.parameters){
						it.parameters += parameter.toParameter(parameter.name, parameter.calculateType)				
	   				}
	   				it.body = ['''
	   					return rawHasMatch(new Object[]{«FOR p : pattern.parameters SEPARATOR ', '»«p.name»«ENDFOR»});
	   				''']
	   			]
	   			
	   			it.members += pattern.toMethod("countMatches", pattern.newTypeRef(typeof(int))) [
	   				it.documentation = pattern.javadocCountMatches.toString
	   				for (parameter : pattern.parameters){
						it.parameters += parameter.toParameter(parameter.name, parameter.calculateType)				
	   				}
	   				it.body = ['''
	   					return rawCountMatches(new Object[]{«FOR p : pattern.parameters SEPARATOR ', '»«p.name»«ENDFOR»});
	   				''']
	   			]
	   			
	   			it.members += pattern.toMethod("forEachMatch", null) [
	   				it.documentation = pattern.javadocForEachMatch.toString
	   				for (parameter : pattern.parameters){
						it.parameters += parameter.toParameter(parameter.name, parameter.calculateType)				
	   				}
					// TODO: add ? super MatchClass to Processor as typeparameter
					it.parameters += pattern.toParameter("processor", pattern.newTypeRef(typeof (IMatchProcessor), cloneWithProxies(matchClassRef)))
	   				it.body = ['''
	   					rawForEachMatch(new Object[]{«FOR p : pattern.parameters SEPARATOR ', '»«p.name»«ENDFOR»}, processor);
	   				''']
	   			]
	   			
	   			it.members += pattern.toMethod("forOneArbitraryMatch", pattern.newTypeRef(typeof(boolean))) [
	   				it.documentation = pattern.javadocForOneArbitraryMatch.toString
	   				for (parameter : pattern.parameters){
						it.parameters += parameter.toParameter(parameter.name, parameter.calculateType)				
	   				}
	   				it.parameters += pattern.toParameter("processor", pattern.newTypeRef(typeof (IMatchProcessor), cloneWithProxies(matchClassRef)))
	   				it.body = ['''
	   					return rawForOneArbitraryMatch(new Object[]{«FOR p : pattern.parameters SEPARATOR ', '»«p.name»«ENDFOR»}, processor);
	   				''']
	   			]
   			} else {
   				it.members += pattern.toMethod("hasMatch", pattern.newTypeRef(typeof(boolean))) [
	   				it.documentation = pattern.javadocHasMatchNoParameter.toString
	   				it.body = ['''
	   					return rawHasMatch(new Object[]{});
	   				''']
	   			]
   			}
   			
   			it.members += pattern.toMethod("tupleToMatch", cloneWithProxies(matchClassRef)) [
	   			it.annotations += pattern.toAnnotation(typeof (Override))
	   			it.parameters += pattern.toParameter("t", pattern.newTypeRef(typeof (Tuple)))
	   			it.body = ['''
	   				try {
	   					return new «pattern.matchClassName»(«FOR p : pattern.parameters SEPARATOR ', '»(«p.calculateType.simpleName») t.get(«pattern.parameters.indexOf(p)»)«ENDFOR»);	
	   				} catch(ClassCastException e) {
	   					throw new IncQueryRuntimeException(e.getMessage());
	   				}
	   			''']
	   		]
	   			
	   		it.members += pattern.toMethod("arrayToMatch", cloneWithProxies(matchClassRef)) [
	   			it.annotations += pattern.toAnnotation(typeof (Override))
	   			it.parameters += pattern.toParameter("match", pattern.newTypeRef(typeof (Object)).addArrayTypeDimension)
	   			it.body = ['''
	   				try {
	   					return new «pattern.matchClassName»(«FOR p : pattern.parameters SEPARATOR ', '»(«p.calculateType.simpleName») match[«pattern.parameters.indexOf(p)»]«ENDFOR»);
	   				} catch(ClassCastException e) {
	   					throw new IncQueryRuntimeException(e.getMessage());
	   				}
	   			''']
	   		]
   			
   		]
   	}
  	
  	def inferMatcherFactoryClass(Pattern pattern, boolean isPrelinkingPhase, String matcherFactoryPackageName, JvmTypeReference matchClassRef, JvmTypeReference matcherClassRef) {
  		return pattern.toClass(pattern.matcherFactoryClassName) [
  			it.packageName = matcherFactoryPackageName
  			it.documentation = pattern.matcherFactoryClassJavadoc.toString
  			it.superTypes += pattern.newTypeRef(typeof (BaseGeneratedMatcherFactory), cloneWithProxies(matchClassRef), cloneWithProxies(matcherClassRef))
  			it.members += pattern.toMethod("instantiate", cloneWithProxies(matcherClassRef)) [
  				it.visibility = JvmVisibility::PROTECTED
  				it.annotations += pattern.toAnnotation(typeof (Override))
  				it.parameters += pattern.toParameter("engine", pattern.newTypeRef(typeof (IncQueryEngine)))
   				it.exceptions += pattern.newTypeRef(typeof (IncQueryRuntimeException))
   				it.body = ['''
   					return new «pattern.matcherClassName»(engine);
   				''']
  			]
  			it.members += pattern.toMethod("parsePattern", pattern.newTypeRef(typeof (Pattern))) [
  				it.visibility = JvmVisibility::PROTECTED
  				it.annotations += pattern.toAnnotation(typeof (Override))
  				it.body = ['''
«««  					«pattern.serializeToJava»
  					throw new UnsupportedOperationException();
   				''']
  			]
  		]
  	}
  	
//  	def serializeToJava(EObject pattern) {
//  		try {
//			val parseString = serializer.serialize(pattern)
//	  		val splits = parseString.split("[\r\n]+")
//	  		val stringRep = '''String patternString = ""''' as StringConcatenation
//	  		stringRep.newLine
//	  		for (s : splits) {
//	  			stringRep.append("+\"" + s + "\"")
//	  			stringRep.newLine
//	  		}
//	  		stringRep.append(";")
//	  		return stringRep   		
//   		} catch (Exception e) {
//  			e.printStackTrace
//		}
//		return ""
//  	}
  	
  	def JvmDeclaredType inferProcessorClass(Pattern pattern, boolean isPrelinkingPhase, String processorPackageName, JvmTypeReference matchClassRef) {
  		return pattern.toClass(pattern.processorClassName) [
  			it.packageName = processorPackageName
  			it.documentation = pattern.processorClassJavadoc.toString
  			it.abstract = true
  			it.superTypes += pattern.newTypeRef(typeof(IMatchProcessor), cloneWithProxies(matchClassRef))
  			it.members += pattern.toMethod("process", null) [
  				it.documentation = pattern.javadocProcess.toString
  				it.abstract = true
  				for (parameter : pattern.parameters){
					it.parameters += parameter.toParameter(parameter.name, parameter.calculateType)
   				}
  			]
  			it.members += pattern.toMethod("process", null) [
  				it.annotations += pattern.toAnnotation(typeof (Override))
  				it.parameters += pattern.toParameter("match", cloneWithProxies(matchClassRef))
  				it.body = ['''
  					process(«FOR p : pattern.parameters SEPARATOR ', '»match.get«p.name.toFirstUpper»()«ENDFOR»);  				
  				''']
  			]
  		]
  	}

	def matcherConstructorBodyNotifier(Pattern pattern, ImportManager manager) {
  		manager.addImportFor(pattern.newTypeRef(typeof (EngineManager)).type)
  		return '''this(EngineManager.getInstance().getIncQueryEngine(notifier));'''
  	}

   	def CharSequence equalsMethodBody(Pattern pattern, ImportManager importManager) {
   		importManager.addImportFor(pattern.newTypeRef(typeof (Arrays)).type)
   		return '''
					if (this == obj)
						return true;
					if (obj == null)
						return false;
					if (!(obj instanceof IPatternMatch))
						return false;
					IPatternMatch otherSig  = (IPatternMatch) obj;
					if (!pattern().equals(otherSig.pattern()))
						return false;
					if (!«pattern.matchClassName».class.equals(obj.getClass()))
						return Arrays.deepEquals(toArray(), otherSig.toArray());
					«IF !pattern.parameters.isEmpty»
					«pattern.matchClassName» other = («pattern.matchClassName») obj;
					«FOR variable : pattern.parameters» 
					if («variable.fieldName» == null) {if (other.«variable.fieldName» != null) return false;}
					else if (!«variable.fieldName».equals(other.«variable.fieldName»)) return false;
					«ENDFOR»
					«ENDIF»
					return true;
				'''
   	}
 
	def matcherFactoryClassName(Pattern pattern) {
		pattern.name.toFirstUpper+"MatcherFactory"
	} 
 
   	def matcherClassName(Pattern pattern) {
   		pattern.name.toFirstUpper+"Matcher"
   	}
   	 
   	def matchClassName(Pattern pattern) {
   		pattern.name.toFirstUpper+"Match"
   	}
   	
   	def processorClassName(Pattern pattern) {
   		pattern.name.toFirstUpper+"Processor"
   	}
   	
   	def fieldName(Variable variable) {
   		"f"+variable.name.toFirstUpper
   	} 
   	
   	// Type calculation: first try
   	// See the XBaseUsageCrossReferencer class, possible solution for local variable usage
	// TODO: Find out how to get the type for variable
   	def JvmTypeReference calculateType(Variable variable) {
//   		if (variable.type != null && !variable.type.typename.nullOrEmpty) {
//   			return variable.newTypeRef(variable.type.typename)
//   		} else {
   			if (variable.eContainer() instanceof Pattern) {
   		 		val pattern = variable.eContainer() as Pattern;
   				for (body : pattern.bodies) {
   					for (constraint : body.constraints) {
   						val typeRef = getTypeRef(constraint, variable)
   						if (typeRef != null) {
   							return typeRef
   						}
   					}
   				}
   			}
   			return variable.newTypeRef(typeof(Object))
//   		}
   	}
   	
   	def dispatch JvmTypeReference getTypeRef(Constraint constraint, Variable variable) {
   	}
   	def dispatch JvmTypeReference getTypeRef(EClassConstraint constraint, Variable variable) {
   		val entityType = constraint.type
   		val variableRef = constraint.getVar
   		if (variableRef != null) {
   			if (variableRef.variable == variable || (!variableRef.getVar.nullOrEmpty && variableRef.getVar.equals(variable.name))) {
	   			if (entityType instanceof ClassType) {
	   				val clazz = (entityType as ClassType).classname.instanceClass
	   				if (clazz != null) {
	   					val typeref = variable.newTypeRef(clazz)
						if (typeref != null) {
							return typeref
						}
	   				}
	   			}
   			}	
   		}
   		return null
   	}
   	   
	def matchClassJavadoc(Pattern pattern) '''
		Pattern-specific match representation of the «pattern.fullyQualifiedName» pattern, 
		to be used in conjunction with «pattern.matcherClassName».
		
		<p>Class fields correspond to parameters of the pattern. Fields with value null are considered unassigned.
		Each instance is a (possibly partial) substitution of pattern parameters, 
		usable to represent a match of the pattern in the result of a query, 
		or to specify the bound (fixed) input parameters when issuing a query.
		
		@see «pattern.matcherClassName»
		@see «pattern.processorClassName»
   	'''

   	def matcherClassJavadoc(Pattern pattern) '''		
		Generated pattern matcher API of the «pattern.fullyQualifiedName» pattern, 
		providing pattern-specific query methods.
		
«««		«serializer.serialize(pattern)»
		
		@see «pattern.matchClassName»
		@see «pattern.matcherFactoryClassName»
		@see «pattern.processorClassName»
   	'''
   	
   	def matcherFactoryClassJavadoc(Pattern pattern) '''
	 	A pattern-specific matcher factory that can instantiate «pattern.matcherClassName» in a type-safe way.
	 	
	 	@see «pattern.matcherClassName»
	 	@see «pattern.matchClassName»
   	'''
   	
   	def processorClassJavadoc(Pattern pattern) '''
		A match processor tailored for the «pattern.fullyQualifiedName» pattern.
		
		Clients should derive an (anonymous) class that implements the abstract process().
	'''
   	
   	def matcherConstructorNotifierJavadoc(Pattern pattern) '''
		Initializes the pattern matcher over a given EMF model root (recommended: Resource or ResourceSet). 
		If a pattern matcher is already constructed with the same root, only a lightweight reference is created.
		The match set will be incrementally refreshed upon updates from the given EMF root and below.
		<p>Note: if emfRoot is a resourceSet, the scope will include even those resources that are not part of the resourceSet but are referenced. 
		This is mainly to support nsURI-based instance-level references to registered EPackages.
		@param emfRoot the root of the EMF tree where the pattern matcher will operate. Recommended: Resource or ResourceSet.
		@throws IncQueryRuntimeException if an error occurs during pattern matcher creation
	'''
	
	def matcherConstructorEngineJavadoc(Pattern pattern) '''
		Initializes the pattern matcher within an existing EMF-IncQuery engine. 
		If the pattern matcher is already constructed in the engine, only a lightweight reference is created.
		The match set will be incrementally refreshed upon updates.
		@param engine the existing EMF-IncQuery engine in which this matcher will be created.
		@throws IncQueryRuntimeException if an error occurs during pattern matcher creation
	'''
	
	def javadocGetAllMatches(Pattern pattern) '''
		Returns the set of all matches of the pattern that conform to the given fixed values of some parameters.
		«FOR p : pattern.parameters»
		@param «p.name» the fixed value of pattern parameter «p.name», or null if not bound.
		«ENDFOR»
		@return matches represented as a «pattern.matchClassName» object.
	'''
	
	def javadocGetOneArbitraryMatch(Pattern pattern) '''
		Returns an arbitrarily chosen match of the pattern that conforms to the given fixed values of some parameters.
		Neither determinism nor randomness of selection is guaranteed.
		«FOR p : pattern.parameters»
		@param «p.name» the fixed value of pattern parameter «p.name», or null if not bound.
		«ENDFOR»
		@return a match represented as a «pattern.matchClassName» object, or null if no match is found.
	'''
	
	def javadocHasMatch(Pattern pattern) '''
		Indicates whether the given combination of specified pattern parameters constitute a valid pattern match,
		under any possible substitution of the unspecified parameters (if any).
		«FOR p : pattern.parameters»
		@param «p.name» the fixed value of pattern parameter «p.name», or null if not bound.
		«ENDFOR»
		@return true if the input is a valid (partial) match of the pattern.
	'''
	
	def javadocHasMatchNoParameter(Pattern pattern) '''
		Indicates whether the (parameterless) pattern matches or not. 
		@return true if the pattern has a valid match.
	'''
	
	def javadocCountMatches(Pattern pattern) '''
		Returns the number of all matches of the pattern that conform to the given fixed values of some parameters.
		«FOR p : pattern.parameters»
		@param «p.name» the fixed value of pattern parameter «p.name», or null if not bound.
		«ENDFOR»
		@return the number of pattern matches found.
	'''
	
	def javadocForEachMatch(Pattern pattern) '''
		Executes the given processor on each match of the pattern that conforms to the given fixed values of some parameters.
		«FOR p : pattern.parameters»
		@param «p.name» the fixed value of pattern parameter «p.name», or null if not bound.
		«ENDFOR»
		@param processor the action that will process each pattern match.
	'''
	
	def javadocForOneArbitraryMatch(Pattern pattern) '''
		Executes the given processor on an arbitrarily chosen match of the pattern that conforms to the given fixed values of some parameters.  
		Neither determinism nor randomness of selection is guaranteed.
		«FOR p : pattern.parameters»
		@param «p.name» the fixed value of pattern parameter «p.name», or null if not bound.
		«ENDFOR»
		@param processor the action that will process the selected match. 
		@return true if the pattern has at least one match with the given parameter values, false if the processor was not invoked
	'''
	
	def javadocProcess(Pattern pattern) '''
		Defines the action that is to be executed on each match.
		«FOR p : pattern.parameters»
		@param «p.name» the value of pattern parameter «p.name» in the currently processed match 
		«ENDFOR»
	'''
}
