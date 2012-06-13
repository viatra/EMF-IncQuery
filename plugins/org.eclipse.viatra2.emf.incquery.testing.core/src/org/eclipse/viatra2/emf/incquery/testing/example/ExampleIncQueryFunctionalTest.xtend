package org.eclipse.viatra2.emf.incquery.testing.example

import com.google.inject.Inject
import org.eclipse.viatra2.emf.incquery.snapshot.EIQSnapshot.EIQSnapshotFactory
import org.eclipse.viatra2.emf.incquery.testing.core.TestExecutor
import org.eclipse.viatra2.patternlanguage.eMFPatternLanguage.PatternModel
import org.eclipse.xtext.junit4.XtextRunner
import org.eclipse.xtext.junit4.util.ParseHelper
import org.junit.Test
import org.junit.runner.RunWith

import static org.junit.Assert.*
import org.eclipse.viatra2.patternlanguage.EMFPatternLanguageInjectorProvider
import org.eclipse.xtext.junit4.InjectWith
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.PatternLanguageFactory

@RunWith(typeof(XtextRunner))
@InjectWith(typeof(EMFPatternLanguageInjectorProvider))
class ExampleIncQueryFunctionalTest {
	
	@Inject extension TestExecutor
	
	@Inject
	ParseHelper parseHelper
	
	@Test
	def basicTest(){
		val model = parseHelper.parse('
			import "http://www.eclipse.org/viatra2/patternlanguage/core/PatternLanguage"

			pattern resolutionTest(Name) = {
				Pattern(Name);
			}
		') as PatternModel
		val matcher = model.initializeMatcherFromModel(model, "resolutionTest");
		val snapshot = EIQSnapshotFactory::eINSTANCE.createIncQuerySnapshot
		val expected = matcher.saveMatchesToSnapshot(snapshot)
		val results = matcher.compareResultSets(expected)
		assertArrayEquals(newHashSet(TestExecutor::CORRECTRESULTS),results)
	}
	
	@Test
	def shortTest(){
		val patternModel = parseHelper.parse('
			import "http://www.eclipse.org/viatra2/patternlanguage/core/PatternLanguage"

			pattern resolutionTest(Name) = {
				Pattern(Name);
			}
		') as PatternModel
		
		/* Preparation not really part of a real test case */
		// NOTE in actual tests: val snapshot = loadModelFromUri("uri/to/test/expected")
		val matcher = patternModel.initializeMatcherFromModel(patternModel, "resolutionTest");
		val snapshot = EIQSnapshotFactory::eINSTANCE.createIncQuerySnapshot
		val expected = matcher.saveMatchesToSnapshot(snapshot)
		expected.modelRoot = patternModel
		/* Preparation over */

		/* Real test */
		// val snapshot = loadModelFromUri("uri/to/test/expected")
		patternModel.assertMatchResults(snapshot)
		return
	}
	
}