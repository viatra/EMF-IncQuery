/*******************************************************************************
 * Copyright (c) 2004-2010 Gabor Bergmann and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Gabor Bergmann - initial API and implementation
 *******************************************************************************/

package org.eclipse.viatra2.emf.incquery.validation.runtime.ui.editorlink;

import org.eclipse.core.resources.IFile;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.viatra2.emf.incquery.runtime.exception.IncQueryRuntimeException;
//import org.eclipse.viatra2.emf.incquery.validation.core.ValidationUtil;

/**
 * @author Bergmann Gábor
 * Maintains a validator engine bound to an Eclipse editor.
 */
public class EditorBoundValidation {
	public static EditorBoundValidation INSTANCE = new EditorBoundValidation();
	
	//Map<IEditorPart, Notifier> activeEditorRoots = new HashMap<IEditorPart, Notifier>();
	//Map<IEditorPart, IFile> activeEditorFiles = new HashMap<IEditorPart, IFile>();

	public IFile getInputFile(IEditorPart editor) {
		if (editor!=null) {
			IEditorInput editorInput = editor.getEditorInput();
			if (editorInput != null && editorInput instanceof FileEditorInput) {
				return ((FileEditorInput)editorInput).getFile();
			}
		}
		return null;
	}
	
	public boolean initializeValidatorsOnEditor(IEditorPart editor, Notifier emfRoot) 
		throws IncQueryRuntimeException 
	{
		IFile inputFile = getInputFile(editor);
		if (inputFile == null) return false;
		boolean initialized = false;
		//ValidationUtil.initValidators(emfRoot, inputFile);
		if (initialized) {
			//activeEditorRoots.put(editor, emfRoot);
			registerPartListener(editor, inputFile, emfRoot);
		}
		return initialized;
	}
	
	private void registerPartListener(final IEditorPart targetEditor, final IFile inputFile, final Notifier emfRoot) {
		final IWorkbenchPage page = targetEditor.getEditorSite().getPage();
		page.addPartListener(new IPartListener2() {
			@Override
			public void partVisible(IWorkbenchPartReference partRef) { }
			
			@Override
			public void partOpened(IWorkbenchPartReference partRef) { }
			
			@Override
			public void partInputChanged(IWorkbenchPartReference partRef) { }
			
			@Override
			public void partHidden(IWorkbenchPartReference partRef) { }
			
			@Override
			public void partDeactivated(IWorkbenchPartReference partRef) { }
			
			@Override
			public void partClosed(IWorkbenchPartReference partRef) {
				IWorkbenchPart part = partRef.getPart(false);
				if (part.equals(targetEditor)) {
					//ValidationUtil.closeValidators(emfRoot, inputFile);
					page.removePartListener(this);
				}
			}
			
			@Override
			public void partBroughtToTop(IWorkbenchPartReference partRef) { }
			
			@Override
			public void partActivated(IWorkbenchPartReference partRef) { }
		});

	}
	

}
