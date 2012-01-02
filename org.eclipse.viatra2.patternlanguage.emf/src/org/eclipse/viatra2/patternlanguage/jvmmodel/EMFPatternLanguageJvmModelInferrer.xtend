package org.eclipse.viatra2.patternlanguage.jvmmodel
 
import com.google.inject.Inject
import org.eclipse.xtext.common.types.JvmDeclaredType
import org.eclipse.xtext.util.IAcceptor
import org.eclipse.xtext.xbase.jvmmodel.AbstractModelInferrer
import org.eclipse.xtext.xbase.jvmmodel.JvmTypesBuilder
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Pattern
import org.eclipse.viatra2.emf.incquery.runtime.api.GenericPatternMatcher

/**
 * <p>Infers a JVM model from the source model.</p> 
 *
 * <p>The JVM model should contain all elements that would appear in the Java code 
 * which is generated from the source model. Other models link against the JVM model rather than the source model.</p>     
 */
class EMFPatternLanguageJvmModelInferrer extends AbstractModelInferrer {

    /**
     * convenience API to build and initialize JvmTypes and their members.
     */
	@Inject extension JvmTypesBuilder
   	
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
   		
   		acceptor.accept(pattern.toClass(pattern.name.toFirstUpper + "Matcher") [
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
   		])
   	}
}
