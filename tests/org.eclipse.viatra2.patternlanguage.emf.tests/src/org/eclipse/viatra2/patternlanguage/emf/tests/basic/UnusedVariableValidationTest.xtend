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
import org.eclipse.viatra2.patternlanguage.emf.tests.util.AbstractValidatorTest
import org.eclipse.viatra2.patternlanguage.validation.EMFIssueCodes
import org.eclipse.viatra2.patternlanguage.validation.EMFPatternLanguageJavaValidator
import org.eclipse.xtext.junit4.InjectWith
import org.eclipse.xtext.junit4.XtextRunner
import org.eclipse.xtext.junit4.util.ParseHelper
import org.eclipse.xtext.junit4.validation.ValidatorTester
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
@RunWith(typeof(XtextRunner))
@InjectWith(typeof(EMFPatternLanguageInjectorProvider))
class UnusedVariableValidationTest extends AbstractValidatorTest {
	
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
	def testSymbolicVariableNoReference() {
		val model = parseHelper.parse(
			'import "http://www.eclipse.org/viatra2/patternlanguage/core/PatternLanguage"
			
			pattern testPattern(p) = {
				Pattern(h);
				Pattern.name(h, "");
			}'
		)
		tester.validate(model).assertAll(getErrorCode(EMFIssueCodes::SYMBOLIC_VARIABLE_NEVER_REFERENCED), getWarningCode(EMFIssueCodes::CARTESIAN_STRICT_WARNING))
	}
	
	@Test
	def testSymbolicVariableOnePositiveReference() {
		val model = parseHelper.parse(
			'import "http://www.eclipse.org/viatra2/patternlanguage/core/PatternLanguage"

			pattern testPattern(p) = {
				Pattern(p);
			}'
		)
		tester.validate(model).assertOK
	}
	
	@Test
	def testParametersEqualityError() {
		val model = parseHelper.parse(
			'import "http://www.eclipse.org/viatra2/patternlanguage/core/PatternLanguage"

			pattern testPattern(p, p2) = {
				p == p2;
			}'
		)
		tester.validate(model).assertAll(
			getErrorCode(EMFIssueCodes::SYMBOLIC_VARIABLE_NO_POSITIVE_REFERENCE),
			getErrorCode(EMFIssueCodes::SYMBOLIC_VARIABLE_NO_POSITIVE_REFERENCE)
		)
	}
	
	@Test
	def testSymbolicVariableOneNegativeReference() {
		val model = parseHelper.parse(
			'import "http://www.eclipse.org/viatra2/patternlanguage/core/PatternLanguage"

			pattern helper(p) = {
				Pattern(p);
			}
			pattern testPattern(p) = {
				neg find helper(p);
			}'
		)
		tester.validate(model).assertError(EMFIssueCodes::SYMBOLIC_VARIABLE_NO_POSITIVE_REFERENCE)
	}
	
	@Test
	def testSymbolicVariableOneReadOnlyReference() {
		val model = parseHelper.parse(
			'import "http://www.eclipse.org/viatra2/patternlanguage/core/PatternLanguage"
			import "http://www.eclipse.org/emf/2002/Ecore"

			pattern helper(p) = {
				Pattern(p);
			}

			pattern testPattern(p) = {
				// Pattern(h);
				EInt(h);
				h == count find helper(p);
			}'
		)
		tester.validate(model).assertError(EMFIssueCodes::SYMBOLIC_VARIABLE_NO_POSITIVE_REFERENCE)
	}
	
	@Test
	def testSymbolicVariableNoPositiveReference() {
		val model = parseHelper.parse(
			'import "http://www.eclipse.org/viatra2/patternlanguage/core/PatternLanguage"

			pattern helper(p) = {
				Pattern(p);
			}

			pattern testPattern(p) = {
				neg find helper(p);
				check(p == 0);
				Pattern(h);
				h != p;
			}'
		)
		tester.validate(model).assertAll(getErrorCode(EMFIssueCodes::SYMBOLIC_VARIABLE_NO_POSITIVE_REFERENCE), getWarningCode(EMFIssueCodes::CARTESIAN_SOFT_WARNING))
	}
	
	@Test
	def testSymbolicVariableAllReferences() {
		val model = parseHelper.parse(
			'import "http://www.eclipse.org/viatra2/patternlanguage/core/PatternLanguage"

			pattern helper(p) = {
				Pattern(p);
			}

			pattern testPattern(p) = {
				Pattern(p);
				neg Pattern.name(p, "");
				neg find helper(p);
				check(p == 0);
				Pattern(h);
				h == p;
			}'
		)
		tester.validate(model).assertAll(getErrorCode(EMFIssueCodes::CHECK_CONSTRAINT_SCALAR_VARIABLE_ERROR))
	}
	
	@Test
	def testLocalVariableOnePositiveReference() {
		val model = parseHelper.parse(
			'import "http://www.eclipse.org/viatra2/patternlanguage/core/PatternLanguage"

			pattern testPattern(c) = {
				Pattern(c);
				Pattern(p);
			}'
		)
		tester.validate(model).assertAll(getWarningCode(EMFIssueCodes::LOCAL_VARIABLE_REFERENCED_ONCE), getWarningCode(EMFIssueCodes::CARTESIAN_STRICT_WARNING))
	}
	
	@Test
	def testLocalVariableOneNegativeReference() {
		val model = parseHelper.parse(
			'import "http://www.eclipse.org/viatra2/patternlanguage/core/PatternLanguage"

			pattern helper(P) = {
				Pattern(P);
			}
			pattern testPattern(c) = {
				Pattern(c);
				neg find helper(p);
			}'
		)
		tester.validate(model).assertAll(getWarningCode(EMFIssueCodes::LOCAL_VARIABLE_QUANTIFIED_REFERENCE), getWarningCode(EMFIssueCodes::CARTESIAN_STRICT_WARNING))
	}
	@Test
	def testLocalVariableOneSingleUseNegativeReference() {
		val model = parseHelper.parse(
			'import "http://www.eclipse.org/viatra2/patternlanguage/core/PatternLanguage"

			pattern testPattern(c) = {
				Pattern(c);
				neg Pattern.name(_p, "");
			}'
		)
		tester.validate(model).assertOK
	}
	
	@Test
	def testLocalVariableOneReadOnlyReference() {
		val model = parseHelper.parse(
			'import "http://www.eclipse.org/viatra2/patternlanguage/core/PatternLanguage"
			import "http://www.eclipse.org/emf/2002/Ecore"

			pattern helper(p) = {
				Pattern(p);
			}

			pattern testPattern(c) = {
				Pattern(c);
				EInt(h);
				h == count find helper(p);
			}'
		)
		tester.validate(model).assertAll(getWarningCode(EMFIssueCodes::LOCAL_VARIABLE_QUANTIFIED_REFERENCE), getWarningCode(EMFIssueCodes::CARTESIAN_STRICT_WARNING))
	}
	
	@Test
	def testLocalVariableMultiplePositiveReferences() {
		val model = parseHelper.parse(
			'import "http://www.eclipse.org/viatra2/patternlanguage/core/PatternLanguage"

			pattern testPattern(c) = {
				Pattern(c);
				Pattern(p);
				Pattern.name(p, "");
			}'
		)
		tester.validate(model).assertWarning(EMFIssueCodes::CARTESIAN_STRICT_WARNING)
	}
	
	@Test
	def testLocalVariableOnePositiveOneNegativeReference() {
		val model = parseHelper.parse(
			'import "http://www.eclipse.org/viatra2/patternlanguage/core/PatternLanguage"

			pattern helper(p) = {
				Pattern(p);
			}
			pattern testPattern(c) = {
				Pattern(c);
				Pattern(p);
				neg find helper(p);
			}'
		)
		tester.validate(model).assertWarning(EMFIssueCodes::CARTESIAN_STRICT_WARNING)
	}
	
	@Test
	def testLocalVariableOnePositiveOneReadOnlyReference() {
		val model = parseHelper.parse(
			'import "http://www.eclipse.org/viatra2/patternlanguage/core/PatternLanguage"
			import "http://www.eclipse.org/emf/2002/Ecore"

			pattern helper(p) = {
				Pattern(p);
			}

			pattern testPattern(c) = {
				Pattern(c);
				Pattern(p);
				EInt(h);
				h == count find helper(p);
			}'
		)
		tester.validate(model).assertWarning(EMFIssueCodes::CARTESIAN_STRICT_WARNING)
	}
	@Test
	def testMultipleUseOfSingleUseVariables() {
		val model = parseHelper.parse(
			'import "http://www.eclipse.org/viatra2/patternlanguage/core/PatternLanguage"

			pattern testPattern(c) = {
				Pattern(c);
				Pattern(_p);
				Pattern(_p);
			}'
		)
		tester.validate(model).assertAll(getErrorCode(EMFIssueCodes::ANONYM_VARIABLE_MULTIPLE_REFERENCE), getErrorCode(EMFIssueCodes::ANONYM_VARIABLE_MULTIPLE_REFERENCE), getWarningCode(EMFIssueCodes::CARTESIAN_STRICT_WARNING))
	}
	@Test
	def testReadOnlyReference() {
		val model = parseHelper.parse(
			'import "http://www.eclipse.org/viatra2/patternlanguage/core/PatternLanguage"

			pattern testPattern(c) = {
				Pattern(c);
				Pattern(P);
				P != Q;
			}'
		)
		tester.validate(model).assertAll(getErrorCode(EMFIssueCodes::LOCAL_VARIABLE_READONLY), getWarningCode(EMFIssueCodes::CARTESIAN_STRICT_WARNING))
	}
	
	@Test
	def testLocalVariableMultipleNegativeReferences() {
		val model = parseHelper.parse(
			'import "http://www.eclipse.org/viatra2/patternlanguage/core/PatternLanguage"

			pattern helper(p) = {
				Pattern(p);
			}

			pattern helper2(p) = {
				Pattern(p);
			}

			pattern testPattern(c) = {
				Pattern(c);
				neg find helper2(p);
				neg find helper(p);
			}'
		)
		tester.validate(model).assertAll(getErrorCode(EMFIssueCodes::LOCAL_VARIABLE_NO_POSITIVE_REFERENCE), getWarningCode(EMFIssueCodes::CARTESIAN_STRICT_WARNING))
	}
	
	@Test
	def testLocalVariableOneNegativeOneReadOnlyReference() {
		val model = parseHelper.parse(
			'import "http://www.eclipse.org/viatra2/patternlanguage/core/PatternLanguage"
			import "http://www.eclipse.org/emf/2002/Ecore"

			pattern helper(p) = {
				Pattern(p);
			}

			pattern testPattern(c) = {
				Pattern(c);
				neg find helper(p);
				EInt(h);
				h == count find helper(p);
			}'
		)
		tester.validate(model).assertAll(getErrorCode(EMFIssueCodes::LOCAL_VARIABLE_NO_POSITIVE_REFERENCE), getWarningCode(EMFIssueCodes::CARTESIAN_STRICT_WARNING))
	}
	
	@Test
	def testLocalVariableMultipleReadOnlyReferences() {
		val model = parseHelper.parse(
			'import "http://www.eclipse.org/viatra2/patternlanguage/core/PatternLanguage"
			import "http://www.eclipse.org/emf/2002/Ecore"

			pattern helper(p) = {
				Pattern(p);
			}

			pattern testPattern(c) = {
				Pattern(c);
				EInt(h);
				EInt(i);
				h == count find helper(p);
				i == count find helper(p);
			}'
		)
		tester.validate(model).assertAll(getErrorCode(EMFIssueCodes::LOCAL_VARIABLE_NO_POSITIVE_REFERENCE), getWarningCode(EMFIssueCodes::CARTESIAN_STRICT_WARNING))
	}
}