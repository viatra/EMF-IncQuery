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

package org.eclipse.incquery.ui.queryexplorer.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.handlers.HandlerUtil;

import com.google.inject.Inject;
import com.google.inject.Injector;

public class LoadEiqPatternHandler extends AbstractHandler {

	@Inject
	Injector injector;

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {

		try {
			IFile file = (IFile) HandlerUtil.getActiveEditorInput(event).getAdapter(IFile.class);
			if (file != null) {
				RuntimeMatcherRegistrator registrator = new RuntimeMatcherRegistrator(file);
				injector.injectMembers(registrator);
				Display.getDefault().asyncExec(registrator);
			}
		} catch (Exception e) {
			throw new ExecutionException("Cannot load pattern file", e);
		}

		return null;
	}
}
