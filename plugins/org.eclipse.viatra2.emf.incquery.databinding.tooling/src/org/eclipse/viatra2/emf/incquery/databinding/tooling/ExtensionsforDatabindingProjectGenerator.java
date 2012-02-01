package org.eclipse.viatra2.emf.incquery.databinding.tooling;

import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.pde.core.plugin.IExtensions;
import org.eclipse.pde.core.plugin.IExtensionsModelFactory;
import org.eclipse.pde.core.plugin.IPluginElement;
import org.eclipse.pde.core.plugin.IPluginExtension;
import org.eclipse.pde.internal.core.bundle.WorkspaceBundleModel;
import org.eclipse.pde.internal.core.plugin.WorkspacePluginModel;
import org.eclipse.pde.internal.core.project.PDEProject;
import org.eclipse.viatra2.emf.incquery.core.codegen.CodeGenerationException;
import org.eclipse.viatra2.emf.incquery.model.incquerygenmodel.IncQueryGenmodel;

@SuppressWarnings("restriction")
public class ExtensionsforDatabindingProjectGenerator {

	IProject project;
	IncQueryGenmodel iqGen;

	public ExtensionsforDatabindingProjectGenerator(IProject project, IncQueryGenmodel iqGen) throws CodeGenerationException {
		super();
		this.iqGen = iqGen;
		this.project = project;
	}

	public void contributeToExtensionPoint(Map<String, DatabindingAdapterData> databindableMatchers,
			Map<String,String> editor2domain, IProgressMonitor monitor)
			throws CodeGenerationException {
		IFile manifest = PDEProject.getManifest(project);
		IFile pluginXml = PDEProject.getPluginXml(project);
		// IPluginModel plugin =
		// (IPluginModel)PDECore.getDefault().getModelManager().findModel(project);
		WorkspaceBundleModel bModel = new WorkspaceBundleModel(manifest);
		bModel.setEditable(true);
		WorkspacePluginModel fModel = new WorkspacePluginModel(pluginXml, true);
		fModel.setEditable(true);
		try {
			fModel.load();
			IExtensions extensions = fModel.getExtensions();
			IExtensionsModelFactory factory = fModel.getFactory();
						
			// generates the handlers
			IPluginExtension contribExtensionConstraint = factory.createExtension();
			contribExtensionConstraint.setId("databinding");
			contribExtensionConstraint.setPoint("org.eclipse.viatra2.emf.incquery.databinding.runtime.databinding");
			for (Map.Entry<String, DatabindingAdapterData> entry : databindableMatchers.entrySet()) {
				IPluginElement builderElementConstraint = factory.createElement(contribExtensionConstraint);
				builderElementConstraint.setName("databinding");
				builderElementConstraint.setAttribute("patternName", entry.getValue().getPatternName());
				if (!entry.getValue().isMessageOnly()) {
					builderElementConstraint.setAttribute("class", entry.getValue().getDatabindableMatcherPackage()+"."+entry.getKey());
				}
				builderElementConstraint.setAttribute("message", entry.getValue().getMessage());
				builderElementConstraint.setAttribute("matcherFactoryClass", entry.getValue().getMatcherFactory());
				contribExtensionConstraint.add(builderElementConstraint);
			}
			extensions.add(contribExtensionConstraint);
			contribExtensionConstraint.setInTheModel(true);
			fModel.save();
		} catch (CoreException e) {
			throw new CodeGenerationException("Error during EMF-IncQuery databinding project generation. ", e);
		}
	}

}
