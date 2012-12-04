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
import org.eclipse.incquery.querybasedfeatures.runtime.util.validation.QueryBasedFeaturePatternValidator


@RunWith(typeof(XtextRunner))
@InjectWith(typeof(EMFPatternLanguageInjectorProvider))
class QueryBasedFeatureAnnotationValidatorTest extends AbstractValidatorTest{
		
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
	def void tooFewParameters() {
		val model = parseHelper.parse(
			'import "http://www.eclipse.org/incquery/patternlanguage/PatternLanguage"

			@QueryBasedFeature
			pattern pattern2(p : Pattern) = {
				Pattern(p);
			}'
		) 
		tester.validate(model).assertError(QueryBasedFeaturePatternValidator::PATTERN_ISSUE_CODE);
	}
	
	@Test
	def void emptyFeatureName() {
		val model = parseHelper.parse(
			'import "http://www.eclipse.org/incquery/patternlanguage/PatternLanguage"

			@QueryBasedFeature(feature = "")
			pattern pattern2(p : Pattern, pb : PatternBody) = {
				Pattern.bodies(p, pb);
			}'
		) 
		tester.validate(model).assertError(QueryBasedFeaturePatternValidator::ANNOTATION_ISSUE_CODE);
	}
	
	@Test
	def void notFoundFeature() {
		val model = parseHelper.parse(
			'import "http://www.eclipse.org/incquery/patternlanguage/PatternLanguage"

			@QueryBasedFeature
			pattern pattern2(p : Pattern, pb : PatternBody) = {
				Pattern.bodies(p, pb);
			}'
		) 
		tester.validate(model).assertError(QueryBasedFeaturePatternValidator::ANNOTATION_ISSUE_CODE);
	}
	
	@Test
	def void incorrectFeature() {
		val model = parseHelper.parse(
			'import "http://www.eclipse.org/incquery/patternlanguage/PatternLanguage"

			@QueryBasedFeature
			pattern bodies(p : Pattern, pb : PatternBody) = {
				Pattern.bodies(p, pb);
			}'
		) 
		tester.validate(model).assertAll(getErrorCode(QueryBasedFeaturePatternValidator::METAMODEL_ISSUE_CODE),
		  getErrorCode(QueryBasedFeaturePatternValidator::METAMODEL_ISSUE_CODE),
		  getErrorCode(QueryBasedFeaturePatternValidator::METAMODEL_ISSUE_CODE)
		);
	}
	
	@Test
	def void notVolatileFeature() {
		val model = parseHelper.parse(
			'import "http://www.eclipse.org/incquery/patternlanguage/PatternLanguage"

			@QueryBasedFeature
			pattern variables(pb : PatternBody, v : Variable) = {
				PatternBody.variables(pb, v);
			}'
		) 
		tester.validate(model).assertError(QueryBasedFeaturePatternValidator::METAMODEL_ISSUE_CODE);
	}
	
}