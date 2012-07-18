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

package org.eclipse.viatra2.emf.incquery.tooling.generator

import com.google.inject.Inject
import org.eclipse.viatra2.emf.incquery.runtime.IExtensions
import org.eclipse.viatra2.emf.incquery.tooling.generator.util.EMFPatternLanguageJvmModelInferrerUtil
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Pattern
import org.eclipse.xtext.common.types.JvmIdentifiableElement
import org.eclipse.xtext.common.types.JvmType
import org.eclipse.xtext.xbase.jvmmodel.IJvmModelAssociations
import org.eclipse.xtext.xbase.lib.Pair

import static extension org.eclipse.viatra2.patternlanguage.core.helper.CorePatternLanguageHelper.*

class GenerateMatcherFactoryExtension {
	
	@Inject
	IJvmModelAssociations associations
	@Inject extension EMFPatternLanguageJvmModelInferrerUtil 
	
	def extensionContribution(Pattern pattern, ExtensionGenerator exGen) {
		newArrayList(
		exGen.contribExtension(pattern.getFullyQualifiedName, IExtensions::MATCHERFACTORY_EXTENSION_POINT_ID) [
			exGen.contribElement(it, "matcher") [
				exGen.contribAttribute(it, "id", pattern.getFullyQualifiedName)
				val el = associations.getJvmElements(pattern).
				  findFirst[it instanceof JvmType && (it as JvmType).simpleName.equals(pattern.matcherFactoryClassName)] as JvmIdentifiableElement
				exGen.contribAttribute(it, "factoryProvider", el.qualifiedName)
			]
		]
		)
	}
	
	def static getRemovableExtensionIdentifiers() {
		newArrayList(
			Pair::of("", IExtensions::MATCHERFACTORY_EXTENSION_POINT_ID)
		)
	}
}