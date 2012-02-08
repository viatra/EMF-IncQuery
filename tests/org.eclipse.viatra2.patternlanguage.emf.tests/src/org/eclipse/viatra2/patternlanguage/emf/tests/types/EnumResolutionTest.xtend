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
import org.eclipse.viatra2.patternlanguage.eMFPatternLanguage.EClassConstraint
import org.eclipse.viatra2.patternlanguage.eMFPatternLanguage.ClassType
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.PatternLanguagePackage
import static org.junit.Assert.*
import org.eclipse.viatra2.patternlanguage.eMFPatternLanguage.EMFPatternLanguagePackage
import org.eclipse.xtext.diagnostics.Diagnostic
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.PathExpressionConstraint
import org.eclipse.viatra2.patternlanguage.eMFPatternLanguage.ReferenceType
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.BOOLEAN
import org.eclipse.viatra2.patternlanguage.eMFPatternLanguage.EnumValue

@RunWith(typeof(XtextRunner))
@InjectWith(typeof(EMFPatternLanguageInjectorProvider))
class EnumResolutionTest {
	@Inject
	ParseHelper parseHelper
	
	@Inject extension ValidationTestHelper
	
	@Test
	def eEnumResolutionSuccess() {
		val model = parseHelper.parse('
			import "http://www.eclipse.org/viatra2/patternlanguage/core/PatternLanguage"

			pattern resolutionTest(Name) = {
				BoolValue(Name);
				BoolValue.value(Name, ::TRUE);
			}
		') as PatternModel
		model.assertNoErrors
		val pattern = model.patterns.get(0)
		val constraint = pattern.bodies.get(0).constraints.get(1) as PathExpressionConstraint
		val tail = constraint.head.tail
		val type = tail.type as ReferenceType
		assertEquals(type.refname.EType, PatternLanguagePackage$Literals::BOOLEAN)
		val value = constraint.head.dst as EnumValue
		assertEquals(value.literal, PatternLanguagePackage$Literals::BOOLEAN.getEEnumLiteral("TRUE"))		
	}
	
	@Test
	def eEnumResolutionInvalidLiteral() {
		val model = parseHelper.parse('
			import "http://www.eclipse.org/viatra2/patternlanguage/core/PatternLanguage"

			pattern resolutionTest(Name) = {
				BoolValue(Name);
				BoolValue.value(Name, ::NOTEXIST);
			}
		') as PatternModel
		model.assertError(EMFPatternLanguagePackage$Literals::ENUM_VALUE,
			Diagnostic::LINKING_DIAGNOSTIC, "reference to EEnumLiteral")
	}
	@Test
	def eEnumResolutionNotEnum() {
		val model = parseHelper.parse('
			import "http://www.eclipse.org/viatra2/patternlanguage/core/PatternLanguage"

			pattern resolutionTest(Name) = {
				Pattern(Name);
				Pattern.name(Name, ::TRUE);
			}
		') as PatternModel
		//XXX With better type inference this error message should be replaced
		model.assertError(EMFPatternLanguagePackage$Literals::ENUM_VALUE,
			Diagnostic::LINKING_DIAGNOSTIC, "reference to EEnumLiteral")
	}
	
	
}