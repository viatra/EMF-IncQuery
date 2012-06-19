package org.eclipse.viatra2.emf.incquery.queryexplorer.util;

import org.eclipse.viatra2.emf.incquery.queryexplorer.QueryExplorer;
import org.eclipse.viatra2.emf.incquery.queryexplorer.content.patternsviewer.PatternComponent;
import org.eclipse.viatra2.emf.incquery.queryexplorer.content.patternsviewer.PatternComposite;

public class PatternsViewerModel {

	private PatternComposite flatRoot;
	private PatternComposite hierarchicalRoot;
	private boolean flat;
	
	public PatternsViewerModel(boolean flat) {
		this.flat = flat;
		this.flatRoot = new PatternComposite("", null, false);
		this.hierarchicalRoot = new PatternComposite("", null, true);
	}

	public PatternComposite getActiveRoot() {
		return (flat) ? flatRoot : hierarchicalRoot;
	}
	
	public void setFlat() {
		this.flat = true;
		QueryExplorer.getInstance().getPatternsViewer().setInput(flatRoot);
		refreshTreeViewer();
	}
	
	public void setHierarchical() {
		this.flat = false;
		QueryExplorer.getInstance().getPatternsViewer().setInput(hierarchicalRoot);
		refreshTreeViewer();
	}
	
	public PatternComponent addComponent(String patternFragment) {
		PatternComponent comp1 = this.flatRoot.addComponent(patternFragment);
		PatternComponent comp2 = this.hierarchicalRoot.addComponent(patternFragment);
		return (flat) ? comp1 : comp2;
	}
	
	public void removeComponent(String patternFragment, boolean handleInWhole) {
		this.flatRoot.removeComponent(patternFragment, handleInWhole);
		this.hierarchicalRoot.removeComponent(patternFragment, handleInWhole);
	}
	
	public void purge() {
		this.flatRoot.purge();
		this.hierarchicalRoot.purge();
	}
	
	private void refreshTreeViewer() {
		PatternComposite root = getActiveRoot();
		
		for (PatternComponent pc : root.getAllChildren()) {
			if (pc.isSelected()) {
				QueryExplorer.getInstance().getPatternsViewer().setChecked(pc, true);
			}
		}
	}
}
