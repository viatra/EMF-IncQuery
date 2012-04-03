package org.eclipse.viatra2.emf.incquery.tooling.generator

import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Pattern
import static extension org.eclipse.viatra2.patternlanguage.core.helper.CorePatternLanguageHelper.*
import org.eclipse.xtext.xbase.jvmmodel.IJvmModelAssociations
import com.google.inject.Inject
import org.eclipse.viatra2.emf.incquery.runtime.api.IMatcherFactory
import org.eclipse.xtext.common.types.JvmIdentifiableElement
import org.eclipse.xtext.common.types.JvmType
import org.eclipse.viatra2.emf.incquery.tooling.generator.util.EMFPatternLanguageJvmModelInferrerUtil

class GenerateMatcherFactoryExtension {
	
	@Inject
	IJvmModelAssociations associations
	@Inject extension EMFPatternLanguageJvmModelInferrerUtil 
	
	def extensionContribution(Pattern pattern, ExtensionGenerator exGen) {
		newArrayList(
		exGen.contribExtension(pattern.getFullyQualifiedName, "org.eclipse.viatra2.emf.incquery.runtime.patternmatcher") [
			exGen.contribElement(it, "matcher") [
				exGen.contribAttribute(it, "id", pattern.getFullyQualifiedName)
				val el = associations.getJvmElements(pattern).
				  findFirst[it instanceof JvmType && (it as JvmType).simpleName.equals(pattern.matcherFactoryClassName)] as JvmIdentifiableElement
				exGen.contribAttribute(it, "factory", el.qualifiedName)
			]
		]
		)
	}
}