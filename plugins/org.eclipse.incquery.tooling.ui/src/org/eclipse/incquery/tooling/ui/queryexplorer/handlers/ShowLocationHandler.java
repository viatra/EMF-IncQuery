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

package org.eclipse.incquery.tooling.ui.queryexplorer.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.incquery.patternlanguage.patternLanguage.Pattern;
import org.eclipse.incquery.tooling.ui.queryexplorer.QueryExplorer;
import org.eclipse.incquery.tooling.ui.queryexplorer.content.matcher.MatcherTreeViewerRootKey;
import org.eclipse.incquery.tooling.ui.queryexplorer.content.matcher.ObservablePatternMatch;
import org.eclipse.incquery.tooling.ui.queryexplorer.content.matcher.ObservablePatternMatcher;
import org.eclipse.incquery.tooling.ui.queryexplorer.util.PatternRegistry;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.part.FileEditorInput;
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
			Object obj = selection.getFirstElement();
			
			if (obj instanceof ObservablePatternMatch) {
				ObservablePatternMatch pm = (ObservablePatternMatch) obj;
				MatcherTreeViewerRootKey key = pm.getParent().getParent().getKey();
				QueryExplorer.getInstance().getModelConnectorMap().get(key).showLocation(pm.getLocationObjects());
			} 
			else if (obj instanceof ObservablePatternMatcher) {
				ObservablePatternMatcher matcher = (ObservablePatternMatcher) obj;
				if (matcher.getMatcher() != null) {
					setSelectionToXTextEditor(matcher.getMatcher().getPattern());
				}
			}
		}
		return null;
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
}