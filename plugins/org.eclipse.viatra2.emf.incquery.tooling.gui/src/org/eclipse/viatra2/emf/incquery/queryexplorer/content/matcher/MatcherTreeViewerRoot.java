/*******************************************************************************
 * Copyright (c) 2010-2012, Zoltan Ujhelyi, Tamas Szabo, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Zoltan Ujhelyi, Tamas Szabo - initial API and implementation
 *******************************************************************************/

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
import org.eclipse.viatra2.emf.incquery.runtime.api.IncQueryEngine;

public class MatcherTreeViewerRoot {
private Map<MatcherTreeViewerRootKey, ObservablePatternMatcherRoot> roots;
	
	public MatcherTreeViewerRoot() {
		roots = new HashMap<MatcherTreeViewerRootKey, ObservablePatternMatcherRoot>();
	}
	
	public void addPatternMatcherRoot(IEditorPart editorPart, Notifier notifier) {
		MatcherTreeViewerRootKey key = new MatcherTreeViewerRootKey(editorPart, notifier);
		addPatternMatcherRoot(key);
	}
	
	public void addPatternMatcherRoot(MatcherTreeViewerRootKey key) {
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
			//Notifier notifier = key.getNotifier();
			//disposing IncQueryEngine instance associated to the given Notifier
			//EngineManager.getInstance().disposeEngine(notifier);
		  ObservablePatternMatcherRoot root = this.roots.get(key);
		  IncQueryEngine engine = root.getKey().getEngine();
			if(engine != null) {
			  engine.dispose();
			}
      root.dispose();
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
