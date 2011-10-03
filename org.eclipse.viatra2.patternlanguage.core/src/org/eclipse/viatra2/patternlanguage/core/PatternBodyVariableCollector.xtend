package org.eclipse.viatra2.patternlanguage.core

import org.eclipse.emf.ecore.EPackage
import org.eclipse.emf.ecore.EClass
import org.eclipse.xtext.xtext.ecoreInference.IXtext2EcorePostProcessor
import org.eclipse.xtext.GeneratedMetamodel
import org.eclipse.emf.ecore.EcoreFactory
import org.eclipse.emf.ecore.EcorePackage
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.PatternLanguagePackage
import org.eclipse.emf.codegen.ecore.genmodel.GenModelPackage
import org.eclipse.emf.common.util.BasicEMap
import org.eclipse.xtext.xbase.XbasePackage

class PatternBodyVariableCollector implements IXtext2EcorePostProcessor {
	
	override void process(GeneratedMetamodel metamodel) {
		metamodel.EPackage.process
	}
	
	def process(EPackage p) {
	   var EClass bodyClass
	   var EClass varClass 
	   var EClass varRefClass
		for (c : p.EClassifiers.filter(typeof(EClass))) {
           switch c.name {
           	 case "PatternBody": bodyClass = c
           	 case "Variable": varClass = c
           	 case "VariableReference": varRefClass = c
           }
       }
       bodyClass.generateEReference
       varClass.generateReferenceToVariableDecl(varRefClass)
       bodyClass.generateEOperation
	}
	
	def generateEReference(EClass bodyClass) {
		val varRef = EcoreFactory::eINSTANCE.createEReference
		varRef.transient = true
		varRef.derived = true
		varRef.name = "variables"
		varRef.lowerBound = 0
		varRef.upperBound = -1
		varRef.EType = PatternLanguagePackage::eINSTANCE.variable
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
	
	def generateReferenceToVariableDecl(EClass varClass, EClass varRefClass) {
		val varRefs = EcoreFactory::eINSTANCE.createEReference
		varRefs.transient = true
		varRefs.derived = true
		varRefs.name = "references"
		varRefs.lowerBound = 0
		varRefs.upperBound = -1
		varRefs.EType = PatternLanguagePackage::eINSTANCE.variableReference
		varRefs.containment = false
		varClass.EStructuralFeatures += varRefs
		
		val variable = EcoreFactory::eINSTANCE.createEReference
		variable.transient = true
		variable.derived = true
		variable.name = "variable"
		variable.lowerBound = 1
		variable.upperBound = 1
		variable.EType = PatternLanguagePackage::eINSTANCE.variable
		variable.containment = false
		varRefClass.EStructuralFeatures += variable
		
		varRefs.EOpposite = variable
		variable.EOpposite = varRefs
	}
	
	def generateEOperation(EClass bodyClass) {
		val op = EcoreFactory::eINSTANCE.createEOperation
		op.name = "getVariables"
		op.EType = PatternLanguagePackage::eINSTANCE.variable
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
       if (obj instanceof org.eclipse.viatra2.patternlanguage.core.patternLanguage.VariableReference 
         && !variables.containsKey(((org.eclipse.viatra2.patternlanguage.core.patternLanguage.VariableReference) obj).getVar())) {
         variables.put(((org.eclipse.viatra2.patternlanguage.core.patternLanguage.VariableReference)obj).getVar(), (org.eclipse.viatra2.patternlanguage.core.patternLanguage.VariableReference) obj);
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
      Variable var = varDefs.get(name);
      ref.setVariable(var);

      }
    }
	return this.variables;"
	        body.details.add(map)
	        op.EAnnotations += body
	        bodyClass.EOperations += op
	}
}