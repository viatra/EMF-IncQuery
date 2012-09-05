/*******************************************************************************
 * Copyright (c) 2010-2012, Tamas Szabo, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Tamas Szabo - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra2.emf.incquery.queryexplorer.handlers.util;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.ui.IEditorPart;
import org.eclipse.viatra2.emf.incquery.gui.IncQueryGUIPlugin;
import org.eclipse.viatra2.emf.incquery.queryexplorer.QueryExplorer;
import org.eclipse.viatra2.emf.incquery.queryexplorer.content.matcher.MatcherTreeViewerRootKey;
import org.eclipse.viatra2.emf.incquery.queryexplorer.util.ModelEditorPartListener;

public class EMFModelConnector extends ModelConnector {
	
	public EMFModelConnector(MatcherTreeViewerRootKey key) {
		super(key);
	}

	@Override
	public void loadModel() {
		workbenchPage.addPartListener(ModelEditorPartListener.getInstance());
		QueryExplorer.getInstance().getMatcherTreeViewerRoot().addPatternMatcherRoot(key);
	}

	@Override
	public void unloadModel() {
		workbenchPage.removePartListener(ModelEditorPartListener.getInstance());
		QueryExplorer.getInstance().getMatcherTreeViewerRoot().removePatternMatcherRoot(key);
	}

	@Override
	public void showLocation(Object[] locationObjects) {
		IStructuredSelection preparedSelection = prepareSelection(locationObjects);
		navigateToElements(key.getEditorPart(), preparedSelection);
		workbenchPage.bringToTop(key.getEditorPart());
		reflectiveSetSelection(key.getEditorPart(), preparedSelection); 
	}
	
	private void reflectiveSetSelection(IEditorPart editorPart, IStructuredSelection preparedSelection) {
		try {
			Method m = editorPart.getClass().getMethod("setSelectionToViewer", Collection.class);
			m.invoke(editorPart, preparedSelection.toList());
		}
		catch (NoSuchMethodException e) {
			logger.log(new Status(IStatus.ERROR, IncQueryGUIPlugin.PLUGIN_ID, "setSelectionToViewer method not found", e));
		}
		catch (Exception e) {
			logger.log(new Status(IStatus.ERROR, IncQueryGUIPlugin.PLUGIN_ID, "setSelectionToViewer call failed", e));
		}
	}
	
	protected TreeSelection prepareSelection(Object[] locationObjects) {
		List<TreePath> paths = new ArrayList<TreePath>();
		for (Object o: locationObjects) {
			if(o instanceof EObject) {
				TreePath path = createTreePath(key.getEditorPart(), (EObject) o);
				if(path != null) {
					paths.add(path);
				}
			}
		}

		if(paths.size() > 0) {
			return new TreeSelection(paths.toArray(new TreePath[1]));
		}
		return new TreeSelection();
	}
	
	protected void navigateToElements(IEditorPart editorPart, IStructuredSelection selection) {
		ISelectionProvider selectionProvider = editorPart.getEditorSite().getSelectionProvider();
		selectionProvider.setSelection(selection);
	}
	
	protected TreePath createTreePath(IEditorPart editorPart, EObject obj) {
		List<Object> nodes = new LinkedList<Object>();
		nodes.add(obj);
		EObject tmp = obj.eContainer();
			
		while (tmp != null) {
			nodes.add(0, tmp);
			tmp = tmp.eContainer();
		}
		
		return new TreePath(nodes.toArray());
	}

}
