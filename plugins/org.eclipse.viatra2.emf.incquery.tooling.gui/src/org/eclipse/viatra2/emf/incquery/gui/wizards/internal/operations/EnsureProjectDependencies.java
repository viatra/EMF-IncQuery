package org.eclipse.viatra2.emf.incquery.gui.wizards.internal.operations;

import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.actions.WorkspaceModifyOperation;
import org.eclipse.viatra2.emf.incquery.core.project.ProjectGenerationHelper;

public class EnsureProjectDependencies extends
		WorkspaceModifyOperation {
	private final IProject project;
	private final List<String> dependencies;

	public EnsureProjectDependencies(IProject project,
			List<String> dependencies) {
		this.project = project;
		this.dependencies = dependencies;
	}

	protected void execute(IProgressMonitor monitor)
			throws CoreException {
		ProjectGenerationHelper.ensureBundleDependencies(project, dependencies);
	}
}