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

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.ui.actions.WorkspaceModifyOperation;

public class CompositeWorkspaceModifyOperation extends WorkspaceModifyOperation {

	WorkspaceModifyOperation[] operations;
	private String description;
	
	public CompositeWorkspaceModifyOperation(
			WorkspaceModifyOperation[] operations, String description) {
		super();
		this.operations = operations;
		this.description = description;
	}

	@Override
	protected void execute(IProgressMonitor monitor) throws CoreException,
			InvocationTargetException, InterruptedException {
		monitor.beginTask(description, 10 * operations.length);
		for (WorkspaceModifyOperation op : operations) {
			op.run(new SubProgressMonitor(monitor, 10));
		}
		monitor.done();
	}

}
