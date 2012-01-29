package org.eclipse.viatra2.emf.incquery.databinding.ui.handlers;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.viatra2.emf.incquery.databinding.ui.observable.PatternMatch;

public class ShowLocationHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IStructuredSelection selection = (IStructuredSelection) HandlerUtil.getCurrentSelection(event);
		
		if (selection instanceof TreeSelection) {
			Object obj = ((TreeSelection) selection).getFirstElement();
			
			if (obj != null && obj instanceof PatternMatch) {
				PatternMatch pm = (PatternMatch) obj;
				
				IEditorPart editorPart = pm.getParent().getParent().getEditorPart();
				Object[] locationObjects = pm.getLocationObjects();
				TreePath[] paths = new TreePath[locationObjects.length];
				int i = 0;
				
				for (Object o: locationObjects) {
					TreePath path = createTreePath((EObject) o);
					paths[i] = path;
					i++;
				}
				
				editorPart.getEditorSite().getSelectionProvider().setSelection(new TreeSelection(paths));
			}
			
		}
		
		return null;
	}
	
	private TreePath createTreePath(EObject obj) {
		List<Object> nodes = new ArrayList<Object>();
		nodes.add(obj);
		EObject tmp = obj.eContainer();
		
		while (tmp != null) {
			nodes.add(0, tmp);
			tmp = tmp.eContainer();
		}
		
		return new TreePath(nodes.toArray());
	}
}
