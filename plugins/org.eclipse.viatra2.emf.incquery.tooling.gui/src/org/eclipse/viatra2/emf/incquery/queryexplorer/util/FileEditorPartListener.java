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
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.viatra2.emf.incquery.queryexplorer.handlers.RuntimeMatcherUnRegistrator;

/**
 * The PartListener is used to observe EditorPart close actions.
 * 
 * @author Tamas Szabo
 */
public class FileEditorPartListener extends BasePartListener {
	
	private final String dialogTitle = ".eiq file editor closing";
	
	@Override
	public void partClosed(IWorkbenchPart part) {
		if (part != null && part instanceof IEditorPart) {
			IEditorPart closedEditor = (IEditorPart) part;
			IEditorInput editorInput = closedEditor.getEditorInput();
			
			if (editorInput != null && editorInput instanceof FileEditorInput) {
				IFile file = ((FileEditorInput) editorInput).getFile();
				
				if (file != null && file.getFileExtension().matches("eiq") && PatternRegistry.getInstance().getFiles().contains(file)) {
					String question = "There are patterns (from file named '"+file.getName()+"') registered in the Query Explorer.\nWould you like to unregister them?";
					boolean answer = MessageDialog.openQuestion(closedEditor.getSite().getShell(), dialogTitle, question);
					if (answer) {
						RuntimeMatcherUnRegistrator job = new RuntimeMatcherUnRegistrator(file);
						job.run();
					}
				}
			}
		}
	}

}
