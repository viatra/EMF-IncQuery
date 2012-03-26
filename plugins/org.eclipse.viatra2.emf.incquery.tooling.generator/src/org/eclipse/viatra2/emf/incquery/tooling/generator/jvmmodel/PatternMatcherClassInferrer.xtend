package org.eclipse.viatra2.emf.incquery.tooling.generator.jvmmodel

import com.google.inject.Inject
import java.util.Collection
import org.eclipse.core.runtime.ILog
import org.eclipse.core.runtime.IStatus
import org.eclipse.core.runtime.Status
import org.eclipse.emf.common.notify.Notifier
import org.eclipse.viatra2.emf.incquery.runtime.api.EngineManager
import org.eclipse.viatra2.emf.incquery.runtime.api.IMatchProcessor
import org.eclipse.viatra2.emf.incquery.runtime.api.IncQueryEngine
import org.eclipse.viatra2.emf.incquery.runtime.api.IncQueryMatcher
import org.eclipse.viatra2.emf.incquery.runtime.api.impl.BaseGeneratedMatcher
import org.eclipse.viatra2.emf.incquery.runtime.exception.IncQueryRuntimeException
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.tuple.Tuple
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Pattern
import org.eclipse.xtext.common.types.JvmDeclaredType
import org.eclipse.xtext.common.types.JvmTypeReference
import org.eclipse.xtext.common.types.JvmVisibility
import org.eclipse.xtext.xbase.compiler.output.ITreeAppendable
import org.eclipse.viatra2.emf.incquery.tooling.generator.util.EMFJvmTypesBuilder
import org.eclipse.viatra2.emf.incquery.tooling.generator.util.EMFPatternLanguageJvmModelInferrerUtil

/**
 * {@link IncQueryMatcher} implementation inferrer.
 * 
 * @author Mark Czotter
 */
class PatternMatcherClassInferrer {

	@Inject extension EMFJvmTypesBuilder
	@Inject extension EMFPatternLanguageJvmModelInferrerUtil
	@Inject extension JavadocInferrer

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
   		matcherClass.inferMatcherClassConstructors(pattern)
   		matcherClass.inferMatcherClassMethods(pattern, matchClassRef)
   		matcherClass.inferMatcherClassToMatchMethods(pattern, matchClassRef)
   		return matcherClass
   	}
   	
	/**
   	 * Infers constructors for Matcher class based on the input 'pattern'.
   	 */
   	def inferMatcherClassConstructors(JvmDeclaredType matcherClass, Pattern pattern) {
   		matcherClass.members += pattern.toConstructor [
   			it.simpleName = pattern.matcherClassName
			it.visibility = JvmVisibility::PUBLIC
			it.documentation = pattern.javadocMatcherConstructorNotifier.toString
			it.parameters += pattern.toParameter("notifier", pattern.newTypeRef(typeof (Notifier)))
			it.exceptions += pattern.newTypeRef(typeof (IncQueryRuntimeException))
			it.body = [
				append('''this(''')
				referClass(pattern, typeof(EngineManager))
				append('''.getInstance().getIncQueryEngine(notifier));''')
			]
		]
		
		matcherClass.members += pattern.toConstructor [
			it.simpleName = pattern.matcherClassName
			it.visibility = JvmVisibility::PUBLIC
			it.documentation = pattern.javadocMatcherConstructorEngine.toString
			it.parameters += pattern.toParameter("engine", pattern.newTypeRef(typeof (IncQueryEngine)))
			it.exceptions += pattern.newTypeRef(typeof (IncQueryRuntimeException))
			it.body = [append('''super(engine, FACTORY);''')]
		]
   	}
   	
   	/**
   	 * Infers methods for Matcher class based on the input 'pattern'.
   	 */
   	def inferMatcherClassMethods(JvmDeclaredType matcherClass, Pattern pattern, JvmTypeReference matchClassRef) {
   		// Adding type-safe matcher calls
		// if the pattern not defines parameters, the Matcher class contains only the hasMatch method
		if (!pattern.parameters.isEmpty) {
			 matcherClass.members += pattern.toMethod("getAllMatches", pattern.newTypeRef(typeof(Collection), cloneWithProxies(matchClassRef))) [
   				it.documentation = pattern.javadocGetAllMatchesMethod.toString
   				for (parameter : pattern.parameters){
					it.parameters += parameter.toParameter(parameter.name, parameter.calculateType)				
   				}
   				it.body = [append('''
   					return rawGetAllMatches(new Object[]{«FOR p : pattern.parameters SEPARATOR ', '»«p.name»«ENDFOR»});
   				''')]
   			]
   			matcherClass.members += pattern.toMethod("getOneArbitraryMatch", cloneWithProxies(matchClassRef)) [
   				it.documentation = pattern.javadocGetOneArbitraryMatchMethod.toString
   				for (parameter : pattern.parameters){
					it.parameters += parameter.toParameter(parameter.name, parameter.calculateType)				
   				}
   				it.body = [append('''
   					return rawGetOneArbitraryMatch(new Object[]{«FOR p : pattern.parameters SEPARATOR ', '»«p.name»«ENDFOR»});
   				''')]
   			]
   			matcherClass.members += pattern.toMethod("hasMatch", pattern.newTypeRef(typeof(boolean))) [
   				it.documentation = pattern.javadocHasMatchMethod.toString
   				for (parameter : pattern.parameters){
					it.parameters += parameter.toParameter(parameter.name, parameter.calculateType)				
   				}
   				it.body = [append('''
   					return rawHasMatch(new Object[]{«FOR p : pattern.parameters SEPARATOR ', '»«p.name»«ENDFOR»});
   				''')]
   			]
   			matcherClass.members += pattern.toMethod("countMatches", pattern.newTypeRef(typeof(int))) [
   				it.documentation = pattern.javadocCountMatchesMethod.toString
   				for (parameter : pattern.parameters){
					it.parameters += parameter.toParameter(parameter.name, parameter.calculateType)				
   				}
   				it.body = [append('''
   					return rawCountMatches(new Object[]{«FOR p : pattern.parameters SEPARATOR ', '»«p.name»«ENDFOR»});
   				''')]
   			]
   			matcherClass.members += pattern.toMethod("forEachMatch", null) [
   				it.documentation = pattern.javadocForEachMatchMethod.toString
   				for (parameter : pattern.parameters){
					it.parameters += parameter.toParameter(parameter.name, parameter.calculateType)				
   				}
				it.parameters += pattern.toParameter("processor", pattern.newTypeRef(typeof (IMatchProcessor), cloneWithProxies(matchClassRef).wildCardSuper))
   				it.body = [append('''
   					rawForEachMatch(new Object[]{«FOR p : pattern.parameters SEPARATOR ', '»«p.name»«ENDFOR»}, processor);
   				''')]
   			]
   			matcherClass.members += pattern.toMethod("forOneArbitraryMatch", pattern.newTypeRef(typeof(boolean))) [
   				it.documentation = pattern.javadocForOneArbitraryMatchMethod.toString
   				for (parameter : pattern.parameters){
					it.parameters += parameter.toParameter(parameter.name, parameter.calculateType)				
   				}
   				it.parameters += pattern.toParameter("processor", pattern.newTypeRef(typeof (IMatchProcessor), cloneWithProxies(matchClassRef).wildCardSuper))
   				it.body = [append('''
   					return rawForOneArbitraryMatch(new Object[]{«FOR p : pattern.parameters SEPARATOR ', '»«p.name»«ENDFOR»}, processor);
   				''')]
   			]
		} else {
			matcherClass.members += pattern.toMethod("hasMatch", pattern.newTypeRef(typeof(boolean))) [
   				it.documentation = pattern.javadocHasMatchMethodNoParameter.toString
   				it.body = [append('''
   					return rawHasMatch(new Object[]{});
   				''')]
   			]
		}
   	}
   	
   	/**
   	 * Infers tupleToMatch, arrayToMatch methods for Matcher class based on the input 'pattern'.
   	 */   	
   	def inferMatcherClassToMatchMethods(JvmDeclaredType matcherClass, Pattern pattern, JvmTypeReference matchClassRef) {
   		// TODO get setting from the generator model
  		val isPluginLogging = false;
	   	val tupleToMatchMethod = pattern.toMethod("tupleToMatch", cloneWithProxies(matchClassRef)) [
   			it.annotations += pattern.toAnnotation(typeof (Override))
   			it.parameters += pattern.toParameter("t", pattern.newTypeRef(typeof (Tuple)))
   		]
   		val arrayToMatchMethod = pattern.toMethod("arrayToMatch", cloneWithProxies(matchClassRef)) [
   			it.annotations += pattern.toAnnotation(typeof (Override))
   			it.parameters += pattern.toParameter("match", pattern.newTypeRef(typeof (Object)).addArrayTypeDimension)
   		]
   		tupleToMatchMethod.setBody([it | pattern.inferTupleToMatchMethodBody(it, isPluginLogging)])
   		arrayToMatchMethod.setBody([it | pattern.inferArrayToMatchMethodBody(it, isPluginLogging)])
   		matcherClass.members += tupleToMatchMethod
   		matcherClass.members += arrayToMatchMethod
   	}
  	
  	/**
  	 * Infers the arrayToMatch method body.
  	 */
  	def inferTupleToMatchMethodBody(Pattern pattern, ITreeAppendable appendable, boolean isPluginLogging) {
  		val activatorClass = pattern.findActivatorClass;
   		appendable.append('''
   			try {
   				return new «pattern.matchClassName»(«FOR p : pattern.parameters SEPARATOR ', '»(«p.calculateType.qualifiedName») t.get(«pattern.parameters.indexOf(p)»)«ENDFOR»);	
   			} catch(ClassCastException e) {''')
   		pattern.inferLogging(isPluginLogging, activatorClass, "tupleToMatch", appendable)
   		appendable.append('''
   				//throw new IncQueryRuntimeException(e.getMessage());
   				return null;
   			}
   		''')
  	}
  	
  	/**
  	 * Infers the arrayToMatch method body.
  	 */
  	def inferArrayToMatchMethodBody(Pattern pattern, ITreeAppendable appendable, boolean isPluginLogging) {
  		val activatorClass = pattern.findActivatorClass;
  		appendable.append('''
   			try {
   				return new «pattern.matchClassName»(«FOR p : pattern.parameters SEPARATOR ', '»(«p.calculateType.qualifiedName») match[«pattern.parameters.indexOf(p)»]«ENDFOR»);
   			} catch(ClassCastException e) {''')
   		pattern.inferLogging(isPluginLogging, activatorClass, "arrayToMatch", appendable)
   		appendable.append('''
   		}
   				//throw new IncQueryRuntimeException(e.getMessage());
   				return null;
   		''')
  	}
	  	
  	/**
  	 * Infers the appropriate logging based on the parameters.
  	 */
  	def inferLogging(Pattern pattern, boolean pluginLogging, JvmTypeReference activator, String methodName, ITreeAppendable appendable) {
  		if (pluginLogging && activator != null) {
  			return pattern.pluginLogging(activator, appendable)
  		} else {
  			return pattern.consoleLogging(methodName, appendable)
  		}
	}
	
	/**
	 * Searches for an Activator class
	 * TODO: generate an Activator or ?
	 */
	def findActivatorClass(Pattern pattern) {
		return pattern.newTypeRef("Activator")
	}
	
	/**
	 * Default logging to System error.
	 */
	def consoleLogging(Pattern pattern, String methodName, ITreeAppendable appendable) {appendable.append('''
		System.err.println("Error when executing «methodName» in «pattern.matcherClassName»:" + e.getMessage());
	''')
	}
	
	/**
	 * Default logging with Activator's ILog.
	 */
	def pluginLogging(Pattern pattern, JvmTypeReference activator, ITreeAppendable appendable) { 
		appendable.referClass(pattern, typeof(ILog))
		appendable.append(''' log = Activator.getDefault().getLog();''')
		appendable.referClass(pattern, typeof(IStatus))
		appendable.append(''' status = new ''')
		appendable.referClass(pattern, typeof(Status))
		appendable.append('''(IStatus.ERROR, log.getBundle().getSymbolicName(), e.getMessage());
		log.log(status);
	''')
	}
	
}