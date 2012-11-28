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

package org.eclipse.incquery.tooling.core.generator

import com.google.inject.Inject
import org.eclipse.incquery.runtime.IExtensions
import org.eclipse.incquery.tooling.core.generator.util.EMFPatternLanguageJvmModelInferrerUtil
import org.eclipse.incquery.patternlanguage.patternLanguage.Pattern
import org.eclipse.xtext.common.types.JvmDeclaredType
import org.eclipse.xtext.xbase.jvmmodel.IJvmModelAssociations
import org.eclipse.xtext.xbase.lib.Pair

import static extension org.eclipse.incquery.patternlanguage.helper.CorePatternLanguageHelper.*
import org.eclipse.xtext.common.types.JvmType
import org.eclipse.xtext.common.types.JvmIdentifiableElement

class GenerateMatcherFactoryExtension {
	
	@Inject	IJvmModelAssociations associations
	@Inject extension EMFPatternLanguageJvmModelInferrerUtil 
	
	def extensionContribution(Pattern pattern, ExtensionGenerator exGen) {
		newArrayList(
		exGen.contribExtension(pattern.getFullyQualifiedName, IExtensions::MATCHERFACTORY_EXTENSION_POINT_ID) [
			exGen.contribElement(it, "matcher") [
				exGen.contribAttribute(it, "id", pattern.getFullyQualifiedName)
				
				val matcherFactoryClass = associations.getJvmElements(pattern).
				  findFirst[it instanceof JvmDeclaredType && (it as JvmDeclaredType).simpleName.equals(pattern.matcherFactoryClassName)] as JvmDeclaredType 
				val providerClass = matcherFactoryClass.members.
				  findFirst([it instanceof JvmType && (it as JvmType).simpleName.equals(pattern.matcherFactoryProviderClassName)]) as JvmIdentifiableElement
				  
				exGen.contribAttribute(it, "factoryProvider", providerClass.qualifiedName)
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