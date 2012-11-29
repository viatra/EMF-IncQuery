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

package org.eclipse.incquery.patternlanguage.emf.tests.annotations

import com.google.inject.Inject
import com.google.inject.Injector
import org.eclipse.incquery.patternlanguage.emf.EMFPatternLanguageInjectorProvider
import org.eclipse.incquery.patternlanguage.emf.tests.util.AbstractValidatorTest
import org.eclipse.incquery.patternlanguage.emf.validation.EMFPatternLanguageJavaValidator
import org.eclipse.xtext.junit4.InjectWith
import org.eclipse.xtext.junit4.XtextRunner
import org.eclipse.xtext.junit4.util.ParseHelper
import org.eclipse.xtext.junit4.validation.ValidatorTester
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.eclipse.incquery.databinding.runtime.util.validation.ObservableValuePatternValidator


@RunWith(typeof(XtextRunner))
@InjectWith(typeof(EMFPatternLanguageInjectorProvider))
class ObservableValueAnnotationValidatorTest extends AbstractValidatorTest{
		
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
	def void expressionShortName() {
		val model = parseHelper.parse(
			'import "http://www.eclipse.org/incquery/patternlanguage/PatternLanguage"

			@ObservableValue(name = "name", expression = "p")
			pattern pattern2(p : Pattern) = {
				Pattern(p);
			}'
		) 
		tester.validate(model).assertOK
	}
	
	@Test
	def void expressionFullName() {
		val model = parseHelper.parse(
			'import "http://www.eclipse.org/incquery/patternlanguage/PatternLanguage"

			@ObservableValue(name = "name", expression = "p.name")
			pattern pattern2(p : Pattern) = {
				Pattern(p);
			}'
		) 
		tester.validate(model).assertOK
	}
	
	@Test
	def void expressionEmpty() {
		val model = parseHelper.parse(
			'import "http://www.eclipse.org/incquery/patternlanguage/PatternLanguage"

			@ObservableValue(name = "name", expression = "")
			pattern pattern2(p : Pattern) = {
				Pattern(p);
			}'
		) 
		tester.validate(model).assertError(ObservableValuePatternValidator::GENERAL_ISSUE_CODE)
	}
	
	@Test
	def void expressionInDollars() {
		val model = parseHelper.parse(
			'import "http://www.eclipse.org/incquery/patternlanguage/PatternLanguage"

			@ObservableValue(name = "name", expression = "$p.name$")
			pattern pattern2(p : Pattern) = {
				Pattern(p);
			}'
		) 
		tester.validate(model).assertAll(getWarningCode(ObservableValuePatternValidator::GENERAL_ISSUE_CODE),getErrorCode(ObservableValuePatternValidator::UNKNOWN_VARIABLE_CODE))
	}
	
	@Test
	def void expressionInvalidParameter1() {
		val model = parseHelper.parse(
			'import "http://www.eclipse.org/incquery/patternlanguage/PatternLanguage"

			@ObservableValue(name = "name", expression = "p1")
			pattern pattern2(p : Pattern) = {
				Pattern(p);
			}'
		) 
		tester.validate(model).assertError(ObservableValuePatternValidator::UNKNOWN_VARIABLE_CODE)
	}
	@Test
	def void expressionInvalidParameter2() {
		val model = parseHelper.parse(
			'import "http://www.eclipse.org/incquery/patternlanguage/PatternLanguage"

			@ObservableValue(name = "name", expression = "p1.name")
			pattern pattern2(p : Pattern) = {
				Pattern(p);
			}'
		) 
		tester.validate(model).assertError(ObservableValuePatternValidator::UNKNOWN_VARIABLE_CODE)
	}
	@Test
	def void expressionInvalidFeature() {
		val model = parseHelper.parse(
			'import "http://www.eclipse.org/incquery/patternlanguage/PatternLanguage"

			@ObservableValue(name = "name", expression = "p.notExists")
			pattern pattern2(p : Pattern) = {
				Pattern(p);
			}'
		) 
		tester.validate(model).assertError(ObservableValuePatternValidator::UNKNOWN_ATTRIBUTE_CODE)
	}
}