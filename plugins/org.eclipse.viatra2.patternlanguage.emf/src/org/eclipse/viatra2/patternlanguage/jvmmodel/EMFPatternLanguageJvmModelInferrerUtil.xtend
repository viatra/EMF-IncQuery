package org.eclipse.viatra2.patternlanguage.jvmmodel

import com.google.inject.Inject
import org.eclipse.emf.ecore.EObject
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Constraint
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Pattern
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.PatternModel
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Variable
import org.eclipse.viatra2.patternlanguage.eMFPatternLanguage.ClassType
import org.eclipse.viatra2.patternlanguage.eMFPatternLanguage.EClassConstraint
import org.eclipse.xtend2.lib.StringConcatenation
import org.eclipse.xtext.common.types.JvmTypeReference
import org.eclipse.xtext.serializer.ISerializer

/**
 * Utility class for the EMFPatternLanguageJvmModelInferrer.
 * 
 * @author Mark Czotter
 */
class EMFPatternLanguageJvmModelInferrerUtil {
	
	@Inject extension EMFJvmTypesBuilder
	@Inject ISerializer serializer
	
	/**
	 * Returns the MatcherFactoryClass name based on the Pattern's name
	 */
	def matcherFactoryClassName(Pattern pattern) {
		pattern.name.toFirstUpper+"MatcherFactory"
	} 
	
	/**
	 * Returns the MatcherClass name based on the Pattern's name
	 */
   	def matcherClassName(Pattern pattern) {
   		pattern.name.toFirstUpper+"Matcher"
   	}

	/**
	 * Returns the MatchClass name based on the Pattern's name
	 */   	 
   	def matchClassName(Pattern pattern) {
   		pattern.name.toFirstUpper+"Match"
   	}
   	
	/**
	 * Returns the ProcessorClass name based on the Pattern's name
	 */   	
   	def processorClassName(Pattern pattern) {
   		pattern.name.toFirstUpper+"Processor"
   	}
   	
   	/**
   	 * Returns the field name of Variable
   	 */
   	def fieldName(Variable variable) {
   		"f"+variable.name.toFirstUpper
   	}
   	
	// Type calculation: first try
   	// See the XBaseUsageCrossReferencer class, possible solution for local variable usage
	// TODO: Find out how to get the type for variable
   	def JvmTypeReference calculateType(Variable variable) {
   		try {
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
//   		}   			
   		} catch (Exception e) {
   			e.printStackTrace
   		}
		return variable.newTypeRef(typeof(Object))
   	}
   	
   	/**
   	 * Returns the JvmTypeReference for variable if it used in the Constraint.
   	 */
   	def dispatch JvmTypeReference getTypeRef(Constraint constraint, Variable variable) {}

   	/**
   	 * Returns the JvmTypeReference for variable if it used in the EClassConstraint.
   	 */   	
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
   	
   	/**
   	 * Serializes the EObject into a String representation
   	 */
   	def serializeToJava(EObject pattern) {
  		try {
			val parseString = serializer.serialize(pattern)
	  		val splits = parseString.split("[\r\n]+")
	  		val stringRep = '''String patternString = ""''' as StringConcatenation
	  		stringRep.newLine
	  		for (s : splits) {
	  			stringRep.append("+\"" + s + "\"")
	  			stringRep.newLine
	  		}
	  		stringRep.append(";")
	  		return stringRep   		
   		} catch (Exception e) {
  			e.printStackTrace
		}
		return ""
  	}
  	
  	/**
  	 * Returns the packageName: PatternModel.packageName + Pattern.name, packageName is ignored, when nullOrEmpty.
  	 */
  	def getPackageName(Pattern pattern) {
  		var packageName = (pattern.eContainer as PatternModel).packageName
	   	if (packageName.nullOrEmpty) {
	   		packageName = ""
	   	} else {
	   		packageName = packageName + "."
	   	}
	   	return packageName + pattern.name
  	}
	
}