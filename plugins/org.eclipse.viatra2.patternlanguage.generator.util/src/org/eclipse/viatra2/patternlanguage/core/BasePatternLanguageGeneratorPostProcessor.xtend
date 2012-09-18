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
package org.eclipse.viatra2.patternlanguage.core

import org.eclipse.emf.ecore.EPackage
import org.eclipse.emf.ecore.EClass
import org.eclipse.xtext.xtext.ecoreInference.IXtext2EcorePostProcessor
import org.eclipse.xtext.GeneratedMetamodel
import org.eclipse.emf.ecore.EcoreFactory
import org.eclipse.emf.ecore.EcorePackage
import org.eclipse.emf.codegen.ecore.genmodel.GenModelPackage
import org.eclipse.emf.common.util.BasicEMap
import org.eclipse.emf.ecore.EReference
import org.eclipse.emf.ecore.util.EcoreUtil

class BasePatternLanguageGeneratorPostProcessor implements IXtext2EcorePostProcessor {
	
	override void process(GeneratedMetamodel metamodel) {
		metamodel.EPackage.process
	}
	
	def process(EPackage p) {
	   var EClass patternClass
	   var EClass bodyClass
	   var EClass varClass 
	   var EClass paramRefClass
	   var EClass varRefClass
	   var EClass pathExpressionConstraint
	   var EClass pathExpressionElement
	   var EClass pathExpressionHead
	   var EClass pathExpressionTail
	   var EClass type
		for (c : p.EClassifiers.filter(typeof(EClass))) {
           switch c.name {
           	 case "Pattern": patternClass = c
           	 case "PatternBody": bodyClass = c
           	 case "Variable": varClass = c
           	 case "ParameterRef": paramRefClass = c
           	 case "VariableReference": varRefClass = c
           	 case "PathExpressionConstraint": pathExpressionConstraint = c
           	 case "PathExpressionElement": pathExpressionElement = c
           	 case "PathExpressionHead": pathExpressionHead = c
           	 case "PathExpressionTail": pathExpressionTail = c
           	 case "Type": type = c
           }
       }
       bodyClass.generateEReference(varClass)
       varClass.generateReferenceToVariableDecl(varRefClass)
       bodyClass.generateEOperation(varClass)
       
       patternClass.addFileNameToPattern
       
       pathExpressionConstraint.changeHeadType(pathExpressionHead)
       pathExpressionElement.changeTailType(pathExpressionTail)
       
       varClass.addJvmIdentifiableOperations
       
       paramRefClass.setTransientReference
       varRefClass.addWarningComment
       
       type.updateTypeClass
	}

	
	def addJvmIdentifiableOperations(EClass varClass) {
		val getSimpleNameOp = EcoreFactory::eINSTANCE.createEOperation
		getSimpleNameOp.name = "getSimpleName"
		getSimpleNameOp.lowerBound = 1
		getSimpleNameOp.upperBound = 1
		getSimpleNameOp.EType = EcorePackage::eINSTANCE.EString
		val body = EcoreFactory::eINSTANCE.createEAnnotation
		body.source = GenModelPackage::eNS_URI
		val map = EcoreFactory::eINSTANCE.create(EcorePackage::eINSTANCE.getEStringToStringMapEntry()) as BasicEMap$Entry<String,String>
	        map.key = "body"
	        map.value = "return this.name;"
	    body.details.add(map)
	    getSimpleNameOp.EAnnotations += body
		varClass.EOperations += getSimpleNameOp
	}
	
	def generateInverseContainerOfBody(EClass bodyClass, EClass patternClass) {
		val patternRef = EcoreFactory::eINSTANCE.createEReference
		patternRef.transient = true
		patternRef.derived = true
		patternRef.name = "pattern"
		patternRef.lowerBound = 1
		patternRef.upperBound = 1
		patternRef.changeable = true
		patternRef.containment = true
		patternRef.EOpposite = (patternClass.getEStructuralFeature("bodies") as EReference)
	}
	
	def generateEReference(EClass bodyClass, EClass varClass) {
		val varRef = EcoreFactory::eINSTANCE.createEReference
		varRef.transient = true
		varRef.derived = true
		varRef.name = "variables"
		varRef.lowerBound = 0
		varRef.upperBound = -1
		varRef.EType = varClass 
		//PatternLanguageClassResolver::variableType
		//PatternLanguagePackage::eINSTANCE.variable
		varRef.changeable = false
		varRef.containment = true
		
		val body = EcoreFactory::eINSTANCE.createEAnnotation
		body.source = GenModelPackage::eNS_URI
		val map = EcoreFactory::eINSTANCE.create(EcorePackage::eINSTANCE.getEStringToStringMapEntry()) as BasicEMap$Entry<String,String>
	        map.key = "suppressedGetVisibility"
	        map.value = "true"
	        body.details.add(map)
	        
	    varRef.EAnnotations += body
		bodyClass.EStructuralFeatures += varRef
	}
	
	/**
	 * Generates a variable reference (and its opposite) in the pattern body and its usages.
	 */
	def generateReferenceToVariableDecl(EClass varClass, EClass varRefClass) {
		val varRefs = EcoreFactory::eINSTANCE.createEReference
		varRefs.transient = true
		varRefs.derived = true
		varRefs.name = "references"
		varRefs.lowerBound = 0
		varRefs.upperBound = -1
		varRefs.EType = varRefClass 
		varRefs.containment = false
		varClass.EStructuralFeatures += varRefs
		
		val variable = EcoreFactory::eINSTANCE.createEReference
		variable.transient = true
		variable.derived = true
		variable.name = "variable"
		variable.lowerBound = 0
		variable.upperBound = 1
		variable.EType = varClass 
		variable.containment = false
		varRefClass.EStructuralFeatures += variable
		
		varRefs.EOpposite = variable
		variable.EOpposite = varRefs
		
		val op = EcoreFactory::eINSTANCE.createEOperation
		op.eType = varClass
		op.name = "getVariable"
		op.upperBound = 1
		val body = EcoreFactory::eINSTANCE.createEAnnotation
		body.source = GenModelPackage::eNS_URI
		val map = EcoreFactory::eINSTANCE.create(EcorePackage::eINSTANCE.getEStringToStringMapEntry()) as BasicEMap$Entry<String,String>
	        map.key = "body"
	        map.value = 
	           "	if (variable == null) {
          InternalEObject container = this.eContainer;
          while(container != null && !(container instanceof org.eclipse.viatra2.patternlanguage.core.patternLanguage.PatternBody)){
              container = container.eInternalContainer();
          }
          if(container != null) {
              //The side-effect of this call initializes the variable
              ((org.eclipse.viatra2.patternlanguage.core.patternLanguage.PatternBody) container).getVariables();
          }
      }  
      
      return variable;"
	    body.details.add(map)
	    op.EAnnotations += body
		varRefClass.EOperations += op
		
		val suppressAnnotation = EcoreFactory::eINSTANCE.createEAnnotation
		suppressAnnotation.source = GenModelPackage::eNS_URI
		val suppressBody = EcoreFactory::eINSTANCE.create(EcorePackage::eINSTANCE.getEStringToStringMapEntry()) as BasicEMap$Entry<String,String>
	        suppressBody.key = "suppressedGetVisibility"
	        suppressBody.value = "true"
	        suppressAnnotation.details.add(suppressBody)
	        
	    varRefClass.EAllReferences.findFirst[name == "variable"].EAnnotations += suppressAnnotation
	}
	
	/**
	 * Generates an EOperation that corresponds with the derived attribute called ''variables'' 
	 * of the PatternBody.
	 */
	def generateEOperation(EClass bodyClass, EClass varClass) {
		val op = EcoreFactory::eINSTANCE.createEOperation
		op.name = "getVariables"
		op.EType = varClass 
		//PatternLanguageClassResolver::variableType 
		//PatternLanguagePackage::eINSTANCE.variable
		op.upperBound = -1
		val body = EcoreFactory::eINSTANCE.createEAnnotation
		body.source = GenModelPackage::eNS_URI
		val map = EcoreFactory::eINSTANCE.create(EcorePackage::eINSTANCE.getEStringToStringMapEntry()) as BasicEMap$Entry<String,String>
	        map.key = "body"
	        map.value = 
	           "	if (variables == null)
      {
          variables = new EObjectContainmentEList<Variable>(Variable.class, this, PatternLanguagePackage.PATTERN_BODY__VARIABLES);
      }  
      
      return org.eclipse.viatra2.patternlanguage.core.helper.CorePatternLanguageHelper.getAllVariablesInBody(this, variables);"
	        body.details.add(map)
	        op.EAnnotations += body
	        bodyClass.EOperations += op
	}
	
	def addFileNameToPattern(EClass patternClass) {
		val nameAttr = EcoreFactory::eINSTANCE.createEAttribute
		nameAttr.name = "fileName"
		nameAttr.EType = EcorePackage::eINSTANCE.EString
		nameAttr.lowerBound = 0
		nameAttr.upperBound = 1
		EcoreUtil::setDocumentation(nameAttr, 
			"Stores a filename where the pattern comes from. It is only set in the build pattern store
			 inside the IncQuery generator - it is not available anywhere else.")
		
		patternClass.EStructuralFeatures += nameAttr
	}
	
	def changeHeadType(EClass constraint, EClass head){
		constraint.EStructuralFeatures.findFirst(e | e.name == "head").EType = head
	}
	/**
	 * The method updates the EClass element: it changes the type of the "tail" EStructuralFeature to the second parameter
	 * @param element the EClass to change
	 * @param tail the type to set
	 */
	def changeTailType(EClass element, EClass tail) {
		element.EStructuralFeatures.findFirst(e | e.name == "tail").EType = tail
	}
	
	def updateTypeClass(EClass type) {
		val nameFeature = type.EStructuralFeatures.findFirst(e | e.name == "typename")
		nameFeature.transient = true
	}
	
	def addWarningComment(EClass varRefClass) {
		val varFeature = varRefClass.EStructuralFeatures.findFirst(e | e.name == "var")
		var annotation = EcoreFactory::eINSTANCE.createEAnnotation
  
		annotation.source = "http://www.eclipse.org/emf/2002/GenModel" 
		annotation.details.put("documentation", 
		"<p>Warning! This feature contains the original reference text,
         not the variable name. For variable name, use the
         {@link #variable}/{@link Variable#getName} reference chain.

		This is significant when using anonymous variables (named '_').</p>"); 
		varFeature.EAnnotations += annotation
	}
	
	def setTransientReference(EClass paramRefClass) { 
		val ref = paramRefClass.EAllReferences.findFirst(r | r.name == "referredParam")
		ref.transient = true
	}
}