package org.eclipse.viatra2.emf.incquery.tooling.generator.validation

import com.google.inject.Inject
import org.eclipse.viatra2.emf.incquery.tooling.generator.ExtensionGenerator
import org.eclipse.viatra2.emf.incquery.tooling.generator.fragments.IGenerationFragment
import org.eclipse.viatra2.emf.incquery.tooling.generator.util.EMFPatternLanguageJvmModelInferrerUtil
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Pattern
import org.eclipse.xtext.generator.IFileSystemAccess

import static extension org.eclipse.viatra2.patternlanguage.core.helper.CorePatternLanguageHelper.*
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.impl.StringValueImpl
import org.eclipse.viatra2.emf.incquery.tooling.generator.databinding.DatabindingGenerator

class ValidationGenerator extends DatabindingGenerator implements IGenerationFragment {
	
	@Inject extension EMFPatternLanguageJvmModelInferrerUtil
	
	private static String annotationLiteral = "Constraint"
	
	override generateFiles(Pattern pattern, IFileSystemAccess fsa) {
		if (hasAnnotationLiteral(pattern, annotationLiteral)) {
			fsa.generateFile(pattern.packagePath + "/validation/" + pattern.name.toFirstUpper + annotationLiteral + ".java", pattern.patternHandler)
		}
	}
	
	override cleanUp(Pattern pattern, IFileSystemAccess fsa) {
		fsa.deleteFile(pattern.packagePath + "/validation/" + pattern.realPatternName.toFirstUpper + annotationLiteral + ".java")
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
		if (hasAnnotationLiteral(pattern, annotationLiteral)) {		
			return newArrayList(
				exGen.contribExtension("", "org.eclipse.viatra2.emf.incquery.validation.runtime.constraint") [
					exGen.contribElement(it, "constraint") [
						exGen.contribAttribute(it, "class", pattern.packagePath+".validation."+pattern.name.toFirstUpper+annotationLiteral)
						exGen.contribAttribute(it, "name", pattern.fullyQualifiedName)
					]
				]
			)
		}
		else {
			return newArrayList()
		}
	}
		
	def getElementOfConstraintAnnotation(Pattern pattern, String elementName) {
		for (a : pattern.annotations) {
			if (a.name.matches(annotationLiteral)) {
				for (ap : a.parameters) {
					if (ap.name.matches(elementName)) {
						return (ap.value as StringValueImpl).value
					}
				}
			}
		}
		return null
	}
	
	override patternHandler(Pattern pattern) '''
		package «pattern.fullyQualifiedName».validation;
		
		import org.eclipse.emf.ecore.EObject;

		import org.eclipse.viatra2.emf.incquery.validation.runtime.Constraint;
		import org.eclipse.viatra2.emf.incquery.validation.runtime.ValidationUtil;
		import org.eclipse.viatra2.emf.incquery.runtime.api.impl.BaseGeneratedMatcherFactory;
		import «pattern.packageName + "." + pattern.matchClassName»;
		import «pattern.packageName + "." + pattern.matcherFactoryClassName»;
		import «pattern.packageName + "." + pattern.matcherClassName»;

		public class «pattern.name.toFirstUpper»«annotationLiteral» extends «annotationLiteral»<«pattern.matchClassName»> {

			private «pattern.matcherFactoryClassName» matcherFactory;

			public «pattern.name.toFirstUpper»Constraint() {
				matcherFactory = new «pattern.matcherFactoryClassName»();
			}

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
			
			@Override
			public BaseGeneratedMatcherFactory<«pattern.matchClassName», «pattern.matcherClassName»> getMatcherFactory() {
				return matcherFactory;
			}
		}
	'''
	
}