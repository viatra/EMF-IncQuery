package org.eclipse.viatra2.patternlanguage.emf.tests.basic

import org.eclipse.viatra2.patternlanguage.EMFPatternLanguageInjectorProvider
import org.eclipse.viatra2.patternlanguage.emf.tests.util.AbstractValidatorTest
import org.eclipse.viatra2.patternlanguage.validation.EMFPatternLanguageJavaValidator
import org.eclipse.xtext.junit4.InjectWith
import org.eclipse.xtext.junit4.XtextRunner
import org.eclipse.xtext.junit4.validation.ValidatorTester
import org.junit.runner.RunWith
import org.junit.Before
import org.junit.Test
import com.google.inject.Inject
import com.google.inject.Injector
import org.eclipse.xtext.junit4.util.ParseHelper
import org.eclipse.viatra2.patternlanguage.validation.EMFIssueCodes
@RunWith(typeof(XtextRunner))
@InjectWith(typeof(EMFPatternLanguageInjectorProvider))
class UnusedVariableValidationTest extends AbstractValidatorTest {
	
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
	def testSymbolicVariableNoReference() {
		val model = parseHelper.parse(
			'import "http://www.eclipse.org/viatra2/patternlanguage/core/PatternLanguage"
			
			pattern testPattern(p) = {
				Pattern(h);
				Pattern.name(h, "");
			}'
		)
		tester.validate(model).assertError(EMFIssueCodes::SYMBOLIC_VARIABLE_NEVER_REFERENCED)
	}
	
	@Test
	def testSymbolicVariableOnePositiveReference() {
		val model = parseHelper.parse(
			'import "http://www.eclipse.org/viatra2/patternlanguage/core/PatternLanguage"

			pattern testPattern(p) = {
				Pattern(p);
			}'
		)
		tester.validate(model).assertOK
	}
	
	@Test
	def testSymbolicVariableOneNegativeReference() {
		val model = parseHelper.parse(
			'import "http://www.eclipse.org/viatra2/patternlanguage/core/PatternLanguage"

			pattern testPattern(p) = {
				neg Pattern.name(p, "");
			}'
		)
		tester.validate(model).assertError(EMFIssueCodes::SYMBOLIC_VARIABLE_NO_POSITIVE_REFERENCE)
	}
	
	@Test
	def testSymbolicVariableOneReadOnlyReference() {
		val model = parseHelper.parse(
			'import "http://www.eclipse.org/viatra2/patternlanguage/core/PatternLanguage"

			pattern helper(p) = {
				Pattern(p);
			}

			pattern testPattern(p) = {
				Pattern(h);
				h == count find helper(p);
			}'
		)
		tester.validate(model).assertError(EMFIssueCodes::SYMBOLIC_VARIABLE_NO_POSITIVE_REFERENCE)
	}
	
	@Test
	def testSymbolicVariableNoPositiveReference() {
		val model = parseHelper.parse(
			'import "http://www.eclipse.org/viatra2/patternlanguage/core/PatternLanguage"

			pattern helper(p) = {
				Pattern(p);
			}

			pattern testPattern(p) = {
				neg Pattern.name(p, "");
				neg find helper(p);
				check(p == 0);
				Pattern(h);
				h == p;
			}'
		)
		tester.validate(model).assertError(EMFIssueCodes::SYMBOLIC_VARIABLE_NO_POSITIVE_REFERENCE)
	}
	
	@Test
	def testSymbolicVariableAllReferences() {
		val model = parseHelper.parse(
			'import "http://www.eclipse.org/viatra2/patternlanguage/core/PatternLanguage"

			pattern helper(p) = {
				Pattern(p);
			}

			pattern testPattern(p) = {
				Pattern(p);
				neg Pattern.name(p, "");
				neg find helper(p);
				check(p == 0);
				Pattern(h);
				h == p;
			}'
		)
		tester.validate(model).assertOK
	}
	
	@Test
	def testLocalVariableOnePositiveReference() {
		val model = parseHelper.parse(
			'import "http://www.eclipse.org/viatra2/patternlanguage/core/PatternLanguage"

			pattern testPattern() = {
				Pattern(p);
			}'
		)
		tester.validate(model).assertWarning(EMFIssueCodes::LOCAL_VARIABLE_REFERENCED_ONCE)
	}
	
	@Test
	def testLocalVariableOneNegativeReference() {
		val model = parseHelper.parse(
			'import "http://www.eclipse.org/viatra2/patternlanguage/core/PatternLanguage"

			pattern testPattern() = {
				neg Pattern.name(p, "");
			}'
		)
		tester.validate(model).assertOK
	}
	
	@Test
	def testLocalVariableOneReadOnlyReference() {
		val model = parseHelper.parse(
			'import "http://www.eclipse.org/viatra2/patternlanguage/core/PatternLanguage"

			pattern helper(p) = {
				Pattern(p);
			}

			pattern testPattern() = {
				Pattern(h);
				h == count find helper(p);
			}'
		)
		tester.validate(model).assertError(EMFIssueCodes::LOCAL_VARIABLE_NO_QUANTIFYING_REFERENCE)
	}
	
	@Test
	def testLocalVariableMultiplePositiveReferences() {
		val model = parseHelper.parse(
			'import "http://www.eclipse.org/viatra2/patternlanguage/core/PatternLanguage"

			pattern testPattern() = {
				Pattern(p);
				Pattern.name(p, "");
			}'
		)
		tester.validate(model).assertOK
	}
	
	@Test
	def testLocalVariableOnePositiveOneNegativeReference() {
		val model = parseHelper.parse(
			'import "http://www.eclipse.org/viatra2/patternlanguage/core/PatternLanguage"

			pattern testPattern() = {
				Pattern(p);
				neg Pattern.name(p, "");
			}'
		)
		tester.validate(model).assertOK
	}
	
	@Test
	def testLocalVariableOnePositiveOneReadOnlyReference() {
		val model = parseHelper.parse(
			'import "http://www.eclipse.org/viatra2/patternlanguage/core/PatternLanguage"

			pattern helper(p) = {
				Pattern(p);
			}

			pattern testPattern() = {
				Pattern(p);
				Pattern(h);
				h == count find helper(p);
			}'
		)
		tester.validate(model).assertOK
	}
	
	@Test
	def testLocalVariableMultipleNegativeReferences() {
		val model = parseHelper.parse(
			'import "http://www.eclipse.org/viatra2/patternlanguage/core/PatternLanguage"

			pattern helper(p) = {
				Pattern(p);
			}

			pattern testPattern() = {
				neg Pattern.name(p, "");
				neg find helper(p);
			}'
		)
		tester.validate(model).assertError(EMFIssueCodes::LOCAL_VARIABLE_NO_POSITIVE_REFERENCE)
	}
	
	@Test
	def testLocalVariableOneNegativeOneReadOnlyReference() {
		val model = parseHelper.parse(
			'import "http://www.eclipse.org/viatra2/patternlanguage/core/PatternLanguage"

			pattern helper(p) = {
				Pattern(p);
			}

			pattern testPattern() = {
				neg Pattern.name(p, "");
				Pattern(h);
				h == count find helper(p);
			}'
		)
		tester.validate(model).assertError(EMFIssueCodes::LOCAL_VARIABLE_NO_POSITIVE_REFERENCE)
	}
	
	@Test
	def testLocalVariableMultipleReadOnlyReferences() {
		val model = parseHelper.parse(
			'import "http://www.eclipse.org/viatra2/patternlanguage/core/PatternLanguage"

			pattern helper(p) = {
				Pattern(p);
			}

			pattern testPattern() = {
				Pattern(h);
				Pattern(i);
				h == count find helper(p);
				i == count find helper(p);
			}'
		)
		tester.validate(model).assertError(EMFIssueCodes::LOCAL_VARIABLE_NO_QUANTIFYING_REFERENCE)
	}
}