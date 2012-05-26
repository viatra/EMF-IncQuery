package org.eclipse.viatra2.emf.incquery.queryexplorer.handlers;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.viatra2.emf.incquery.queryexplorer.content.matcher.ObservablePatternMatch;

public class ShowLocationHandler extends AbstractHandler {
	
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
		
		if (obj != null && obj instanceof ObservablePatternMatch) {
			ObservablePatternMatch pm = (ObservablePatternMatch) obj;
			
			IEditorPart editorPart = pm.getParent().getParent().getEditorPart();
			Object[] locationObjects = pm.getLocationObjects();
			IStructuredSelection preparedSelection = prepareSelection(editorPart, locationObjects);
			ISelectionProvider selectionProvider = editorPart.getEditorSite().getSelectionProvider();
			selectionProvider.setSelection(preparedSelection);
			
			//bring editor part to top
			editorPart.getSite().getPage().bringToTop(editorPart);
			
			reflectiveSetSelection(editorPart, preparedSelection); 
		}
	}

	private void reflectiveSetSelection(IEditorPart editorPart, IStructuredSelection preparedSelection) {
		//Reflection API is used here!!!
		try {
			Method m = editorPart.getClass().getMethod("setSelectionToViewer", Collection.class);
			m.invoke(editorPart, preparedSelection.toList());
		} 
		catch (Exception e) {
			System.out.println(e.getMessage());
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
			TreePath path = createTreePath(editorPart, (EObject) o);
			if(path != null) {
				paths.add(path);
			}
		}

		TreeSelection treeSelection = new TreeSelection((TreePath[]) paths.toArray(new TreePath[0]));
		return treeSelection;
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
