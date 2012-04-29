package org.eclipse.viatra2.patternlanguage.emf.tests.values

import org.eclipse.viatra2.patternlanguage.EMFPatternLanguageInjectorProvider
import org.eclipse.xtext.junit4.XtextRunner
import org.junit.runner.RunWith
import org.eclipse.xtext.junit4.InjectWith
import com.google.inject.Inject
import org.eclipse.xtext.junit4.util.ParseHelper
import org.eclipse.xtext.junit4.validation.ValidationTestHelper
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.PatternLanguagePackage
import org.eclipse.xtext.diagnostics.Diagnostic
import org.junit.Test

@RunWith(typeof(XtextRunner))
@InjectWith(typeof(EMFPatternLanguageInjectorProvider))
class AggregationTest {
	@Inject
	ParseHelper parseHelper
	
	@Inject extension ValidationTestHelper
	
	@Test
	def void testCountNothingPassed() {
		parseHelper.parse(
			'import "http://www.eclipse.org/viatra2/patternlanguage/core/PatternLanguage"

			pattern calledPattern(p : Pattern, v: Variable) = {
				Pattern(p);
				Variable(v);
			}

			pattern callerPattern(output) = {
				output == aggregate count find calledPattern(anyp, anyv);
			}'
		).assertNoErrors
	}
	
	@Test
	def void testCountSomeStuffPassed() {
		parseHelper.parse(
			'import "http://www.eclipse.org/viatra2/patternlanguage/core/PatternLanguage"

			pattern calledPattern(p : Pattern, v: Variable) = {
				Pattern(p);
				Variable(v);
			}

			pattern callerPattern(p : Pattern, output) = {
				Pattern(p);
				output == aggregate count find calledPattern(p, anyv);
			}'
		).assertNoErrors
	}
	
	@Test
	def void testCountSomeStuffPassedNoReturn() {
		parseHelper.parse(
			'import "http://www.eclipse.org/viatra2/patternlanguage/core/PatternLanguage"

			pattern calledPattern(p : Pattern, v: Variable) = {
				Pattern(p);
				Variable(v);
			}

			pattern callerPattern(p : Pattern) = {
				Pattern(p);
				3 == aggregate count find calledPattern(p, anyv);
			}'
		).assertNoErrors
	}
	
	@Test
	def void testCountAllPassed() {
		parseHelper.parse(
			'import "http://www.eclipse.org/viatra2/patternlanguage/core/PatternLanguage"

			pattern calledPattern(p : Pattern, v: Variable) = {
				Pattern(p);
				Variable(v);
			}

			pattern callerPattern(p : Pattern, output) = {
				Pattern(p);
				Variable(v);
				output == aggregate count find calledPattern(p, v);
			}'
		).assertNoErrors			
	}
	
	@Test
	def void testMissingComposition() {
		var parsed = parseHelper.parse(
			'
			import "http://www.eclipse.org/viatra2/patternlanguage/core/PatternLanguage"

			pattern callerPattern(p : Pattern, output) = {
				Pattern(p);
				output == aggregate count find calledPatternMissing(p, anyv);
			}'
		);
		parsed.assertError(PatternLanguagePackage::eINSTANCE.patternCall, 
			Diagnostic::LINKING_DIAGNOSTIC, 
			"Couldn't resolve reference to Pattern 'calledPatternMissing'."
		)

	}
		
}