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

package org.eclipse.viatra2.patternlanguage.emf.tests.types

import com.google.inject.Inject
import com.google.inject.Injector
import org.eclipse.viatra2.patternlanguage.EMFPatternLanguageInjectorProvider
import org.eclipse.viatra2.patternlanguage.eMFPatternLanguage.PatternModel
import org.eclipse.viatra2.patternlanguage.validation.EMFPatternLanguageJavaValidator
import org.eclipse.xtext.junit4.InjectWith
import org.eclipse.xtext.junit4.XtextRunner
import org.eclipse.xtext.junit4.util.ParseHelper
import org.eclipse.xtext.junit4.validation.ValidationTestHelper
import org.eclipse.xtext.junit4.validation.ValidatorTester
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.eclipse.viatra2.patternlanguage.validation.EMFIssueCodes
import org.eclipse.viatra2.patternlanguage.core.validation.IssueCodes
import org.eclipse.viatra2.patternlanguage.emf.tests.util.AbstractValidatorTest

@RunWith(typeof(XtextRunner))
@InjectWith(typeof(EMFPatternLanguageInjectorProvider))
class LiteralAndComputationTest extends AbstractValidatorTest {
	
	@Inject
	ParseHelper parseHelper
	
	@Inject
	EMFPatternLanguageJavaValidator validator
	
	@Inject
	Injector injector
	
	ValidatorTester<EMFPatternLanguageJavaValidator> tester
	
	@Inject extension ValidationTestHelper
	
	@Before
	def void initialize() {
		tester = new ValidatorTester(validator, injector)
	}
	
	@Test
	def countFind() {
		val model = parseHelper.parse('
			import "http://www.eclipse.org/emf/2002/Ecore"

			pattern Good(X) {
				EClass(X);
			}

			pattern CountFind(X) = {
				EClass(X);
				10 == count find Good(X);
			}
		') as PatternModel
		model.assertNoErrors
		tester.validate(model).assertOK
	}
	
	@Test
	def innerCountFind() {
		val model = parseHelper.parse('
			import "http://www.eclipse.org/emf/2002/Ecore"

			pattern Good(X) {
				EClass(X);
			}

			pattern InnerCountFind(X) = {
				EClass(X);
				10 == count find Good(count find Good(X));
			}
		') as PatternModel
		tester.validate(model).assertError(EMFIssueCodes::LITERAL_OR_COMPUTATION_TYPE_MISMATCH_IN_PATTERN_CALL)
	}
	
	@Test
	def doubleCountFind() {
		val model = parseHelper.parse('
			import "http://www.eclipse.org/emf/2002/Ecore"

			pattern Good(X) {
				EClass(X);
			}

			pattern DoubleCountFind(X) = {
				EClass(X);
				count find Good(X) == count find Good(X);
			}
		') as PatternModel
		model.assertNoErrors
		tester.validate(model).assertOK
	}
	
	@Test
	def normalFind() {
		val model = parseHelper.parse('
			import "http://www.eclipse.org/emf/2002/Ecore"

			pattern Good(X) {
				EClass(X);
			}

			pattern NormalFind(X) = {
				EClass(X);
				find Good(X);
			}
		') as PatternModel
		model.assertNoErrors
		tester.validate(model).assertOK
	}
	
	@Test
	def normalFindError() {
		val model = parseHelper.parse('
			import "http://www.eclipse.org/emf/2002/Ecore"

			pattern Good(X) {
				EClass(X);
			}

			pattern NormalFindError(X) = {
				EClass(X);
				find Good(10);
			}
		') as PatternModel
		tester.validate(model).assertError(EMFIssueCodes::LITERAL_OR_COMPUTATION_TYPE_MISMATCH_IN_PATTERN_CALL)
	}
	
	@Test
	def constantWarning1() {
		val model = parseHelper.parse('
			import "http://www.eclipse.org/emf/2002/Ecore"

			pattern Good(X) {
				EClass(X);
			}

			pattern ConstantWarning1(X) = {
				EClass(X);
				10 == 20;
			}
		') as PatternModel
		model.assertNoErrors
		tester.validate(model).assertAll(getWarningCode(IssueCodes::CONSTANT_COMPARE_CONSTRAINT), getWarningCode(IssueCodes::CONSTANT_COMPARE_CONSTRAINT))
	}
	
	@Test
	def constantWarning2() {
		val model = parseHelper.parse('
			import "http://www.eclipse.org/emf/2002/Ecore"

			pattern Good(X) {
				EClass(X);
			}

			pattern ConstantWarning2(X) = {
				EClass(X);
				"apple" == "orange";
			}
		') as PatternModel
		model.assertNoErrors
		tester.validate(model).assertAll(getWarningCode(IssueCodes::CONSTANT_COMPARE_CONSTRAINT), getWarningCode(IssueCodes::CONSTANT_COMPARE_CONSTRAINT))
	}
	
	@Test
	def constantMismatchError() {
		val model = parseHelper.parse('
			import "http://www.eclipse.org/emf/2002/Ecore"

			pattern Good(X) {
				EClass(X);
			}

			pattern ConstantMismatchError(X) = {
				EClass(X);
				"apple" == 10;
			}
		') as PatternModel
		tester.validate(model).assertAll(getWarningCode(IssueCodes::CONSTANT_COMPARE_CONSTRAINT), 
			getWarningCode(IssueCodes::CONSTANT_COMPARE_CONSTRAINT),
			getErrorCode(EMFIssueCodes::LITERAL_OR_COMPUTATION_TYPE_MISMATCH_IN_COMPARE)
		)
	}
	
	@Test
	def constantComputationMismatchError() {
		val model = parseHelper.parse('
			import "http://www.eclipse.org/emf/2002/Ecore"

			pattern Good(X) {
				EClass(X);
			}

			pattern ConstantComputationMismatchError(X) = {
				EClass(X);
				"test" == count find Good(X);
			}
		') as PatternModel
		tester.validate(model).assertError(EMFIssueCodes::LITERAL_OR_COMPUTATION_TYPE_MISMATCH_IN_COMPARE)
	}
	
	@Test
	def constantInPathExpressionGood() {
		val model = parseHelper.parse('
			import "http://www.eclipse.org/emf/2002/Ecore"

			pattern ConstantInPathExpressionGood(X) = {
				EClass.name(X, "Name");
			}
		') as PatternModel
		model.assertNoErrors
		tester.validate(model).assertOK
	}
	
	@Test
	def constantInPathExpressionMismatch() {
		val model = parseHelper.parse('
			import "http://www.eclipse.org/emf/2002/Ecore"

			pattern ConstantInPathExpressionMismatch(X) = {
				EClass.name(X, 10);
			}
		') as PatternModel
		tester.validate(model).assertError(EMFIssueCodes::LITERAL_OR_COMPUTATION_TYPE_MISMATCH_IN_PATH_EXPRESSION)
	}
	
	@Test
	def countFindInPathExpressionMismatch() {
		val model = parseHelper.parse('
			import "http://www.eclipse.org/emf/2002/Ecore"

			pattern Good(X) {
				EClass(X);
			}

			pattern CountFindInPathExpressionMismatch(X) = {
				EClass.name(X, count find Good(_));
			}
		') as PatternModel
		tester.validate(model).assertError(EMFIssueCodes::LITERAL_OR_COMPUTATION_TYPE_MISMATCH_IN_PATH_EXPRESSION)
	}

}