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

package org.eclipse.viatra2.patternlanguage.emf.tests.basic

import org.eclipse.xtext.junit4.XtextRunner
import org.junit.runner.RunWith
import org.eclipse.xtext.junit4.InjectWith
import org.eclipse.viatra2.patternlanguage.EMFPatternLanguageInjectorProvider
import com.google.inject.Inject
import org.eclipse.xtext.junit4.util.ParseHelper
import org.junit.Test
import org.eclipse.viatra2.patternlanguage.eMFPatternLanguage.PatternModel
import static org.junit.Assert.*
import org.eclipse.xtext.junit4.validation.ValidatorTester
import org.eclipse.viatra2.patternlanguage.validation.EMFPatternLanguageJavaValidator
import org.eclipse.viatra2.patternlanguage.core.validation.IssueCodes
import com.google.inject.Injector
import org.junit.Before
import org.eclipse.viatra2.patternlanguage.emf.tests.util.AbstractValidatorTest
import org.eclipse.viatra2.patternlanguage.validation.EMFIssueCodes

@RunWith(typeof(XtextRunner))
@InjectWith(typeof(EMFPatternLanguageInjectorProvider))
class PatternValidationTest extends AbstractValidatorTest {
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
	def emptyBodyValidation() {
		val model = parseHelper.parse('import "http://www.eclipse.org/viatra2/patternlanguage/core/PatternLanguage"
        pattern resolutionTest(A) = {}') as PatternModel
		tester.validate(model).assertAll(getErrorCode(IssueCodes::PATTERN_BODY_EMPTY), getErrorCode(EMFIssueCodes::SYMBOLIC_VARIABLE_NEVER_REFERENCED))
	}
	@Test
	def emptyParameterListValidation() {
		val model = parseHelper.parse('import "http://www.eclipse.org/viatra2/patternlanguage/core/PatternLanguage"
		pattern resolutionTest() = {Pattern(A);}') as PatternModel
		tester.validate(model).assertAll(getWarningCode(IssueCodes::MISSING_PATTERN_PARAMETERS), getWarningCode(EMFIssueCodes::LOCAL_VARIABLE_REFERENCED_ONCE))
	}
	
	@Test
	def unusedPrivatePatternValidation() {
		val model = parseHelper.parse('
			import "http://www.eclipse.org/viatra2/patternlanguage/core/PatternLanguage"
			private pattern unusedPrivatePattern(Pattern) {
				Pattern(Pattern);
			}
		')
		tester.validate(model).assertWarning(IssueCodes::UNUSED_PRIVATE_PATTERN, "The pattern 'unusedPrivatePattern'")
	}
	
	@Test
	def singleUseParameterValidation() {
		val model = parseHelper.parse('
			import "http://www.eclipse.org/viatra2/patternlanguage/core/PatternLanguage"
			pattern unusedPrivatePattern(_Pattern) {
				Pattern(_Pattern);
			}
		')
		tester.validate(model).assertError(EMFIssueCodes::SINGLEUSE_PARAMETER)
	}

}