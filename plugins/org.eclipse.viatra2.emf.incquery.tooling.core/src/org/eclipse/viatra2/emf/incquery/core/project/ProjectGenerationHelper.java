package org.eclipse.viatra2.emf.incquery.core.project;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.pde.core.plugin.IExtensions;
import org.eclipse.pde.core.plugin.IPluginExtension;
import org.eclipse.pde.core.plugin.IPluginExtensionPoint;
import org.eclipse.pde.core.plugin.IPluginModel;
import org.eclipse.pde.core.plugin.IPluginObject;
import org.eclipse.pde.core.project.IBundleClasspathEntry;
import org.eclipse.pde.core.project.IBundleProjectDescription;
import org.eclipse.pde.core.project.IBundleProjectService;
import org.eclipse.pde.core.project.IPackageExportDescription;
import org.eclipse.pde.core.project.IRequiredBundleDescription;
import org.eclipse.pde.internal.core.PDECore;
import org.eclipse.pde.internal.core.plugin.WorkspacePluginModel;
import org.eclipse.pde.internal.core.project.PDEProject;
import org.eclipse.viatra2.emf.incquery.core.IncQueryPlugin;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Pattern;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.PatternBody;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.PatternLanguageFactory;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.PatternModel;
import org.eclipse.viatra2.patternlanguage.eMFPatternLanguage.EMFPatternLanguageFactory;
import org.eclipse.xtext.ui.resource.IResourceSetProvider;
import org.eclipse.xtext.xbase.lib.Functions;
import org.eclipse.xtext.xbase.lib.IterableExtensions;
import org.eclipse.xtext.xbase.lib.Pair;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.Version;

import com.google.common.base.Function;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.inject.Inject;

/**
 * A common helper class for generating IncQuery-related projects.
 * 
 * @author Zoltan Ujhelyi
 */
public abstract class ProjectGenerationHelper {

	private static final class IDToRequireBundleTransformer implements
			Function<String, IRequiredBundleDescription> {
		private final IBundleProjectService service;

		private IDToRequireBundleTransformer(IBundleProjectService service) {
			this.service = service;
		}

		@Override
		public IRequiredBundleDescription apply(String input) {
			return service.newRequiredBundle(input, null, false, false);
		}
	}

	@Inject
	private static IResourceSetProvider resourceSetProvider;

	/**
	 * Two source folders: src to be manually written and src-gen to contain
	 * generated code
	 */
	public static final List<String> sourceFolders = ImmutableList.of(
			IncQueryNature.SRC_DIR, IncQueryNature.SRCGEN_DIR);
	/**
	 * A single source folder named src
	 */
	public static final String[] singleSourceFolder = { "src" };

	/**
	 * Adds a collection of natures to the project
	 * 
	 * @param proj
	 * @param natures
	 * @param monitor
	 * @return
	 * @throws CoreException
	 */
	public static IProjectDescription addNatures(IProject proj,
			String[] natures, IProgressMonitor monitor) throws CoreException {
		IProjectDescription desc = proj.getDescription();
		List<String> newNatures = new ArrayList<String>();
		newNatures.addAll(Arrays.asList(desc.getNatureIds()));
		newNatures.addAll(Arrays.asList(natures));
		desc.setNatureIds(newNatures.toArray(new String[newNatures.size()]));
		proj.setDescription(desc, monitor);
		return desc;
	}

	/**
	 * Adds a file to a container.
	 * 
	 * @param container
	 *            the container to add the file to
	 * @param path
	 *            the path of the newly created file
	 * @param contentStream
	 *            the file will be filled with this stream's contents
	 * @param monitor
	 * @throws CoreException
	 */
	public static void addFileToProject(IContainer container, Path path,
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
	 * @param folder
	 * @param monitor
	 * @throws CoreException
	 */
	public static void deleteJavaFiles(IFolder folder,
			final IProgressMonitor monitor) throws CoreException {
		folder.refreshLocal(IResource.DEPTH_INFINITE, monitor);
		CollectDeletedElement visitor = new CollectDeletedElement();
		folder.accept(visitor);
		for (IResource res : visitor.toDelete) {
			res.delete(false, new SubProgressMonitor(monitor, 1));
		}
	}

	/**
	 * The method will create a new query definiton file inside the given
	 * container.
	 * 
	 * @param containerPath
	 *            the full path of the container of the file to be generated
	 * @param fileName
	 *            must end with eiq extension
	 * @param patternName
	 *            the name of the initial pattern in the generated query
	 *            definition file
	 */
	public static void createEiqFile(String containerPath, String packageName,
			String fileName, String patternName) {

		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		IResource containerResource = root.findMember(new Path(containerPath));
		ResourceSet resourceSet = resourceSetProvider.get(containerResource
				.getProject());

		URI fileURI = URI.createPlatformResourceURI(containerResource
				.getFullPath().append(packageName + "/" + fileName).toString(),
				false);
		Resource resource = resourceSet.createResource(fileURI);

		PatternModel pm = EMFPatternLanguageFactory.eINSTANCE
				.createPatternModel();
		Pattern pattern = PatternLanguageFactory.eINSTANCE.createPattern();
		pattern.setName(patternName);
		PatternBody body = PatternLanguageFactory.eINSTANCE.createPatternBody();
		pattern.getBodies().add(body);
		pm.getPatterns().add(pattern);
		resource.getContents().add(pm);

		try {
			resource.save(Collections.EMPTY_MAP);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void initializePluginProject(IProject project,
			final List<String> dependencies) throws CoreException {
		initializePluginProject(project, dependencies,
				new NullProgressMonitor());
	}

	public static void initializePluginProject(IProject project,
			final List<String> dependencies, IProgressMonitor monitor)
			throws CoreException {
		BundleContext context = null;
		ServiceReference<IBundleProjectService> ref = null;
		try {
			context = IncQueryPlugin.plugin.context;
			ref = context.getServiceReference(IBundleProjectService.class);
			final IBundleProjectService service = context.getService(ref);
			IBundleProjectDescription bundleDesc = service
					.getDescription(project);
			fillProjectMetadata(project, dependencies, service, bundleDesc);
			bundleDesc.apply(monitor);
		} finally {
			if (context != null && ref != null)
				context.ungetService(ref);
		}
	}

	/**
	 * Initializes the plug-in metadata of a newly created project.
	 * 
	 * @param project
	 *            the plug-in project to create the metadata for. The plug-in id
	 *            will be the same as the project name
	 * @param dependencies
	 *            a list of required bundles to add
	 * @param service
	 * @param bundleDesc
	 */
	public static void fillProjectMetadata(IProject project,
			final List<String> dependencies,
			final IBundleProjectService service,
			IBundleProjectDescription bundleDesc) {
		bundleDesc.setBundleName(project.getName());
		bundleDesc.setBundleVersion(new Version(0, 0, 1, "qualifier"));
		bundleDesc.setSingleton(true);
		bundleDesc.setTargetVersion(IBundleProjectDescription.VERSION_3_6);
		bundleDesc.setSymbolicName(project.getName());
		bundleDesc.setExtensionRegistry(true);

		IBundleClasspathEntry[] classpathEntries = Lists.transform(
				sourceFolders, new Function<String, IBundleClasspathEntry>() {

					@Override
					public IBundleClasspathEntry apply(String input) {
						return service.newBundleClasspathEntry(new Path(input),
								null, null);
					}
				}).toArray(new IBundleClasspathEntry[sourceFolders.size()]);
		bundleDesc.setBundleClasspath(classpathEntries);
		bundleDesc
				.setExecutionEnvironments(new String[] { IncQueryNature.EXECUTION_ENVIRONMENT });
		// Adding dependencies
		IRequiredBundleDescription[] reqBundles = Lists.transform(dependencies,
				new IDToRequireBundleTransformer(service)).toArray(
				new IRequiredBundleDescription[dependencies.size()]);
		bundleDesc.setRequiredBundles(reqBundles);
	}

	/**
	 * Updates project manifest to ensure the selected bundle dependencies are
	 * set. Does not change existing dependencies.
	 * 
	 * @param project
	 * @param dependencies
	 * @throws CoreException
	 */
	public static void ensureBundleDependencies(IProject project,
			final List<String> dependencies) throws CoreException {
		ensureBundleDependencies(project, dependencies,
				new NullProgressMonitor());
	}

	/**
	 * Updates project manifest to ensure the selected bundle dependencies are
	 * set. Does not change existing dependencies.
	 * 
	 * @param project
	 * @param dependencies
	 * @param monitor
	 * @throws CoreException
	 */
	public static void ensureBundleDependencies(IProject project,
			final List<String> dependencies, IProgressMonitor monitor)
			throws CoreException {
		BundleContext context = null;
		ServiceReference<IBundleProjectService> ref = null;
		try {
			context = IncQueryPlugin.plugin.context;
			ref = context.getServiceReference(IBundleProjectService.class);
			final IBundleProjectService service = context.getService(ref);
			IBundleProjectDescription bundleDesc = service
					.getDescription(project);
			ensureBundleDependencies(service, bundleDesc, dependencies);
			bundleDesc.apply(monitor);
		} finally {
			if (context != null && ref != null)
				context.ungetService(ref);
		}
	}

	/**
	 * Updates project manifest to ensure the selected bundle dependencies are
	 * set. Does not change existing dependencies.
	 * @param service
	 * @param bundleDesc
	 * @param dependencies
	 */
	public static void ensureBundleDependencies(IBundleProjectService service,
			IBundleProjectDescription bundleDesc,
			final List<String> dependencies) {
		IRequiredBundleDescription[] requiredBundles = bundleDesc
				.getRequiredBundles();
		List<String> missingDependencies = new ArrayList<String>(dependencies);
		if (requiredBundles != null) {
			for (IRequiredBundleDescription bundle : requiredBundles) {
				if (missingDependencies.contains(bundle.getName())) {
					missingDependencies.remove(bundle.getName());
				}
			}
		}
		bundleDesc.setRequiredBundles(Lists.transform(missingDependencies,
				new IDToRequireBundleTransformer(service)).toArray(
				new IRequiredBundleDescription[missingDependencies.size()]));
	}

	/**
	 * Updates project manifest to ensure the selected packages are exported.
	 * Does not change existing exports.
	 * 
	 * @param project
	 * @param dependencies
	 * @throws CoreException
	 */
	public static void ensurePackageExports(IProject project,
			final List<String> dependencies) throws CoreException {
		ensurePackageExports(project, dependencies, new NullProgressMonitor());
	}

	/**
	 * Updates project manifest to ensure the selected packages are exported.
	 * Does not change existing exports.
	 * 
	 * @param project
	 * @param dependencies
	 * @param monitor
	 * @throws CoreException
	 */
	public static void ensurePackageExports(IProject project,
			final List<String> dependencies, IProgressMonitor monitor)
			throws CoreException {
		BundleContext context = null;
		ServiceReference<IBundleProjectService> ref = null;
		try {
			context = IncQueryPlugin.plugin.context;
			ref = context.getServiceReference(IBundleProjectService.class);
			final IBundleProjectService service = context.getService(ref);
			IBundleProjectDescription bundleDesc = service
					.getDescription(project);
			ensurePackageExports(service, bundleDesc, dependencies);
			bundleDesc.apply(monitor);
		} finally {
			if (context != null && ref != null)
				context.ungetService(ref);
		}
	}
	
	/**
	 * Updates project manifest to ensure the selected packages are removed.
	 * Does not change existing exports.
	 * 
	 * @param project
	 * @param dependencies
	 * @param monitor
	 * @throws CoreException
	 */
	public static void removePackageExports(IProject project,
			final List<String> dependencies, IProgressMonitor monitor)
			throws CoreException {
		BundleContext context = null;
		ServiceReference<IBundleProjectService> ref = null;
		try {
			context = IncQueryPlugin.plugin.context;
			ref = context.getServiceReference(IBundleProjectService.class);
			final IBundleProjectService service = context.getService(ref);
			IBundleProjectDescription bundleDesc = service
					.getDescription(project);
			removePackageExports(service, bundleDesc, dependencies);
			bundleDesc.apply(monitor);
		} finally {
			if (context != null && ref != null)
				context.ungetService(ref);
		}
	}

	/**
	 * Updates project manifest to ensure the selected packages are exported.
	 * Does not change existing exports.
	 * 
	 * @param service
	 * @param bundleDesc
	 * @param exports
	 */
	public static void ensurePackageExports(
			final IBundleProjectService service,
			IBundleProjectDescription bundleDesc, final List<String> exports) {
		IPackageExportDescription[] packageExports = bundleDesc
				.getPackageExports();
		List<String> missingExports = new ArrayList<String>(exports);
		List<IPackageExportDescription> exportList = new ArrayList<IPackageExportDescription>();
		if (packageExports != null) {
			for (IPackageExportDescription export : packageExports) {
				if (!missingExports.contains(export.getName())) {
					missingExports.remove(export.getName());
				}
				exportList.add(export);
			}
		}
		exportList.addAll(Lists.transform(missingExports,
				new Function<String, IPackageExportDescription>() {

					@Override
					public IPackageExportDescription apply(String input) {
						return service
								.newPackageExport(input, null, true, null);
					}
				}));

		bundleDesc.setPackageExports(exportList
				.toArray(new IPackageExportDescription[exportList.size()]));
	}
	
	/**
	 * Updates project manifest to ensure the selected packages are removed.
	 * Does not change existing exports.
	 * 
	 * @param service
	 * @param bundleDesc
	 * @param exports
	 */
	public static void removePackageExports(
			final IBundleProjectService service,
			IBundleProjectDescription bundleDesc, final List<String> exports) {
		IPackageExportDescription[] packageExports = bundleDesc
				.getPackageExports();
		List<IPackageExportDescription> exportList = new ArrayList<IPackageExportDescription>();
		if (packageExports != null) {
			for (IPackageExportDescription export : packageExports) {
				if (!exports.contains(export.getName())) {
					exportList.add(export);
				}
			}
		}
		bundleDesc.setPackageExports(exportList
				.toArray(new IPackageExportDescription[exportList.size()]));
	}

	/**
	 * Updates the selected project to contain the selected extension. The
	 * extensions are identified using an identifier and extension point
	 * together; old extensions are replaced with the new ones, other extensions
	 * are kept intact.
	 * 
	 * @param project
	 * @param contributedExtensions
	 * @throws CoreException
	 */
	public static void ensureExtensions(IProject project,
			Iterable<IPluginExtension> contributedExtensions)
			throws CoreException {
		ensureExtensions(project, contributedExtensions,
				new NullProgressMonitor());
	}

	/**
	 * Updates the selected project to contain the selected extension. The
	 * extensions are identified using an identifier and extension point
	 * together; old extensions are replaced with the new ones, other extensions
	 * are kept intact.
	 * 
	 * @param project
	 * @param contributedExtensions
	 * @param monitor
	 * @throws CoreException
	 */
	@SuppressWarnings("restriction")
	public static void ensureExtensions(IProject project,
			Iterable<IPluginExtension> contributedExtensions,
			IProgressMonitor monitor) throws CoreException {
		Multimap<String, IPluginExtension> extensionMap = ArrayListMultimap
				.create();
		for (IPluginExtension extension : contributedExtensions) {
			extensionMap.put(extension.getId(), extension);
		}
		// XXX Using two APIs to extension generation: one to read and one to
		// write
		IFile pluginXml = PDEProject.getPluginXml(project);
		IPluginModel plugin = (IPluginModel) PDECore.getDefault()
				.getModelManager().findModel(project);
		WorkspacePluginModel fModel = new WorkspacePluginModel(pluginXml, false);
		fModel.setEditable(true);
		fModel.load();
		// Storing a write-only plugin.xml model
		IExtensions extensions = fModel.getExtensions();
		// Storing a read-only plugin.xml model
		if (plugin != null) {
			IExtensions readExtension = plugin.getExtensions();
			nextExtension: for (IPluginExtension extension : readExtension
					.getExtensions()) {
				String id = extension.getId();
				if (id.startsWith(project.getName())) {
					id = id.substring(
						project.getName().length() + 1);
				}
				if (extensionMap.containsKey(id)) {
					String point = extension.getPoint();
					for (IPluginExtension ex : extensionMap.get(id)) {
						if (ex.getPoint().equals(point)) {
							continue nextExtension;
						}
					}
				}
				// XXX cloning extensions to remove project name prefixes
				IPluginExtension cloneExtension = fModel.createExtension();
				cloneExtension.setId(id);
				cloneExtension.setName(extension.getName());
				cloneExtension.setPoint(extension.getPoint());
				for (IPluginObject obj : extension.getChildren()) {
					cloneExtension.add(obj);
				}
				cloneExtension.setInTheModel(true);
				extensions.add(cloneExtension);
			}
			for (IPluginExtensionPoint point : readExtension
					.getExtensionPoints()) {
				extensions.add(point);
			}
		}
		for (IPluginExtension contribExtension : contributedExtensions) {
			extensions.add(contribExtension);
			contribExtension.setInTheModel(true);
		}
		fModel.save();
	}

	/**
	 * Updates project manifest to ensure the selected packages are removed.
	 * Does not change existing exports.
	 * 
	 * @param project
	 * @param dependencies
	 * @throws CoreException
	 */
	public static void removePackageExports(IProject project,
			ArrayList<String> dependencies) throws CoreException {
		removePackageExports(project, dependencies, new NullProgressMonitor());
	}

	/**
	 * Updates the selected project to not contain the selected extension. The
	 * extensions are identified using an identifier and extension point
	 * together; other extensions are kept intact.
	 * 
	 * @param project
	 * @param contributedExtensions
	 * @throws CoreException
	 */
	public static void removeExtensions(IProject project,
			List<Pair<String, String>> contributedExtensions) throws CoreException {
		removeExtensions(project, contributedExtensions, new NullProgressMonitor());
	}
	
	/**
	 * Updates the selected project to not contain the selected extension. The
	 * extensions are identified using an identifier and extension point
	 * together; other extensions are kept intact.
	 * 
	 * @param project
	 * @param contributedExtensions
	 * @param monitor
	 * @throws CoreException
	 */
	@SuppressWarnings("restriction")
	public static void removeExtensions(IProject project,
			List<Pair<String, String>> contributedExtensions, IProgressMonitor monitor) throws CoreException {
		IFile pluginXml = PDEProject.getPluginXml(project);
		IPluginModel plugin = (IPluginModel) PDECore.getDefault()
				.getModelManager().findModel(project);
		WorkspacePluginModel fModel = new WorkspacePluginModel(pluginXml, false);
		fModel.setEditable(true);
		fModel.load();
		// Storing a write-only plugin.xml model
		IExtensions extensions = fModel.getExtensions();
		if (plugin != null) {
			IExtensions readExtension = plugin.getExtensions();
			for (final IPluginExtension extension : readExtension.getExtensions()) {
				String id = extension.getId();
				if (id.startsWith(project.getName())) {
					id = id.substring(
						project.getName().length() + 1);
				}
				final String extensionId = id;
				Pair<String, String> contrExt = IterableExtensions.
						findFirst(contributedExtensions, new Functions.Function1<Pair<String, String>, Boolean>() {
					@Override
					public Boolean apply(Pair<String, String> input) {
						if (input.getKey().equals(extensionId)) {
							if (input.getValue().equals(extension.getPoint())) {
								return true;
							}
						}
						return false;
					}
				});
				// add this extension if not generated previously
				// XXX cloning extensions to remove project name prefixes
				if (contrExt == null) {
					IPluginExtension cloneExtension = fModel.createExtension();
					cloneExtension.setId(id);
					cloneExtension.setName(extension.getName());
					cloneExtension.setPoint(extension.getPoint());
					for (IPluginObject obj : extension.getChildren()) {
						cloneExtension.add(obj);
					}
					cloneExtension.setInTheModel(true);
					extensions.add(cloneExtension);
				}
			}
			for (IPluginExtensionPoint point : readExtension
					.getExtensionPoints()) {
				extensions.add(point);
			}
		}
		fModel.save();
	}
	
}