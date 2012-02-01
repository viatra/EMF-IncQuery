package org.eclipse.viatra2.emf.incquery.databinding.tooling;

import java.io.IOException;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.pde.core.project.IBundleProjectDescription;
import org.eclipse.pde.core.project.IBundleProjectService;
import org.eclipse.pde.core.project.IRequiredBundleDescription;
import org.eclipse.viatra2.emf.incquery.core.project.IncQueryNature;
import org.eclipse.viatra2.emf.incquery.core.project.ProjectGenerationHelper;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.Version;

public class DatabindingProjectSupport {

	public static IProject createProject(IProgressMonitor monitor, String correspondingIncQueryProjectID, String incQueryProjectName)
			throws CoreException, OperationCanceledException {
		BundleContext context = null;
		ServiceReference<?> ref = null;

		try {
			monitor.beginTask("", 2000);

			String databindingProjectName = incQueryProjectName+".databinding";
			IProject proj = ProjectGenerationHelper.initializeProject(databindingProjectName, ProjectGenerationHelper.generatedNatures, monitor);
			IProjectDescription desc = proj.getDescription();
			/* Creating plug-in information */
			context = DatabindingToolingActivator.context;
			ref = context.getServiceReference(IBundleProjectService.class.getName());
			IBundleProjectService service = (IBundleProjectService) context.getService(ref);
			
			IBundleProjectDescription bundleDesc = service.getDescription(proj);
			bundleDesc.setBundleName(desc.getName());
			bundleDesc.setBundleVersion(new Version(0, 0, 1, "qualifier"));
			bundleDesc.setSingleton(true);
			bundleDesc.setTargetVersion(IBundleProjectDescription.VERSION_3_6);
			bundleDesc.setSymbolicName(desc.getName());
			bundleDesc.setExtensionRegistry(true);
			bundleDesc.setActivator("databinding.Activator");
			bundleDesc.setExecutionEnvironments(new String[] {"JavaSE-1.7"});
			// Adding dependencies
			IRequiredBundleDescription[] reqBundles = new IRequiredBundleDescription[] {
					service.newRequiredBundle(correspondingIncQueryProjectID, null, false, true),
					service.newRequiredBundle("org.eclipse.ui", null, false, false),
					service.newRequiredBundle("org.eclipse.viatra2.emf.incquery.databinding.runtime", null, false, false),
					service.newRequiredBundle("org.eclipse.core.databinding.observable", null, false, false),
					service.newRequiredBundle("org.eclipse.emf.databinding", null, false, false),
					service.newRequiredBundle("org.eclipse.core.databinding.property", null, false, false),
					service.newRequiredBundle(correspondingIncQueryProjectID, null, false, false)
					};
			bundleDesc.setRequiredBundles(reqBundles);
			bundleDesc.apply(monitor);
			context.ungetService(ref);
			/* Creating Java folders */
			ProjectGenerationHelper.initializeClasspath(proj, monitor, ProjectGenerationHelper.sourceFolders);
			ProjectGenerationHelper.initializeBuildProperties(proj, monitor);
			return proj;
		} catch (IOException ioe) {
			IStatus status = new Status(IStatus.ERROR,
					IncQueryNature.BUNDLE_ID, IStatus.ERROR,
					ioe.getLocalizedMessage(), ioe);
			throw new CoreException(status);
		} finally {
			monitor.done();
			if (context != null && ref != null)
				context.ungetService(ref);
		}

	}
}
