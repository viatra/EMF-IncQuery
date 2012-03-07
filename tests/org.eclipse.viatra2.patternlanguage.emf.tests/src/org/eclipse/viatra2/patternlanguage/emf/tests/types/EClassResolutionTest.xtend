package org.eclipse.viatra2.patternlanguage.emf.tests.types

import org.eclipse.xtext.junit4.XtextRunner
import org.junit.runner.RunWith
import org.eclipse.xtext.junit4.InjectWith
import org.eclipse.viatra2.patternlanguage.EMFPatternLanguageInjectorProvider
import com.google.inject.Inject
import org.eclipse.xtext.junit4.util.ParseHelper
import org.eclipse.xtext.junit4.validation.ValidationTestHelper
import org.junit.Test
import org.eclipse.viatra2.patternlanguage.eMFPatternLanguage.PatternModel
import org.eclipse.viatra2.patternlanguage.eMFPatternLanguage.EClassifierConstraint
import org.eclipse.viatra2.patternlanguage.eMFPatternLanguage.ClassType
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.PatternLanguagePackage
import static org.junit.Assert.*
import org.eclipse.viatra2.patternlanguage.eMFPatternLanguage.EMFPatternLanguagePackage
import org.eclipse.xtext.diagnostics.Diagnostic
import org.eclipse.emf.ecore.EcorePackage

@RunWith(typeof(XtextRunner))
@InjectWith(typeof(EMFPatternLanguageInjectorProvider))
class EClassResolutionTest {
	@Inject
	ParseHelper parseHelper
	
	@Inject extension ValidationTestHelper
	
	@Test
	def eClassResolutionSuccess() {
		val model = parseHelper.parse('
			import "http://www.eclipse.org/viatra2/patternlanguage/core/PatternLanguage"

			pattern resolutionTest(Name) = {
				Pattern(Name2);
			}
		') as PatternModel
		model.assertNoErrors
		val pattern = model.patterns.get(0)
		val constraint = pattern.bodies.get(0).constraints.get(0) as EClassifierConstraint
		val type = constraint.type as ClassType
		assertEquals(type.classname, PatternLanguagePackage$Literals::PATTERN)		
	}
	
	@Test
	def eClassifierResolutionSuccess() {
		val model = parseHelper.parse('
			import "http://www.eclipse.org/emf/2002/Ecore"

			pattern ECoreNamedElement(Name) = {
				EString(Name);
			}
		') as PatternModel
		model.assertNoErrors
		val pattern = model.patterns.get(0)
		val constraint = pattern.bodies.get(0).constraints.get(0) as EClassifierConstraint
		val type = constraint.type as ClassType
		assertEquals(type.classname, EcorePackage$Literals::ESTRING)		
	}

	@Test
	def eClassResolutionFailed() {
		val model = parseHelper.parse('
			import "http://www.eclipse.org/viatra2/patternlanguage/core/PatternLanguage"

			pattern resolutionTest(Name) = {
				UndefinedType(Name2);
			}
		') as PatternModel
		val pattern = model.patterns.get(0)
		val constraint = pattern.bodies.get(0).constraints.get(0) as EClassifierConstraint
		val type = constraint.type as ClassType
		type.assertError(EMFPatternLanguagePackage$Literals::CLASS_TYPE, 
			Diagnostic::LINKING_DIAGNOSTIC, "reference to EClass")		
	}
	
	@Test
	def eClassResolutionFailedMissingImport() {
		val model = parseHelper.parse('
			pattern resolutionTest(Name) = {
				Pattern(Name2);
			}
		') as PatternModel
		val pattern = model.patterns.get(0)
		val constraint = pattern.bodies.get(0).constraints.get(0) as EClassifierConstraint
		val type = constraint.type as ClassType
		type.assertError(EMFPatternLanguagePackage$Literals::CLASS_TYPE, 
			Diagnostic::LINKING_DIAGNOSTIC, "reference to EClass")		
	}
}