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

package org.eclipse.incquery.patternlanguage.emf.tests.imports

import org.eclipse.incquery.patternlanguage.emf.tests.util.AbstractValidatorTest
import org.eclipse.incquery.patternlanguage.emf.EMFPatternLanguageInjectorProvider
import org.eclipse.xtext.junit4.InjectWith
import org.junit.runner.RunWith
import org.eclipse.xtext.junit4.XtextRunner
import org.eclipse.xtext.junit4.util.ParseHelper
import com.google.inject.Inject
import org.eclipse.xtext.junit4.validation.ValidationTestHelper
import org.eclipse.incquery.patternlanguage.emf.eMFPatternLanguage.PatternModel
import org.junit.Test
import org.eclipse.incquery.patternlanguage.emf.validation.EMFPatternLanguageJavaValidator
import org.eclipse.xtext.junit4.validation.ValidatorTester
import com.google.inject.Injector
import org.junit.Before
import org.eclipse.incquery.patternlanguage.emf.validation.EMFIssueCodes

@RunWith(typeof(XtextRunner))
@InjectWith(typeof(EMFPatternLanguageInjectorProvider))
class ImportValidationTest extends AbstractValidatorTest {
	
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
	def duplicateImport() {
		val model = parseHelper.parse('
			import "http://www.eclipse.org/incquery/patternlanguage/PatternLanguage"
			import "http://www.eclipse.org/incquery/patternlanguage/PatternLanguage"

			pattern resolutionTest(Name) = {
				Pattern(Name);
			}
		') as PatternModel
		model.assertNoErrors
		tester.validate(model).assertAll(getWarningCode(EMFIssueCodes::DUPLICATE_IMPORT), getWarningCode(EMFIssueCodes::DUPLICATE_IMPORT));
	}
}