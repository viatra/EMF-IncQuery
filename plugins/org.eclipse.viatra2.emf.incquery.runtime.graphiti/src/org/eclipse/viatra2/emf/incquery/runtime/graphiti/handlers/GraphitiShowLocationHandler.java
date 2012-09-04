/*******************************************************************************
 * Copyright (c) 2010-2012, Zoltan Ujhelyi, Abel Hegedus, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Zoltan Ujhelyi, Abel Hegedus - initial API and implementation
 *******************************************************************************/

package org.eclipse.viatra2.emf.incquery.runtime.graphiti.handlers;

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
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.ui.IEditorPart;
import org.eclipse.viatra2.emf.incquery.queryexplorer.content.matcher.ObservablePatternMatch;
import org.eclipse.viatra2.emf.incquery.queryexplorer.handlers.ShowLocationHandler;

public class GraphitiShowLocationHandler extends ShowLocationHandler {
	
	@Override
	public void showLocation(TreeSelection selection) {
		Object obj = selection.getFirstElement();
		
		if (obj instanceof ObservablePatternMatch) {
			ObservablePatternMatch pm = (ObservablePatternMatch) obj;
			
			IEditorPart editorPart = pm.getParent().getParent().getEditorPart();
			
			Object[] locationObjects = pm.getLocationObjects();
			IStructuredSelection preparedSelection = prepareSelection(editorPart, locationObjects);
			navigateToElements(editorPart, preparedSelection);

			editorPart.getSite().getPage().bringToTop(editorPart);
		}
	}
	
	@SuppressWarnings("restriction")
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
	
	@SuppressWarnings("restriction")
	@Override
	protected void navigateToElements(IEditorPart editorPart, IStructuredSelection selection) {
		if (editorPart instanceof DiagramEditor) {
			DiagramEditor providerEditor = (DiagramEditor) editorPart;
			providerEditor.getGraphicalViewer().setSelection(selection);
		}
	}
}
