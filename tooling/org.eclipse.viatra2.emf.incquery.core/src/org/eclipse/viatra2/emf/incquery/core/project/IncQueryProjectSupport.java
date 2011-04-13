package org.eclipse.viatra2.emf.incquery.core.project;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.pde.core.project.IBundleProjectDescription;
import org.eclipse.pde.core.project.IBundleProjectService;
import org.eclipse.pde.core.project.IRequiredBundleDescription;
import org.eclipse.viatra2.emf.importer.generic.core.importer.EcoreImporter;
import org.eclipse.viatra2.emf.incquery.core.IncQueryPlugin;
import org.eclipse.viatra2.framework.FrameworkManager;
import org.eclipse.viatra2.framework.IFramework;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.Version;

/**
 * @author Zoltan Ujhelyi, Istvan Rath
 * 
 * Support methods for creating a project
 */
public class IncQueryProjectSupport {

	/**
	 * <p>
	 * Creates a new IncQuery project together with its default folder
	 * structure:
	 * </p>
	 * <ul>
	 * <li>models
	 * <ul>
	 * <li>vtcl</li>
	 * </ul>
	 * </li>
	 * </ul>
	 * <p>
	 * It also creates a VPM model file in the models folder.
	 * </p>
	 * @param description
	 * @param proj
	 * @param monitor
	 * @throws CoreException
	 * @throws OperationCanceledException
	 */
	public static void createProject(IProjectDescription description,
			IProject proj, IProgressMonitor monitor) throws CoreException,
			OperationCanceledException {
		BundleContext context = null;
		ServiceReference ref = null;
		
		try {

			monitor.beginTask("", 2000);
			proj.create(description, new SubProgressMonitor(monitor, 1000));
			if (monitor.isCanceled()) {
				throw new OperationCanceledException();
			}

			proj.open(IResource.BACKGROUND_REFRESH, new SubProgressMonitor(
					monitor, 1000));
			/* Adding project nature */
			IProjectDescription desc = proj.getDescription();
			List<String> newNatures = new ArrayList<String>();
			newNatures.addAll(Arrays.asList(desc.getNatureIds()));
			newNatures.add(IncQueryNature.NATURE_ID);
			newNatures.add(JavaCore.NATURE_ID);
			newNatures.add("org.eclipse.pde.PluginNature");
			description.setNatureIds(newNatures.toArray(new String[] {}));
			proj.setDescription(description, monitor);
			/* Creating folder structure */
			final IFolder modelFolder = proj.getFolder(new Path(
					IncQueryNature.MODELS_DIR));
			modelFolder.create(true, true, monitor);
			final IFolder vtclFolder = proj.getFolder(new Path(
					IncQueryNature.VTCL_DIR));
			vtclFolder.create(true, true, monitor);
			/* Add the vpml to the project */
			File vpml = FrameworkManager.getFileFromBundle(
					IncQueryNature.MODEL_BUNDLE_ID, IncQueryNature.SOURCE_VPML);
			addFileToProject(proj, new Path(IncQueryNature.TARGET_VPML),
					new FileInputStream(vpml), monitor);
			/* Creating plug-in information */
			context = IncQueryPlugin.plugin.context;
			ref = context
					.getServiceReference(IBundleProjectService.class.getName());
			IBundleProjectService service = (IBundleProjectService) context
					.getService(ref);
			IBundleProjectDescription bundleDesc = service.getDescription(proj);
			bundleDesc.setBundleName(desc.getName());
			bundleDesc.setBundleVersion(new Version(0, 0, 1, "qualifier"));
			bundleDesc.setSingleton(true);
			bundleDesc.setTargetVersion(IBundleProjectDescription.VERSION_3_6);
			bundleDesc.setSymbolicName(desc.getName());
			bundleDesc.setExtensionRegistry(true);
			// Adding dependencies
			IRequiredBundleDescription[] reqBundles = new IRequiredBundleDescription[] { 
					service.newRequiredBundle("org.eclipse.pde.core", null, false, false),
					service.newRequiredBundle("org.eclipse.emf.ecore", null, false, false),
					service.newRequiredBundle("org.eclipse.emf.transaction", null, false, true),
					service.newRequiredBundle("org.eclipse.viatra2.emf.incquery.runtime", null, false, true)};
			bundleDesc.setRequiredBundles(reqBundles);
			bundleDesc.apply(monitor);
			context.ungetService(ref);
			/* Creating Java folders */
			final List<IClasspathEntry> classpathEntries = new ArrayList<IClasspathEntry>();
			final IJavaProject javaProject = JavaCore.create(proj);
			final IFolder srcContainer = proj.getFolder("src");
			srcContainer.create(true, true, monitor);
			final IClasspathEntry srcClasspathEntry = JavaCore
					.newSourceEntry(srcContainer.getFullPath());
			classpathEntries.add(srcClasspathEntry);
			final IFolder srcGenContainer = proj.getFolder("src-gen");
			srcGenContainer.create(true, true, monitor);
			final IClasspathEntry srcGenClasspathEntry = JavaCore
					.newSourceEntry(srcGenContainer.getFullPath());
			classpathEntries.add(srcGenClasspathEntry);
			// Plug-in classpath
			classpathEntries.add(JavaCore.newContainerEntry(new Path(
					"org.eclipse.pde.core.requiredPlugins")));
			classpathEntries.add(JavaRuntime.getDefaultJREContainerEntry());
			javaProject.setRawClasspath(classpathEntries
					.toArray(new IClasspathEntry[classpathEntries.size()]),
					monitor);
			
			/* TODO UGLY: Add the build.properties to the project */
			File buildProp = FrameworkManager.getFileFromBundle(
					IncQueryNature.BUNDLE_ID, IncQueryNature.SOURCE_BUILD_PROPERTIES);
			addFileToProject(proj, new Path("build.properties"), new FileInputStream(buildProp), monitor);
		} catch (IOException ioe) {
			IStatus status = new Status(IStatus.ERROR,
					IncQueryNature.BUNDLE_ID, IStatus.ERROR,
					ioe.getLocalizedMessage(), ioe);
			throw new CoreException(status);
		} finally {
			monitor.done();
			if(context != null && ref != null)
				context.ungetService(ref);
		}
	}

	/**
	 * Adds a file to a container.
	 * @param container the container to add the file to
	 * @param path the path of the newly created file
	 * @param contentStream the file will be filled with this stream's contents
	 * @param monitor
	 * @throws CoreException
	 */
	private static void addFileToProject(IContainer container, Path path,
			InputStream contentStream, IProgressMonitor monitor)
			throws CoreException {
		final IFile file = container.getFile(path);

		if (file.exists()) {
			file.setContents(contentStream, true, true, monitor);
		} else {
			file.create(contentStream, true, monitor);
		}

	}

	/**
	 * Fills the VPML file of the project with nEMF content generated according to the EPackage given.
	 * @param ePackages a collection of {@link EPackage}s we are importing into the VPML
	 * @param project the INCQuery project we are targeting
	 * @param modelPluginID the plugin ID of the emf.model project that will be added to the required bundles
	 * @throws Exception 
	 * @throws IOException 
	 * @throws CoreException 
	 */
	public static void fillVPMLContent(Collection<EPackage> ePackages, IProject project, String modelPluginID) 
	throws 	Exception, IOException, CoreException 
	{
		EcoreImporter importer = new EcoreImporter();
		FrameworkManager fwManager = FrameworkManager.getInstance();
		IFramework framework = null;
		boolean frameworkFound = false;
//		try {
			IResource file = project.findMember(new Path(
					IncQueryNature.TARGET_VPML), false);
			String fileName = file.getLocation().toOSString();

			// locate the framework that may already be open for the given VPML file
			for (String frameworkID : fwManager.getAllFrameWorks()) {
				IFramework fw = fwManager.getFramework(frameworkID);
				if (fw.getCurrentFilename().contains(fileName)) {
					framework = fw;
					frameworkFound = true;
					break;
				}
			}
			
			// if not found, create a new one
			if (framework == null) {
				framework = fwManager.createFramework(fileName);
			}
			
			framework.getTopmodel().getTransactionManager().beginTransaction(Boolean.TRUE);
			// errors are also signalled here!
			importer._processPackages(ePackages, framework, true);
			framework.getTopmodel().getTransactionManager().commitTransaction();
			
			if (!frameworkFound) { // Only save framework, if it is not opened
				framework.saveFile(fileName);
				file.refreshLocal(0, new NullProgressMonitor());
				fwManager.disposeFramework(framework.getId());
			}
			
			// add modelPluginID to the list of required bundles
			BundleContext context = IncQueryPlugin.plugin.context;
			ServiceReference ref = context
					.getServiceReference(IBundleProjectService.class.getName());
			IBundleProjectService service = (IBundleProjectService) context
					.getService(ref);
			IBundleProjectDescription bundleDesc = service
					.getDescription(project);
			IRequiredBundleDescription[] requiredBundles = bundleDesc
					.getRequiredBundles();
			if (requiredBundles != null) {
				boolean found = false;
				for (IRequiredBundleDescription desc : requiredBundles) {
					if (desc.getName().contentEquals(modelPluginID))
						found = true;
				}
				if (!found) {
					IRequiredBundleDescription[] newBundles = Arrays.copyOf(
							requiredBundles, requiredBundles.length + 1);
					newBundles[requiredBundles.length] = service
							.newRequiredBundle(modelPluginID, null, false, true);
					bundleDesc.setRequiredBundles(newBundles);
					bundleDesc.apply(new NullProgressMonitor());
				}
			} else {
				bundleDesc
						.setRequiredBundles(new IRequiredBundleDescription[] { service
								.newRequiredBundle(modelPluginID, null, false, true) });
				bundleDesc.apply(new NullProgressMonitor());
			}
			context.ungetService(ref);
//		} 
/*		
		catch (FrameworkManagerException e) {
			// this comes if VIATRA is unable to function at a very basic level
			e.printStackTrace();
		} catch (FrameworkException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (CoreException e) {
			Activator.log(e, e.getMessage());
		} catch (VPMCoreException e) {
			// this signals error during the import
			e.printStackTrace();
		}
*/
	}
	
}
