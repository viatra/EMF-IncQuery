package org.eclipse.viatra2.patternlanguage.jvmmodel

import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Pattern
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Variable
import org.eclipse.xtext.common.types.JvmTypeReference
import com.google.inject.Inject
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Constraint
import org.eclipse.viatra2.patternlanguage.eMFPatternLanguage.EClassConstraint
import org.eclipse.viatra2.patternlanguage.eMFPatternLanguage.ClassType

class EMFPatternLanguageJvmModelInferrerUtil {
	
	@Inject extension EMFJvmTypesBuilder
		
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
	
}