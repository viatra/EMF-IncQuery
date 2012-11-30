/*******************************************************************************
 * Copyright (c) 2010-2012, Tamas Szabo, Zoltan Ujhelyi, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Tamas Szabo, Zoltan Ujhelyi - initial API and implementation
 *******************************************************************************/

package org.eclipse.incquery.tooling.ui.wizards.internal.operations;

import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.incquery.tooling.core.project.ProjectGenerationHelper;
import org.eclipse.ui.actions.WorkspaceModifyOperation;

public class EnsureProjectDependencies extends WorkspaceModifyOperation {
    private final IProject project;
    private final List<String> dependencies;

    public EnsureProjectDependencies(IProject project, List<String> dependencies) {
        this.project = project;
        this.dependencies = dependencies;
    }

    protected void execute(IProgressMonitor monitor) throws CoreException {
        ProjectGenerationHelper.ensureBundleDependencies(project, dependencies);
    }
}