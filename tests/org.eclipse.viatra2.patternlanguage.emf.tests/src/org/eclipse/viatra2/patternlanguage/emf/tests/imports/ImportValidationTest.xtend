package org.eclipse.viatra2.patternlanguage.emf.tests.imports

import org.eclipse.viatra2.patternlanguage.emf.tests.AbstractValidatorTest
import org.eclipse.viatra2.patternlanguage.EMFPatternLanguageInjectorProvider
import org.eclipse.xtext.junit4.InjectWith
import org.junit.runner.RunWith
import org.eclipse.xtext.junit4.XtextRunner
import org.eclipse.xtext.junit4.util.ParseHelper
import com.google.inject.Inject
import org.eclipse.xtext.junit4.validation.ValidationTestHelper
import org.eclipse.viatra2.patternlanguage.eMFPatternLanguage.PatternModel
import org.junit.Test
import org.eclipse.viatra2.patternlanguage.validation.EMFPatternLanguageJavaValidator
import org.eclipse.xtext.junit4.validation.ValidatorTester
import com.google.inject.Injector
import org.junit.Before
import org.eclipse.viatra2.patternlanguage.validation.EMFIssueCodes

@RunWith(typeof(XtextRunner))
@InjectWith(typeof(EMFPatternLanguageInjectorProvider))
class ImportValidationTest extends AbstractValidatorTest {
	
	@Inject
	ParseHelper parseHelper
	@Inject
	EMFPatternLanguageJavaValidator validator
	@Inject
	Injector injector
	
	ValidatorTester<EMFPatternLanguageJavaValidator> tester
	
	@Inject extension ValidationTestHelper
	
	@Before
	def void initialize() {
		tester = new ValidatorTester(validator, injector)
	}
	
	@Test
	def duplicateImport() {
		val model = parseHelper.parse('
			import "http://www.eclipse.org/viatra2/patternlanguage/core/PatternLanguage"
			import "http://www.eclipse.org/viatra2/patternlanguage/core/PatternLanguage"

			pattern resolutionTest(Name) = {
				Pattern(Name);
			}
		') as PatternModel
		model.assertNoErrors
		tester.validate(model).assertAll(getWarningCode(EMFIssueCodes::DUPLICATE_IMPORT), getWarningCode(EMFIssueCodes::DUPLICATE_IMPORT));
	}
}