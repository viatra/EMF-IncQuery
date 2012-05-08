package org.eclipse.viatra2.emf.incquery.queryexplorer.content.detail;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

public class ModelElementListDialogContentProvider implements IStructuredContentProvider {

	private Notifier root;
	
	public ModelElementListDialogContentProvider(Notifier root) {
		this.root = root;
	}
	
	@Override
	public void dispose() {
		
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		if (newInput != null && newInput instanceof EObject) {
			root = (EObject) newInput;
		}
	}

	@Override
	public Object[] getElements(Object inputElement) {
		List<Object> result = new ArrayList<Object>();
		TreeIterator<EObject> iterator = null;
		
		if (root instanceof EObject) {
			iterator = ((EObject) root).eAllContents();
			
			while (iterator.hasNext()) {
				result.add(iterator.next());
			}
		}
		else if (root instanceof Resource) {
			iterator = ((Resource) root).getAllContents();
			
			while (iterator.hasNext()) {
				result.add(iterator.next());
			}
		}
		else if (root instanceof ResourceSet) {
			for (Resource res : ((ResourceSet) root).getResources()) {
				iterator = res.getAllContents();
				while (iterator.hasNext()) {
					result.add(iterator.next());
				}
			}
		}
		
		return result.toArray();
	}
	


}
