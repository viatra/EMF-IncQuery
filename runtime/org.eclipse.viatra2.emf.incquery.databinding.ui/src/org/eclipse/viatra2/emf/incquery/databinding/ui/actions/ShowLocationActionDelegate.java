package org.eclipse.viatra2.emf.incquery.databinding.ui.actions;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.viatra2.emf.incquery.databinding.ui.observable.PatternMatch;

public class ShowLocationActionDelegate implements IViewActionDelegate {

	Object selection;
	
	@Override
	public void run(IAction action) {
		if (this.selection != null && this.selection instanceof PatternMatch) {
			PatternMatch pm = (PatternMatch) this.selection;
			
			IEditorPart editorPart = pm.getParent().getParent().getEditorPart();
			Object[] locationObjects = pm.getLocationObjects();
			for (Object o: locationObjects) {
				TreePath path = createTreePath((EObject) o);
				System.out.println(editorPart.getEditorSite());
				editorPart.getEditorSite().getSelectionProvider().setSelection(new TreeSelection(path));
			}
		}
	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		if (selection instanceof TreeSelection) {
			this.selection = ((TreeSelection) selection).getFirstElement();
		}
	}

	@Override
	public void init(IViewPart view) {
		
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
