package org.eclipse.viatra2.emf.incquery.queryexplorer.content.matcher;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.ui.IEditorPart;
import org.eclipse.viatra2.emf.incquery.queryexplorer.QueryExplorer;
import org.eclipse.viatra2.emf.incquery.queryexplorer.util.DatabindingUtil;
import org.eclipse.viatra2.emf.incquery.runtime.api.EngineManager;

public class MatcherTreeViewerRoot {
private Map<MatcherTreeViewerRootKey, ObservablePatternMatcherRoot> roots;
	
	public MatcherTreeViewerRoot() {
		roots = new HashMap<MatcherTreeViewerRootKey, ObservablePatternMatcherRoot>();
	}
	
	public void addPatternMatcherRoot(IEditorPart editorPart, ResourceSet res) {
		MatcherTreeViewerRootKey key = new MatcherTreeViewerRootKey(editorPart, res);
		if (!roots.containsKey(key)) {
			ObservablePatternMatcherRoot root = DatabindingUtil.createPatternMatcherRoot(key);	
			this.roots.put(key, root);
			QueryExplorer.getInstance().getMatcherTreeViewer().refresh(this);
		}
	}
	
	public void removePatternMatcherRoot(IEditorPart editorPart, ResourceSet res) {
		MatcherTreeViewerRootKey key = new MatcherTreeViewerRootKey(editorPart, res);
		removePatternMatcherRoot(key);
	}
	
	public void removePatternMatcherRoot(MatcherTreeViewerRootKey key) {
		if (roots.containsKey(key)) {
			Notifier notifier = key.getNotifier();
			//disposing IncQueryEngine instance associated to the given Notifier
			EngineManager.getInstance().getIncQueryEngine(notifier).dispose();			
			this.roots.get(key).dispose();
			this.roots.remove(key);
			QueryExplorer.getInstance().getMatcherTreeViewer().refresh(this);
		}
	}
	
	public Map<MatcherTreeViewerRootKey, ObservablePatternMatcherRoot> getRootsMap() {
		return roots;
	}
	
	public List<ObservablePatternMatcherRoot> getRoots() {
		return new ArrayList<ObservablePatternMatcherRoot>(roots.values());
	}
}
