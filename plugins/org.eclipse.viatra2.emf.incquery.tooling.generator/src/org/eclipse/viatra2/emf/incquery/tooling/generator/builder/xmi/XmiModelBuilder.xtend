/*******************************************************************************
 * Copyright (c) 2010-2012, Zoltan Ujhelyi, Mark Czotter, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Zoltan Ujhelyi, Mark Czotter - initial API and implementation
 *******************************************************************************/

package org.eclipse.viatra2.emf.incquery.tooling.generator.builder.xmi

import java.util.ArrayList
import java.util.HashSet
import org.apache.log4j.Logger
import org.eclipse.emf.common.util.URI
import org.eclipse.emf.ecore.EPackage
import org.eclipse.emf.ecore.resource.ResourceSet
import org.eclipse.emf.ecore.xmi.XMLResource
import org.eclipse.viatra2.emf.incquery.tooling.generator.util.EMFPatternURIHandler
import org.eclipse.viatra2.patternlanguage.core.helper.CorePatternLanguageHelper
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Pattern
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.PatternCall
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Variable
import org.eclipse.viatra2.patternlanguage.eMFPatternLanguage.EMFPatternLanguageFactory
import org.eclipse.viatra2.patternlanguage.eMFPatternLanguage.PatternModel
import org.eclipse.xtext.EcoreUtil2
import org.eclipse.xtext.xbase.XFeatureCall

/**
 * @author Mark Czotter
 */
class XmiModelBuilder {
	
	Logger logger = Logger::getLogger(getClass())
	
	/**
	 * Builds one model file (XMI) from the input into the folder.
	 */
	def build(ResourceSet resourceSet, String fileFullPath) {
		try {
			// create the model in memory
			val xmiModelRoot = EMFPatternLanguageFactory::eINSTANCE.createPatternModel()
			val xmiResource = resourceSet.createResource(URI::createPlatformResourceURI(fileFullPath, true))
			// add import declarations 
			val HashSet<EPackage> importDeclarations = newHashSet()
			/*
			 * The following change avoids two different errors:
			 *  * concurrentmodification of the growing list of resources
			 *  * and a bug wrt Guice flatten and EMF BasicEList
			 */ 
			//val packageImports = resourceSet.resources.map(r | r.allContents.toIterable.filter(typeof (PackageImport))).flatten
			val resources = new ArrayList(resourceSet.resources)
			for (r : resources) {
				for (obj : r.contents) {
					if (obj instanceof PatternModel && !obj.equals(xmiModelRoot)) {
						for (importDecl : (obj as PatternModel).importPackages){
							if (!importDeclarations.contains(importDecl.EPackage)) {
								importDeclarations.add(importDecl.EPackage)
							}
						}
					}
				}
//				val packageImports = r.allContents.toIterable.filter(typeof (PackageImport))
//				if (!packageImports.empty) {
//					for (importDecl : packageImports) {
//						if (!importDeclarations.contains(importDecl.EPackage)) {
//							importDeclarations.add(importDecl.EPackage)
//							xmiModelRoot.importPackages.add(importDecl)
//						}
//					}
//				}
			}
			xmiModelRoot.importPackages.addAll(importDeclarations.map[
				val imp = EMFPatternLanguageFactory::eINSTANCE.createPackageImport
				imp.setEPackage(it)
				return imp
			])
			// first add all patterns
			val fqnToPatternMap = newHashMap();
			for (pattern : resources.map(r | r.allContents.toIterable.filter(typeof (Pattern))).flatten) {
				val p = (EcoreUtil2::copy(pattern)) as Pattern //casting required to avoid build error
				val fqn = CorePatternLanguageHelper::getFullyQualifiedName(pattern)
				p.name = fqn
				p.fileName = pattern.eResource.URI.toString
				if (fqnToPatternMap.get(fqn) != null) {
					logger.error("Pattern already set in the Map: " + fqn)
				} else {
					fqnToPatternMap.put(fqn, p)
					xmiModelRoot.patterns.add(p)
				}
				
				// first add all parameter variables
				val nameToParameterMap = newHashMap();
				for (variable : p.parameters) {
					val vfqn = variable.name
					if (nameToParameterMap.get(vfqn) != null) {
						logger.error("Variable already set in the Map: " + vfqn)
					} else {
						nameToParameterMap.put(vfqn, variable)
					}	
				}
				// iterate over each body
				for(body : p.bodies) {
					// add local variables
					val nameToLocalVariableParameterMap = newHashMap();
					for(variable : body.variables){
						val vfqn = variable.name
						if (nameToLocalVariableParameterMap.get(vfqn) != null) {
							logger.error("Variable already set in the Map: " + vfqn)
						} else {
							nameToLocalVariableParameterMap.put(vfqn, variable)
						}	
					}
					// then iterate over all added FeatureCalls and change feature to proper variable
					for(expression : p.eAllContents.toIterable.filter(typeof (XFeatureCall))){
						val f = expression.feature
						if(f instanceof Variable){
							val vfqn = (f as Variable).name
							val v = nameToLocalVariableParameterMap.get(vfqn);
							if(v == null){
								val par = nameToParameterMap.get(vfqn);
								if(par == null){								
									logger.error("Variable not found: " + vfqn)
								} else {
									expression.setFeature(par as Variable)
								}
							} else {
								expression.setFeature(v as Variable)
							}
						}
					}
				}
			}
			// then iterate over all added PatternCall and change the patternRef
			for (call : xmiModelRoot.eAllContents.toIterable.filter(typeof (PatternCall))) {
				val fqn = CorePatternLanguageHelper::getFullyQualifiedName(call.patternRef)
				val p = fqnToPatternMap.get(fqn)
				if (p == null) {
					logger.error("Pattern not found: " +fqn)
				} else {
					call.setPatternRef(p as Pattern)
				}
			}
			// save the xmi file 
			xmiResource.contents.add(xmiModelRoot)
			val options = newHashMap(XMLResource::OPTION_URI_HANDLER -> new EMFPatternURIHandler(importDeclarations))
			xmiResource.save(options) 
		} catch(Exception e) {
			logger.error("Exception during XMI build!", e)
		}
	}
	
}
