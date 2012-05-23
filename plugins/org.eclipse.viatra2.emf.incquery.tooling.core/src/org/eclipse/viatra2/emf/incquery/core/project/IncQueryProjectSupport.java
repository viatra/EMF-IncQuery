package org.eclipse.viatra2.emf.incquery.core.project;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.pde.core.project.IBundleProjectDescription;
import org.eclipse.pde.core.project.IBundleProjectService;
import org.eclipse.viatra2.emf.incquery.core.IncQueryPlugin;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import com.google.common.collect.ImmutableList;

/**
 * @author Zoltan Ujhelyi, Istvan Rath
 * 
 *         Support methods for creating a project
 */
public class IncQueryProjectSupport {

	
	private IncQueryProjectSupport() {}
	/**
	 * Creates a new IncQuery project: a plug-in project with src and src-gen
	 * folders and specific dependencies.
	 * 
	 * @param description
	 * @param proj
	 * @param monitor
	 * @throws CoreException
	 * @throws OperationCanceledException
	 */
	public static void createProject(IProjectDescription description,
			IProject proj, IProgressMonitor monitor) throws CoreException {
		ImmutableList<String> dependencies = ImmutableList.of(
				"org.eclipse.pde.core", "org.eclipse.emf.ecore",
				"org.eclipse.emf.transaction",
				"org.eclipse.viatra2.emf.incquery.runtime",
				"org.eclipse.xtext.xbase.lib");
		BundleContext context = null;
		ServiceReference<IBundleProjectService> ref = null;

		try {

			monitor.beginTask("", 2000);
			/* Creating plug-in information */
			context = IncQueryPlugin.plugin.context;
			ref = context.getServiceReference(IBundleProjectService.class);
			final IBundleProjectService service = context.getService(ref);
			IBundleProjectDescription bundleDesc = service.getDescription(proj);
			ProjectGenerationHelper.fillProjectMetadata(proj, dependencies, service, bundleDesc);
			bundleDesc.apply(monitor);
			// Adding IncQuery-specific natures
			ProjectGenerationHelper.addNatures(proj, new String[] {
					IncQueryNature.NATURE_ID,
					"org.eclipse.xtext.ui.shared.xtextNature" }, monitor);
		} finally {
			monitor.done();
			if (context != null && ref != null) {
				context.ungetService(ref);
			}
		}
	}

}
