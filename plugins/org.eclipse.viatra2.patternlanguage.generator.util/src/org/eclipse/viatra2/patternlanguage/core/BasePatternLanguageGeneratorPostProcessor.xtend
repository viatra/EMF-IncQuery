/*******************************************************************************
 * Copyright (c) 2011 Zoltan Ujhelyi and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Zoltan Ujhelyi - initial API and implementation
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

class BasePatternLanguageGeneratorPostProcessor implements IXtext2EcorePostProcessor {
	
	override void process(GeneratedMetamodel metamodel) {
		metamodel.EPackage.process
	}
	
	def process(EPackage p) {
	   var EClass patternClass
	   var EClass bodyClass
	   var EClass varClass 
	   var EClass varRefClass
	   var EClass pathExpressionConstraint
	   var EClass pathExpressionElement
	   var EClass pathExpressionHead
	   var EClass pathExpressionTail
		for (c : p.EClassifiers.filter(typeof(EClass))) {
           switch c.name {
           	 case "Pattern": patternClass = c
           	 case "PatternBody": bodyClass = c
           	 case "Variable": varClass = c
           	 case "VariableReference": varRefClass = c
           	 case "PathExpressionConstraint": pathExpressionConstraint = c
           	 case "PathExpressionElement": pathExpressionElement = c
           	 case "PathExpressionHead": pathExpressionHead = c
           	 case "PathExpressionTail": pathExpressionTail = c
           }
       }
       bodyClass.generateEReference(varClass)
       varClass.generateReferenceToVariableDecl(varRefClass)
       bodyClass.generateEOperation(varClass)
       
       pathExpressionConstraint.changeHeadType(pathExpressionHead)
       pathExpressionElement.changeTailType(pathExpressionTail)
       
       varClass.addJvmIdentifiableOperations;
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
	 * Genearates a variable reference (and its opposite) in the pattern body and its usages.
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
    EList<Variable> parameters = ((org.eclipse.viatra2.patternlanguage.core.patternLanguage.Pattern)eContainer).getParameters();
    java.util.Iterator<org.eclipse.emf.ecore.EObject> it = eAllContents();
    java.util.HashMap<String, org.eclipse.viatra2.patternlanguage.core.patternLanguage.VariableReference> variables = new java.util.HashMap<String, org.eclipse.viatra2.patternlanguage.core.patternLanguage.VariableReference>();
    while(it.hasNext()) {
       org.eclipse.emf.ecore.EObject obj = it.next(); 
       if (obj instanceof org.eclipse.viatra2.patternlanguage.core.patternLanguage.VariableReference) {
             String varName = ((org.eclipse.viatra2.patternlanguage.core.patternLanguage.VariableReference) obj).getVar();
			 if (varName != null && !variables.containsKey(varName)) {
				 variables.put(varName, (org.eclipse.viatra2.patternlanguage.core.patternLanguage.VariableReference) obj);
             }
       }
    }
    java.util.Hashtable<String, Variable> varDefs = new java.util.Hashtable<String, Variable>();
    for (Variable var : parameters) {
      varDefs.put(var.getName(), var);
    }
    if (this.variables != null) {
	    for (Variable var : this.variables) {
	       	if (variables.containsKey(var.getName())) {
                varDefs.put(var.getName(), var);
	       	}
	    }
    }
    for (String name : variables.keySet()) {
       if (!varDefs.containsKey(name)) {
       	  Variable decl = org.eclipse.viatra2.patternlanguage.core.patternLanguage.PatternLanguageFactory.eINSTANCE.createVariable();
       	  decl.setName(name);
       	  this.variables.add(decl);
          varDefs.put(name, decl);
       }
    }
	it = eAllContents();
    while(it.hasNext()) {
      org.eclipse.emf.ecore.EObject obj = it.next(); 
      if (obj instanceof org.eclipse.viatra2.patternlanguage.core.patternLanguage.VariableReference) {
      org.eclipse.viatra2.patternlanguage.core.patternLanguage.VariableReference ref = (org.eclipse.viatra2.patternlanguage.core.patternLanguage.VariableReference)obj;
      String name = ref.getVar();
      if (name != null) {
      	  Variable var = varDefs.get(name);
          ref.setVariable(var);
      }

      }
    }
	return this.variables;"
	        body.details.add(map)
	        op.EAnnotations += body
	        bodyClass.EOperations += op
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
}