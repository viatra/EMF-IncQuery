package org.eclipse.viatra2.emf.incquery.testing.example

import com.google.inject.Inject
import org.eclipse.viatra2.emf.incquery.snapshot.EIQSnapshot.EIQSnapshotFactory
import org.eclipse.viatra2.emf.incquery.testing.core.TestExecutor
import org.eclipse.viatra2.patternlanguage.EMFPatternLanguageInjectorProvider
import org.eclipse.viatra2.patternlanguage.eMFPatternLanguage.PatternModel
import org.eclipse.xtext.junit4.InjectWith
import org.eclipse.xtext.junit4.XtextRunner
import org.eclipse.xtext.junit4.util.ParseHelper
import org.junit.Test
import org.junit.runner.RunWith

import static org.junit.Assert.*
import org.eclipse.viatra2.emf.incquery.snapshot.EIQSnapshot.IncQuerySnapshot

@RunWith(typeof(XtextRunner))
@InjectWith(typeof(EMFPatternLanguageInjectorProvider))
class ExampleIncQueryFunctionalTest {
	
	@Inject extension TestExecutor
	
	@Inject
	ParseHelper parseHelper
	
	def testInput(){
		parseHelper.parse('
			import "http://www.eclipse.org/viatra2/patternlanguage/core/PatternLanguage"

			pattern resolutionTest(Name) = {
				Pattern(Name);
			}
		') as PatternModel
	}
	
	def prepareSelfTest(PatternModel patternModel){
		/* Preparation not really part of a real test case */
		// NOTE in actual tests: val snapshot = loadModelFromUri("uri/to/test/expected")
		val matcher = patternModel.initializeMatcherFromModel(patternModel, "resolutionTest");
		val snapshot = EIQSnapshotFactory::eINSTANCE.createIncQuerySnapshot
		matcher.saveMatchesToSnapshot(snapshot)
		/* Preparation over */
	}
	
	@Test
	def basicTest(){
		val model = testInput
		val matcher = model.initializeMatcherFromModel(model, "resolutionTest");
		val snapshot = EIQSnapshotFactory::eINSTANCE.createIncQuerySnapshot
		val expected = matcher.saveMatchesToSnapshot(snapshot)
		val results = matcher.compareResultSets(expected)
		assertArrayEquals(newHashSet,results)
	}
	
	@Test
	def shortTest(){
		val patternModel = testInput
		val expected = patternModel.prepareSelfTest
		// patternModel.assertMatchResults("uri/to/test/expected")
		patternModel.assertMatchResults(expected.eContainer as IncQuerySnapshot)
	}
	
}