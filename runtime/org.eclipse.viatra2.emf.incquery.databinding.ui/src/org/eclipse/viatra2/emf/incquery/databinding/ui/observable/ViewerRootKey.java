package org.eclipse.viatra2.emf.incquery.databinding.ui.observable;

import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.ui.IEditorPart;

/**
 * the class is used to join an IEditorPart instance and a ResourceSet instance.
 * Such a key will be used to map the PatternMatcherRoot elements in the ViewerRoot.
 * 
 * @author Tamas Szabo
 *
 */
public class ViewerRootKey {

	private IEditorPart editor;
	private ResourceSet resourceSet;

	public ViewerRootKey(IEditorPart editor, ResourceSet resourceSet) {
		super();
		this.editor = editor;
		this.resourceSet = resourceSet;
	}

	public IEditorPart getEditor() {
		return editor;
	}

	public void setEditor(IEditorPart editor) {
		this.editor = editor;
	}

	public ResourceSet getResourceSet() {
		return resourceSet;
	}

	public void setResourceSet(ResourceSet resourceSet) {
		this.resourceSet = resourceSet;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof ViewerRootKey) {
			ViewerRootKey key = (ViewerRootKey) obj;
			if (key.getEditor().equals(editor)
					&& key.getResourceSet().equals(resourceSet))
				return true;
		}
		return false;
	}

	@Override
	public int hashCode() {
		return editor.hashCode() + resourceSet.hashCode();
	}

	@Override
	public String toString() {
		String uris = " (";
		
		int i = 0;
		for (Resource r : resourceSet.getResources()) {
			uris += r.getURI().toString();
			if (i != resourceSet.getResources().size()-1) {
				uris += " ,";
			}
		}
		
		uris += ")";
		
		return editor.getEditorSite().getId() + uris;
	}

}
