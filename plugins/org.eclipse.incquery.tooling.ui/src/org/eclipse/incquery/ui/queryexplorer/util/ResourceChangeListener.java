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

package org.eclipse.incquery.ui.queryexplorer.util;

import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.incquery.ui.IncQueryGUIPlugin;

import com.google.inject.Injector;

public class ResourceChangeListener implements IResourceChangeListener {
	private final Injector injector;

	public ResourceChangeListener(Injector injector) {
		this.injector = injector;
	}

	public void resourceChanged(IResourceChangeEvent event) {	
		if (event.getType() == IResourceChangeEvent.PRE_BUILD) {
			try {
				event.getDelta().accept(new DeltaVisitor(injector));
			} catch (CoreException e) {
				IncQueryGUIPlugin.getDefault().logException("Visitor failed on delta", e);
			}
		}
	}
}