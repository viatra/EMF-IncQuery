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

package org.eclipse.incquery.patternlanguage.emf.tests.types

import org.junit.runner.RunWith
import org.eclipse.xtext.junit4.InjectWith
import org.eclipse.incquery.patternlanguage.emf.EMFPatternLanguageInjectorProvider
import org.eclipse.xtext.junit4.XtextRunner
import com.google.inject.Inject
import org.eclipse.xtext.junit4.util.ParseHelper
import org.eclipse.xtext.junit4.validation.ValidationTestHelper
import static org.junit.Assert.*
import org.eclipse.incquery.patternlanguage.emf.eMFPatternLanguage.EMFPatternLanguagePackage
import org.eclipse.xtext.diagnostics.Diagnostic
import org.junit.Test
import org.eclipse.incquery.patternlanguage.emf.eMFPatternLanguage.PatternModel
import org.eclipse.incquery.patternlanguage.patternLanguage.PathExpressionConstraint
import org.eclipse.incquery.patternlanguage.emf.eMFPatternLanguage.ReferenceType
import org.eclipse.incquery.patternlanguage.patternLanguage.PatternLanguagePackage

@RunWith(typeof(XtextRunner))
@InjectWith(typeof(EMFPatternLanguageInjectorProvider))
class EReferenceResolutionTest {

	@Inject
	ParseHelper parseHelper
	
	@Inject extension ValidationTestHelper
	
	@Test
	def referenceResolution() {
		val model = parseHelper.parse('
			import "http://www.eclipse.org/incquery/patternlanguage/PatternLanguage"

			pattern resolutionTest(Name : Pattern, Body) = {
				Pattern.bodies(Name, Body);
			}
		') as PatternModel
		model.assertNoErrors
		val pattern = model.patterns.get(0)
		val constraint = pattern.bodies.get(0).constraints.get(0) as PathExpressionConstraint
		val tail = constraint.head.tail
		val type = tail.type as ReferenceType
		assertEquals(type.refname.EType, PatternLanguagePackage$Literals::PATTERN_BODY)		
	}
	
	@Test
	def referenceResolutionChain() {
		val model = parseHelper.parse('
			import "http://www.eclipse.org/incquery/patternlanguage/PatternLanguage"

			pattern resolutionTest(Name : Pattern, Constraint) = {
				Pattern.bodies.constraints(Name, Constraint);
			}
		') as PatternModel
		model.assertNoErrors
		val pattern = model.patterns.get(0)
		val constraint = pattern.bodies.get(0).constraints.get(0) as PathExpressionConstraint
		val interim = constraint.head.tail
		val interimType = interim.type as ReferenceType
		assertEquals(interimType.refname.EType, PatternLanguagePackage$Literals::PATTERN_BODY)		
		val tail = interim.tail
		val type = tail.type as ReferenceType
		assertEquals(type.refname.EType, PatternLanguagePackage$Literals::CONSTRAINT)		
	}
	
	@Test
	def referenceResolutionEscapedKeyword() {
		val model = parseHelper.parse('
			import "http://www.eclipse.org/incquery/patternlanguage/emf/EMFPatternLanguage"

			pattern keywordAsIdentifier(A,B) = {
				EClassifierConstraint.^var(A,B); 
			}
		') as PatternModel
		model.assertNoErrors
		val pattern = model.patterns.get(0)
		val constraint = pattern.bodies.get(0).constraints.get(0) as PathExpressionConstraint
		val type = constraint.head.tail.type as ReferenceType
		assertEquals(type.refname, EMFPatternLanguagePackage$Literals::ECLASSIFIER_CONSTRAINT__VAR)
	}
	
	@Test
	def referenceResolutionInvalid(){
		val model = parseHelper.parse('
			import "http://www.eclipse.org/incquery/patternlanguage/PatternLanguage"

			pattern resolutionTest(Name : Pattern, Constraint) = {
				Pattern.notExist(Name, Constraint);
			}
		') as PatternModel
		model.assertError(EMFPatternLanguagePackage$Literals::REFERENCE_TYPE,
			Diagnostic::LINKING_DIAGNOSTIC, "reference to EStructuralFeature")
	}
}