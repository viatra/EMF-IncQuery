/*******************************************************************************
 * Copyright (c) 2010-2012, Zoltan Ujhelyi, Tamas Szabo, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Zoltan Ujhelyi, Tamas Szabo - initial API and implementation
 *******************************************************************************/

package org.eclipse.viatra2.emf.incquery.tooling.generator.databinding

import com.google.inject.Inject
import org.eclipse.viatra2.emf.incquery.tooling.generator.ExtensionGenerator
import org.eclipse.viatra2.emf.incquery.tooling.generator.fragments.IGenerationFragment
import org.eclipse.viatra2.emf.incquery.tooling.generator.util.EMFPatternLanguageJvmModelInferrerUtil
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Pattern
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.impl.StringValueImpl
import org.eclipse.xtext.generator.IFileSystemAccess

import static extension org.eclipse.viatra2.patternlanguage.core.helper.CorePatternLanguageHelper.*
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Annotation
import org.eclipse.xtext.xbase.lib.Pair
import org.eclipse.viatra2.patternlanguage.core.helper.CorePatternLanguageHelper
import org.eclipse.core.runtime.Path
import org.eclipse.viatra2.emf.incquery.databinding.runtime.util.DatabindingAdapterUtil

class DatabindingGenerator implements IGenerationFragment {
	
	private static String DATABINDINGEXTENSION_PREFIX = "extension.databinding."
	private static String DATABINDINGEXTENSION_POINT = "org.eclipse.viatra2.emf.incquery.databinding.runtime.databinding"
	@Inject extension EMFPatternLanguageJvmModelInferrerUtil
	private static String annotationLiteral = "ObservableValue"

	override generateFiles(Pattern pattern, IFileSystemAccess fsa) {
		if (hasAnnotationLiteral(pattern, annotationLiteral)) {
			fsa.generateFile(pattern.packagePath + "/" + pattern.name.toFirstUpper + "DatabindingAdapter.java", pattern.patternHandler)
		}
	}
	
	override cleanUp(Pattern pattern, IFileSystemAccess fsa) {
		fsa.deleteFile(pattern.packagePath + "/" + pattern.realPatternName.toFirstUpper + "DatabindingAdapter.java")
	}
	
	override removeExtension(Pattern pattern) {
		newArrayList(
			Pair::of(pattern.databindingContributionId, DATABINDINGEXTENSION_POINT)
		)
	}
	
	override getRemovableExtensions() {
		newArrayList(
			Pair::of(DATABINDINGEXTENSION_PREFIX, DATABINDINGEXTENSION_POINT)
		)
	}
	
	override getProjectDependencies() {
		newArrayList("org.eclipse.core.databinding.property", 
		"org.eclipse.core.databinding.observable", 
		"org.eclipse.viatra2.emf.incquery.databinding.runtime",
		"org.eclipse.viatra2.emf.incquery.runtime")
	}
	
	override getProjectPostfix() {
		"databinding"
	}
	
	def databindingContributionId(Pattern pattern) {
		DATABINDINGEXTENSION_PREFIX+CorePatternLanguageHelper::getFullyQualifiedName(pattern)
	}
	
	override extensionContribution(Pattern pattern, ExtensionGenerator exGen) {
		if (hasAnnotationLiteral(pattern, annotationLiteral)) {		
			var tmp = ""
			
			for (a : pattern.annotations) {
				if (a.name.matches("PatternUI")) {
					for (ap : a.parameters) {
						if (ap.name.matches("message")) {
							tmp = (ap.value as StringValueImpl).value
						}
					}
				}
			}
			
			val message = tmp;
			
			newArrayList(
			exGen.contribExtension(pattern.databindingContributionId, DATABINDINGEXTENSION_POINT) [
				exGen.contribElement(it, "databinding") [
					exGen.contribAttribute(it, "class", pattern.packageName+"."+pattern.name.toFirstUpper+"DatabindingAdapter")
					exGen.contribAttribute(it, "patternName", pattern.fullyQualifiedName)
					exGen.contribAttribute(it, "message", message)
					exGen.contribAttribute(it, "matcherFactoryClass", pattern.packageName+"."+pattern.matcherFactoryClassName)
				]
			]
			)
		}
		else {
			return newArrayList()
		}
	}
	
	def getElementOfObservableValue(Annotation a, String literal) {
		for (ap : a.parameters) {
			if (ap.name.matches(literal)) {
				return (ap.value as StringValueImpl).value
			}
		}
		
		return null
	}
	
	def hasAnnotationLiteral(Pattern pattern, String literal) {
		for (a : pattern.annotations) {
			if (a.name.matches(literal)) {
				return true;
			}
		}
		return false;
	}
	
	def patternHandler(Pattern pattern) '''
		package «pattern.packageName»;
		
		import java.util.HashMap;

		import org.eclipse.viatra2.emf.incquery.databinding.runtime.BaseGeneratedDatabindingAdapter;

		import «pattern.packageName + "." + pattern.matchClassName»;

		public class «pattern.name.toFirstUpper»DatabindingAdapter extends BaseGeneratedDatabindingAdapter<«pattern.matchClassName»> {
		
			public «pattern.name.toFirstUpper»DatabindingAdapter() {
				super();
				parameterMap = new HashMap<String, String>();
				«val valueMap = DatabindingAdapterUtil::calculateObservableValues(pattern)»
				«FOR value : valueMap.keySet »
				    parameterMap.put("«value»", "«valueMap.get(value)»");
				«ENDFOR»
			}
			
		}

	'''

	
	override getAdditionalBinIncludes() {
		return newArrayList(new Path("plugin.xml"))
	}
	
}