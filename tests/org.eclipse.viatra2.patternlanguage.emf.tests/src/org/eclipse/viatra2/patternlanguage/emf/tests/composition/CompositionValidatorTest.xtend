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
import com.google.inject.Injector
import org.eclipse.viatra2.patternlanguage.EMFPatternLanguageInjectorProvider
import org.eclipse.viatra2.patternlanguage.core.validation.IssueCodes
import org.eclipse.viatra2.patternlanguage.emf.tests.util.AbstractValidatorTest
import org.eclipse.viatra2.patternlanguage.validation.EMFPatternLanguageJavaValidator
import org.eclipse.xtext.junit4.InjectWith
import org.eclipse.xtext.junit4.XtextRunner
import org.eclipse.xtext.junit4.util.ParseHelper
import org.eclipse.xtext.junit4.validation.ValidatorTester
import org.junit.Before
import org.junit.Ignore
import org.junit.Test
import org.junit.runner.RunWith
import org.eclipse.viatra2.patternlanguage.validation.EMFIssueCodes


@RunWith(typeof(XtextRunner))
@InjectWith(typeof(EMFPatternLanguageInjectorProvider))
class CompositionValidatorTest extends AbstractValidatorTest{
		
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
	def void duplicatePatterns() {
		val model = parseHelper.parse(
			'import "http://www.eclipse.org/viatra2/patternlanguage/core/PatternLanguage"

			pattern calledPattern(p : Pattern) = {
				Pattern(p);
			}

			pattern calledPattern(p : Pattern) = {
				Pattern(p);
			}'
		) 
		tester.validate(model).assertAll(getErrorCode(IssueCodes::DUPLICATE_PATTERN_DEFINITION), getErrorCode(IssueCodes::DUPLICATE_PATTERN_DEFINITION));
	}	
	@Test
	def void duplicateParameters() {
		val model = parseHelper.parse(
			'import "http://www.eclipse.org/viatra2/patternlanguage/core/PatternLanguage"

			pattern calledPattern(p : Pattern, p) = {
				Pattern(p);
			}

			pattern callPattern(p : Pattern) = {
				Pattern(p);
			}'
		)
		tester.validate(model).assertAll(
			getErrorCode(IssueCodes::DUPLICATE_PATTERN_PARAMETER_NAME),
			getErrorCode(IssueCodes::DUPLICATE_PATTERN_PARAMETER_NAME),
			getErrorCode(EMFIssueCodes::SYMBOLIC_VARIABLE_NEVER_REFERENCED)
		)
	}	
	@Test
	def void testTooFewParameters() {
		val model = parseHelper.parse(
			'import "http://www.eclipse.org/viatra2/patternlanguage/core/PatternLanguage"

			pattern calledPattern(p : Pattern, p2) = {
				Pattern(p);
				Pattern(p2);
			}

			pattern callPattern(p : Pattern) = {
				find calledPattern(p);
			}'
		)
		tester.validate(model).assertAll(getErrorCode(IssueCodes::WRONG_NUMBER_PATTERNCALL_PARAMETER), getWarningCode(EMFIssueCodes::CARTESIAN_STRICT_WARNING))
	}
	@Test
	def void testTooMuchParameters() {
		val model = parseHelper.parse(
			'import "http://www.eclipse.org/viatra2/patternlanguage/core/PatternLanguage"

			pattern calledPattern(p : Pattern) = {
				Pattern(p);
			}

			pattern callPattern(p : Pattern) = {
				find calledPattern(p, p);
			}'
		)
		tester.validate(model).assertError(IssueCodes::WRONG_NUMBER_PATTERNCALL_PARAMETER);
	}
	@Test
	def void testSymbolicParameterSafe() {
		val model = parseHelper.parse(
			'import "http://www.eclipse.org/viatra2/patternlanguage/core/PatternLanguage"

			pattern calledPattern(p : Pattern) = {
				Pattern(p);
				neg find calledPattern(p);
			}'
		)
		tester.validate(model).assertOK;
	}
	@Test
	def void testQuantifiedLocalVariable() {
		val model = parseHelper.parse(
			'import "http://www.eclipse.org/viatra2/patternlanguage/core/PatternLanguage"

			pattern calledPattern(p : Pattern) = {
				Pattern(p);
			}

			pattern callerPattern(c) = {
				Pattern(c);
				Pattern(p);
				neg find calledPattern(p);
			}'
		)
		tester.validate(model).assertWarning(EMFIssueCodes::CARTESIAN_STRICT_WARNING);
	}
	@Test @Ignore(value = "This call is unsafe because of a negative call circle. 
						   p: Pattern is a positive reference.")
	def void testNegativeCallCircle() {
		val model = parseHelper.parse(
			'import "http://www.eclipse.org/viatra2/patternlanguage/core/PatternLanguage"

			pattern calledPattern(p : Pattern) = {
				Pattern(p);
			} or {
				neg find calledPattern(p);
			}'
		)
		tester.validate(model).assertError("");
	}
}
