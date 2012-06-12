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

package org.eclipse.viatra2.emf.incquery.queryexplorer.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.xtext.xbase.ui.editor.XbaseEditor;

import com.google.inject.Inject;
import com.google.inject.Injector;

@SuppressWarnings("restriction")
public class RuntimeMatcherRegistrationHandler extends AbstractHandler {

	@Inject
	Injector injector;
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IFile file = null;
		ISelection selection = HandlerUtil.getCurrentSelection(event);
		
		if (selection != null && selection instanceof IStructuredSelection) {
			Object firstElement = ((IStructuredSelection) selection).getFirstElement();
			if (firstElement != null && firstElement instanceof IFile) {
				file = (IFile) firstElement;
			}
		}
		else {
			IEditorPart editor = HandlerUtil.getActiveEditor(event);
			if (editor instanceof XbaseEditor) {
				FileEditorInput input = (FileEditorInput) HandlerUtil.getActiveEditorInput(event);
				file = input.getFile();
			}
		}
		
		if (file != null) {
			RuntimeMatcherRegistrator registrator = new RuntimeMatcherRegistrator(file);
			injector.injectMembers(registrator);
			Display.getDefault().asyncExec(registrator);
		}
		
		return null;
	}

}
