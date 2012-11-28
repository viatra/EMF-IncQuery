/*******************************************************************************
 * Copyright (c) 2010-2012, Zoltan Ujhelyi, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Zoltan Ujhelyi - initial API and implementation
 *******************************************************************************/

package org.eclipse.incquery.patternlanguage.emf.tests.values

import org.eclipse.incquery.patternlanguage.emf.EMFPatternLanguageInjectorProvider
import org.eclipse.xtext.junit4.XtextRunner
import org.junit.runner.RunWith
import org.eclipse.xtext.junit4.InjectWith
import com.google.inject.Inject
import org.eclipse.xtext.junit4.util.ParseHelper
import org.eclipse.xtext.junit4.validation.ValidationTestHelper
import org.eclipse.incquery.patternlanguage.patternLanguage.PatternLanguagePackage
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
				output == count find calledPattern(anyp, anyv);	// anyp and anyv should be single variables, e.g. _anyp, _anyv
				Pattern(anyp);									// Then these lines...
				Variable(anyv);									// ...can be deleted.
				IntValue.value(h, output);	// h should be a single variable, e.g. _h
				IntValue(h);				// Then this line can be deleted.
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
				output == count find calledPattern(p, anyv);	// anyv should be a single variable, e.g. _anyv
				Variable(anyv);									// Then this line can be deleted.
				IntValue.value(h, output);	// h should be a single variable, e.g. _h
				IntValue(h);				// Then this line can be deleted.
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
				3 == count find calledPattern(p, anyv);	// anyv should be a single variable, e.g. _anyv
				Variable(anyv);							// Then this line can be deleted.
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
				output == count find calledPattern(p, v);
				IntValue.value(h, output);	// h should be a single variable, e.g. _h
				IntValue(h);				// Then this line can be deleted.
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
				output == count find calledPatternMissing(p, anyv);	// anyv should be a single variable, e.g. _anyv
			}'
		);
		parsed.assertError(PatternLanguagePackage::eINSTANCE.patternCall, 
			Diagnostic::LINKING_DIAGNOSTIC, 
			"Couldn't resolve reference to Pattern 'calledPatternMissing'."
		)

	}
		
}