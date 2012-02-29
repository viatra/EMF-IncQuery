package org.eclipse.viatra2.emf.incquery.tooling.generator.jvmmodel

import com.google.inject.Inject
import java.util.Arrays
import org.eclipse.viatra2.emf.incquery.runtime.api.IPatternMatch
import org.eclipse.viatra2.emf.incquery.runtime.api.impl.BasePatternMatch
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Pattern
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Variable
import org.eclipse.xtext.common.types.JvmDeclaredType
import org.eclipse.xtext.common.types.JvmVisibility
import org.eclipse.xtext.naming.IQualifiedNameProvider
import org.eclipse.xtext.xbase.compiler.ImportManager

/**
 * {@link IPatternMatch} implementation inferer.
 * 
 * @author Mark Czotter
 */
class PatternMatchClassInferrer {
	
	@Inject extension EMFJvmTypesBuilder
	@Inject extension IQualifiedNameProvider
	@Inject extension EMFPatternLanguageJvmModelInferrerUtil
	@Inject extension JavadocInferrer
	
	/**
	 * Infers the {@link IPatternMatch} implementation class from {@link Pattern} parameters.
	 */
	def JvmDeclaredType inferMatchClass(Pattern pattern, boolean isPrelinkingPhase, String matchPackageName) {
		val matchClass = pattern.toClass(pattern.matchClassName) [
   			it.packageName = matchPackageName
   			it.documentation = pattern.javadocMatchClass.toString
   			it.final = true
   			it.superTypes += pattern.newTypeRef(typeof (BasePatternMatch))
   			it.superTypes += pattern.newTypeRef(typeof (IPatternMatch))
   		]
   		matchClass.inferMatchClassFields(pattern)
   		matchClass.inferMatchClassConstructors(pattern)
   		matchClass.inferMatchClassGetters(pattern)
   		matchClass.inferMatchClassSetters(pattern)
   		matchClass.inferMatchClassMethods(pattern)
   		return matchClass
   	}
   	
   	/**
   	 * Infers fields for Match class based on the input 'pattern'.
   	 */
   	def inferMatchClassFields(JvmDeclaredType matchClass, Pattern pattern) {
   		for (Variable variable : pattern.parameters) {
   			matchClass.members += pattern.toField(variable.fieldName, variable.calculateType)
   		}
		matchClass.members += pattern.toField("parameterNames", pattern.newTypeRef(typeof (String)).addArrayTypeDimension) [
 			it.setStatic(true);
   			it.setInitializer(['''{«FOR variable : pattern.parameters SEPARATOR ', '»"«variable.name»"«ENDFOR»}'''])
   		]
   	}
   	
   	/**
   	 * Infers constructors for Match class based on the input 'pattern'.
   	 */
   	def inferMatchClassConstructors(JvmDeclaredType matchClass, Pattern pattern) {
   		matchClass.members += pattern.toConstructor(pattern.matchClassName) [
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
   	}
   	
   	/**
   	 * Infers getters for Match class based on the input 'pattern'.
   	 */
   	def inferMatchClassGetters(JvmDeclaredType matchClass, Pattern pattern) {
		matchClass.members += pattern.toMethod("get", pattern.newTypeRef(typeof (Object))) [
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
   			matchClass.members += pattern.toMethod("get" + variable.name.toFirstUpper, variable.calculateType) [
   				it.body = ['''
   					return this.«variable.fieldName»;
   				''']
   			]
   		}
   	}
   	
   	/**
   	 * Infers setters for Match class based on the input 'pattern'.
   	 */
   	def inferMatchClassSetters(JvmDeclaredType matchClass, Pattern pattern) {
   		matchClass.members += pattern.toMethod("set", pattern.newTypeRef(typeof (boolean))) [
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
   			matchClass.members += pattern.toMethod("set" + variable.name.toFirstUpper, null) [
   				it.parameters += pattern.toParameter(variable.name, variable.calculateType)
   				it.body = ['''
   					this.«variable.fieldName» = «variable.name»;
   				''']
   			]
   		}
   	}
   	
	/**
   	 * Infers methods for Match class based on the input 'pattern'.
   	 */
   	def inferMatchClassMethods(JvmDeclaredType matchClass, Pattern pattern) {
   		matchClass.members += pattern.toMethod("patternName", pattern.newTypeRef(typeof(String))) [
   			it.annotations += pattern.toAnnotation(typeof (Override))
   			it.body = ['''
   				return "«pattern.fullyQualifiedName»";
   			''']
   		]
		// add extra methods like equals, hashcode, toArray, parameterNames
		matchClass.members += pattern.toMethod("parameterNames", pattern.newTypeRef(typeof (String)).addArrayTypeDimension) [
   			it.annotations += pattern.toAnnotation(typeof (Override))
   			it.body = ['''
   				return «pattern.matchClassName».parameterNames;
   			''']
   		]
   		matchClass.members += pattern.toMethod("toArray", pattern.newTypeRef(typeof (Object)).addArrayTypeDimension) [
   			it.annotations += pattern.toAnnotation(typeof (Override))
   			it.body = ['''
   				return new Object[]{«FOR variable : pattern.parameters SEPARATOR ', '»«variable.fieldName»«ENDFOR»};
   			''']
   		]
		matchClass.members += pattern.toMethod("prettyPrint", pattern.newTypeRef(typeof (String))) [
			it.annotations += pattern.toAnnotation(typeof (Override))
			it.body = ['''
				StringBuilder result = new StringBuilder();
				«FOR variable : pattern.parameters»
				result.append("\"«variable.name»\"=" + prettyPrintValue(«variable.fieldName») + "\n");
				«ENDFOR»
				return result.toString();
			''']
		]
		matchClass.members += pattern.toMethod("hashCode", pattern.newTypeRef(typeof (int))) [
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
		matchClass.members += pattern.toMethod("equals", pattern.newTypeRef(typeof (boolean))) [
			it.annotations += pattern.toAnnotation(typeof (Override))
			it.parameters += pattern.toParameter("obj", pattern.newTypeRef(typeof (Object)))
			it.body = [it | pattern.equalsMethodBody(it)]
		]
		matchClass.members += pattern.toMethod("pattern", pattern.newTypeRef(typeof (Pattern))) [
			it.annotations += pattern.toAnnotation(typeof (Override))
			it.body = ['''return «pattern.matcherClassName».FACTORY.getPattern();''']
		]
  	}
   	
   	/**
   	 * Infers an equals method based on the 'pattern' parameter.
   	 */
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
	
}