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
import org.eclipse.xtext.xbase.typing.ITypeProvider
import static org.junit.Assert.*
import org.eclipse.emf.ecore.EClass
import org.eclipse.emf.ecore.EAttribute
import org.eclipse.emf.ecore.EDataType
import org.eclipse.emf.ecore.EClassifier
import java.util.List

@RunWith(typeof(XtextRunner))
@InjectWith(typeof(EMFPatternLanguageInjectorProvider))
class TypeInferenceTest {
	
	@Inject
	ParseHelper parseHelper
	
	@Inject
	EMFPatternLanguageJavaValidator validator
	
	@Inject
	Injector injector
	
	@Inject
	private ITypeProvider typeProvider
	
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

			pattern first(class1) = {
				EClass(class1);
			}
		') as PatternModel
		model.assertNoErrors
		tester.validate(model).assertOK
		val param = model.patterns.get(0).parameters.get(0)
		val type = typeProvider.getTypeForIdentifiable(param)
		
		assertEquals(typeof(EClass).canonicalName, type.qualifiedName) 
	}
	
	@Test
	def firstLevelFindType() {
		val model = parseHelper.parse('
			import "http://www.eclipse.org/emf/2002/Ecore"

			pattern first(class1) = {
				EClass(class1);
			}

			pattern second(class2) = {
				find first(class2);
			}
		') as PatternModel
		model.assertNoErrors
		tester.validate(model).assertOK
		
		val param1 = model.patterns.get(0).parameters.get(0)
		val param2 = model.patterns.get(1).parameters.get(0)
		val type1 = typeProvider.getTypeForIdentifiable(param1)
		val type2 = typeProvider.getTypeForIdentifiable(param2)
		
		assertEquals(typeof(EClass).canonicalName, type1.qualifiedName)
		assertEquals(typeof(EClass).canonicalName, type2.qualifiedName)
	}
	
	@Test
	def secondLevelFindType() {
		val model = parseHelper.parse('
			import "http://www.eclipse.org/emf/2002/Ecore"

			pattern first(class1) = {
				EClass(class1);
				check (class1.abstract != false);
			}

			pattern second(class2) = {
				find first(class2);
				check (class2.abstract != false);
			}

			pattern third(class3) = {
				find second(class3);
				check (class3.abstract != false);
			}
		') as PatternModel
		model.assertNoErrors
		tester.validate(model).assertOK
		
		val param1 = model.patterns.get(0).parameters.get(0)
		val param2 = model.patterns.get(1).parameters.get(0)
		val param3 = model.patterns.get(2).parameters.get(0)
		val type1 = typeProvider.getTypeForIdentifiable(param1)
		val type2 = typeProvider.getTypeForIdentifiable(param2)
		val type3 = typeProvider.getTypeForIdentifiable(param3)
		
		assertEquals(typeof(EClass).canonicalName, type1.qualifiedName)
		assertEquals(typeof(EClass).canonicalName, type2.qualifiedName)
		assertEquals(typeof(EClass).canonicalName, type3.qualifiedName)
	}
	
	@Test
	def zeroLevelPathType() {
		val model = parseHelper.parse('
			import "http://www.eclipse.org/emf/2002/Ecore"

			pattern firstPath(class1, attribute1) = {
				EClass.eAttributes(class1, attribute1);
			}
		') as PatternModel
		model.assertNoErrors
		tester.validate(model).assertOK
		
		val param1 = model.patterns.get(0).parameters.get(0)
		val param2 = model.patterns.get(0).parameters.get(1)
		
		val type1 = typeProvider.getTypeForIdentifiable(param1)
		val type2 = typeProvider.getTypeForIdentifiable(param2)
		
		assertEquals(typeof(EClass).canonicalName, type1.qualifiedName)
		assertEquals(typeof(EAttribute).canonicalName, type2.qualifiedName)
	}
	
	@Test
	def firstLevelPathType() {
		val model = parseHelper.parse('
			import "http://www.eclipse.org/emf/2002/Ecore"

			pattern firstPath(class1, attribute1) = {
				EClass.eAttributes(class1, attribute1);
			}

			pattern secondPath(class2, attribute2) = {
				find firstPath(class2, attribute2);
			}
		') as PatternModel
		model.assertNoErrors
		tester.validate(model).assertOK
		
		val param11 = model.patterns.get(0).parameters.get(0)
		val param21 = model.patterns.get(0).parameters.get(1)
		val param12 = model.patterns.get(1).parameters.get(0)
		val param22 = model.patterns.get(1).parameters.get(1)
		
		val type11 = typeProvider.getTypeForIdentifiable(param11)
		val type21 = typeProvider.getTypeForIdentifiable(param21)
		val type12 = typeProvider.getTypeForIdentifiable(param12)
		val type22 = typeProvider.getTypeForIdentifiable(param22)
		
		assertEquals(typeof(EClass).canonicalName, type11.qualifiedName)
		assertEquals(typeof(EClass).canonicalName, type12.qualifiedName)
		assertEquals(typeof(EAttribute).canonicalName, type21.qualifiedName)
		assertEquals(typeof(EAttribute).canonicalName, type22.qualifiedName)
	}
	
	@Test
	def injectivityConstraintTest() {
		val model = parseHelper.parse('
			import "http://www.eclipse.org/emf/2002/Ecore"

			pattern injectivity1(class1, class2) = {
				EClass(class1);
				class1 == class2;
			}
		') as PatternModel
		model.assertNoErrors
		tester.validate(model).assertOK
		
		val param1 = model.patterns.get(0).parameters.get(0)
		val param2 = model.patterns.get(0).parameters.get(1)
		
		val type1 = typeProvider.getTypeForIdentifiable(param1)
		val type2 = typeProvider.getTypeForIdentifiable(param2)
		
		assertEquals(typeof(EClass).canonicalName, type1.qualifiedName)
		assertEquals(typeof(EClass).canonicalName, type2.qualifiedName)
	}
	
	@Test
	def parameterTest() {
		val model = parseHelper.parse('
			import "http://www.eclipse.org/emf/2002/Ecore"

			pattern parameterTest(parameter) = {
				EDataType(parameter); 
			} or { 
				EClass(parameter);
			} 
		') as PatternModel
		model.assertNoErrors
		tester.validate(model).assertOK
		
		val parameter1 = model.patterns.get(0).parameters.get(0)
		val variable1 = model.patterns.get(0).bodies.get(0).variables.get(0)
		val variable2 = model.patterns.get(0).bodies.get(1).variables.get(0)
		
		val type1 = typeProvider.getTypeForIdentifiable(parameter1)
		val type2 = typeProvider.getTypeForIdentifiable(variable1)
		val type3 = typeProvider.getTypeForIdentifiable(variable2)
		
		assertEquals(typeof(EClassifier).canonicalName, type1.qualifiedName)
		assertEquals(typeof(EDataType).canonicalName, type2.qualifiedName)
		assertEquals(typeof(EClass).canonicalName, type3.qualifiedName)
	}
	
	@Test
	def intLiteralType() {
		val model = parseHelper.parse('
			pattern literalValue(literalType) = {
				literalType == 10;
			}
		') as PatternModel
		model.assertNoErrors
		tester.validate(model).assertOK
		val param = model.patterns.get(0).parameters.get(0)
		val type = typeProvider.getTypeForIdentifiable(param)
		
		assertEquals(typeof(Integer).canonicalName, type.qualifiedName) 
	}
	
	@Test
	def stringLiteralType() {
		val model = parseHelper.parse('
			pattern literalValue(literalType) = {
				literalType == "helloworld";
			}
		') as PatternModel
		model.assertNoErrors
		tester.validate(model).assertOK
		val param = model.patterns.get(0).parameters.get(0)
		val type = typeProvider.getTypeForIdentifiable(param)
		
		assertEquals(typeof(String).canonicalName, type.qualifiedName) 
	}
	
	@Test
	def boolLiteralType() {
		val model = parseHelper.parse('
			pattern literalValue(literalType) = {
				literalType == true;
			}
		') as PatternModel
		model.assertNoErrors
		tester.validate(model).assertOK
		val param = model.patterns.get(0).parameters.get(0)
		val type = typeProvider.getTypeForIdentifiable(param)
		
		assertEquals(typeof(Boolean).canonicalName, type.qualifiedName) 
	}
	
	@Test
	def doubleLiteralType() {
		val model = parseHelper.parse('
			pattern literalValue(literalType) = {
				literalType == 3.14;
			}
		') as PatternModel
		model.assertNoErrors
		tester.validate(model).assertOK
		val param = model.patterns.get(0).parameters.get(0)
		val type = typeProvider.getTypeForIdentifiable(param)
		
		assertEquals(typeof(Double).canonicalName, type.qualifiedName) 
	}
	
	@Test
	def countAggregatedComputationValueType() {
		val model = parseHelper.parse('
			pattern literalValue(literalType) = {
				uselessVariable == 10;
				literalType == count find patternToFind(uselessVariable);
			}

			pattern patternToFind(uselessParameter) = {
				uselessParameter == 10;
				check(true);
			}
		') as PatternModel
		model.assertNoErrors
		tester.validate(model).assertOK
		val param = model.patterns.get(0).parameters.get(0)
		val type = typeProvider.getTypeForIdentifiable(param)
		
		assertEquals("literalType", param.name) 
		assertEquals(typeof(Integer).canonicalName, type.qualifiedName) 
	}
	
}