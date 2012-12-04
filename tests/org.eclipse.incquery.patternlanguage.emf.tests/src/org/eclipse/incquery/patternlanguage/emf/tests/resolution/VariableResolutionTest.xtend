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

package org.eclipse.incquery.patternlanguage.emf.tests.resolution

import com.google.inject.Inject
import org.eclipse.incquery.patternlanguage.emf.EMFPatternLanguageInjectorProvider
import org.eclipse.incquery.patternlanguage.patternLanguage.PathExpressionConstraint
import org.eclipse.incquery.patternlanguage.patternLanguage.PatternCompositionConstraint
import org.eclipse.incquery.patternlanguage.patternLanguage.VariableValue
import org.eclipse.incquery.patternlanguage.emf.eMFPatternLanguage.EClassifierConstraint
import org.eclipse.incquery.patternlanguage.emf.eMFPatternLanguage.PatternModel
import org.eclipse.incquery.patternlanguage.emf.validation.EMFIssueCodes
import org.eclipse.xtext.junit4.InjectWith
import org.eclipse.xtext.junit4.XtextRunner
import org.eclipse.xtext.junit4.util.ParseHelper
import org.eclipse.xtext.junit4.validation.ValidationTestHelper
import org.junit.Test
import org.junit.runner.RunWith

import static org.junit.Assert.*
import org.eclipse.incquery.patternlanguage.validation.IssueCodes

@RunWith(typeof(XtextRunner))
@InjectWith(typeof(EMFPatternLanguageInjectorProvider))
class VariableResolutionTest {
	@Inject
	ParseHelper parseHelper
	
	@Inject extension ValidationTestHelper
	
	@Test
	def parameterResolution() {
		val model = parseHelper.parse('
			import "http://www.eclipse.org/incquery/patternlanguage/PatternLanguage"

			pattern resolutionTest(Name) = {
				Pattern(Name);
			}
		') as PatternModel
		model.assertNoErrors
		val pattern = model.patterns.get(0)
		val parameter = pattern.parameters.get(0)
		val constraint = pattern.bodies.get(0).constraints.get(0) as EClassifierConstraint
		assertEquals(parameter.name, constraint.getVar().getVar())
	}
	
	@Test
	def singleUseResolution() {
		val model = parseHelper.parse('
			import "http://www.eclipse.org/incquery/patternlanguage/PatternLanguage"

			pattern resolutionTest(Name) = {
				Pattern.parameters(Name,_parameter);
			}
		') as PatternModel
		
		model.assertNoErrors
		val pattern = model.patterns.get(0)
		val parameter = pattern.parameters.get(0)
		val constraint = pattern.bodies.get(0).constraints.get(0) as PathExpressionConstraint
		assertEquals(parameter.name, constraint.head.src.variable.name)
	}
	@Test
	def anonymVariablesResolution() {
		val model = parseHelper.parse('
			import "http://www.eclipse.org/incquery/patternlanguage/PatternLanguage"
			pattern helper(A,B,C) = {
				Pattern(A);
				Pattern(B);
				Pattern(C);
			}
			pattern resolutionTest(A) = {
				find helper(A, _, _);
			}
		') as PatternModel
		model.assertNoErrors
		val pattern = model.patterns.get(1)
		val constraint = pattern.bodies.get(0).constraints.get(0) as PatternCompositionConstraint
		assertNotSame((constraint.call.parameters.get(1) as VariableValue).value.variable.name,
			(constraint.call.parameters.get(2) as VariableValue).value.variable.name
		)
	}
	
	@Test
	def parameterResolutionFailed() {
		val model = parseHelper.parse('
			import "http://www.eclipse.org/incquery/patternlanguage/PatternLanguage"

			pattern resolutionTest(Name) = {
				Pattern(Name2);
			}
		') as PatternModel
		val pattern = model.patterns.get(0)
		val parameter = pattern.parameters.get(0)
		val constraint = pattern.bodies.get(0).constraints.get(0) as EClassifierConstraint
		model.assertError(parameter.eClass, IssueCodes::SYMBOLIC_VARIABLE_NEVER_REFERENCED)
		model.assertWarning(constraint.getVar().eClass, IssueCodes::LOCAL_VARIABLE_REFERENCED_ONCE)
		assertTrue(parameter.name != constraint.getVar().getVar())
	}
	
	@Test
	def constraintVariableResolution() {
		val model = parseHelper.parse('
			import "http://www.eclipse.org/incquery/patternlanguage/PatternLanguage"

			pattern resolutionTest(Name) = {
				Pattern(Name2);
				Pattern(Name2);
			}
		') as PatternModel
		val pattern = model.patterns.get(0)
		val parameter = pattern.parameters.get(0)
		model.assertError(parameter.eClass, IssueCodes::SYMBOLIC_VARIABLE_NEVER_REFERENCED)
		val constraint0 = pattern.bodies.get(0).constraints.get(0) as EClassifierConstraint
		val constraint1 = pattern.bodies.get(0).constraints.get(0) as EClassifierConstraint
		assertTrue(parameter.name != constraint0.getVar().getVar())
		assertEquals(constraint0.getVar().getVar(), constraint1.getVar().getVar())				
	}
}