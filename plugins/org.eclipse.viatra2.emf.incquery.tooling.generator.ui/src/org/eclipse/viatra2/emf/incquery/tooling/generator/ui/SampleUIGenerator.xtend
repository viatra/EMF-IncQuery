package org.eclipse.viatra2.emf.incquery.tooling.generator.ui

import org.eclipse.viatra2.emf.incquery.tooling.generator.fragments.IGenerationFragment
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Pattern
import org.eclipse.xtext.generator.IFileSystemAccess
import com.google.inject.Inject
import org.eclipse.viatra2.emf.incquery.tooling.generator.util.EMFPatternLanguageJvmModelInferrerUtil
import org.eclipse.viatra2.emf.incquery.tooling.generator.ExtensionGenerator
import org.eclipse.xtext.naming.IQualifiedNameProvider

class SampleUIGenerator implements IGenerationFragment {
	
	@Inject extension EMFPatternLanguageJvmModelInferrerUtil
	@Inject 
		IQualifiedNameProvider nameProvider

	override generateFiles(Pattern pattern, IFileSystemAccess fsa) {
		fsa.generateFile(pattern.packagePath + "/handlers/" + pattern.name + "Handler.java", pattern.patternHandler)
	}
	
	override getProjectDependencies() {
		newArrayList("org.eclipse.core.runtime", "org.eclipse.ui",
		 "org.eclipse.emf.ecore", "org.eclipse.pde.core", "org.eclipse.core.resources", "org.eclipse.viatra2.emf.incquery.runtime")
	}
	
	override getProjectPostfix() {
		"ui"
	}
	
	override extensionContribution(Pattern pattern, ExtensionGenerator exGen) {
		newArrayList(
		exGen.contribExtension(nameProvider.getFullyQualifiedName(pattern).toString + "Command", "org.eclipse.ui.commands") [
			exGen.contribElement(it, "command") [
				exGen.contribAttribute(it, "commandId", nameProvider.getFullyQualifiedName(pattern).toString + "CommandId")
				exGen.contribAttribute(it, "style", "push")
			]
		]
		)
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

		public class «pattern.name + "Handler"» extends AbstractHandler {

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