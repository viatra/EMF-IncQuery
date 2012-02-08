package org.eclipse.viatra2.patternlanguage.emf.tests.resolution

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
import static org.junit.Assert.*

@RunWith(typeof(XtextRunner))
@InjectWith(typeof(EMFPatternLanguageInjectorProvider))
class VariableResolutionTest {
	@Inject
	ParseHelper parseHelper
	
	@Inject extension ValidationTestHelper
	
	@Test
	def parameterResolution() {
		val model = parseHelper.parse('
			import "http://www.eclipse.org/viatra2/patternlanguage/core/PatternLanguage"

			pattern resolutionTest(Name) = {
				Pattern(Name);
			}
		') as PatternModel
		model.assertNoErrors
		val pattern = model.patterns.get(0)
		val parameter = pattern.parameters.get(0)
		val constraint = pattern.bodies.get(0).constraints.get(0) as EClassConstraint
		assertEquals(parameter.name, constraint.getVar().getVar())
	}
	
	@Test
	def parameterResolutionFailed() {
		val model = parseHelper.parse('
			import "http://www.eclipse.org/viatra2/patternlanguage/core/PatternLanguage"

			pattern resolutionTest(Name) = {
				Pattern(Name2);
			}
		') as PatternModel
		model.assertNoErrors
		val pattern = model.patterns.get(0)
		val parameter = pattern.parameters.get(0)
		val constraint = pattern.bodies.get(0).constraints.get(0) as EClassConstraint
		assertTrue(parameter.name != constraint.getVar().getVar())		
	}
	
	@Test
	def constraintVariableResolution() {
		val model = parseHelper.parse('
			import "http://www.eclipse.org/viatra2/patternlanguage/core/PatternLanguage"

			pattern resolutionTest(Name) = {
				Pattern(Name2);
				Pattern(Name2);
			}
		') as PatternModel
		model.assertNoErrors
		val pattern = model.patterns.get(0)
		val parameter = pattern.parameters.get(0)
		val constraint0 = pattern.bodies.get(0).constraints.get(0) as EClassConstraint
		val constraint1 = pattern.bodies.get(0).constraints.get(0) as EClassConstraint
		assertTrue(parameter.name != constraint0.getVar().getVar())
		assertEquals(constraint0.getVar().getVar(), constraint1.getVar().getVar())				
	}
}