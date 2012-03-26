package org.eclipse.viatra2.emf.incquery.tooling.generator.jvmmodel

import com.google.inject.Inject
import org.eclipse.viatra2.emf.incquery.runtime.api.IMatchProcessor
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Pattern
import org.eclipse.xtext.common.types.JvmDeclaredType
import org.eclipse.xtext.common.types.JvmTypeReference
import org.eclipse.viatra2.emf.incquery.tooling.generator.util.EMFJvmTypesBuilder
import org.eclipse.viatra2.emf.incquery.tooling.generator.util.EMFPatternLanguageJvmModelInferrerUtil

/**
 * {@link IMatchProcessor} implementation inferer.
 * 
 * @author Mark Czotter
 */
class PatternMatchProcessorClassInferrer {

	@Inject extension EMFJvmTypesBuilder
	@Inject extension EMFPatternLanguageJvmModelInferrerUtil
	@Inject extension JavadocInferrer

	/**
	 * Infers the {@link IMatchProcessor} implementation class from a {@link Pattern}.
	 */
	def JvmDeclaredType inferProcessorClass(Pattern pattern, boolean isPrelinkingPhase, String processorPackageName, JvmTypeReference matchClassRef) {
		val processorClass = pattern.toClass(pattern.processorClassName) [
  			it.packageName = processorPackageName
  			it.documentation = pattern.javadocProcessorClass.toString
  			it.abstract = true
  			it.superTypes += pattern.newTypeRef(typeof(IMatchProcessor), cloneWithProxies(matchClassRef))
  		]
  		processorClass.inferProcessorClassMethods(pattern, matchClassRef)
  		return processorClass
  	}
  	
	/**
   	 * Infers methods for Processor class based on the input 'pattern'.
   	 */  	
  	def inferProcessorClassMethods(JvmDeclaredType processorClass, Pattern pattern, JvmTypeReference matchClassRef) {
  		processorClass.members += pattern.toMethod("process", null) [
			it.documentation = pattern.javadocProcessMethod.toString
			it.abstract = true
			for (parameter : pattern.parameters){
				it.parameters += parameter.toParameter(parameter.name, parameter.calculateType)
			}
		]
		processorClass.members += pattern.toMethod("process", null) [
			it.annotations += pattern.toAnnotation(typeof (Override))
			it.parameters += pattern.toParameter("match", cloneWithProxies(matchClassRef))
			it.body = [it.append('''
				process(«FOR p : pattern.parameters SEPARATOR ', '»match.get«p.name.toFirstUpper»()«ENDFOR»);  				
			''')]
		]
  	}
	
}