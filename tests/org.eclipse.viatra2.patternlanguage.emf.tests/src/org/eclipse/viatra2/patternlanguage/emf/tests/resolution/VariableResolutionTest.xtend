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

package org.eclipse.viatra2.patternlanguage.emf.tests.resolution

import com.google.inject.Inject
import org.eclipse.viatra2.patternlanguage.EMFPatternLanguageInjectorProvider
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.PathExpressionConstraint
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.PatternCompositionConstraint
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.VariableValue
import org.eclipse.viatra2.patternlanguage.eMFPatternLanguage.EClassifierConstraint
import org.eclipse.viatra2.patternlanguage.eMFPatternLanguage.PatternModel
import org.eclipse.viatra2.patternlanguage.validation.EMFIssueCodes
import org.eclipse.xtext.junit4.InjectWith
import org.eclipse.xtext.junit4.XtextRunner
import org.eclipse.xtext.junit4.util.ParseHelper
import org.eclipse.xtext.junit4.validation.ValidationTestHelper
import org.junit.Test
import org.junit.runner.RunWith

import static org.junit.Assert.*

@RunWith(typeof(XtextRunner))
@InjectWith(typeof(EMFPatternLanguageInjectorProvider))
class VariableResolutionTest {
	@Inject
	ParseHelper parseHelper
	
	@Inject extension ValidationTestHelper
	
	@Test
	def parameterResolution() {
		val model = parseHelper.parse('
			import "http://www.eclipse.org/viatra2/patternlanguage/core/PatternLanguage"

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
			import "http://www.eclipse.org/viatra2/patternlanguage/core/PatternLanguage"

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
			import "http://www.eclipse.org/viatra2/patternlanguage/core/PatternLanguage"
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
			import "http://www.eclipse.org/viatra2/patternlanguage/core/PatternLanguage"

			pattern resolutionTest(Name) = {
				Pattern(Name2);
			}
		') as PatternModel
		val pattern = model.patterns.get(0)
		val parameter = pattern.parameters.get(0)
		val constraint = pattern.bodies.get(0).constraints.get(0) as EClassifierConstraint
		model.assertError(parameter.eClass, EMFIssueCodes::SYMBOLIC_VARIABLE_NEVER_REFERENCED)
		model.assertWarning(constraint.getVar().eClass, EMFIssueCodes::LOCAL_VARIABLE_REFERENCED_ONCE)
		assertTrue(parameter.name != constraint.getVar().getVar())
	}
	
	@Test
	def constraintVariableResolution() {
		val model = parseHelper.parse('
			import "http://www.eclipse.org/viatra2/patternlanguage/core/PatternLanguage"

			pattern resolutionTest(Name) = {
				Pattern(Name2);
				Pattern(Name2);
			}
		') as PatternModel
		val pattern = model.patterns.get(0)
		val parameter = pattern.parameters.get(0)
		model.assertError(parameter.eClass, EMFIssueCodes::SYMBOLIC_VARIABLE_NEVER_REFERENCED)
		val constraint0 = pattern.bodies.get(0).constraints.get(0) as EClassifierConstraint
		val constraint1 = pattern.bodies.get(0).constraints.get(0) as EClassifierConstraint
		assertTrue(parameter.name != constraint0.getVar().getVar())
		assertEquals(constraint0.getVar().getVar(), constraint1.getVar().getVar())				
	}
}