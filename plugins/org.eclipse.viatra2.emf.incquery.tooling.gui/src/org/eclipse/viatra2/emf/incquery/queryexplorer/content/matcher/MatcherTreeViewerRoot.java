package org.eclipse.viatra2.emf.incquery.queryexplorer.content.matcher;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.ui.IEditorPart;
import org.eclipse.viatra2.emf.incquery.queryexplorer.QueryExplorer;
import org.eclipse.viatra2.emf.incquery.queryexplorer.util.DatabindingUtil;

public class MatcherTreeViewerRoot {
private Map<MatcherTreeViewerRootKey, PatternMatcherRoot> roots;
	
	public MatcherTreeViewerRoot() {
		roots = new HashMap<MatcherTreeViewerRootKey, PatternMatcherRoot>();
	}
	
	public void addPatternMatcherRoot(IEditorPart editorPart, ResourceSet res) {
		MatcherTreeViewerRootKey key = new MatcherTreeViewerRootKey(editorPart, res);
		if (!roots.containsKey(key)) {
			PatternMatcherRoot root = DatabindingUtil.createPatternMatcherRoot(key);	
			this.roots.put(key, root);
			QueryExplorer.getInstance().getMatcherTreeViewer().refresh(this);
		}
	}
	
	public void removePatternMatcherRoot(IEditorPart editorPart, ResourceSet res) {
		MatcherTreeViewerRootKey key = new MatcherTreeViewerRootKey(editorPart, res);
		if (roots.containsKey(key)) {
			this.roots.get(key).dispose();
			this.roots.remove(key);
			QueryExplorer.getInstance().getMatcherTreeViewer().refresh(this);
		}
	}
	
	public Map<MatcherTreeViewerRootKey, PatternMatcherRoot> getRootsMap() {
		return roots;
	}
	
	public List<PatternMatcherRoot> getRoots() {
		return new ArrayList<PatternMatcherRoot>(roots.values());
	}
}
