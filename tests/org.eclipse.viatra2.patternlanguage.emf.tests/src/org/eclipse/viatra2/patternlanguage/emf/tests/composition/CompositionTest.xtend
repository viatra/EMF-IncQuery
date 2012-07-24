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

package org.eclipse.viatra2.patternlanguage.emf.tests.composition

import com.google.inject.Inject
import org.eclipse.xtext.junit4.util.ParseHelper
import org.eclipse.xtext.junit4.validation.ValidationTestHelper
import org.junit.Test
import org.junit.runner.RunWith
import org.eclipse.xtext.junit4.InjectWith
import org.eclipse.xtext.junit4.XtextRunner
import org.eclipse.viatra2.patternlanguage.EMFPatternLanguageInjectorProvider
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.PatternLanguagePackage
import org.eclipse.xtext.diagnostics.Diagnostic

@RunWith(typeof(XtextRunner))
@InjectWith(typeof(EMFPatternLanguageInjectorProvider))
class CompositionTest {//} extends AbstractEMFPatternLanguageTest{
	
	
	@Inject
	ParseHelper parseHelper
	
	@Inject extension ValidationTestHelper

	
	@Test
	def void testSimpleComposition() {
		parseHelper.parse(
			'import "http://www.eclipse.org/viatra2/patternlanguage/core/PatternLanguage"

			pattern calledPattern(p : Pattern) = {
				Pattern(p);
			}

			pattern callPattern(p : Pattern) = {
				find calledPattern(p);
			}'
		).assertNoErrors
	}
	
	@Test
	def void testRecursiveComposition() {
		parseHelper.parse(
			'import "http://www.eclipse.org/viatra2/patternlanguage/core/PatternLanguage"

			pattern calledPattern(p : Pattern) = {
				Pattern(p);
			} or {
				find calledPattern(p);
			}'
		).assertNoErrors
	}
	
	@Test
	def void testNegativeComposition() {
		parseHelper.parse(
			'import "http://www.eclipse.org/viatra2/patternlanguage/core/PatternLanguage"

			pattern calledPattern(p : Pattern) = {
				Pattern(p);
				neg find calledPattern(p);
			}'
		).assertNoErrors
	}
	
	@Test
	def void testMissingComposition() {
		var parsed = parseHelper.parse(
			'
			import "http://www.eclipse.org/viatra2/patternlanguage/core/PatternLanguage"

			pattern callPattern(p : Pattern) = {
				find calledPatternMissing(p);
			}'
		);
		parsed.assertError(PatternLanguagePackage::eINSTANCE.patternCall, 
			Diagnostic::LINKING_DIAGNOSTIC, 
			"Couldn't resolve reference to Pattern 'calledPatternMissing'."
		)
	}

}