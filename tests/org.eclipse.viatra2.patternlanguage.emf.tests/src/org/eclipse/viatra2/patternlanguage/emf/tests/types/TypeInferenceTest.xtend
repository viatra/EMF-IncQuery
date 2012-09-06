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
import org.junit.Ignore

@RunWith(typeof(XtextRunner))
@InjectWith(typeof(EMFPatternLanguageInjectorProvider))
class TypeInferenceTest {
	
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
	def zeroLevelType() {
		val model = parseHelper.parse('
			import "http://www.eclipse.org/emf/2002/Ecore"

			pattern first(class) = {
				EClass(class);
				check (class.abstract != false);
			}
		') as PatternModel
		model.assertNoErrors
		tester.validate(model).assertOK
	}
	
	@Test @Ignore
	def firstLevelFindType() {
		val model = parseHelper.parse('
			import "http://www.eclipse.org/emf/2002/Ecore"

			pattern first(class) = {
				EClass(class);
				check (class.abstract != false);
			}

			pattern second(class) = {
				find first(class);
				check (class.abstract != false);
			}
		') as PatternModel
		model.assertNoErrors
		tester.validate(model).assertOK
	}
	
	@Test @Ignore
	def secondLevelFindType() {
		val model = parseHelper.parse('
			import "http://www.eclipse.org/emf/2002/Ecore"

			pattern first(class) = {
				EClass(class);
				check (class.abstract != false);
			}

			pattern second(class) = {
				find first(class);
				check (class.abstract != false);
			}

			pattern third(class) = {
				find second(class);
				check (class.abstract != false);
			}
		') as PatternModel
		model.assertNoErrors
		tester.validate(model).assertOK
	}
	
	@Test
	def zeroLevelPathType() {
		val model = parseHelper.parse('
			import "http://www.eclipse.org/emf/2002/Ecore"

			pattern firstPath(class, attribute) = {
				EClass.eAttributes(class, attribute);
				check (class.abstract != false);
			}
		') as PatternModel
		model.assertNoErrors
		tester.validate(model).assertOK
	}
	
	@Test @Ignore
	def firstLevelPathType() {
		val model = parseHelper.parse('
			import "http://www.eclipse.org/emf/2002/Ecore"

			pattern firstPath(class, attribute) = {
				EClass.eAttributes(class, attribute);
				check (class.abstract != false);
			}

			pattern secondPath(class, attribute) = {
				find firstPath(class, attribute);
				check (class.abstract != false);
			}
		') as PatternModel
		model.assertNoErrors
		tester.validate(model).assertOK
	}
	
}