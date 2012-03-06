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
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.PatternLanguagePackage
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
	
	
}