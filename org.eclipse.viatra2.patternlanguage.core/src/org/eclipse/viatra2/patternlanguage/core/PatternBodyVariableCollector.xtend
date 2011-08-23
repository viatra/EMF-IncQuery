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
		for (c : p.EClassifiers.filter(typeof(EClass))) {
           if (c.name == "PatternBody") {
               c.handle
           }
       }
	}
	
	def handle(EClass c) {
		c.generateEReference
		c.generateEOperation		

	}
	
	def generateEReference(EClass c) {
		val varRef = EcoreFactory::eINSTANCE.createEReference
		varRef.volatile = true
		varRef.transient = true
		varRef.derived = true
		varRef.name = "variables"
		varRef.lowerBound = 0
		varRef.upperBound = -1
		varRef.EType = XbasePackage::eINSTANCE.XVariableDeclaration
		varRef.changeable = false
		
		val body = EcoreFactory::eINSTANCE.createEAnnotation
		body.source = GenModelPackage::eNS_URI
		val map = EcoreFactory::eINSTANCE.create(EcorePackage::eINSTANCE.getEStringToStringMapEntry()) as BasicEMap$Entry<String,String>
	        map.key = "suppressedGetVisibility"
	        map.value = "true"
	        body.details.add(map)
	        
	    varRef.EAnnotations += body
		c.EStructuralFeatures += varRef
	}
	
	def generateEOperation(EClass c) {
		val op = EcoreFactory::eINSTANCE.createEOperation
		op.name = "getVariables"
		op.EType = XbasePackage::eINSTANCE.XVariableDeclaration
		op.upperBound = -1
		val body = EcoreFactory::eINSTANCE.createEAnnotation
		body.source = GenModelPackage::eNS_URI
		val map = EcoreFactory::eINSTANCE.create(EcorePackage::eINSTANCE.getEStringToStringMapEntry()) as BasicEMap$Entry<String,String>
	        map.key = "body"
	        map.value = 
	           "java.util.Iterator<org.eclipse.emf.ecore.EObject> it = eAllContents();
				java.util.HashMap<String, org.eclipse.viatra2.patternlanguage.core.patternLanguage.Variable> variables = new java.util.HashMap<String, org.eclipse.viatra2.patternlanguage.core.patternLanguage.Variable>();
				while(it.hasNext()) {
					org.eclipse.emf.ecore.EObject obj = it.next(); 
					if (obj instanceof org.eclipse.viatra2.patternlanguage.core.patternLanguage.Variable 
						&& !variables.containsKey(((org.eclipse.viatra2.patternlanguage.core.patternLanguage.Variable) obj).getName())) {
						variables.put(((org.eclipse.viatra2.patternlanguage.core.patternLanguage.Variable)obj).getName(), (org.eclipse.viatra2.patternlanguage.core.patternLanguage.Variable) obj);
					}
				}
				EList<XVariableDeclaration> declarations = new org.eclipse.emf.common.util.BasicEList<XVariableDeclaration>();
				for (String name : variables.keySet()) {
					XVariableDeclaration decl = org.eclipse.xtext.xbase.XbaseFactory.eINSTANCE.createXVariableDeclaration();
					decl.setWriteable(false);
					decl.setName(name);
					declarations.add(decl);
				}
    			return declarations;"
	        body.details.add(map)
	        op.EAnnotations += body
	        c.EOperations += op
	}
}