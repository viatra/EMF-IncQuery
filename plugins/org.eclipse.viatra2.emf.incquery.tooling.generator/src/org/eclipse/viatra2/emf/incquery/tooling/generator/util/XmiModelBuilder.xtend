package org.eclipse.viatra2.emf.incquery.tooling.generator.util

import java.util.ArrayList
import java.util.HashSet
import org.apache.log4j.Logger
import org.eclipse.core.resources.IProject
import org.eclipse.core.resources.IResource
import org.eclipse.emf.common.util.URI
import org.eclipse.emf.ecore.EPackage
import org.eclipse.emf.ecore.resource.ResourceSet
import org.eclipse.viatra2.emf.incquery.runtime.util.XmiModelUtil
import org.eclipse.viatra2.patternlanguage.core.helper.CorePatternLanguageHelper
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Pattern
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.PatternCall
import org.eclipse.viatra2.patternlanguage.eMFPatternLanguage.EMFPatternLanguageFactory
import org.eclipse.viatra2.patternlanguage.eMFPatternLanguage.PatternModel
import org.eclipse.xtext.EcoreUtil2
import org.eclipse.xtext.xbase.XFeatureCall
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Variable
import com.google.inject.Inject
import org.eclipse.xtext.naming.IQualifiedNameProvider
/**
 * @author Mark Czotter
 */
class XmiModelBuilder {
	
	Logger logger = Logger::getLogger(getClass())
	
	@Inject
	IQualifiedNameProvider nameProvider
	
	/**
	 * Builds one model file (XMI) from the input into the folder.
	 */
	def build(ResourceSet resourceSet, IProject project) {
		try {
			val folder = project.getFolder(XmiModelUtil::XMI_OUTPUT_FOLDER)
			val file = folder.getFile(XmiModelUtil::GLOBAL_EIQ_FILENAME)
			if (!folder.exists) {
				folder.create(IResource::DEPTH_INFINITE, false, null)
			}
			if (file.exists) {
				file.delete(true, null)
			}
			// create the model in memory
			val xmiModelRoot = EMFPatternLanguageFactory::eINSTANCE.createPatternModel()
			val xmiResource = resourceSet.createResource(URI::createPlatformResourceURI(file.fullPath.toOSString, true))
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
			
			xmiModelRoot.importPackages.addAll(importDeclarations.map[EMFPatternLanguageFactory::eINSTANCE.createPackageImport])				
			// first add all patterns
			val fqnToPatternMap = newHashMap();
			for (pattern : resourceSet.resources.map(r | r.allContents.toIterable.filter(typeof (Pattern))).flatten) {
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
			xmiResource.save(null)
		} catch(Exception e) {
			logger.error("Exception during XMI build!", e)
		}
	}
	
}
