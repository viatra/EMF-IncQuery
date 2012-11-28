/*******************************************************************************
 * Copyright (c) 2010-2012, Zoltan Ujhelyi, Abel Hegedus, Tamas Szabo, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Zoltan Ujhelyi, Abel Hegedus, Tamas Szabo - initial API and implementation
 *******************************************************************************/

package org.eclipse.incquery.validation.tooling

import com.google.inject.Inject
import org.eclipse.incquery.tooling.core.generator.ExtensionGenerator
import org.eclipse.incquery.databinding.tooling.DatabindingGenerator
import org.eclipse.incquery.tooling.core.generator.fragments.IGenerationFragment
import org.eclipse.incquery.tooling.core.generator.genmodel.IEiqGenmodelProvider
import org.eclipse.incquery.tooling.core.generator.util.EMFPatternLanguageJvmModelInferrerUtil
import org.eclipse.incquery.patternlanguage.helper.CorePatternLanguageHelper
import org.eclipse.incquery.patternlanguage.patternLanguage.Pattern
import org.eclipse.xtext.generator.IFileSystemAccess
import org.eclipse.xtext.xbase.lib.Pair

import static extension org.eclipse.incquery.patternlanguage.helper.CorePatternLanguageHelper.*
import org.eclipse.incquery.patternlanguage.emf.eMFPatternLanguage.PatternModel
import org.eclipse.incquery.patternlanguage.patternLanguage.StringValue
import org.eclipse.incquery.patternlanguage.patternLanguage.Annotation
import org.eclipse.incquery.tooling.core.generator.builder.IErrorFeedback
import org.eclipse.xtext.diagnostics.Severity

class ValidationGenerator extends DatabindingGenerator implements IGenerationFragment {
	
	@Inject extension EMFPatternLanguageJvmModelInferrerUtil
	
	@Inject
	private IEiqGenmodelProvider eiqGenModelProvider
	
	@Inject
	private IErrorFeedback feedback
	
	private static String VALIDATIONEXTENSION_PREFIX = "validation.constraint."
	private static String UI_VALIDATION_MENUS_PREFIX = "generated.incquery.validation.menu."
	private static String UI_VALIDATION_MENU_URI = "popup:org.eclipse.viatra2.emf.incquery.validation.runtime.ui.menu?after=additions"
	private static String VALIDATION_EXTENSION_POINT = "org.eclipse.viatra2.emf.incquery.validation.runtime.constraint"
	private static String ECLIPSE_MENUS_EXTENSION_POINT = "org.eclipse.ui.menus"
	private static String annotationLiteral = "Constraint"
	private static String VALIDATION_ERROR_CODE = "org.eclipse.viatra2.emf.incquery.validation.error"
	
	override generateFiles(Pattern pattern, IFileSystemAccess fsa) {
		
		for(ann : pattern.annotations){
		  if(ann.name == annotationLiteral){
  			fsa.generateFile(pattern.constraintClassJavaFile(ann), pattern.patternHandler(ann))
		  }
		}
	}
	
	override cleanUp(Pattern pattern, IFileSystemAccess fsa) {
		for(ann : pattern.annotations){
		  if(ann.name == annotationLiteral){
	     	fsa.deleteFile(pattern.constraintClassJavaFile(ann))
  		}
		}
	}
	
	override removeExtension(Pattern pattern) {
		val p = Pair::of(pattern.constraintContributionId, VALIDATION_EXTENSION_POINT)
		val extensionList = newArrayList(p)
		
		val patternModel = pattern.eContainer as PatternModel;
    for (imp : patternModel.importPackages) {
      val pack = imp.EPackage;
      val genPackage = eiqGenModelProvider.findGenPackage(pattern, pack);
      
      if (genPackage != null) {
        val editorId = genPackage.qualifiedEditorClassName+"ID";
        if (!editorId.nullOrEmpty) {
          extensionList.add(Pair::of(menuContributionId(editorId), ECLIPSE_MENUS_EXTENSION_POINT))
        }
      }
    }
    
    for(ann : pattern.annotations){
      if(ann.name == annotationLiteral){
        val editorIds = ann.getAnnotationParameterValue("targetEditorId")
        for (id : editorIds){
          val editorId = (id as StringValue).value
          extensionList.add(Pair::of(menuContributionId(editorId), ECLIPSE_MENUS_EXTENSION_POINT))
        }
      }
    }
		return extensionList
	}
	
	override getRemovableExtensions() {
		newArrayList(
			Pair::of(VALIDATIONEXTENSION_PREFIX, VALIDATION_EXTENSION_POINT), 
			Pair::of(UI_VALIDATION_MENUS_PREFIX, ECLIPSE_MENUS_EXTENSION_POINT)
		)
	}
	
	override getProjectDependencies() {
		newArrayList("org.eclipse.viatra2.emf.incquery.runtime",
			"org.eclipse.viatra2.emf.incquery.validation.runtime"
		)
	}
	
	override getProjectPostfix() {
		"validation"
	}
	
	override extensionContribution(Pattern pattern, ExtensionGenerator exGen) {
		val extensionList = newArrayList(
      exGen.contribExtension(pattern.constraintContributionId, VALIDATION_EXTENSION_POINT) [
        for(ann : pattern.annotations){
          if(ann.name == annotationLiteral){
            exGen.contribElement(it, "constraint") [
              exGen.contribAttribute(it, "class", pattern.constraintClassName(ann))
              exGen.contribAttribute(it, "name", pattern.fullyQualifiedName)
              
              val editorIds = ann.getAnnotationParameterValue("targetEditorId")
              for (id : editorIds){
                val editorId = (id as StringValue).value
                exGen.contribElement(it, "enabledForEditor")[
                  exGen.contribAttribute(it, "editorId", editorId)
                ]
              }
              
              val patternModel = pattern.eContainer as PatternModel;
              for (imp : patternModel.importPackages) {
                val pack = imp.EPackage;
                val genPackage = eiqGenModelProvider.findGenPackage(pattern, pack);
                
                if (genPackage != null) {
                  val editorId = genPackage.qualifiedEditorClassName+"ID";
                  exGen.contribElement(it, "enabledForEditor")[
                    exGen.contribAttribute(it, "editorId", editorId)
                  ]
                }
              }
            ]
          }
        }
      ]
    )
    /*for(ann : pattern.annotations){
      if(ann.name == annotationLiteral){
        val editorIds = ann.getAnnotationParameterValue("targetEditorId")
        for (id : editorIds){
          val editorId = (id as StringValue).value
          editorId.createExtensionContributionForEditorId(exGen,extensionList)
        }
      }
    }
		
		if(pattern.hasAnnotationLiteral(annotationLiteral)){
  		val patternModel = pattern.eContainer as PatternModel;
  		for (imp : patternModel.importPackages) {
  			val pack = imp.EPackage;
  			val genPackage = eiqGenModelProvider.findGenPackage(pattern, pack);
  			
  			if (genPackage != null) {
  				val editorId = genPackage.qualifiedEditorClassName+"ID";
  				editorId.createExtensionContributionForEditorId(exGen,extensionList)
  			}
  		}
		}*/
			
		return extensionList
	}
	
	/*def createExtensionContributionForEditorId(String editorId, ExtensionGenerator exGen, List extensionList){
   
   if (!editorId.nullOrEmpty && !contributedEditorIds.contains(editorId)) {
      val editorMenuContribution = exGen.contribExtension(menuContributionId(editorId), ECLIPSE_MENUS_EXTENSION_POINT) [
      exGen.contribElement(it, "menuContribution") [
        exGen.contribAttribute(it, "locationURI", UI_VALIDATION_MENU_URI)
          exGen.contribElement(it, "command") [
            exGen.contribAttribute(it, "commandId", "org.eclipse.viatra2.emf.incquery.validation.runtime.ui.initValidatorsOnEditor")
            exGen.contribAttribute(it, "style", "push")
            exGen.contribElement(it, "visibleWhen") [
              exGen.contribAttribute(it, "checkEnabled", "false")
              exGen.contribElement(it, "with")[
                exGen.contribAttribute(it, "variable", "activeEditorId")
                exGen.contribElement(it, "equals")[
                  exGen.contribAttribute(it, "value", editorId)
                ]
              ]
            ]
          ]
        ] 
      ]
      extensionList.add(editorMenuContribution)
      contributedEditorIds.add(editorId)
    }
	}*/
	
	def constraintClassName(Pattern pattern, Annotation annotation) {
		String::format("%s.%s%s%s", pattern.packageName, pattern.realPatternName.toFirstUpper, annotationLiteral,pattern.annotations.indexOf(annotation))
	}
	
	def constraintClassPath(Pattern pattern, Annotation annotation) {
		String::format("%s/%s%s%s", pattern.packagePath, pattern.realPatternName.toFirstUpper, annotationLiteral,pattern.annotations.indexOf(annotation))
	}
	
	def constraintClassJavaFile(Pattern pattern, Annotation annotation) {
		pattern.constraintClassPath(annotation) + ".java"
	}
	
	def constraintContributionId(Pattern pattern) {
		return VALIDATIONEXTENSION_PREFIX+CorePatternLanguageHelper::getFullyQualifiedName(pattern)
	}
	
	def menuContributionId(String editorId) {
		return String::format("%s%s", UI_VALIDATION_MENUS_PREFIX, editorId)
	}
	
	def getElementOfConstraintAnnotation(Annotation annotation, String elementName) {
    val ap = annotation.getAnnotationParameterValue(elementName)
    if(ap != null && ap.size == 1){
      return (ap.get(0) as StringValue).value
    } else {
      return null
    }
  }
	
	def getAnnotationParameterValue(Annotation annotation, String elementName) {
	  val values = newArrayList()
    for (ap : annotation.parameters) {
      if (ap.name.matches(elementName)) {
        values.add(ap.value)
      }
    }
    return values
	}
	
	def patternHandler(Pattern pattern, Annotation annotation) '''
		package «pattern.packageName»;
		
		import org.eclipse.emf.ecore.EObject;

		import org.eclipse.viatra2.emf.incquery.validation.runtime.Constraint;
		import org.eclipse.viatra2.emf.incquery.validation.runtime.ValidationUtil;
		import org.eclipse.viatra2.emf.incquery.runtime.api.impl.BaseGeneratedMatcherFactory;
		import org.eclipse.viatra2.emf.incquery.runtime.exception.IncQueryException;
		
		import «pattern.packageName + "." + pattern.matchClassName»;
		import «pattern.packageName + "." + pattern.matcherFactoryClassName»;
		import «pattern.packageName + "." + pattern.matcherClassName»;

		public class «pattern.name.toFirstUpper»«annotationLiteral»«pattern.annotations.indexOf(annotation)» extends «annotationLiteral»<«pattern.matchClassName»> {

			private «pattern.matcherFactoryClassName» matcherFactory;

			public «pattern.name.toFirstUpper»«annotationLiteral»«pattern.annotations.indexOf(annotation)»() throws IncQueryException {
				matcherFactory = «pattern.matcherFactoryClassName».instance();
			}

			@Override
			public String getMessage() {
				return "«getElementOfConstraintAnnotation(annotation, "message")»";
			}

			@Override
			public EObject getLocationObject(«pattern.matchClassName» signature) {
				Object location = signature.get("«
				{
				  val loc = getElementOfConstraintAnnotation(annotation, "location")
  				if(!pattern.parameterPositionsByName.containsKey(loc)){
  				  feedback.reportError(annotation, "Location is not a valid parameter name!", VALIDATION_ERROR_CODE, Severity::ERROR, IErrorFeedback::FRAGMENT_ERROR_TYPE)
   				}
 				  loc
				}
				  »"
				);
				if(location instanceof EObject){
					return (EObject) location;
				}
				return null;
			}
			
			@Override
			public int getSeverity() {
				return ValidationUtil.getSeverity("«getElementOfConstraintAnnotation(annotation, "severity")»");
			}
			
			@Override
			public BaseGeneratedMatcherFactory<«pattern.matcherClassName»> getMatcherFactory() {
				return matcherFactory;
			}
		}
	'''
	
}
