package org.eclipse.viatra2.emf.incquery.queryexplorer.observable;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.ui.IEditorPart;
import org.eclipse.viatra2.emf.incquery.queryexplorer.util.DatabindingUtil;

/**
 * Top level element of the treeviewer.
 * It's children will be PatternMatcherRoot elements.
 * 
 * @author Tamas Szabo
 *
 */
public class ViewerRoot {
	private Map<ViewerRootKey, PatternMatcherRoot> roots;
	private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);
	
	public ViewerRoot() {
		roots = new HashMap<ViewerRootKey, PatternMatcherRoot>();
	}
	
	public void addPatternMatcherRoot(IEditorPart editorPart, ResourceSet res) {
		ViewerRootKey key = new ViewerRootKey(editorPart, res);
		if (!roots.containsKey(key)) {
			PatternMatcherRoot root = DatabindingUtil.createPatternMatcherRoot(key);	
			List<PatternMatcherRoot> oldValue = new ArrayList<PatternMatcherRoot>(roots.values());
			this.roots.put(key, root);
			List<PatternMatcherRoot> newValue = new ArrayList<PatternMatcherRoot>(roots.values());
			this.propertyChangeSupport.firePropertyChange("roots", oldValue, newValue);
		}
	}
	
	public void removePatternMatcherRoot(IEditorPart editorPart, ResourceSet res) {
		ViewerRootKey key = new ViewerRootKey(editorPart, res);
		if (roots.containsKey(key)) {
			List<PatternMatcherRoot> oldValue = new ArrayList<PatternMatcherRoot>(roots.values());
			this.roots.get(key).dispose();
			this.roots.remove(key);
			List<PatternMatcherRoot> newValue = new ArrayList<PatternMatcherRoot>(roots.values());
			this.propertyChangeSupport.firePropertyChange("roots", oldValue, newValue);
		}
	}
	
	public List<PatternMatcherRoot> getRoots() {
		return new ArrayList<PatternMatcherRoot>(roots.values());
	}
	
	public Map<ViewerRootKey, PatternMatcherRoot> getRootsMap() {
		return roots;
	}

	public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
		propertyChangeSupport.addPropertyChangeListener(propertyName, listener);
	}

	public void removePropertyChangeListener(PropertyChangeListener listener) {
		propertyChangeSupport.removePropertyChangeListener(listener);
	}
}
