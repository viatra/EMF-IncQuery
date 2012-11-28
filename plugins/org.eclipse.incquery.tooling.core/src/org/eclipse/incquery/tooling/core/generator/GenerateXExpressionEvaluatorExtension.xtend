/*******************************************************************************
 * Copyright (c) 2010-2012, Andras Okros, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Andras Okros - initial API and implementation
 *******************************************************************************/

package org.eclipse.incquery.tooling.core.generator

import com.google.inject.Inject
import org.eclipse.viatra2.emf.incquery.runtime.IExtensions
import org.eclipse.viatra2.emf.incquery.tooling.generator.util.EMFPatternLanguageJvmModelInferrerUtil
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Pattern
import org.eclipse.xtext.xbase.lib.Pair

import static extension org.eclipse.viatra2.patternlanguage.core.helper.CorePatternLanguageHelper.*

class GenerateXExpressionEvaluatorExtension {
	
	@Inject extension EMFPatternLanguageJvmModelInferrerUtil
	
	def extensionContribution(Pattern pattern, String expressionUniqueID, String expressionUniqueNameInPattern, ExtensionGenerator exGen) {
		newArrayList(
			exGen.contribExtension(pattern.fullyQualifiedName, IExtensions::XEXPRESSIONEVALUATOR_EXTENSION_POINT_ID) [
				exGen.contribElement(it, "evaluator") [
					exGen.contribAttribute(it, "id", expressionUniqueID)
					exGen.contribAttribute(it, "evaluatorClass", pattern.packageName + "." + pattern.evaluatorClassName + expressionUniqueNameInPattern)
				]
			]
		)
	}
	
	def static getRemovableExtensionIdentifiers() {
		newArrayList(
			Pair::of("", IExtensions::XEXPRESSIONEVALUATOR_EXTENSION_POINT_ID)
		)
	}

}