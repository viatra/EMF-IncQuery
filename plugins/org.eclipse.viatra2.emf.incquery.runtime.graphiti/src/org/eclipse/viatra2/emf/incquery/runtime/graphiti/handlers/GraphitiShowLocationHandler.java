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

import org.eclipse.viatra2.emf.incquery.queryexplorer.handlers.ShowLocationHandler;

public class GraphitiShowLocationHandler extends ShowLocationHandler {

//	/* (non-Javadoc)
//	 * @see org.eclipse.viatra2.emf.incquery.queryexplorer.handlers.ShowLocationHandler#createTreePath(java.lang.Object)
//	 */
//	@Override
//	protected TreePath createTreePath(IEditorPart editor, EObject obj) {
//		if (editor instanceof DiagramDocumentEditor) {
//			DiagramDocumentEditor providerEditor = (DiagramDocumentEditor) editor;
//			EditPart epBegin = providerEditor.getDiagramEditPart().getPrimaryChildEditPart();
//			if(epBegin instanceof GraphicalEditPart) {
//				List<Object> nodes = new ArrayList<Object>();
//				epBegin = ((GraphicalEditPart) epBegin).findEditPart(epBegin.getRoot() , obj);
//				if(epBegin != null) {
//					nodes.add(epBegin);
//					return new TreePath(nodes.toArray());
//				}
//			}
//		}
//		return null;
//	}
//	
//	/* (non-Javadoc)
//	 * @see org.eclipse.viatra2.emf.incquery.queryexplorer.handlers.ShowLocationHandler#navigateToElements(org.eclipse.ui.IEditorPart, org.eclipse.jface.viewers.IStructuredSelection)
//	 */
//	@Override
//	protected void navigateToElements(IEditorPart editorPart, IStructuredSelection selection) {
//		super.navigateToElements(editorPart, selection);
//		if (editorPart instanceof DiagramDocumentEditor) {
//				DiagramDocumentEditor providerEditor = (DiagramDocumentEditor) editorPart;
//				IDiagramGraphicalViewer viewer = providerEditor.getDiagramGraphicalViewer();
//				if (selection.getFirstElement() instanceof GraphicalEditPart) {
//					GraphicalEditPart part = (GraphicalEditPart) selection.getFirstElement();
//					viewer.reveal(part);
//				}
//		}
//	}

}
