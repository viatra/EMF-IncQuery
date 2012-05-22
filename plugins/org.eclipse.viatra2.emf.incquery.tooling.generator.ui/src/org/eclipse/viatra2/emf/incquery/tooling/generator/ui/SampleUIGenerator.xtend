package org.eclipse.viatra2.emf.incquery.tooling.generator.ui

import com.google.inject.Inject
import org.eclipse.pde.core.plugin.IPluginExtension
import org.eclipse.viatra2.emf.incquery.tooling.generator.ExtensionGenerator
import org.eclipse.viatra2.emf.incquery.tooling.generator.fragments.IGenerationFragment
import org.eclipse.viatra2.emf.incquery.tooling.generator.util.EMFPatternLanguageJvmModelInferrerUtil
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Pattern
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.StringValue
import org.eclipse.xtext.generator.IFileSystemAccess
import org.eclipse.xtext.xbase.lib.Pair

import static org.eclipse.viatra2.emf.incquery.tooling.generator.ui.SampleUIGenerator.*

import static extension org.eclipse.viatra2.patternlanguage.core.helper.CorePatternLanguageHelper.*

class SampleUIGenerator implements IGenerationFragment {
	
	@Inject extension EMFPatternLanguageJvmModelInferrerUtil
	private static String ECLIPSE_UI_COMMANDS_EXTENSION_POINT = "org.eclipse.ui.commands"
	private static String ECLIPSE_UI_HANDLERS_EXTENSION_POINT = "org.eclipse.ui.handlers"
	private static String ECLIPSE_UI_MENUS_EXTENSION_POINT = "org.eclipse.ui.menus"

	override generateFiles(Pattern pattern, IFileSystemAccess fsa) {
		fsa.generateFile(pattern.handlerClassJavaFile, pattern.patternHandler)
	}
	
	override cleanUp(Pattern pattern, IFileSystemAccess fsa) {
		fsa.deleteFile(pattern.handlerClassJavaFile)
	}
	
	override removeExtension(Pattern pattern) {
		newArrayList(
			Pair::of(pattern.commandExtensionId, ECLIPSE_UI_COMMANDS_EXTENSION_POINT),
			Pair::of(pattern.handlerExtensionId, ECLIPSE_UI_HANDLERS_EXTENSION_POINT),
			Pair::of(pattern.menuExtensionId, ECLIPSE_UI_MENUS_EXTENSION_POINT)
		)
	}
	
	override getRemovableExtensions() {
		newArrayList(ECLIPSE_UI_COMMANDS_EXTENSION_POINT, ECLIPSE_UI_HANDLERS_EXTENSION_POINT, ECLIPSE_UI_MENUS_EXTENSION_POINT)
	}
	
	override getProjectDependencies() {
		newArrayList("org.eclipse.core.runtime", "org.eclipse.ui",
		 "org.eclipse.emf.ecore", "org.eclipse.pde.core", "org.eclipse.core.resources", "org.eclipse.viatra2.emf.incquery.runtime")
	}
	
	override getProjectPostfix() {
		"ui"
	}
	
	override extensionContribution(Pattern pattern, ExtensionGenerator exGen) {
		val menuContribution = pattern.menuContribution(exGen)
		newArrayList(
		exGen.contribExtension(pattern.commandExtensionId, ECLIPSE_UI_COMMANDS_EXTENSION_POINT) [
			exGen.contribElement(it, "command") [
				exGen.contribAttribute(it, "id", pattern.commandId)
				exGen.contribAttribute(it, "name", "Get All Matches for " + pattern.fullyQualifiedName)
				exGen.contribAttribute(it, "categoryId", "org.eclipse.viatra2.emf.incquery.tooling.category")
			]
		],
		exGen.contribExtension(pattern.handlerExtensionId, ECLIPSE_UI_HANDLERS_EXTENSION_POINT) [
			exGen.contribElement(it, "handler") [
				exGen.contribAttribute(it, "commandId", pattern.commandId)
				exGen.contribAttribute(it, "class", pattern.handlerClassName)
			]
		],
		menuContribution
		)
	}
	
	def IPluginExtension menuContribution(Pattern pattern, ExtensionGenerator exGen) {
		val fileExtension = pattern.handlerFileExtension
		if (fileExtension.nullOrEmpty) {
			throw new IllegalArgumentException("FileExtension must be defined for Handler annotation in pattern: " + pattern.fullyQualifiedName);
		}
		exGen.contribExtension(pattern.menuExtensionId, ECLIPSE_UI_MENUS_EXTENSION_POINT) [
			exGen.contribElement(it, "menuContribution") [
				exGen.contribAttribute(it, "locationURI", "popup:org.eclipse.ui.popup.any")
				exGen.contribElement(it, "menu") [
					exGen.contribAttribute(it, "label", "EMF-IncQuery")
					exGen.contribElement(it, "command") [
						exGen.contribAttribute(it, "commandId", pattern.commandId)
						exGen.contribAttribute(it, "style", "push")
						exGen.contribElement(it, "visibleWhen") [
							exGen.contribAttribute(it, "checkEnabled", "false")
							exGen.contribElement(it, "with") [
								exGen.contribAttribute(it, "variable", "selection")
								exGen.contribElement(it, "iterate") [
									exGen.contribAttribute(it, "ifEmpty", "false")
									exGen.contribElement(it, "adapt") [
										exGen.contribAttribute(it, "type", "org.eclipse.core.resources.IFile")
										exGen.contribElement(it, "test") [
											exGen.contribAttribute(it, "property", "org.eclipse.core.resources.name")
											exGen.contribAttribute(it, "value", String::format("*.%s", fileExtension))
										]
									]	
								]
							]
						]
					]
				]
			]
		]
	}
	
	def handlerFileExtension(Pattern pattern) {
		for (annotation : pattern.annotations) {
			if ("Handler".equals(annotation.name)) {
				for (parameter : annotation.parameters) {
					if ("fileExtension".equals(parameter.name)) {
						if (parameter.value instanceof StringValue) {
							return (parameter.value as StringValue).value
						}
					}
				}
			}
		}
		return null
	}
	
	def handlerClassName(Pattern pattern) {
		String::format("%s.handlers.%sHandler", pattern.packageName, pattern.realPatternName.toFirstUpper)
	}
	
	def handlerClassPath(Pattern pattern) {
		String::format("%s/handlers/%sHandler", pattern.packagePath, pattern.realPatternName.toFirstUpper) 
	}
	
	def handlerClassJavaFile(Pattern pattern) {
		pattern.handlerClassPath + ".java"
	}
	
	def handlerExtensionId(Pattern pattern) {
		pattern.getFullyQualifiedName + "Handler"
	}
	def commandExtensionId(Pattern pattern) {
		pattern.getFullyQualifiedName + "Command"
	}
	def menuExtensionId(Pattern pattern) {
		pattern.getFullyQualifiedName + "MenuContribution"
	}
	def commandId(Pattern pattern) {
		pattern.getFullyQualifiedName + "CommandId"
	}
	
	def patternHandler(Pattern pattern) '''
		package «pattern.packageName».handlers;

		import java.util.Collection;

		import org.eclipse.core.commands.AbstractHandler;
		import org.eclipse.core.commands.ExecutionEvent;
		import org.eclipse.core.commands.ExecutionException;
		import org.eclipse.emf.ecore.resource.Resource;
		import org.eclipse.emf.ecore.resource.ResourceSet;
		import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
		import org.eclipse.core.resources.IFile;
		import org.eclipse.emf.common.notify.Notifier;
		import org.eclipse.emf.common.util.URI;

		import org.eclipse.jface.dialogs.MessageDialog;
		import org.eclipse.jface.viewers.IStructuredSelection;
		import org.eclipse.swt.widgets.Display;
		import org.eclipse.ui.handlers.HandlerUtil;

		import «pattern.packageName + "." + pattern.matcherClassName»;
		import «pattern.packageName + "." + pattern.matchClassName»;

		public class «pattern.name.toFirstUpper + "Handler"» extends AbstractHandler {

			@Override
			public Object execute(ExecutionEvent event) throws ExecutionException {
				//returns the selected element
				IStructuredSelection selection = (IStructuredSelection) HandlerUtil.getCurrentSelection(event);
				Object firstElement = selection.getFirstElement();
				//the filter is set in the command declaration no need for type checking
				IFile file = (IFile)firstElement;
		
				//Loads the resource
				ResourceSet resourceSet = new ResourceSetImpl();
				URI fileURI = URI.createPlatformResourceURI(file.getFullPath()
						.toString(), false);
				Resource resource = resourceSet.getResource(fileURI, true);

				String matches = getMatches(resource);
		
				//prints the match set to a dialog window 
				MessageDialog.openInformation(Display.getCurrent().getActiveShell(), "Match set of the \"«pattern.name»\" pattern", 
						matches);
		
				return null;
			}

			/**
			* Returns the match set of the «pattern.name» pattern on the input EMF resource
			* @param emfRoot the container of the EMF model on which the pattern matching is invoked
			* @return The serialized form of the match set
			*/
			private String getMatches(Notifier emfRoot){
				//the match set will be serialized into a string builder
				StringBuilder builder = new StringBuilder();
		
				if(emfRoot != null) {	
					//get all matches of the pattern
					«pattern.matcherClassName» matcher = «pattern.matcherClassName».FACTORY.getMatcher(emfRoot);
					Collection<«pattern.matchClassName»> matches = matcher.getAllMatches();
					//serializes the current match into the string builder
					if(matches.size() > 0)
						for(«pattern.matchClassName» match: matches) {
							builder.append(match.toString());
					 		builder.append("\n");
						}
					else
						builder.append("The «pattern.name» pattern has an empty match set.");	
				}
				//returns the match set in a serialized form
				return builder.toString();
			}
		}	
	'''
	
}