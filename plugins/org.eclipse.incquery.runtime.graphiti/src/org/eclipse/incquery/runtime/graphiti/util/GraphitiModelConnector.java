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

package org.eclipse.incquery.runtime.graphiti.util;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.gef.EditPart;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.ui.editor.DiagramEditor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.viatra2.emf.incquery.queryexplorer.QueryExplorer;
import org.eclipse.viatra2.emf.incquery.queryexplorer.content.matcher.MatcherTreeViewerRootKey;
import org.eclipse.viatra2.emf.incquery.queryexplorer.handlers.util.EMFModelConnector;

public class GraphitiModelConnector extends EMFModelConnector {

	private IWorkbenchPage workbenchPage;
	
	public GraphitiModelConnector(MatcherTreeViewerRootKey key) {
		super(key);
		this.workbenchPage = key.getEditorPart().getSite().getPage();
	}

	@Override
	public void loadModel() {
		workbenchPage.addPartListener(GraphitiEditorPartListener.getInstance());
		if (QueryExplorer.getInstance() != null) {
			QueryExplorer.getInstance().getMatcherTreeViewerRoot().addPatternMatcherRoot(key);
		}
	}

	@Override
	public void unloadModel() {
		workbenchPage.removePartListener(GraphitiEditorPartListener.getInstance());
		if (QueryExplorer.getInstance() != null) {
			QueryExplorer.getInstance().getMatcherTreeViewerRoot().removePatternMatcherRoot(key);
		}
	}

	@Override
	public void showLocation(Object[] locationObjects) {
		//reflective set selection is not needed
		IStructuredSelection preparedSelection = prepareSelection(locationObjects);
		navigateToElements(key.getEditorPart(), preparedSelection);
		workbenchPage.bringToTop(key.getEditorPart());
	}
	
	@Override
	protected TreePath createTreePath(IEditorPart editor, EObject obj) {
		if (editor instanceof DiagramEditor) {			
			Diagram diagram = ((DiagramEditor) editor).getDiagramTypeProvider().getDiagram();
			List<PictogramElement> pictogramElements = Graphiti.getLinkService().getPictogramElements(diagram, obj);
			if (!pictogramElements.isEmpty()) {
				List<EditPart> parts = new ArrayList<EditPart>();
				for (PictogramElement element : pictogramElements) {
					EditPart part = ((DiagramEditor) editor).getEditPartForPictogramElement(element);
					if (part != null) {
						parts.add(part);
					}
				}
				return new TreePath(parts.toArray());
			}
		}
		return null;
	}
	
	@Override
	protected void navigateToElements(IEditorPart editorPart, IStructuredSelection selection) {
		if (editorPart instanceof DiagramEditor) {
			DiagramEditor providerEditor = (DiagramEditor) editorPart;
			providerEditor.getGraphicalViewer().setSelection(selection);
		}
	}

}
