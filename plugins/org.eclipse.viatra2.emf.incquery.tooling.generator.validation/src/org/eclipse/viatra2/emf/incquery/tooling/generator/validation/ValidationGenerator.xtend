package org.eclipse.viatra2.emf.incquery.tooling.generator.validation

import com.google.inject.Inject
import org.eclipse.viatra2.emf.incquery.tooling.generator.ExtensionGenerator
import org.eclipse.viatra2.emf.incquery.tooling.generator.fragments.IGenerationFragment
import org.eclipse.viatra2.emf.incquery.tooling.generator.util.EMFPatternLanguageJvmModelInferrerUtil
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Pattern
import org.eclipse.xtext.generator.IFileSystemAccess

import static extension org.eclipse.viatra2.patternlanguage.core.helper.CorePatternLanguageHelper.*

class ValidationGenerator implements IGenerationFragment {
	
	@Inject extension EMFPatternLanguageJvmModelInferrerUtil
	
	override generateFiles(Pattern pattern, IFileSystemAccess fsa) {
		fsa.generateFile(pattern.packagePath + "/validation/" + pattern.name.toFirstUpper + "Constraint.java", pattern.patternHandler)
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
		newArrayList(
			exGen.contribExtension("", "org.eclipse.viatra2.emf.incquery.validation.runtime") [
				exGen.contribElement(it, "constraint") [
					exGen.contribAttribute(it, "class", pattern.packagePath+".validation."+pattern.name.toFirstUpper+"Constraint")
					exGen.contribAttribute(it, "name", pattern.fullyQualifiedName)
				]
			]
		)
	}

	
	def patternHandler(Pattern pattern) '''
		package «pattern.fullyQualifiedName».validation;
		
		import org.eclipse.emf.ecore.EObject;

		import org.eclipse.viatra2.emf.incquery.validation.runtime.Constraint;

		import «pattern.packageName + "." + pattern.matchClassName»;

		public class «pattern.name.toFirstUpper»Constraint extends Constraint<«pattern.matchClassName»> {
		
			public «pattern.name.toFirstUpper»Constraint() {
				
			}

			@Override
			public String getMessage(BookMatch signature) {
				return null;
			}

			@Override
			public EObject getLocationObject(BookMatch signature) {
				return null;
			}
			
		}
	'''
	
}