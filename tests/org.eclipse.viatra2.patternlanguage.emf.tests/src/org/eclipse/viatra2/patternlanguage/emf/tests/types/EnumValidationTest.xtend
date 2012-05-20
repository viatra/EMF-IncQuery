package org.eclipse.viatra2.patternlanguage.emf.tests.types

import org.eclipse.viatra2.patternlanguage.emf.tests.util.AbstractValidatorTest
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
class EnumValidationTest extends AbstractValidatorTest {
	
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
	def validateEnum() {
		val model = parseHelper.parse('
			import "http://www.eclipse.org/emf/2002/GenModel"

			pattern resolutionTest(Model) = {
				GenModel(Model);
				GenModel.runtimeVersion(Model, ::EMF23);
			}
		') as PatternModel
		model.assertNoErrors
		tester.validate(model).assertOK
	}
	@Test
	def validateQualifiedEnum() {
		val model = parseHelper.parse('
			import "http://www.eclipse.org/emf/2002/GenModel"

			pattern resolutionTest(Model) = {
				GenModel(Model);
				GenModel.runtimeVersion(Model, GenRuntimeVersion::EMF23);
			}
		') as PatternModel
		model.assertNoErrors
		tester.validate(model).assertOK
	}
	@Test
	def validateQualifiedEnumWithEquality() {
		val model = parseHelper.parse('
			import "http://www.eclipse.org/emf/2002/GenModel"

			pattern resolutionTest(Model) = {
				GenModel(Model);
				GenModel.runtimeVersion(Model, Version);
				Version == GenRuntimeVersion::EMF23;
			}
		') as PatternModel
		model.assertNoErrors
		tester.validate(model).assertOK
	}

	@Test
	def validateIncorrectEnum() {
		val model = parseHelper.parse('
			import "http://www.eclipse.org/emf/2002/GenModel"

			pattern resolutionTest(Model) = {
				GenModel(Model);
				GenModel.runtimeVersion(Model, GenDelegationKind::None);
			}
		') as PatternModel
		tester.validate(model).assertError(EMFIssueCodes::INVALID_ENUM_LITERAL)
	}
	@Test
	def validateEnumConstraintPatternCall() {
		val model = parseHelper.parse('
			import "http://www.eclipse.org/emf/2002/GenModel"


			pattern runtimeVersion(Version) = {
				GenRuntimeVersion(Version);
			}

			pattern call() = {
				find runtimeVersion(GenRuntimeVersion::EMF24);
			}
		') as PatternModel
		tester.validate(model).assertOK
	}
	
}