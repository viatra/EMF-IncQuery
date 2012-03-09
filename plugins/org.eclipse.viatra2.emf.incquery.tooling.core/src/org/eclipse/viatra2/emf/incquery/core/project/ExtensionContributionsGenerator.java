package org.eclipse.viatra2.emf.incquery.core.project;


import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.pde.core.plugin.IExtensions;
import org.eclipse.pde.core.plugin.IExtensionsModelFactory;
import org.eclipse.pde.core.plugin.IPluginExtension;
import org.eclipse.pde.core.plugin.IPluginExtensionPoint;
import org.eclipse.pde.core.plugin.IPluginModel;
import org.eclipse.pde.internal.core.PDECore;
import org.eclipse.pde.internal.core.plugin.WorkspacePluginModel;
import org.eclipse.pde.internal.core.project.PDEProject;

/**
 * @author Bergmann GÃ¡bor
 *
 */
public class ExtensionContributionsGenerator {
	IProject project;

	/**
	 * @param project
	 * @throws CodeGenerationException 
	 */
	public ExtensionContributionsGenerator(IProject project) {
		super();
		this.project = project;
	}
	
	
	@SuppressWarnings("restriction")
	public void contributeToExtensionPoint(Iterable<IPluginExtension> contributedExtensions, IProgressMonitor monitor)  {
		IFile pluginXml = PDEProject.getPluginXml(project);
		IPluginModel plugin = (IPluginModel)PDECore.getDefault().getModelManager().findModel(project);
		WorkspacePluginModel fModel = new WorkspacePluginModel(pluginXml, true);
		fModel.setEditable(true);
		try {
			String extensionID = project.getName() + ".generatedContribution";
			fModel.load();
			//Storing a write-only plugin.xml model
			IExtensions extensions = fModel.getExtensions();
			//Storing a read-only plugin.xml model
			IExtensions readExtension = plugin.getExtensions();
			for (IPluginExtension extension : readExtension.getExtensions()) {
				String id = extension.getId();
				//The second contentEquals is needed as the returned id contains the project name twice 
				if (id==null || !(id.contentEquals(extensionID) || id.contentEquals(project.getName() + "." + extensionID))) {
					extensions.add(extension);
				}
			}
			for (IPluginExtensionPoint point : readExtension.getExtensionPoints()) {
				extensions.add(point);
			}
			IExtensionsModelFactory factory = fModel.getFactory();
			IPluginExtension contribExtension = factory.createExtension();
			contribExtension.setId(extensionID);
			/*contribExtension.setPoint(org.eclipse.viatra2.emf.incquery.runtime.IExtensions.EXTENSION_POINT_ID);
			for (Map.Entry<GTPattern, String> entry : contributionClassNames.entrySet()) {
				IPluginElement builderElement = factory.createElement(contribExtension);
				builderElement.setName("pattern-builder");
				builderElement.setAttribute("build-class", entry.getValue());
				builderElement.setAttribute("pattern-fqn", entry.getKey().getFqn());
				contribExtension.add(builderElement);
			}
			extensions.add(contribExtension);*/
			contribExtension.setInTheModel(true);
			fModel.save();
		} catch (CoreException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

}
