package org.eclipse.viatra2.emf.incquery.tooling.generator

import com.google.inject.Inject
import org.eclipse.viatra2.emf.incquery.runtime.IExtensions
import org.eclipse.viatra2.emf.incquery.tooling.generator.util.EMFPatternLanguageJvmModelInferrerUtil
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Pattern
import org.eclipse.xtext.common.types.JvmIdentifiableElement
import org.eclipse.xtext.common.types.JvmType
import org.eclipse.xtext.xbase.jvmmodel.IJvmModelAssociations
import org.eclipse.xtext.xbase.lib.Pair

import static extension org.eclipse.viatra2.patternlanguage.core.helper.CorePatternLanguageHelper.*

class GenerateMatcherFactoryExtension {
	
	@Inject
	IJvmModelAssociations associations
	@Inject extension EMFPatternLanguageJvmModelInferrerUtil 
	
	def extensionContribution(Pattern pattern, ExtensionGenerator exGen) {
		newArrayList(
		exGen.contribExtension(pattern.getFullyQualifiedName, IExtensions::MATCHERFACTORY_EXTENSION_POINT_ID) [
			exGen.contribElement(it, "matcher") [
				exGen.contribAttribute(it, "id", pattern.getFullyQualifiedName)
				val el = associations.getJvmElements(pattern).
				  findFirst[it instanceof JvmType && (it as JvmType).simpleName.equals(pattern.matcherFactoryClassName)] as JvmIdentifiableElement
				exGen.contribAttribute(it, "factory", el.qualifiedName)
			]
		]
		)
	}
	
	def static getRemovableExtensionIdentifiers() {
		newArrayList(
			Pair::of("", IExtensions::MATCHERFACTORY_EXTENSION_POINT_ID)
		)
	}
}