package org.eclipse.viatra2.emf.incquery.core.project;

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
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jdt.core.JavaCore;

/**
 * A common helper class for generating IncQuery-related projects.
 * @author Zoltan Ujhelyi
 */
public abstract class ProjectGenerationHelper {

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
	public static final String[] sourceFolders = { "src", "src-gen" };
	/**
	 * A single source folder named src
	 */
	public static final String[] singleSourceFolder = { "src" };

	/**
	 * This method initializes a new project with a selected name, but deletes
	 * any existing project with the same name - it shall be called with care!
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
	public static void deleteJavaFiles(IFolder folder, final IProgressMonitor monitor) throws CoreException {
		folder.refreshLocal(IResource.DEPTH_INFINITE, monitor);
		CollectDeletedElement visitor = new CollectDeletedElement();
		folder.accept(visitor);
		for (IResource res : visitor.toDelete) {
			res.delete(false, new SubProgressMonitor(monitor, 1));
		}
	};
	
}