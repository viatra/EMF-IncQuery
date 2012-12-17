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

import org.eclipse.xtext.junit4.XtextRunner
import org.junit.runner.RunWith
import org.eclipse.xtext.junit4.InjectWith
import org.eclipse.incquery.patternlanguage.emf.EMFPatternLanguageInjectorProvider
import com.google.inject.Inject
import org.eclipse.xtext.junit4.util.ParseHelper
import org.eclipse.xtext.junit4.validation.ValidationTestHelper
import org.junit.Test
import org.eclipse.incquery.patternlanguage.emf.eMFPatternLanguage.PatternModel
import static org.junit.Assert.*
import org.eclipse.incquery.patternlanguage.emf.eMFPatternLanguage.EMFPatternLanguagePackage
import org.eclipse.xtext.diagnostics.Diagnostic
import org.eclipse.incquery.patternlanguage.emf.helper.EMFPatternLanguageHelper

@RunWith(typeof(XtextRunner))
@InjectWith(typeof(EMFPatternLanguageInjectorProvider))
class ImportResolutionTest {
	@Inject
	ParseHelper parseHelper
	
	@Inject extension ValidationTestHelper
	
	@Test
	def importResolution() {
		val model = parseHelper.parse('
			import "http://www.eclipse.org/incquery/patternlanguage/PatternLanguage"

			pattern resolutionTest(Name) = {
				Pattern(Name);
			}
		') as PatternModel
		model.assertNoErrors
		val importDecl = EMFPatternLanguageHelper::getAllPackageImports(model).get(0)
		val ePackage = importDecl.EPackage
		assertNotNull(ePackage)
		assertEquals(ePackage.nsURI, "http://www.eclipse.org/incquery/patternlanguage/PatternLanguage")
	}
	
	@Test
	def multipleImportResolution() {
		val model = parseHelper.parse('
			import "http://www.eclipse.org/incquery/patternlanguage/PatternLanguage";
			import "http://www.eclipse.org/incquery/patternlanguage/emf/EMFPatternLanguage";

			pattern resolutionTest(Name) = {
				Pattern(Name);
			}
		') as PatternModel
		model.assertNoErrors
		val imports = EMFPatternLanguageHelper::getAllPackageImports(model)
		var importDecl = imports.get(0)
		var ePackage = importDecl.EPackage
		assertNotNull(ePackage)
		assertEquals(ePackage.nsURI, "http://www.eclipse.org/incquery/patternlanguage/PatternLanguage")
		importDecl = imports.get(1)
		ePackage = importDecl.EPackage
		assertNotNull(ePackage)
		assertEquals(ePackage.nsURI, "http://www.eclipse.org/incquery/patternlanguage/emf/EMFPatternLanguage")
	}
	
	@Test
	def importResolutionFailed() {
		val model = parseHelper.parse('
			import "http://nonexisting.package.uri"

			pattern resolutionTest(Name) = {
				Pattern(Name2);
			}
		') as PatternModel
		val importDecl = model.importPackages.get(0)
		importDecl.assertError(EMFPatternLanguagePackage$Literals::PACKAGE_IMPORT,
			Diagnostic::LINKING_DIAGNOSTIC, "reference to EPackage"
		)
	}
	
	
}