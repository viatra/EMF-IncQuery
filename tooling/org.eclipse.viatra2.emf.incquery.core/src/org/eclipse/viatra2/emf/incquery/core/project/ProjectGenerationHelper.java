package org.eclipse.viatra2.emf.incquery.core.project;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.viatra2.framework.FrameworkManager;

/**
 * A common helper class for generating IncQuery-related projects.
 * @author Zoltan Ujhelyi
 */
public abstract class ProjectGenerationHelper {

	public static final String[] allNatures = {IncQueryNature.NATURE_ID, JavaCore.NATURE_ID, "org.eclipse.pde.PluginNature"};
	public static final String[] generatedNatures = {JavaCore.NATURE_ID, "org.eclipse.pde.PluginNature"};
	public static final String[] sourceFolders = {"src", "src-gen"};
	
	/**
	 * This method creates  new project using the input project description, and adds the following project natures to the project: 
	 * @param description
	 * @param proj
	 * @param monitor
	 * @return
	 * @throws CoreException
	 */
	public static IProjectDescription initializeProject(
			IProjectDescription description, IProject proj,
			IProgressMonitor monitor, String[] natures) throws CoreException {
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
		newNatures.addAll(Arrays.asList(natures));
		description.setNatureIds(newNatures.toArray(new String[] {}));
		proj.setDescription(description, monitor);
		return desc;
	}

	/**
	 * Initializes the Java project classpath with the selected source
	 * containers, the default JRE containers and the plug-in classpath
	 * @param proj
	 * @param monitor
	 * @param sourceFolders
	 * @throws CoreException
	 * @throws JavaModelException
	 */
	public static void initializeClasspath(IProject proj,
			IProgressMonitor monitor, String[] sourceFolders)
			throws CoreException, JavaModelException {
		final List<IClasspathEntry> classpathEntries = new ArrayList<IClasspathEntry>();
		final IJavaProject javaProject = JavaCore.create(proj);
		for (String folderName : sourceFolders) {
			final IFolder srcContainer = proj.getFolder(folderName);
			srcContainer.create(true, true, monitor);
			final IClasspathEntry srcClasspathEntry = JavaCore
					.newSourceEntry(srcContainer.getFullPath());
			classpathEntries.add(srcClasspathEntry);
		}
		// Plug-in classpath
		classpathEntries.add(JavaCore.newContainerEntry(new Path(
				"org.eclipse.pde.core.requiredPlugins")));
		classpathEntries.add(JavaRuntime.getDefaultJREContainerEntry());
		javaProject
				.setRawClasspath(classpathEntries
						.toArray(new IClasspathEntry[classpathEntries.size()]),
						monitor);
	}
	
	public static void initializeBuildProperties(IProject proj, IProgressMonitor monitor) throws FileNotFoundException, CoreException {
		/* TODO UGLY: Add the build.properties to the project */
		File buildProp = FrameworkManager.getFileFromBundle(
				IncQueryNature.BUNDLE_ID, IncQueryNature.SOURCE_BUILD_PROPERTIES);
		addFileToProject(proj, new Path("build.properties"), new FileInputStream(buildProp), monitor);
	}
	
	/**
	 * Adds a file to a container.
	 * @param container the container to add the file to
	 * @param path the path of the newly created file
	 * @param contentStream the file will be filled with this stream's contents
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
}
