/*******************************************************************************
 * Copyright (c) 2010-2012, Andras Okros, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Andras Okros - initial API and implementation
 *******************************************************************************/

package org.eclipse.viatra2.patternlanguage.emf.tests.basic

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

@RunWith(typeof(XtextRunner))
@InjectWith(typeof(EMFPatternLanguageInjectorProvider))
class CartesianProductTest {
	
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
	def testGood1() {
		val model = parseHelper.parse('
			import "http://www.eclipse.org/emf/2002/Ecore"

			pattern Good1(X, Y) {
				EClass(X);
				EClass(Y);
				X == Y;
			}
		') as PatternModel
		model.assertNoErrors
		tester.validate(model).assertOK
	}
	
	@Test
	def testGood2() {
		val model = parseHelper.parse('
			import "http://www.eclipse.org/emf/2002/Ecore"

			pattern Good1(X, Y) {
				EClass(X);
				EClass(Y);
				X == Y;
			}

			pattern Good2(X, Y) {
				EClass(X);
				EClass(Y);
				find Good1(X,Y);
			}
		') as PatternModel
		model.assertNoErrors
		tester.validate(model).assertOK
	}
	
	@Test
	def testGood3() {
		val model = parseHelper.parse('
			import "http://www.eclipse.org/emf/2002/Ecore"

			pattern Good1(X, Y) {
				EClass(X);
				EClass(Y);
				X == Y;
			}

			pattern Good3(X, Y) {
				EClass(X);
				EClass(Y);
				X == Y;
				Z == count find Good1(X,Y);
				check(Z > 10); 
			}
		') as PatternModel
		model.assertNoErrors
		tester.validate(model).assertOK
	}
	
	@Test
	def testGood4() {
		val model = parseHelper.parse('
			import "http://www.eclipse.org/emf/2002/Ecore"

			pattern Good1(X, Y) {
				EClass(X);
				EClass(Y);
				X == Y;
			}

			pattern IntAndClassPattern(X, Y) {
				EInt(X);
				EClass(Y);
				EClass.eAllAttributes.upperBound(Y,X);
			}

			pattern Good4(X, Y, Z) {
				EClass(X);
				EClass(Y);
				X == Y;
				EClass(Z);
				find IntAndClassPattern(count find Good1(X,Y), Z);
			}
		') as PatternModel
		model.assertNoErrors
		tester.validate(model).assertOK
	}
	
	@Test
	def testGood5() {
		val model = parseHelper.parse('
			import "http://www.eclipse.org/emf/2002/Ecore"

			pattern Good1(X, Y) {
				EClass(X);
				EClass(Y);
				X == Y;
			}

			pattern Good5(X, Y, Z) {
				EClass(X);
				EClass(Y);
				X == Y;
				EClass(Z);
				EClass.eAllAttributes.upperBound(Z,count find Good1(X,Y));
			}
		') as PatternModel
		model.assertNoErrors
		tester.validate(model).assertOK
	}
	
	@Test
	def testGood6() {
		val model = parseHelper.parse('
			import "http://www.eclipse.org/emf/2002/Ecore"

			pattern Good1(X, Y) {
				EClass(X);
				EClass(Y);
				X == Y;
			}

			pattern Good6(X) {
				EClass(X);
				neg find Good1(X,_A);
			}
		') as PatternModel
		model.assertNoErrors
		tester.validate(model).assertOK
	}
	
	@Test
	def testGood7() {
		val model = parseHelper.parse('
			import "http://www.eclipse.org/emf/2002/Ecore"

			pattern Good1(X, Y) {
				EClass(X);
				EClass(Y);
				X == Y;
			}

			pattern Good7(X) {
				EClass(X);
				neg find Good1(_A,_B);
			}
		') as PatternModel
		model.assertNoErrors
		tester.validate(model).assertOK
	}
	
	@Test
	def testGood8() {
		val model = parseHelper.parse('
			import "http://www.eclipse.org/emf/2002/Ecore"

			pattern Good1(X, Y) {
				EClass(X);
				EClass(Y);
				X == Y;
			}

			pattern Good8(X) {
				EClass(X);
				M == count find Good1(X,_A);
				check(M>10);
			}
		') as PatternModel
		model.assertNoErrors
		tester.validate(model).assertOK
	}
	
	@Test
	def testSoft1() {
		val model = parseHelper.parse('
			import "http://www.eclipse.org/emf/2002/Ecore"
			
			pattern Soft1(X, Y) {
				EClass(X);
				EClass(Y);
				check(X.abstract == Y.abstract);
			}
		')
		tester.validate(model).assertWarning(EMFIssueCodes::CARTESIAN_SOFT_WARNING)
	}
	
	@Test
	def testSoft2() {
		val model = parseHelper.parse('
			import "http://www.eclipse.org/emf/2002/Ecore"
			
			pattern Good1(X, Y) {
				EClass(X);
				EClass(Y);
				X == Y;
			}

			pattern Soft2(X, Y) {
				EClass(X);
				EClass(Y);
				_A == count find Good1(X,Y);
			}
		')
		tester.validate(model).assertWarning(EMFIssueCodes::CARTESIAN_SOFT_WARNING)
	}
	
	@Test
	def testSoft3() {
		val model = parseHelper.parse('
			import "http://www.eclipse.org/emf/2002/Ecore"
			
			pattern Good1(X, Y) {
				EClass(X);
				EClass(Y);
				X == Y;
			}

			pattern Soft3(X, Y) {
				EClass(X);
				EClass(Y);
				neg find Good1(X,Y);
			}
		')
		tester.validate(model).assertWarning(EMFIssueCodes::CARTESIAN_SOFT_WARNING)
	}
	
	@Test
	def testSoft4() {
		val model = parseHelper.parse('
			import "http://www.eclipse.org/emf/2002/Ecore"
			
			pattern Soft4(X, Y) {
				EClass(X);
				EClass(Y);
				X != Y;
			}
		')
		tester.validate(model).assertWarning(EMFIssueCodes::CARTESIAN_SOFT_WARNING)
	}

	@Test
	def testStrict1() {
		val model = parseHelper.parse('
			import "http://www.eclipse.org/emf/2002/Ecore"
			
			pattern Strict1(X, Y) {
				EClass(X);
				EClass(Y);
			}
		')
		tester.validate(model).assertWarning(EMFIssueCodes::CARTESIAN_STRICT_WARNING)
	}
	
	@Test
	def testStrict2() {
		val model = parseHelper.parse('
			import "http://www.eclipse.org/emf/2002/Ecore"
			
			pattern Strict1(X) {
				EClass(X);
				EClass(_Y);
			}
		')
		tester.validate(model).assertWarning(EMFIssueCodes::CARTESIAN_STRICT_WARNING)
	}
		
}