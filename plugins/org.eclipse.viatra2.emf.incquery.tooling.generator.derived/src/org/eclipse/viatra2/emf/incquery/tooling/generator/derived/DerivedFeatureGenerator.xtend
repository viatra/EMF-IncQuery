/*******************************************************************************
 * Copyright (c) 2010-2012, Abel Hegedus, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Abel Hegedus - initial API and implementation
 *******************************************************************************/

package org.eclipse.viatra2.emf.incquery.tooling.generator.derived

import com.google.inject.Inject
import java.util.HashMap
import java.util.List
import java.util.Map
import org.apache.log4j.Logger
import org.eclipse.emf.codegen.ecore.genmodel.GenClass
import org.eclipse.emf.codegen.ecore.genmodel.GenFeature
import org.eclipse.emf.codegen.ecore.genmodel.GenPackage
import org.eclipse.emf.ecore.EClass
import org.eclipse.emf.ecore.EStructuralFeature
import org.eclipse.jdt.core.ICompilationUnit
import org.eclipse.jdt.core.IJavaProject
import org.eclipse.jdt.core.JavaCore
import org.eclipse.jdt.core.dom.AST
import org.eclipse.jdt.core.dom.ASTNode
import org.eclipse.jdt.core.dom.ASTParser
import org.eclipse.jdt.core.dom.AbstractTypeDeclaration
import org.eclipse.jdt.core.dom.Block
import org.eclipse.jdt.core.dom.CompilationUnit
import org.eclipse.jdt.core.dom.ImportDeclaration
import org.eclipse.jdt.core.dom.Javadoc
import org.eclipse.jdt.core.dom.MethodDeclaration
import org.eclipse.jdt.core.dom.Modifier$ModifierKeyword
import org.eclipse.jdt.core.dom.TagElement
import org.eclipse.jdt.core.dom.TextElement
import org.eclipse.jdt.core.dom.TypeDeclaration
import org.eclipse.jdt.core.dom.VariableDeclarationFragment
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite
import org.eclipse.jdt.core.dom.rewrite.ListRewrite
import org.eclipse.jface.text.Document
import org.eclipse.viatra2.emf.incquery.runtime.derived.FeatureKind
import org.eclipse.viatra2.emf.incquery.tooling.generator.ExtensionGenerator
import org.eclipse.viatra2.emf.incquery.tooling.generator.fragments.IGenerationFragment
import org.eclipse.viatra2.emf.incquery.tooling.generator.genmodel.IEiqGenmodelProvider
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.BoolValue
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Pattern
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.StringValue
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.VariableValue
import org.eclipse.viatra2.patternlanguage.eMFPatternLanguage.ClassType
import org.eclipse.xtext.generator.IFileSystemAccess
import org.eclipse.xtext.xbase.lib.Pair

import static org.eclipse.viatra2.emf.incquery.tooling.generator.derived.DerivedFeatureGenerator.*

import static extension org.eclipse.viatra2.patternlanguage.core.helper.CorePatternLanguageHelper.*
import org.eclipse.viatra2.emf.incquery.tooling.generator.builder.IErrorFeedback
import org.eclipse.xtext.diagnostics.Severity
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Annotation
import org.eclipse.emf.ecore.EcoreFactory
import org.eclipse.emf.ecore.EcorePackage
import org.eclipse.emf.common.util.BasicEMap
import java.util.ArrayList

class DerivedFeatureGenerator implements IGenerationFragment {
	
	@Inject IEiqGenmodelProvider provider
	@Inject extension DerivedFeatureSourceCodeUtil
	@Inject Logger logger
	@Inject IErrorFeedback errorFeedback
	//@Inject extension EMFPatternLanguageJvmModelInferrerUtil
	
	/* usage: @DerivedFeature(
	 * 			feature="featureName", (default: patten name)
	 * 			source="Src" (default: first parameter),
	 * 			target="Trg" (default: second parameter),
	 * 			kind="single/many/counter/sum/iteration" (default: feature.isMany?many:single)
	 * 			keepCache="true/false" (default: true)
	 * 			disabled="true/false" (default: false)
	 * 		  )
	 */
	private static String annotationLiteral 		= "DerivedFeature"
	private static String DERIVED_EXTENSION_POINT 	= "org.eclipse.viatra2.emf.incquery.base.wellbehaving.derived.features"
	private static String ANNOTATION_NAME 	= "org.eclipse.viatra2.emf.incquery.derived.feature"
	private static String IMPORT_QUALIFIER 			= "org.eclipse.viatra2.emf.incquery.runtime.derived"
	private static String FEATUREKIND_IMPORT		= "FeatureKind"
	private static String HELPER_IMPORT 			= "IncqueryFeatureHelper"
	//private static String HANDLER_NAME 				= "IncqueryFeatureHandler"
	private static String HANDLER_NAME 				= "IncqueryDerivedFeature"
	private static String HANDLER_FIELD_SUFFIX 		= "Handler"
	private static String DERIVED_ERROR_CODE = "org.eclipse.viatra2.emf.incquery.runtime.derived.error"
	
	private static String DERIVED_EXTENSION_PREFIX 	= "extension.derived."
	private static Map<String,FeatureKind> kinds = newHashMap(
	  Pair::of("single",FeatureKind::SINGLE_REFERENCE),
		Pair::of("many",FeatureKind::MANY_REFERENCE),
		Pair::of("counter",FeatureKind::COUNTER),
		Pair::of("sum",FeatureKind::SUM),
		Pair::of("iteration",FeatureKind::ITERATION)
	)

	

/*   override cleanUp(Pattern pattern, IFileSystemAccess fsa) {
    throw new UnsupportedOperationException("Auto-generated function stub")
  }
  
  override extensionContribution(Pattern pattern, ExtensionGenerator exGen) {
    throw new UnsupportedOperationException("Auto-generated function stub")
  }
  
  override generateFiles(Pattern pattern, IFileSystemAccess fsa) {
    throw new UnsupportedOperationException("Auto-generated function stub")
  }
  
  override getAdditionalBinIncludes() {
    throw new UnsupportedOperationException("Auto-generated function stub")
  }
  
  override getProjectDependencies() {
    throw new UnsupportedOperationException("Auto-generated function stub")
  }
  
  override getProjectPostfix() {
    throw new UnsupportedOperationException("Auto-generated function stub")
  }
  
  override getRemovableExtensions() {
    throw new UnsupportedOperationException("Auto-generated function stub")
  }
  
  override removeExtension(Pattern pattern) {
    throw new UnsupportedOperationException("Auto-generated function stub")
  }
  
}*/
	
	override generateFiles(Pattern pattern, IFileSystemAccess fsa) {
		processJavaFiles(pattern, true)
	}
		
	override cleanUp(Pattern pattern, IFileSystemAccess fsa) {
		processJavaFiles(pattern, false)
	}
	
	def private processJavaFiles(Pattern pattern, boolean generate) {
		if (hasAnnotationLiteral(pattern, annotationLiteral)) {
			try{
				val parameters = pattern.processDerivedFeatureAnnotation(generate)
							
				val pckg = parameters.get("package") as GenPackage
				val source =  parameters.get("source") as EClass
				val feature = parameters.get("feature") as EStructuralFeature
				
				val genSourceClass = pckg.findGenClassForSource(source, pattern)
				val genFeature = genSourceClass.findGenFeatureForFeature(feature, pattern)
				
				val javaProject = pckg.findJavaProject
				if(javaProject == null){
				  errorFeedback.reportError(pattern,"Model project for GenPackage " + pckg.NSURI + " not found!", DERIVED_ERROR_CODE, Severity::ERROR, IErrorFeedback::FRAGMENT_ERROR_TYPE)
          throw new IllegalArgumentException("Derived feature pattern "+pattern.fullyQualifiedName+": Model project for GenPackage " + pckg.NSURI + " not found!")
				}
				val compunit = pckg.findJavaFile(genSourceClass, javaProject)
				
				val docSource = compunit.source
				val parser = ASTParser::newParser(AST::JLS3)
				val document = new Document(docSource)
				parser.setSource(compunit)
				val astNode = parser.createAST(null) as CompilationUnit
				val ast = astNode.AST
				val rewrite = ASTRewrite::create(ast)
				val types = astNode.types as List<AbstractTypeDeclaration>
				val type = types.findFirst[
					val type = it as AbstractTypeDeclaration
					type.name.identifier == genSourceClass.className
				] as TypeDeclaration
				
				val bodyDeclListRewrite = rewrite.getListRewrite(type, TypeDeclaration::BODY_DECLARATIONS_PROPERTY)
				
				if(generate){
					ast.ensureImports(rewrite, astNode, type)
					ast.ensureHandlerField(bodyDeclListRewrite, type, genFeature)
					ast.ensureGetterMethod(document, type, rewrite, bodyDeclListRewrite, genSourceClass, genFeature, pattern, parameters)
					
					val annotations = new ArrayList(feature.EAnnotations)
          annotations.forEach[
            if(it.source == ANNOTATION_NAME){
              feature.EAnnotations.remove(it)
            }
          ]
					val annotation = EcoreFactory::eINSTANCE.createEAnnotation
					annotation.source = ANNOTATION_NAME
          feature.EAnnotations.add(annotation)
					
				  // add entry ("patternFQN", pattern.fullyQualifiedName)
			    val entry = EcoreFactory::eINSTANCE.create(EcorePackage::eINSTANCE.getEStringToStringMapEntry()) as BasicEMap$Entry<String,String>
			    entry.key = "patternFQN"
			    entry.value = pattern.fullyQualifiedName
			    annotation.details.add(entry)
			    feature.EContainingClass.eResource.save(null)
				} else {
					ast.removeHandlerField(bodyDeclListRewrite, type, genFeature.name)
					ast.restoreGetterMethod(document, compunit, type, rewrite, bodyDeclListRewrite, genSourceClass, genFeature)
					val annotations = new ArrayList(feature.EAnnotations)
					annotations.forEach[
            if(it.source == ANNOTATION_NAME){
              feature.EAnnotations.remove(it)
            }
          ]
          feature.EContainingClass.eResource.save(null)
				}

				val edits = rewrite.rewriteAST(document, javaProject.getOptions(true));
				edits.apply(document);
				val newSource = document.get
				compunit.buffer.setContents(newSource)
				// save!
				compunit.buffer.save(null, false)
				
			} catch(IllegalArgumentException e){
			  if(generate){
				  logger.error(e.message,e);
				}
			}
			
		}
	}
	
	def private findGenClassForSource(GenPackage pckg, EClass source, Pattern pattern){
		val genSourceClass = pckg.genClasses.findFirst[it.ecoreClass == source]
		if(genSourceClass == null){
			throw new IllegalArgumentException("Derived feature pattern "+pattern.fullyQualifiedName+": Source EClass " + source.name + " not found in GenPackage " + pckg + "!")
		}
		return genSourceClass
	}
	
	def private findGenFeatureForFeature(GenClass genSourceClass, EStructuralFeature feature, Pattern pattern){
		val genFeature = genSourceClass.genFeatures.findFirst[it.ecoreFeature == feature]
		if(genFeature == null){
			throw new IllegalArgumentException("Derived feature pattern "+pattern.fullyQualifiedName+": Feature " + feature.name + " not found in GenClass " + genSourceClass.name + "!")
		}
		return genFeature
	}
	
	def private findJavaProject(GenPackage pckg){
		// find java project
		val projectDir = pckg.genModel.modelProjectDirectory
		//val project = ProjectLocator::locateProject(projectDir)
		ProjectLocator::locateProject(projectDir,logger)
		//ProjectGenerationHelper::ensureBundleDependencies(project, newArrayList("org.eclipse.viatra2.emf.incquery.runtime"))
		//JavaCore::create(project)
	}
	
	def private findJavaFile(GenPackage pckg, GenClass genSourceClass, IJavaProject javaProject){
		val prefix = pckg.ecorePackage.name
		val suffix = pckg.classPackageSuffix
		val base = pckg.basePackage
		var packageNameTmp = ""
		if(base != null && base != ""){
			packageNameTmp = base
		}
		if(prefix != null && prefix != ""){
			if(packageNameTmp != ""){
				packageNameTmp = packageNameTmp + "." + prefix
			} else {
				packageNameTmp = prefix
			}
		}
		if(suffix != null && suffix != ""){
			if(packageNameTmp != ""){
				packageNameTmp = packageNameTmp + "." + suffix
			} else {
				packageNameTmp = suffix
			}
		}
		val packageName = packageNameTmp
		val implPackage = javaProject.packageFragments.findFirst[it.elementName == packageName]
		if(implPackage == null){
		  throw new IllegalArgumentException("Derived feature generation: Implementation package "+packageName+" not found!")
		}			
		implPackage.compilationUnits.findFirst[it.elementName == genSourceClass.className+".java"]
	}
	
	def private ensureImports(AST ast, ASTRewrite rewrite, CompilationUnit astNode, TypeDeclaration type){
		val importListRewrite = rewrite.getListRewrite(astNode, CompilationUnit::IMPORTS_PROPERTY)
		// check import
		val imports = astNode.imports as List<ImportDeclaration>
		val handlerImport = imports.findFirst[
			it.name.fullyQualifiedName == IMPORT_QUALIFIER + "." + HANDLER_NAME
		]
		if(handlerImport == null){
			val handlerImportNew = ast.newImportDeclaration
			handlerImportNew.setName(ast.newQualifiedName(ast.newName(IMPORT_QUALIFIER), ast.newSimpleName(HANDLER_NAME)))
			importListRewrite.insertLast(handlerImportNew, null)
		}
		val kindImport = imports.findFirst[
			it.name.fullyQualifiedName == IMPORT_QUALIFIER + "."+ FEATUREKIND_IMPORT
		]
		if(kindImport == null){
			val kindImportNew = ast.newImportDeclaration
			kindImportNew.setName(ast.newQualifiedName(ast.newName(IMPORT_QUALIFIER),ast.newSimpleName(FEATUREKIND_IMPORT)))
			importListRewrite.insertLast(kindImportNew, null)
		}
		val helperImport = imports.findFirst[
			it.name.fullyQualifiedName == IMPORT_QUALIFIER + "." + HELPER_IMPORT
		]
		if(helperImport == null){
			val helperImportNew = ast.newImportDeclaration
			helperImportNew.setName(ast.newQualifiedName(ast.newName(IMPORT_QUALIFIER), ast.newSimpleName(HELPER_IMPORT)))
			importListRewrite.insertLast(helperImportNew, null)
		}
	}
	
	def private ensureHandlerField(AST ast, ListRewrite bodyDeclListRewrite, TypeDeclaration type, GenFeature feature){
		val handler = type.fields.findFirst[
			val fragments = it.fragments as List<VariableDeclarationFragment>
			fragments.exists()[
				it.name.identifier == feature.name+HANDLER_FIELD_SUFFIX
			]
		]
		
		if(handler == null){
			// insert handler
			val handlerFragment = ast.newVariableDeclarationFragment
			handlerFragment.setName(ast.newSimpleName(feature.name+HANDLER_FIELD_SUFFIX))
			val handlerField = ast.newFieldDeclaration(handlerFragment)
			val handlerType = ast.newSimpleType(ast.newSimpleName(HANDLER_NAME))
			// NOTE code for parameterized feature, ensure proper imports...
			//val handlerType = ast.newParameterizedType(ast.newSimpleType(ast.newSimpleName(HANDLER_NAME)))
			//handlerType.typeArguments.add(ast.newSimpleType(ast.newSimpleName(feature.ecoreFeature.EType.name)))
			handlerField.setType(handlerType)
			handlerField.modifiers.add(ast.newModifier(Modifier$ModifierKeyword::PRIVATE_KEYWORD))
			val handlerTag = ast.newTagElement
			val tagText = ast.newTextElement
			tagText.text = "EMF-IncQuery handler for derived feature "+feature.name 
			handlerTag.fragments.add(tagText)
			val javaDoc = ast.newJavadoc
			javaDoc.tags.add(handlerTag)
			handlerField.javadoc = javaDoc
			bodyDeclListRewrite.insertLast(handlerField, null)		
		}
	}
	
	def private removeHandlerField(AST ast, ListRewrite bodyDeclListRewrite, TypeDeclaration type, String featureName){
		val handler = type.fields.findFirst[
			val fragments = it.fragments as List<VariableDeclarationFragment>
			fragments.exists()[
				it.name.identifier == featureName+HANDLER_FIELD_SUFFIX
			]
		]
		
		if(handler != null){
			// remove handler
			bodyDeclListRewrite.remove(handler, null)		
		}
	}
	
	def private ensureGetterMethod(AST ast, Document document, TypeDeclaration type, ASTRewrite rewrite, ListRewrite bodyDeclListRewrite,
		 GenClass sourceClass, GenFeature genFeature, Pattern pattern, Map<String,Object> parameters){
		val sourceName =  parameters.get("sourceVar") as String
		val targetName = parameters.get("targetVar") as String
		val kind = parameters.get("kind") as FeatureKind
		val keepCache = parameters.get("keepCache") as Boolean
		
		val getMethod = findFeatureMethod(type, genFeature, "")
		val getGenMethod = findFeatureMethod(type, genFeature, "Gen")
		
		var methodSource = methodBody(sourceClass, genFeature, pattern, sourceName, targetName, kind, keepCache)
		var dummyMethod = processDummyComputationUnit(methodSource.toString)
		
		if(getMethod != null){
			val javadoc = getMethod.javadoc
			var generatedBody = false
			if(javadoc != null){
				val tags = javadoc.tags as List<TagElement>
				val generatedTag = tags.findFirst[
					(it as TagElement).tagName == "@generated"
				]
				val derivedTag = tags.findFirst[
					(it as TagElement).tagName == "@derived"
				]
				if(derivedTag == null && generatedTag != null && (generatedTag.fragments.size == 0)){
					// generated tag intact
					generatedBody = true
					// rename method to name+"Gen"
					val methodName = getMethod.name.identifier
					// create method
					val method = ASTNode::copySubtree(ast, getMethod) as MethodDeclaration
					rewrite.replace(method.name, ast.newSimpleName(methodName), null)
					rewrite.replace(method.body, dummyMethod.body, null)
					
					val methodtags = method.javadoc.tags as List<TagElement>
					val oldTag = methodtags.findFirst[
						(it as TagElement).tagName == "@generated"
					]
					//oldTag.tagName = "@derived"
					rewrite.set(oldTag,TagElement::TAG_NAME_PROPERTY,"@derived",null)
					val tagsRewrite = rewrite.getListRewrite(oldTag,TagElement::FRAGMENTS_PROPERTY)
					val tagText = ast.newTextElement
					tagText.text = "getter created by EMF-IncQuery for derived feature "+genFeature.name 
					tagsRewrite.insertLast(tagText, null)
					bodyDeclListRewrite.insertLast(method, null)	
					if(getGenMethod == null){
						rewrite.replace(getMethod.name,ast.newSimpleName(getMethod.name.identifier+"Gen"),null)
					} else {
						rewrite.replace(getMethod.name,ast.newSimpleName("_"+getMethod.name.identifier),null)
					}
				}
				if(derivedTag != null){
					generatedBody = true
					replaceMethodBody(ast, rewrite, getMethod.body, dummyMethod.body,
						javadoc, document, false, null,	null, null)
				}
			}
			
			
			if(!generatedBody){
				// generated tag dirty or javadoc null
				replaceMethodBody(ast, rewrite, getMethod.body, dummyMethod.body,
					javadoc, document, true, "@derived",
					"getter created by EMF-IncQuery for derived feature "+genFeature.name, null
				)
			}
			
		}
	}
	
	def private restoreGetterMethod(AST ast, Document document, ICompilationUnit compunit, TypeDeclaration type, ASTRewrite rewrite, ListRewrite bodyDeclListRewrite,
		 GenClass sourceClass, GenFeature genFeature){
		val getMethod = findFeatureMethod(type, genFeature, "")
		val getGenMethod = findFeatureMethod(type, genFeature, "Gen")
		
		if(getGenMethod != null){
			// there is a gen method, we can use that and remove the other
			if(getMethod != null){
				rewrite.replace(getGenMethod.name,ast.newSimpleName(getMethod.name.identifier),null)
				bodyDeclListRewrite.remove(getMethod, null)
			}
		} else {
			var methodSource = defaultMethod(genFeature.ecoreFeature.many)
			var dummyMethod = processDummyComputationUnit(methodSource.toString)
			
			if(getMethod != null){
				val javadoc = getMethod.javadoc
				if(javadoc != null){
					val tags = javadoc.tags as List<TagElement>
					val derivedTag = tags.findFirst[
						(it as TagElement).tagName == "@derived"
					]
					
					val originalTag = tags.findFirst[
						(it as TagElement).tagName == "@original"
					]
					
					val generatedTag = tags.findFirst[
						(it as TagElement).tagName == "@generated"
					]
					if(generatedTag != null && (generatedTag.fragments.size == 0)){
						// generated tag intact
						return
					}
					if(derivedTag != null && originalTag != null){
						if(originalTag.fragments.size != 0){
							// original tag found
							
							//rewrite.set(derivedTag,TagElement::TAG_NAME_PROPERTY,"@generated",null)
							val tagsRewrite = rewrite.getListRewrite(javadoc, Javadoc::TAGS_PROPERTY)
							tagsRewrite.remove(derivedTag, null)
							
							val tagFragments = originalTag.fragments
							val oldBody = new StringBuilder
							for(Object o : tagFragments){
								if(o instanceof TextElement){
									oldBody.append((o as TextElement).text)
								}
							}
							val oldMethod = compunit.prepareOriginalMethod(type, getMethod, oldBody)
							rewrite.replace(getMethod.body,oldMethod.body,null)
							tagsRewrite.remove(originalTag, null)
							return
						}
					}
					if(generatedTag != null){
						return
					}
				}		
				ast.replaceMethodBody(rewrite, getMethod.body, dummyMethod.body, javadoc,
					document, false, "@generated","", "@derived"
				)
			}
		}
	}
	
	def private findFeatureMethod(TypeDeclaration type, GenFeature genFeature, String suffix){
		type.methods.findFirst[
			if(genFeature.basicGet){
				it.name.identifier == "basic"+genFeature.getAccessor.toFirstUpper + suffix
			} else {
				it.name.identifier == genFeature.getAccessor + suffix
			}
		]
	}
	
	def private processDummyComputationUnit(String dummySource){
		val methodBodyParser = ASTParser::newParser(AST::JLS3)
		methodBodyParser.setSource(dummySource.toCharArray)
		val options = JavaCore::getOptions();
		JavaCore::setComplianceOptions(JavaCore::VERSION_1_5, options);
		methodBodyParser.setCompilerOptions(options);
		val dummyAST = methodBodyParser.createAST(null)
		val dummyCU = dummyAST as CompilationUnit
		val dummyType = dummyCU.types.get(0) as TypeDeclaration
		dummyType.methods.get(0) as MethodDeclaration
	}
	
	def private prepareOriginalMethod(ICompilationUnit cu, TypeDeclaration type, MethodDeclaration method, StringBuilder originalBody){
		originalBody.insert(0,'''public class Dummy{public void DummyMethod()''')
		cu.imports.forEach[
			originalBody.insert(0,it.source+"\n")
		]
		originalBody.append("}")
		val dummyCU = originalBody.toString
		dummyCU.processDummyComputationUnit
	}
	
	def private replaceMethodBody(
		AST ast, ASTRewrite rewrite, Block oldBody, Block newBody,
		Javadoc javadoc, Document document, boolean keepOld,
		String newTagName, String newTagText, String removeTagName
	){
		// generated tag dirty or javadoc null
		// copy method body into block comment with @original fragment
		val tags = javadoc.tags as List<TagElement>
		val tagsRewrite = rewrite.getListRewrite(javadoc, Javadoc::TAGS_PROPERTY)
		val originalTag = tags.findFirst[
			(it as TagElement).tagName == "@original"
		]
		val newTag = tags.findFirst[
			(it as TagElement).tagName == newTagName
		]
		if(removeTagName != null){
			val removeTag = tags.findFirst[
					(it as TagElement).tagName == removeTagName
			]
			if(removeTag != null){
				tagsRewrite.remove(removeTag, null)
			}
		}
		if(originalTag == null){
			if(keepOld){
				val tag = ast.newTagElement
				tag.tagName = "@original"
				val text = ast.newTextElement
				text.setText(oldBody.toString.replace("\n",document.defaultLineDelimiter))
				tag.fragments.add(text)
				//tags.add(tag)
				tagsRewrite.insertLast(tag,null)
			}
		} else {
			if(!keepOld){
				tagsRewrite.remove(originalTag,null)
			}
		}
		if(newTag == null){
			val newTagToInsert = ast.newTagElement
			newTagToInsert.tagName = newTagName
			val tagText = ast.newTextElement
			tagText.text = newTagText 
			newTagToInsert.fragments.add(tagText)
			tagsRewrite.insertLast(newTagToInsert,null)
		}
		// overwrite method body
		rewrite.replace(oldBody, newBody, null)
	}
	
	override extensionContribution(Pattern pattern, ExtensionGenerator exGen) {
		if (hasAnnotationLiteral(pattern, annotationLiteral)) {
			// create wellbehaving extension using nsUri, classifier name and feature name
			try{
				val parameters = pattern.processDerivedFeatureAnnotation(false)
				val wellbehaving = newArrayList(
				exGen.contribExtension(pattern.derivedContributionId, DERIVED_EXTENSION_POINT) [
					exGen.contribElement(it, "wellbehaving-derived-feature") [
						exGen.contribAttribute(it, "package-nsUri", (parameters.get("package") as GenPackage).NSURI)
						exGen.contribAttribute(it, "classifier-name", (parameters.get("source") as EClass).name)
						exGen.contribAttribute(it, "feature-name", (parameters.get("feature") as EStructuralFeature).name)
					]
				]
				)
				
				return wellbehaving
			} catch(IllegalArgumentException e){
				logger.error(e.message)
				return newArrayList
			}

		}
		else {
			return newArrayList
		}
	}
	
	override removeExtension(Pattern pattern) {
		newArrayList(
			Pair::of(pattern.derivedContributionId, DERIVED_EXTENSION_POINT)
		)
	}
	
	override getRemovableExtensions() {
		newArrayList(
			Pair::of(DERIVED_EXTENSION_PREFIX, DERIVED_EXTENSION_POINT)
		)
	}
	
	def private hasAnnotationLiteral(Pattern pattern, String literal) {
		for (a : pattern.annotations) {
			if (a.name.matches(literal)) {
				return true;
			}
		}
		return false;
	}
	
	def private derivedContributionId(Pattern pattern) {
		DERIVED_EXTENSION_PREFIX+getFullyQualifiedName(pattern)
	}
	
	
	def private processDerivedFeatureAnnotation(Pattern pattern, boolean feedback){
		val parameters = new HashMap<String,Object>
		var sourceTmp = ""
		var targetTmp = ""
		var featureTmp = ""
		var kindTmp = ""
		var keepCacheTmp = true
		
		if(pattern.parameters.size < 2){
		  if(feedback)
  		  errorFeedback.reportError(pattern,"Pattern has less than 2 parameters!", DERIVED_ERROR_CODE, Severity::ERROR, IErrorFeedback::FRAGMENT_ERROR_TYPE)
			throw new IllegalArgumentException("Derived feature pattern "+pattern.fullyQualifiedName+" has less than 2 parameters!")
		}
		
		var Annotation annotation = null
		for (a : pattern.annotations) {
			if (a.name.matches(annotationLiteral)) {
			  annotation = a
				for (ap : a.parameters) {
					if (ap.name.matches("source")) {
						sourceTmp = (ap.value as VariableValue).value.getVar
					} else if (ap.name.matches("target")) {
						targetTmp = (ap.value as VariableValue).value.getVar
					} else if (ap.name.matches("feature")) {
						featureTmp = (ap.value as StringValue).value
					} else if (ap.name.matches("kind")) {
						kindTmp = (ap.value as StringValue).value
					} else if (ap.name.matches("keepCache")) {
						keepCacheTmp = (ap.value as BoolValue).value
					}
				}
			}	
		}
		
		if(featureTmp == ""){
			//throw new IllegalArgumentException("Derived feature pattern "+pattern.fullyQualifiedName+": Feature not defined!")
			featureTmp = pattern.name
		}
		
		if(sourceTmp == ""){
			sourceTmp = pattern.parameters.get(0).name
		}
		if(!pattern.parameterPositionsByName.keySet.contains(sourceTmp)){
		  if(feedback)
		    errorFeedback.reportError(annotation,"No parameter for source " + sourceTmp +" !", DERIVED_ERROR_CODE, Severity::ERROR, IErrorFeedback::FRAGMENT_ERROR_TYPE)
			throw new IllegalArgumentException("Derived feature pattern "+pattern.fullyQualifiedName+": No parameter for source " + sourceTmp +" !")
		}
		
		val sourcevar = pattern.parameters.get(pattern.parameterPositionsByName.get(sourceTmp))
		val sourceType = sourcevar.type
		if(!(sourceType instanceof ClassType) || !((sourceType as ClassType).classname instanceof EClass)){
			if(feedback)
        errorFeedback.reportError(sourcevar,"Source " + sourceTmp +" is not EClass!", DERIVED_ERROR_CODE, Severity::ERROR, IErrorFeedback::FRAGMENT_ERROR_TYPE)
      throw new IllegalArgumentException("Derived feature pattern "+pattern.fullyQualifiedName+": Source " + sourceTmp +" is not EClass!")
		}
		var source = (sourceType as ClassType).classname as EClass
		
		parameters.put("sourceVar", sourceTmp)
		parameters.put("source", source)
		//parameters.put("sourceJVMRef", pattern.parameters.get(pattern.parameterPositionsByName.get(sourceTmp)).calculateType)
		
		if(source == null || source.EPackage == null){
		  if(feedback)
        errorFeedback.reportError(sourcevar,"Source EClass or EPackage not found!", DERIVED_ERROR_CODE, Severity::ERROR, IErrorFeedback::FRAGMENT_ERROR_TYPE)
		  throw new IllegalArgumentException("Derived feature pattern "+pattern.fullyQualifiedName+": Source EClass or EPackage not found!")
		}
		val pckg = provider.findGenPackage(pattern, source.EPackage)
		if(pckg == null){
			if(feedback)
        errorFeedback.reportError(sourcevar,"GenPackage not found!", DERIVED_ERROR_CODE, Severity::ERROR, IErrorFeedback::FRAGMENT_ERROR_TYPE)
      throw new IllegalArgumentException("Derived feature pattern "+pattern.fullyQualifiedName+": GenPackage not found!")
		}
		parameters.put("package", pckg)
		
		val featureString = featureTmp
		val features = source.EAllStructuralFeatures.filter[it.name == featureString]
		if(features.size != 1){
			if(feedback)
        errorFeedback.reportError(annotation,"Feature " + featureTmp +" not found in class " + source.name +"!", DERIVED_ERROR_CODE, Severity::ERROR, IErrorFeedback::FRAGMENT_ERROR_TYPE)
      throw new IllegalArgumentException("Derived feature pattern "+pattern.fullyQualifiedName+": Feature " + featureTmp +" not found in class " + source.name +"!")
		}
		val feature = features.iterator.next
		if(!(feature.derived && feature.transient && feature.volatile)){ //&& !feature.changeable
			if(feedback)
        errorFeedback.reportError(annotation,"Feature " + featureTmp +" must be set derived, transient, volatile, non-changeable!", DERIVED_ERROR_CODE, Severity::ERROR, IErrorFeedback::FRAGMENT_ERROR_TYPE)
      throw new IllegalArgumentException("Derived feature pattern "+pattern.fullyQualifiedName+": Feature " + featureTmp +" must be set derived, transient, volatile!")
		}
		parameters.put("feature", feature)
		
		if(kindTmp == ""){
			if(feature.many){
				kindTmp = "many"
			} else {
				kindTmp = "single"
			}
		}
		
		if(kinds.empty){
		  kinds.put("single",FeatureKind::SINGLE_REFERENCE)
      kinds.put("many",FeatureKind::MANY_REFERENCE)
      kinds.put("counter",FeatureKind::COUNTER)
      kinds.put("sum",FeatureKind::SUM)
      kinds.put("iteration",FeatureKind::ITERATION) 
		}
		
		if(!kinds.keySet.contains(kindTmp)){
			if(feedback)
        errorFeedback.reportError(annotation,"Kind not set, or not in " + kinds.keySet + "!", DERIVED_ERROR_CODE, Severity::ERROR, IErrorFeedback::FRAGMENT_ERROR_TYPE)
      throw new IllegalArgumentException("Derived feature pattern "+pattern.fullyQualifiedName+": Kind not set, or not in " + kinds.keySet + "!")
		}
		val kind = kinds.get(kindTmp)
		parameters.put("kind", kind)
		
		if(targetTmp == ""){
			targetTmp = pattern.parameters.get(1).name
		} else {
			if(!pattern.parameterPositionsByName.keySet.contains(targetTmp)){
			  if(feedback)
          errorFeedback.reportError(annotation,"Target " + targetTmp +" not set or no such parameter!", DERIVED_ERROR_CODE, Severity::ERROR, IErrorFeedback::FRAGMENT_ERROR_TYPE)
				throw new IllegalArgumentException("Derived feature pattern "+pattern.fullyQualifiedName+": Target " + targetTmp +" not set or no such parameter!")
			}
		}
		/*val targetType = pattern.parameters.get(pattern.parameterPositionsByName.get(targetTmp)).type
		if(!(targetType instanceof ClassType)){
			//if(targetType instanceof )
			throw new IllegalArgumentException("Derived feature pattern "+pattern.fullyQualifiedName+": Target " + targetTmp +" is not EClassifier!")
		}
		val target = (targetType as ClassType).classname*/
		parameters.put("targetVar", targetTmp)
		//parameters.put("target", target)
		//parameters.put("targetJVMRef", pattern.parameters.get(pattern.parameterPositionsByName.get(targetTmp)).calculateType)
				
		/*val featureTarget = feature.EGenericType
		target.ETypeParameters.forEach[
			it.EBounds.forEach[
				println(it)
				if(featureTarget == it){
					println("equal")
				}
			]
		]*/
		
		/*if(keepCacheTmp == ""){
			keepCacheTmp = "true"
		}
		if(keepCacheTmp != "true" && keepCacheTmp != "false"){
			throw new IllegalArgumentException("Derived feature pattern "+pattern.fullyQualifiedName+": keepCache value must be true or false!")
		}*/
		//val keepCache = new Boolean(keepCacheTmp)
		parameters.put("keepCache", keepCacheTmp)
		
		/*if(disabledTmp == ""){
			disabledTmp = "false"
		}
		if(disabledTmp != "true" && disabledTmp != "false"){
			throw new IllegalArgumentException("Derived feature pattern "+pattern.fullyQualifiedName+": disabled value must be true or false!")
		}*/
		//val disabled = new Boolean(disabledTmp)
		//parameters.put("disabled", disabledTmp)
		
		return parameters
	}

	override getAdditionalBinIncludes() {
		return newArrayList()
	}
	
	override getProjectDependencies() {
		return newArrayList()
	}
	
	override getProjectPostfix() {
		return null
	}

}
