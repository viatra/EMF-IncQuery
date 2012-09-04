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
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.viatra2.emf.incquery.queryexplorer.QueryExplorer;
import org.eclipse.viatra2.emf.incquery.queryexplorer.content.matcher.MatcherTreeViewerRootKey;
import org.eclipse.viatra2.emf.incquery.queryexplorer.handlers.util.EMFContentModel;
import org.eclipse.viatra2.emf.incquery.runtime.graphiti.util.GraphitiEditorPartListener;

public class GraphitiContentModel extends EMFContentModel {

	private IWorkbenchPage workbenchPage;
	
	public GraphitiContentModel(MatcherTreeViewerRootKey key) {
		super(key);
		this.workbenchPage = key.getEditor().getSite().getPage();
	}

	@Override
	public void loadModel() {
		workbenchPage.addPartListener(GraphitiEditorPartListener.getInstance());
		QueryExplorer.getInstance().getMatcherTreeViewerRoot().addPatternMatcherRoot(key);
	}

	@Override
	public void unloadModel() {
		workbenchPage.removePartListener(GraphitiEditorPartListener.getInstance());
		QueryExplorer.getInstance().getMatcherTreeViewerRoot().removePatternMatcherRoot(key);
	}

	@Override
	public void showLocation(Object[] locationObjects) {
		//reflective set selection is not needed
		IStructuredSelection preparedSelection = prepareSelection(locationObjects);
		navigateToElements(key.getEditor(), preparedSelection);
		workbenchPage.bringToTop(key.getEditor());
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
