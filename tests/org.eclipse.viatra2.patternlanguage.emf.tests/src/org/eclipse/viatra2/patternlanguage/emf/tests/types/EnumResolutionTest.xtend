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

import org.eclipse.xtext.junit4.XtextRunner
import org.junit.runner.RunWith
import org.eclipse.xtext.junit4.InjectWith
import org.eclipse.viatra2.patternlanguage.EMFPatternLanguageInjectorProvider
import com.google.inject.Inject
import org.eclipse.xtext.junit4.util.ParseHelper
import org.eclipse.xtext.junit4.validation.ValidationTestHelper
import org.junit.Test
import org.eclipse.viatra2.patternlanguage.eMFPatternLanguage.PatternModel
import static org.junit.Assert.*
import org.eclipse.viatra2.patternlanguage.eMFPatternLanguage.EMFPatternLanguagePackage
import org.eclipse.xtext.diagnostics.Diagnostic
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.PathExpressionConstraint
import org.eclipse.viatra2.patternlanguage.eMFPatternLanguage.ReferenceType
import org.eclipse.viatra2.patternlanguage.eMFPatternLanguage.EnumValue
import org.eclipse.emf.codegen.ecore.genmodel.GenModelPackage

@RunWith(typeof(XtextRunner))
@InjectWith(typeof(EMFPatternLanguageInjectorProvider))
class EnumResolutionTest {
	@Inject
	ParseHelper parseHelper
	
	@Inject extension ValidationTestHelper
	
	@Test
	def eEnumResolutionSuccess() {
		val model = parseHelper.parse('
			import "http://www.eclipse.org/emf/2002/GenModel"

			pattern resolutionTest(Model) = {
				GenModel(Model);
				GenModel.runtimeVersion(Model, ::EMF23);
			}
		') as PatternModel
		model.assertNoErrors
		val pattern = model.patterns.get(0)
		val constraint = pattern.bodies.get(0).constraints.get(1) as PathExpressionConstraint
		val tail = constraint.head.tail
		val type = tail.type as ReferenceType
		assertEquals(type.refname.EType, GenModelPackage$Literals::GEN_RUNTIME_VERSION)
		val value = constraint.head.dst as EnumValue
		assertEquals(value.literal, GenModelPackage$Literals::GEN_RUNTIME_VERSION.getEEnumLiteral("EMF23"))		
	}
	
	@Test
	def eQualifiedEnumResolutionSuccess() {
		val model = parseHelper.parse('
			import "http://www.eclipse.org/emf/2002/GenModel"

			pattern resolutionTest(Model) = {
				GenModel(Model);
				GenModel.runtimeVersion(Model, GenRuntimeVersion::EMF23);
			}
		') as PatternModel
		model.assertNoErrors
		val pattern = model.patterns.get(0)
		val constraint = pattern.bodies.get(0).constraints.get(1) as PathExpressionConstraint
		val tail = constraint.head.tail
		val type = tail.type as ReferenceType
		assertEquals(type.refname.EType, GenModelPackage$Literals::GEN_RUNTIME_VERSION)
		val value = constraint.head.dst as EnumValue
		assertEquals(value.literal, GenModelPackage$Literals::GEN_RUNTIME_VERSION.getEEnumLiteral("EMF23"))		
	}
	
	@Test
	def eEnumResolutionInvalidLiteral() {
		val model = parseHelper.parse('
			import "http://www.eclipse.org/emf/2002/GenModel"

			pattern resolutionTest(Model) = {
				GenModel(Model);
				GenModel.runtimeVersion(Model, ::NOTEXIST);
			}
		') as PatternModel
		model.assertError(EMFPatternLanguagePackage$Literals::ENUM_VALUE,
			Diagnostic::LINKING_DIAGNOSTIC, "reference to EEnumLiteral")
	}
	@Test
	def eEnumResolutionNotEnum() {
		val model = parseHelper.parse('
			import "http://www.eclipse.org/emf/2002/GenModel"

			pattern resolutionTest(Model) = {
				GenModel(Model);
				GenModel.copyrightText(Model, ::EMF23);
			}
		') as PatternModel
		//XXX With better type inference this error message should be replaced
		model.assertError(EMFPatternLanguagePackage$Literals::ENUM_VALUE,
			Diagnostic::LINKING_DIAGNOSTIC, "reference to EEnumLiteral")
	}
		
	@Test
	def eEnumResolutionMissingQualifier() {
		val model = parseHelper.parse('
			import "http://www.eclipse.org/emf/2002/GenModel"


			pattern runtimeVersion(Version) = {
				GenRuntimeVersion(Version);
			}

			pattern call() = {
				find runtimeVersion(::EMF24);
			}
		') as PatternModel
		model.assertError(EMFPatternLanguagePackage$Literals::ENUM_VALUE,
			Diagnostic::LINKING_DIAGNOSTIC, "reference to EEnumLiteral")
	}
	
	@Test
	def validateIncorrectEnumWithEquality() {
		val model = parseHelper.parse('
			import "http://www.eclipse.org/emf/2002/GenModel"

			pattern resolutionTest(Model) = {
				GenModel(Model);
				GenModel.runtimeVersion(Model, Version);
				Version == ::EMF23;
			}
		') as PatternModel
		model.assertError(EMFPatternLanguagePackage$Literals::ENUM_VALUE,
			Diagnostic::LINKING_DIAGNOSTIC, "reference to EEnumLiteral")
	}
	
}