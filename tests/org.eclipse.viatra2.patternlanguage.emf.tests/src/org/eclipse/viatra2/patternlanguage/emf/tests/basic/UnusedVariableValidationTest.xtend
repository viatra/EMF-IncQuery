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
	def testAllOK() {
		val model = parseHelper.parse(
			'import "http://www.eclipse.org/viatra2/patternlanguage/core/PatternLanguage"
			
			pattern helperPattern(p, q) = {
				Pattern(p);
				Pattern(q);
			}
			
			pattern negHelperPattern(n) = {
				Constraint(n);
			}
			
			pattern testPattern(p, q)= {
				Pattern(p);
				Pattern.name(q, r);
				r == "";
			} or {
				find helperPattern(p, q);
				neg find negHelperPattern(p);
			}'
		)
		tester.validate(model).assertOK
	}
	
	@Test
	def testSymbolicParameterNeverReferenced() {
		val model = parseHelper.parse(
			'import "http://www.eclipse.org/viatra2/patternlanguage/core/PatternLanguage"
			
			pattern helperPattern(p, q) = {
				Pattern(p);
				Pattern(q);
			}
			
			pattern negHelperPattern(n) = {
				Constraint(n);
			}
			
			pattern testPattern(p, q)= {
				Pattern.name(q, r);
				r == "";
			} or {
				find helperPattern(p, q);
				neg find negHelperPattern(p);
			}'
		)
		tester.validate(model).assertError(EMFIssueCodes::UNUSED_VARIABLE)
	}
	
	@Test
	def testSymbolicParameterHasNoPositiveReferences() {
		val model = parseHelper.parse(
			'import "http://www.eclipse.org/viatra2/patternlanguage/core/PatternLanguage"
			
			pattern helperPattern(p, q) = {
				Pattern(p);
				Pattern(q);
			}
			
			pattern negHelperPattern(n) = {
				Constraint(n);
			}
			
			pattern testPattern(p, q)= {
				Pattern(p);
				neg Pattern.name(q, r);
				r == "";
			} or {
				find helperPattern(p, q);
				neg find negHelperPattern(p);
			}'
		)
		tester.validate(model).assertError(EMFIssueCodes::UNUSED_VARIABLE)
	}
	
	@Test
	def testLocalVariableHasOnlyOneReference() {
		val model = parseHelper.parse(
			'import "http://www.eclipse.org/viatra2/patternlanguage/core/PatternLanguage"
			
			pattern helperPattern(p, q) = {
				Pattern(p);
				Pattern(q);
			}
			
			pattern negHelperPattern(n) = {
				Constraint(n);
			}
			
			pattern testPattern(p, q)= {
				Pattern(p);
				Pattern.name(q, r);
			} or {
				find helperPattern(p, q);
				neg find negHelperPattern(p);
			}'
		)
		tester.validate(model).assertWarning(EMFIssueCodes::UNUSED_VARIABLE)		
	}
	
	@Test
	def testLocalVariableHasNoPositiveReferences() {
		val model = parseHelper.parse(
			'import "http://www.eclipse.org/viatra2/patternlanguage/core/PatternLanguage"
			
			pattern helperPattern(p, q) = {
				Pattern(p);
				Pattern(q);
			}
			
			pattern negHelperPattern(n) = {
				Constraint(n);
			}
			
			pattern testPattern(p, q)= {
				Pattern(p);
				Pattern.name(q, r);
				r == "";
			} or {
				neg find helperPattern(p, r);
				neg find helperPattern(r, q);
				find negHelperPattern(p);
				find negHelperPattern(q);
			}'
		)
		tester.validate(model).assertError(EMFIssueCodes::UNUSED_VARIABLE)		
	}
}