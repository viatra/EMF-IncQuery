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

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.NotEnabledException;
import org.eclipse.core.commands.NotHandledException;
import org.eclipse.core.commands.common.NotDefinedException;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.viatra2.emf.incquery.runtime.api.IncQueryEngine;

public class DoubleClickListener implements IDoubleClickListener {

	private static final String EXCEPTION_WHEN_ACTIVATING_SHOW_LOCATION = "Exception when activating show location!";

	@Override
	public void doubleClick(DoubleClickEvent event) {
		ISelection selection = event.getSelection();
		if (selection instanceof TreeSelection) {
			IHandlerService handlerService = (IHandlerService) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getService(IHandlerService.class);
			try {
				handlerService.executeCommand(CommandConstants.SHOW_LOCATION_COMMAND_ID, null);
			} catch (ExecutionException e) {
				IncQueryEngine.getDefaultLogger().error(EXCEPTION_WHEN_ACTIVATING_SHOW_LOCATION, e);
			} catch (NotDefinedException e) {
				IncQueryEngine.getDefaultLogger().error(EXCEPTION_WHEN_ACTIVATING_SHOW_LOCATION, e);
			} catch (NotEnabledException e) {
				IncQueryEngine.getDefaultLogger().error(EXCEPTION_WHEN_ACTIVATING_SHOW_LOCATION, e);
			} catch (NotHandledException e) {
				IncQueryEngine.getDefaultLogger().error(EXCEPTION_WHEN_ACTIVATING_SHOW_LOCATION, e);
			}
		}
	}

}
