package org.eclipse.viatra2.emf.incquery.tooling.generator.validation

import com.google.inject.Inject
import org.eclipse.viatra2.emf.incquery.tooling.generator.ExtensionGenerator
import org.eclipse.viatra2.emf.incquery.tooling.generator.fragments.IGenerationFragment
import org.eclipse.viatra2.emf.incquery.tooling.generator.util.EMFPatternLanguageJvmModelInferrerUtil
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Pattern
import org.eclipse.xtext.generator.IFileSystemAccess

import static extension org.eclipse.viatra2.patternlanguage.core.helper.CorePatternLanguageHelper.*
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.impl.StringValueImpl

class ValidationGenerator implements IGenerationFragment {
	
	@Inject extension EMFPatternLanguageJvmModelInferrerUtil
	
	override generateFiles(Pattern pattern, IFileSystemAccess fsa) {
		if (hasConstraintAnnotation(pattern)) {
			fsa.generateFile(pattern.packagePath + "/validation/" + pattern.name.toFirstUpper + "Constraint.java", pattern.patternHandler)
		}
	}
	
	override getProjectDependencies() {
		newArrayList("org.eclipse.viatra2.emf.incquery.runtime",
			"org.eclipse.viatra2.emf.incquery.validation.runtime"
		)
	}
	
	override getProjectPostfix() {
		"validation"
	}
	
	override extensionContribution(Pattern pattern, ExtensionGenerator exGen) {
		if (hasConstraintAnnotation(pattern)) {		
			return newArrayList(
				exGen.contribExtension("", "org.eclipse.viatra2.emf.incquery.validation.runtime.constraint") [
					exGen.contribElement(it, "constraint") [
						exGen.contribAttribute(it, "class", pattern.packagePath+".validation."+pattern.name.toFirstUpper+"Constraint")
						exGen.contribAttribute(it, "name", pattern.fullyQualifiedName)
					]
				]
			)
		}
		else {
			return newArrayList()
		}
	}
	
	def hasConstraintAnnotation(Pattern pattern) {
		for (a : pattern.annotations) {
			if (a.name.matches("Constraint")) {
				return true;
			}
		}
		return false;
	}
	
	def getElementOfConstraintAnnotation(Pattern pattern, String elementName) {
		for (a : pattern.annotations) {
			if (a.name.matches("Constraint")) {
				for (ap : a.parameters) {
					if (ap.name.matches(elementName)) {
						return (ap.value as StringValueImpl).value
					}
				}
			}
		}
		return null
	}
	
	def patternHandler(Pattern pattern) '''
		package «pattern.fullyQualifiedName».validation;
		
		import org.eclipse.emf.ecore.EObject;

		import org.eclipse.viatra2.emf.incquery.validation.runtime.Constraint;
		import org.eclipse.viatra2.emf.incquery.validation.runtime.ValidationUtil;

		import «pattern.packageName + "." + pattern.matchClassName»;

		public class «pattern.name.toFirstUpper»Constraint extends Constraint<«pattern.matchClassName»> {

			@Override
			public String getMessage() {
				return "«getElementOfConstraintAnnotation(pattern, "message")»";
			}

			@Override
			public EObject getLocationObject(«pattern.matchClassName» signature) {
				Object location = signature.get("«getElementOfConstraintAnnotation(pattern, "location")»");
				if(location instanceof EObject){
					return (EObject) location;
				}
				return null;
			}
			
			@Override
			public int getSeverity() {
				return ValidationUtil.getSeverity("«getElementOfConstraintAnnotation(pattern, "severity")»");
			}
		}
	'''
	
}