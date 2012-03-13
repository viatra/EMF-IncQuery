package org.eclipse.viatra2.emf.incquery.tooling.generator.databinding

import com.google.inject.Inject
import org.eclipse.viatra2.emf.incquery.tooling.generator.ExtensionGenerator
import org.eclipse.viatra2.emf.incquery.tooling.generator.util.EMFPatternLanguageJvmModelInferrerUtil
import org.eclipse.viatra2.emf.incquery.tooling.generator.fragments.IGenerationFragment
import org.eclipse.xtext.generator.IFileSystemAccess
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Pattern
import static extension org.eclipse.viatra2.patternlanguage.core.helper.CorePatternLanguageHelper.*

class DatabindingGenerator implements IGenerationFragment {
	
	@Inject extension EMFPatternLanguageJvmModelInferrerUtil

	override generateFiles(Pattern pattern, IFileSystemAccess fsa) {
		fsa.generateFile(pattern.packagePath + "/databinding/" + pattern.name + "DatabindingAdapter.java", pattern.patternHandler)
	}
	
	override getProjectDependencies() {
		newArrayList("org.eclipse.core.runtime", "org.eclipse.ui", "org.eclipse.emf.ecore", "org.eclipse.pde.core", "org.eclipse.core.resources", "org.eclipse.viatra2.emf.incquery.runtime")
	}
	
	override getProjectPostfix() {
		"databinding"
	}
	
	override extensionContribution(Pattern pattern, ExtensionGenerator exGen) {
		newArrayList(
		exGen.contribExtension(pattern.getFullyQualifiedName + "Command", "org.eclipse.ui.commands") [
			exGen.contribElement(it, "command") [
				exGen.contribAttribute(it, "commandId", pattern.getFullyQualifiedName + "CommandId")
				exGen.contribAttribute(it, "style", "push")
			]
		]
		)
	}
	
	def patternHandler(Pattern pattern) '''
		
	'''

}