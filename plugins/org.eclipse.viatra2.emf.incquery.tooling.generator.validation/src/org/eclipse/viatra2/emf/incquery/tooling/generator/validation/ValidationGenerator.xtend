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
import org.eclipse.xtext.xbase.lib.Pair
import org.eclipse.viatra2.patternlanguage.core.helper.CorePatternLanguageHelper
import java.util.Set

class ValidationGenerator extends DatabindingGenerator implements IGenerationFragment {
	
	@Inject extension EMFPatternLanguageJvmModelInferrerUtil
	private static String VALIDATIONEXTENSION_PREFIX = "validation.constraint."
	private static String UI_VALIDATION_MENUS_PREFIX = "generated.incquery.validation.menu."
	private static String VALIDATION_EXTENSION_POINT = "org.eclipse.viatra2.emf.incquery.validation.runtime.constraint"
	private static String ECLIPSE_MENUS_EXTENSION_POINT = "org.eclipse.ui.menus"
	private static String annotationLiteral = "Constraint"
	private Set<String> contributedEditorIds = newHashSet(); 
	
	override generateFiles(Pattern pattern, IFileSystemAccess fsa) {
		if (hasAnnotationLiteral(pattern, annotationLiteral)) {
			fsa.generateFile(pattern.constraintClassJavaFile, pattern.patternHandler)
		}
	}
	
	override cleanUp(Pattern pattern, IFileSystemAccess fsa) {
		if (!contributedEditorIds.empty) {
			contributedEditorIds.clear
		}
		fsa.deleteFile(pattern.constraintClassJavaFile)
	}
	
	override removeExtension(Pattern pattern) {
		val p = Pair::of(pattern.constraintContributionId, VALIDATION_EXTENSION_POINT)
		val extensionList = newArrayList(p)
		val editorId = pattern.getElementOfConstraintAnnotation("targetEditorId")
		if (!editorId.nullOrEmpty) {
			extensionList.add(Pair::of(menuContributionId(editorId), ECLIPSE_MENUS_EXTENSION_POINT))
		}
		return extensionList
	}
	
	override getRemovableExtensions() {
		if (!contributedEditorIds.empty) {
			contributedEditorIds.clear
		}
		newArrayList(
			Pair::of(VALIDATIONEXTENSION_PREFIX, VALIDATION_EXTENSION_POINT), 
			Pair::of(UI_VALIDATION_MENUS_PREFIX, ECLIPSE_MENUS_EXTENSION_POINT)
		)
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
			val extensionList = newArrayList(
				exGen.contribExtension(pattern.constraintContributionId, VALIDATION_EXTENSION_POINT) [
					exGen.contribElement(it, "constraint") [
						exGen.contribAttribute(it, "class", pattern.constraintClassName)
						exGen.contribAttribute(it, "name", pattern.fullyQualifiedName)
					]
				]
			)
			val editorId = pattern.getElementOfConstraintAnnotation("targetEditorId")
			if (!editorId.nullOrEmpty && !contributedEditorIds.contains(editorId)) {
				val editorMenuContribution = exGen.contribExtension(menuContributionId(editorId), ECLIPSE_MENUS_EXTENSION_POINT) [
					exGen.contribElement(it, "menuContribution") [
						exGen.contribAttribute(it, "locationURI", String::format("popup:%s", editorId))
						exGen.contribElement(it, "menu") [
							exGen.contribAttribute(it, "label", "EMF-IncQuery")
							exGen.contribElement(it, "command") [
								exGen.contribAttribute(it, "commandId", "org.eclipse.viatra2.emf.incquery.validation.runtime.ui.initValidators")
								exGen.contribAttribute(it, "style", "push")
								exGen.contribAttribute(it, "label", "Initialize EMF-IncQuery Validators")
								exGen.contribElement(it, "visibleWhen") [
									exGen.contribAttribute(it, "checkEnabled", "false")
									exGen.contribElement(it, "reference") [
										exGen.contribAttribute(it, "definitionId", "org.eclipse.viatra2.emf.incquery.validation.runtime.ui.notifierdef")	
									]
								]
							]
						]
					]	
				]
				extensionList.add(editorMenuContribution)
				contributedEditorIds.add(editorId)
			}
			return extensionList
		} else {
			return newArrayList()
		}
	}
	
	def constraintClassName(Pattern pattern) {
		String::format("%s.%s%s", pattern.packageName, pattern.realPatternName.toFirstUpper, annotationLiteral)
	}
	
	def constraintClassPath(Pattern pattern) {
		String::format("%s/%s%s", pattern.packagePath, pattern.realPatternName.toFirstUpper, annotationLiteral)
	}
	
	def constraintClassJavaFile(Pattern pattern) {
		pattern.constraintClassPath + ".java"
	}
	
	def constraintContributionId(Pattern pattern) {
		return VALIDATIONEXTENSION_PREFIX+CorePatternLanguageHelper::getFullyQualifiedName(pattern)
	}
	
	def menuContributionId(String editorId) {
		return String::format("%s%s", UI_VALIDATION_MENUS_PREFIX, editorId)
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
		package «pattern.packageName»;
		
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