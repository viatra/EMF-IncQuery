package org.eclipse.viatra2.patternlanguage.emf.tests.basic

import org.eclipse.xtext.junit4.XtextRunner
import org.junit.runner.RunWith
import org.eclipse.xtext.junit4.InjectWith
import org.eclipse.viatra2.patternlanguage.EMFPatternLanguageInjectorProvider
import com.google.inject.Inject
import org.eclipse.xtext.junit4.util.ParseHelper
import org.junit.Test
import org.eclipse.viatra2.patternlanguage.eMFPatternLanguage.PatternModel
import static org.junit.Assert.*
import org.eclipse.xtext.junit4.validation.ValidatorTester
import org.eclipse.viatra2.patternlanguage.validation.EMFPatternLanguageJavaValidator
import org.eclipse.viatra2.patternlanguage.core.validation.IssueCodes
import com.google.inject.Injector
import org.junit.Before
import org.eclipse.viatra2.patternlanguage.emf.tests.util.AbstractValidatorTest

@RunWith(typeof(XtextRunner))
@InjectWith(typeof(EMFPatternLanguageInjectorProvider))
class PatternValidationTest extends AbstractValidatorTest {
	@Inject
	ParseHelper parseHelper
	@Inject
	EMFPatternLanguageJavaValidator validator
	@Inject
	Injector injector
	
	ValidatorTester<EMFPatternLanguageJavaValidator> tester
	
	@Before
	def void initialize() {
		tester = new ValidatorTester(validator, injector)
	}
	@Test
	def emptyBodyValidation() {
		val model = parseHelper.parse('pattern resolutionTest(Name) = {}') as PatternModel
		tester.validate(model).assertError(IssueCodes::PATTERN_BODY_EMPTY)
	}
	
}