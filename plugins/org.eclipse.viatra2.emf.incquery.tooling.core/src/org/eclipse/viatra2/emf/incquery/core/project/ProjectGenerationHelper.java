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
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.pde.core.project.IBundleClasspathEntry;
import org.eclipse.pde.core.project.IBundleProjectDescription;
import org.eclipse.pde.core.project.IBundleProjectService;
import org.eclipse.pde.core.project.IRequiredBundleDescription;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Pattern;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.PatternBody;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.PatternLanguageFactory;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.PatternModel;
import org.eclipse.viatra2.patternlanguage.eMFPatternLanguage.EMFPatternLanguageFactory;
import org.eclipse.xtext.ui.resource.IResourceSetProvider;
import org.osgi.framework.Version;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.inject.Inject;

/**
 * A common helper class for generating IncQuery-related projects.
 * 
 * @author Zoltan Ujhelyi
 */
public abstract class ProjectGenerationHelper {

	@Inject
	private static IResourceSetProvider resourceSetProvider;

	/**
	 * The list of all natures used in an IncQuery project.
	 */
	public static final String[] allNatures = { IncQueryNature.NATURE_ID,
			JavaCore.NATURE_ID, "org.eclipse.pde.PluginNature" };
	/**
	 * The natures used in a generated sample project.
	 */
	public static final String[] generatedNatures = { JavaCore.NATURE_ID,
			"org.eclipse.pde.PluginNature" };
	/**
	 * Two source folders: src to be manually written and src-gen to contain
	 * generated code
	 */
	//public static final String[] sourceFolders = { "src", "src-gen" };
	public static final List<String> sourceFolders = ImmutableList.of(IncQueryNature.SRC_DIR, IncQueryNature.SRCGEN_DIR);
	/**
	 * A single source folder named src
	 */
	public static final String[] singleSourceFolder = { "src" };

	/**
	 * This method initializes a new project with a selected name, but deletes
	 * any existing project with the same name - it shall be called with care!
	 * 
	 * @param projectName
	 * @param natures
	 * @param monitor
	 * @return the project description of the created
	 * @throws CoreException
	 */
	public static IProject initializeProject(String projectName,
			String[] natures, IProgressMonitor monitor) throws CoreException {
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IWorkspaceRoot root = workspace.getRoot();
		IProject proj = root.getProject(projectName);
		
		if (proj.exists()) {
			proj.delete(true, true, monitor);
		}

		return initializeProject(proj, natures, monitor);
	}

	/**
	 * @param proj
	 * @param natures
	 * @param monitor
	 * @return
	 * @throws CoreException
	 */
	public static IProject initializeProject(IProject proj, String[] natures,
			IProgressMonitor monitor) throws CoreException {
		proj.create(new SubProgressMonitor(monitor, 1000));
		if (monitor.isCanceled()) {
			throw new OperationCanceledException();
		}

		proj.open(IResource.BACKGROUND_REFRESH, new SubProgressMonitor(monitor,
				1000));
		addNatures(proj, natures, new SubProgressMonitor(monitor, 500));
		return proj;
	}

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
		desc.setNatureIds(newNatures.toArray(new String[] {}));
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
	 * The method will create a new query definiton file inside the given container.
	 *  
	 * @param containerPath the full path of the container of the file to be generated
	 * @param fileName must end with eiq extension
	 * @param patternName the name of the initial pattern in the generated query definition file
	 */
	public static void createEiqFile(String containerPath, String packageName, String fileName,	String patternName) {

		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		IResource containerResource = root.findMember(new Path(containerPath));
		ResourceSet resourceSet = resourceSetProvider.get(containerResource.getProject());

		URI fileURI = URI.createPlatformResourceURI(containerResource.getFullPath().append(packageName+"/"+fileName).toString(), false);
		Resource resource = resourceSet.createResource(fileURI);

		PatternModel pm = EMFPatternLanguageFactory.eINSTANCE.createPatternModel();
		Pattern pattern = PatternLanguageFactory.eINSTANCE.createPattern();
		pattern.setName(patternName);
		PatternBody body = PatternLanguageFactory.eINSTANCE.createPatternBody();
		pattern.getBodies().add(body);
		pm.getPatterns().add(pattern);
		resource.getContents().add(pm);

		try {
			resource.save(Collections.EMPTY_MAP);
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Initializes the plug-in metadata of a newly created project.
	 * @param project the plug-in project to create the metadata for. The plug-in id will be the same as the project name
	 * @param dependencies a list of required bundles to add
	 * @param service 
	 * @param bundleDesc
	 */
	public static void fillProjectMetadata(IProject project,
			ImmutableList<String> dependencies,
			final IBundleProjectService service,
			IBundleProjectDescription bundleDesc) {
		bundleDesc.setBundleName(project.getName());
		bundleDesc.setBundleVersion(new Version(0, 0, 1, "qualifier"));
		bundleDesc.setSingleton(true);
		bundleDesc.setTargetVersion(IBundleProjectDescription.VERSION_3_6);
		bundleDesc.setSymbolicName(project.getName());
		bundleDesc.setExtensionRegistry(true);
	
		IBundleClasspathEntry[] classpathEntries = Lists.transform(
				sourceFolders,
				new Function<String, IBundleClasspathEntry>() {
	
					@Override
					public IBundleClasspathEntry apply(String input) {
						return service.newBundleClasspathEntry(new Path(
								input), null, null);
					}
				}).toArray(new IBundleClasspathEntry[sourceFolders.size()]);
		bundleDesc.setBundleClasspath(classpathEntries);
		bundleDesc
				.setExecutionEnvironments(new String[] { IncQueryNature.EXECUTION_ENVIRONMENT });
		// Adding dependencies
		IRequiredBundleDescription[] reqBundles = Lists.transform(
				dependencies,
				new Function<String, IRequiredBundleDescription>() {
	
					@Override
					public IRequiredBundleDescription apply(String input) {
						return service.newRequiredBundle(input, null,
								false, false);
					}
				}).toArray(
				new IRequiredBundleDescription[dependencies.size()]);
		bundleDesc.setRequiredBundles(reqBundles);
	}

}