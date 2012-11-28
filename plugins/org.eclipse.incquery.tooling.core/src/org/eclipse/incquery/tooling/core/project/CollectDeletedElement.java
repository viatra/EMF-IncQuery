/*******************************************************************************
 * Copyright (c) 2004-2011 Abel Hegedus and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Abel Hegedus - initial API and implementation
 *******************************************************************************/
package org.eclipse.incquery.tooling.core.project;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.runtime.CoreException;

class CollectDeletedElement implements IResourceVisitor {
	List<IResource> toDelete = new ArrayList<IResource>();
	@Override
	public boolean visit(IResource resource) throws CoreException {
		if (resource instanceof IFile
				&& "java".equalsIgnoreCase(((IFile) resource)
						.getFileExtension())) {
			toDelete.add(resource);
			return false;
		}
		return true;
	}
}