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

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.viatra2.emf.incquery.queryexplorer.content.matcher.ObservablePatternMatch;
import org.eclipse.viatra2.emf.incquery.queryexplorer.content.matcher.ObservablePatternMatcher;
import org.eclipse.viatra2.emf.incquery.queryexplorer.util.PatternRegistry;
import org.eclipse.viatra2.emf.incquery.runtime.api.IncQueryEngine;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Pattern;
import org.eclipse.xtext.resource.ILocationInFileProvider;
import org.eclipse.xtext.ui.editor.XtextEditor;
import org.eclipse.xtext.util.ITextRegion;

import com.google.inject.Inject;

public class ShowLocationHandler extends AbstractHandler {
	
	@Inject
	private ILocationInFileProvider locationProvider;
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IStructuredSelection selection = (IStructuredSelection) HandlerUtil.getCurrentSelection(event);
		if (selection instanceof TreeSelection) {
			showLocation((TreeSelection) selection);
		}
		return null;
	}
	
	public void showLocation(TreeSelection selection) {
		Object obj = selection.getFirstElement();
		
		if (obj instanceof ObservablePatternMatch) {
			ObservablePatternMatch pm = (ObservablePatternMatch) obj;
			
			IEditorPart editorPart = pm.getParent().getParent().getEditorPart();
			
			Object[] locationObjects = pm.getLocationObjects();
			IStructuredSelection preparedSelection = prepareSelection(editorPart, locationObjects);
			navigateToElements(editorPart, preparedSelection);

			editorPart.getSite().getPage().bringToTop(editorPart);
			
			reflectiveSetSelection(editorPart, preparedSelection); 
		} else if(obj instanceof ObservablePatternMatcher) {
			ObservablePatternMatcher matcher = (ObservablePatternMatcher) obj;
			if (matcher.getMatcher() != null) {
				setSelectionToXTextEditor(matcher.getMatcher().getPattern());
			}
		}
	}
	
	protected void setSelectionToXTextEditor(Pattern pattern) {
		IFile file = PatternRegistry.getInstance().getFileForPattern(pattern);

		for (IEditorReference ref : PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getEditorReferences()) {
			String id = ref.getId();
			IEditorPart editor = ref.getEditor(true);
			if(id.equals("org.eclipse.viatra2.patternlanguage.EMFPatternLanguage")) {
				//The editor id always registers an Xtext editor
				assert editor instanceof XtextEditor;
				XtextEditor providerEditor = (XtextEditor) editor;
				// Bringing editor to top
				IEditorInput input = providerEditor.getEditorInput();
				if (input instanceof FileEditorInput) {
					FileEditorInput editorInput = (FileEditorInput) input;
					if (editorInput.getFile().equals(file)) {
						editor.getSite().getPage().bringToTop(editor);
					}
				}
				// Finding location using location service
				ITextRegion location = locationProvider.getSignificantTextRegion(pattern);
				//Location can be null in case of error
				if (location != null) {
					providerEditor.reveal(location.getOffset(),location.getLength());
					providerEditor.getSelectionProvider().setSelection(new TextSelection(location.getOffset(), location.getLength()));
				}
			}
		}
	}

	private void reflectiveSetSelection(IEditorPart editorPart, IStructuredSelection preparedSelection) {
		//Reflection API is used here!!!
		try {
			Method m = editorPart.getClass().getMethod("setSelectionToViewer", Collection.class);
			m.invoke(editorPart, preparedSelection.toList());
		}
		catch (NoSuchMethodException e) {
			IncQueryEngine.getDefaultLogger().logDebug("setSelectionToViewer method not found");
		}
		catch (Exception e) {
			IncQueryEngine.getDefaultLogger().logDebug("setSelectionToViewer call failed");
		}
	}

	/**
	 * @param editorPart
	 * @param locationObjects
	 * @return
	 */
	private TreeSelection prepareSelection(IEditorPart editorPart, Object[] locationObjects) {
		List<TreePath> paths = new ArrayList<TreePath>(); //[locationObjects.length]
		for (Object o: locationObjects) {
			if(o instanceof EObject) {
				TreePath path = createTreePath(editorPart, (EObject) o);
				if(path != null) {
					paths.add(path);
				}
			}
		}

		if(paths.size() > 0) {
			return new TreeSelection((TreePath[]) paths.toArray(new TreePath[1]));
		}
		return new TreeSelection();
	}
	
	protected void navigateToElements(IEditorPart editorPart, IStructuredSelection selection) {
		ISelectionProvider selectionProvider = editorPart.getEditorSite().getSelectionProvider();
		selectionProvider.setSelection(selection);
	}
	
	protected TreePath createTreePath(IEditorPart editor, EObject obj) {
		List<Object> nodes = new ArrayList<Object>();
		nodes.add(obj);
		EObject tmp = ((EObject) obj).eContainer();
			
		while (tmp != null) {
			nodes.add(0, tmp);
			tmp = tmp.eContainer();
		}
		
		return new TreePath(nodes.toArray());
	}
	
}