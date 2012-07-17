/*******************************************************************************
 * Copyright (c) 2010-2012, Zoltan Ujhelyi, Tamas Szabo, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Zoltan Ujhelyi, Tamas Szabo - initial API and implementation
 *******************************************************************************/

package org.eclipse.viatra2.emf.incquery.queryexplorer.util;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.swt.widgets.Display;
import org.eclipse.viatra2.emf.incquery.queryexplorer.handlers.RuntimeMatcherRegistrator;

import com.google.inject.Injector;

class DeltaVisitor implements IResourceDeltaVisitor {
	private final Injector injector;

	public DeltaVisitor(Injector injector) {
		this.injector = injector;
	}

	public boolean visit(IResourceDelta delta) {
		IResource res = delta.getResource();
		
		if (res instanceof IFile && delta.getKind() == IResourceDelta.CHANGED) {
			
			IFile file = (IFile) res;
			if (PatternRegistry.getInstance().getFiles().contains(file)) {
				RuntimeMatcherRegistrator registrator = new RuntimeMatcherRegistrator((IFile) file);
				injector.injectMembers(registrator);
				Display.getDefault().asyncExec(registrator);
			}
			return false;
		}
		return true;
	}
}
