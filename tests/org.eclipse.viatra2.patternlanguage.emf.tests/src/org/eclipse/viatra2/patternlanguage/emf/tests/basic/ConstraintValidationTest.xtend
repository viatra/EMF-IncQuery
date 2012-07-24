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

import com.google.inject.Inject
import com.google.inject.Injector
import org.eclipse.viatra2.patternlanguage.EMFPatternLanguageInjectorProvider
import org.eclipse.viatra2.patternlanguage.core.validation.IssueCodes
import org.eclipse.viatra2.patternlanguage.eMFPatternLanguage.PatternModel
import org.eclipse.viatra2.patternlanguage.validation.EMFPatternLanguageJavaValidator
import org.eclipse.xtext.junit4.InjectWith
import org.eclipse.xtext.junit4.XtextRunner
import org.eclipse.xtext.junit4.util.ParseHelper
import org.eclipse.xtext.junit4.validation.ValidatorTester
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.eclipse.viatra2.patternlanguage.emf.tests.util.AbstractValidatorTest
import org.eclipse.viatra2.patternlanguage.validation.EMFIssueCodes

@RunWith(typeof(XtextRunner))
@InjectWith(typeof(EMFPatternLanguageInjectorProvider))
class ConstraintValidationTest extends AbstractValidatorTest {
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
	def intConstantCompareValidation() {
		val model = parseHelper.parse('
			pattern constantCompareTest() = {
				1 == 2;
			}
		') as PatternModel
		tester.validate(model).assertAll(getWarningCode(IssueCodes::CONSTANT_COMPARE_CONSTRAINT), getWarningCode(IssueCodes::CONSTANT_COMPARE_CONSTRAINT))
	}
	@Test
	def stringDoubleConstantCompareValidation() {
		val model = parseHelper.parse('
			pattern constantCompareTest() = {
				1.2 == "String";
			}
		') as PatternModel
		tester.validate(model).assertAll(getWarningCode(IssueCodes::CONSTANT_COMPARE_CONSTRAINT), getWarningCode(IssueCodes::CONSTANT_COMPARE_CONSTRAINT))
	}
	@Test
	def enumIntConstantCompareValidation() {
		val model = parseHelper.parse('
			pattern constantCompareTest() = {
				false == 2;
			}
		') as PatternModel
		tester.validate(model).assertAll(getWarningCode(IssueCodes::CONSTANT_COMPARE_CONSTRAINT), getWarningCode(IssueCodes::CONSTANT_COMPARE_CONSTRAINT))
	}
	@Test
	def rightVariableCompareValidation() {
		val model = parseHelper.parse('
			pattern constantCompareTest(Name) = {
				1 == Name;
				IntValue.value(Name2, Name);	// Name2 should be a single variable, e.g. _Name2
				IntValue(Name2);				// Then this line can be deleted.
			}
		') as PatternModel
		tester.validate(model).assertOK
	}
	@Test
	def rightNewVariableCompareValidation() {
		val model = parseHelper.parse('
			pattern constantCompareTest() = {
				1 == Name2;
				IntValue.value(Name3, Name2);	// Name3 should be a single variable, e.g. _Name3
				IntValue(Name3);				// Then this line can be deleted.
			}
		') as PatternModel
		tester.validate(model).assertOK
	}
	@Test
	def leftVariableCompareValidation() {
		val model = parseHelper.parse('
			pattern constantCompareTest(Name) = {
				Name == "Test";
				StringValue.value(Name2, Name);	// Name2 should be a single variable, e.g. _Name2
				StringValue(Name2);				// Then this line can be deleted.
			}
		') as PatternModel
		tester.validate(model).assertOK
	}
	@Test
	def leftNewVariableCompareValidation() {
		val model = parseHelper.parse('
			pattern constantCompareTest() = {
				Name2 == "Test";
				StringValue.value(Name3, Name2);	// Name3 should be a single variable, e.g. _Name3
				StringValue(Name3);					// Then this line can be deleted.
			}
		') as PatternModel
		tester.validate(model).assertOK
	}
	@Test
	def bothVariableCompareValidation() {
		val model = parseHelper.parse('
			pattern constantCompareTest(Name) = {
				Name == Name2;
				Pattern(Name2);
			}
		') as PatternModel
		tester.validate(model).assertOK
	}
	@Test
	def selfCompareValidation() {
		val model = parseHelper.parse('
			pattern constantCompareTest(Name) = {
				Name == Name;
			}
		') as PatternModel
		tester.validate(model).assertAll(
			getWarningCode(IssueCodes::SELF_COMPARE_CONSTRAINT),
			getWarningCode(IssueCodes::SELF_COMPARE_CONSTRAINT),
			getErrorCode(EMFIssueCodes::SYMBOLIC_VARIABLE_NO_POSITIVE_REFERENCE)
		)
	}
}