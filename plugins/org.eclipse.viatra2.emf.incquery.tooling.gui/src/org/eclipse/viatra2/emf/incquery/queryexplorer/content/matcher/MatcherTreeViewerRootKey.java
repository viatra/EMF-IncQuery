package org.eclipse.viatra2.emf.incquery.queryexplorer.content.matcher;

import org.eclipse.emf.common.notify.Notifier;
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
public class MatcherTreeViewerRootKey {

	private IEditorPart editor;
	private Notifier notifier;

	public MatcherTreeViewerRootKey(IEditorPart editor, Notifier notifier) {
		super();
		this.editor = editor;
		this.notifier = notifier;
	}

	public IEditorPart getEditor() {
		return editor;
	}

	public void setEditor(IEditorPart editor) {
		this.editor = editor;
	}

	public Notifier getNotifier() {
		return notifier;
	}

	public void setNotifier(Notifier notifier) {
		this.notifier = notifier;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof MatcherTreeViewerRootKey) {
			MatcherTreeViewerRootKey key = (MatcherTreeViewerRootKey) obj;
			if (key.getEditor().equals(editor)
					&& key.getNotifier().equals(notifier))
				return true;
		}
		return false;
	}

	@Override
	public int hashCode() {
		return editor.hashCode() + notifier.hashCode();
	}

	@Override
	public String toString() {
		String uris = " (";
		
		int i = 0;
		
		if (notifier instanceof ResourceSet) {
			ResourceSet rs = (ResourceSet) notifier;
			
			for (Resource r : rs.getResources()) {
				uris += r.getURI().toString();
				if (i != rs.getResources().size()-1) {
					uris += " ,";
				}
			}
		}
		else if (notifier instanceof Resource) {
			uris += ((Resource) notifier).getURI().toString();
		}
		else {
			uris += notifier.toString();
		}
		
		uris += ")";
		
		return editor.getEditorSite().getId() + uris;
	}

}
