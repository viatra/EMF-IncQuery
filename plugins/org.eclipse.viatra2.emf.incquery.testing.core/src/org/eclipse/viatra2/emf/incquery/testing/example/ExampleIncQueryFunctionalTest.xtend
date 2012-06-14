/*******************************************************************************
 * Copyright (c) 2010-2012, Abel Hegedus, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Abel Hegedus - initial API and implementation
 *******************************************************************************/

package org.eclipse.viatra2.emf.incquery.testing.example

import com.google.inject.Inject
import org.eclipse.viatra2.emf.incquery.snapshot.EIQSnapshot.EIQSnapshotFactory
import org.eclipse.viatra2.emf.incquery.testing.core.ModelLoadHelper
import org.eclipse.viatra2.emf.incquery.testing.core.SnapshotHelper
import org.eclipse.viatra2.emf.incquery.testing.core.TestExecutor
import org.eclipse.viatra2.patternlanguage.EMFPatternLanguageInjectorProvider
import org.eclipse.viatra2.patternlanguage.eMFPatternLanguage.PatternModel
import org.eclipse.xtext.junit4.InjectWith
import org.eclipse.xtext.junit4.XtextRunner
import org.eclipse.xtext.junit4.util.ParseHelper
import org.junit.Test
import org.junit.runner.RunWith

import static org.junit.Assert.*

@RunWith(typeof(XtextRunner))
@InjectWith(typeof(EMFPatternLanguageInjectorProvider))
class ExampleIncQueryFunctionalTest {
	
	@Inject extension TestExecutor
	@Inject extension ModelLoadHelper
	@Inject extension SnapshotHelper
	
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
		return snapshot
		/* Preparation over */
	}
	
	@Test
	def basicTest(){
		val model = testInput
		val matcher = model.initializeMatcherFromModel(model, "resolutionTest");
		val snapshot = EIQSnapshotFactory::eINSTANCE.createIncQuerySnapshot
		val expected = matcher.saveMatchesToSnapshot(snapshot)
		val results = matcher.compareResultSets(expected)
		assertNull(results)
		
	}
	
	@Test
	def shortTest(){
		val patternModel = testInput
		val snapshot = patternModel.prepareSelfTest
		// patternModel.assertMatchResults("uri/to/test/expected")
		patternModel.assertMatchResults(snapshot)
	}
	
}