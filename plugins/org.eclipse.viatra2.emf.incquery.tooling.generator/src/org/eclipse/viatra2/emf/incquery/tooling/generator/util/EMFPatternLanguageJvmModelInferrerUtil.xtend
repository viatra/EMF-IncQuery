package org.eclipse.viatra2.emf.incquery.tooling.generator.util

import com.google.inject.Inject
import org.apache.log4j.Logger
import org.eclipse.core.resources.IWorkspaceRoot
import org.eclipse.core.runtime.Path
import org.eclipse.emf.ecore.EObject
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Constraint
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Pattern
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.PatternModel
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Variable
import org.eclipse.viatra2.patternlanguage.eMFPatternLanguage.ClassType
import org.eclipse.viatra2.patternlanguage.eMFPatternLanguage.EClassifierConstraint
import org.eclipse.xtend2.lib.StringConcatenation
import org.eclipse.xtext.EcoreUtil2
import org.eclipse.xtext.common.types.JvmTypeReference
import org.eclipse.xtext.nodemodel.util.NodeModelUtils
import org.eclipse.xtext.serializer.ISerializer
import org.eclipse.xtext.xbase.typing.ITypeProvider
import org.eclipse.xtext.xbase.compiler.output.ITreeAppendable

/**
 * Utility class for the EMFPatternLanguageJvmModelInferrer.
 * 
 * @author Mark Czotter
 */
class EMFPatternLanguageJvmModelInferrerUtil {
	
	@Inject extension EMFJvmTypesBuilder
	@Inject ISerializer serializer
	Logger logger = Logger::getLogger(getClass())
	private String MULTILINE_COMMENT_PATTERN = "(/\\*([^*]|[\\r\\n]|(\\*+([^*/]|[\\r\\n])))*\\*+/)"
	@Inject	IWorkspaceRoot workspaceRoot
	@Inject ITypeProvider typeProvider
	
	def bundleName(Pattern pattern) {
		val project = workspaceRoot.getFile(
				new Path(pattern.eResource.getURI().toPlatformString(true)))
				.getProject();
		project.name
	}
	
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
   	
	/**
	 * Calculates type for a Variable.
	 * See the XBaseUsageCrossReferencer class, possible solution for local variable usage
	 * TODO: improve type calculation 
	 * @return JvmTypeReference pointing the EClass that defines the Variable's type.
	 */
   	def JvmTypeReference calculateType(Variable variable) {
   		// resolve the possible proxies
   		EcoreUtil2::resolveAll(variable)
   		try {
   			// first try to get the type through the variable's type ref 
   			if (variable.type instanceof ClassType) {
   				val eClassifier = (variable.type as ClassType).classname
   				if (eClassifier != null && eClassifier.instanceClass != null) {
	   				return variable.newTypeRef(eClassifier.instanceClass)	
   				}
   			} 
	   		// if the first try didnt return anything, try to 
	   		// infer the type from one of the Pattern bodies.
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
   		} catch (Exception e) {
   			if (logger != null) {
   				logger.error("Error during type calculation for " + variable.name, e)	
   			}
   		}
   		// if all calculation failed, simply fall back to java Object.
		return variable.newTypeRef(typeof(Object))
   	}
   	
   	/**
   	 * Returns the JvmTypeReference for variable if it used in the Constraint.
   	 */
   	def dispatch JvmTypeReference getTypeRef(Constraint constraint, Variable variable) {}

   	/**
   	 * Returns the JvmTypeReference for variable if it used in the EClassConstraint.
   	 */   	
   	def dispatch JvmTypeReference getTypeRef(EClassifierConstraint constraint, Variable variable) {
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
   	 * Serializes the EObject into Java String variable.
   	 */
   	def serializeToJava(EObject eObject) {
		val parseString = eObject.serialize
		if (parseString.nullOrEmpty) {
			return "";
		}
		val splits = parseString.split("[\r\n]+")
		val stringRep = '''String patternString = ""''' as StringConcatenation
	  	stringRep.newLine
	  	for (s : splits) {
	  		// Extra space needed before and after every line, 
	  		// otherwise parser parses the entire string (or part of it) as package name).
	  		stringRep.append("+\" " + s + " \"")
	  		stringRep.newLine
		}
	  	stringRep.append(";")
	  	return stringRep   		
  	}
  	
  	/**
  	 * Serializes the input for Javadoc
  	 */
  	def serializeToJavadoc(Pattern pattern) {
  		val javadocString = pattern.serialize
  		if (javadocString.nullOrEmpty) {
  			return "Serialization error, check Log"
  		}
  		return javadocString
  	}
  	
  	/**
  	 * Serializes EObject to a String representation. Escapes only the double qoutes.
  	 */
  	def private serialize(EObject eObject) {
  		try {
  			// This call sometimes causes ConcurrentModificationException
//			val serializedObject = serializer.serialize(eObject)
			// Another way to serialize the eObject, uses the current node model
			// simple getText returns the currently text, that parsed by the editor 
			val eObjectNode = NodeModelUtils::getNode(eObject)
			if (eObjectNode != null) {
				return escape(eObjectNode.text)	
			}
			// getTokenText returns the string without hidden tokens
//			NodeModelUtils::getTokenText(NodeModelUtils::getNode(eObject)).replaceAll("\"", "\\\\\"")
		} catch (Exception e) {
			if (logger != null) {
				logger.error("Error when serializing " + eObject.eClass.name, e)	
			}
		}
		return null
  	}
  	
  	def private escape(String escapable) {
  		if (escapable == null) return null
  		// escape double quotes
  		var escapedString = escapable.replaceAll("\"", "\\\\\"")
  		// escape javadoc comments to single space
  		// FIXME need a better replacement, or better way to do this
  		escapedString = escapedString.replaceAll(MULTILINE_COMMENT_PATTERN, " ")
  		return escapedString
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
	
	def getPackagePath(Pattern pattern) {
		pattern.packageName.replace(".","/")
	}
	
	def referClass(ITreeAppendable appendable, EObject ctx, Class<?> clazz) {
		val type = ctx.newTypeRef(clazz).type
		appendable.append(type)
		//'''«type.simpleName»'''
	}
}
