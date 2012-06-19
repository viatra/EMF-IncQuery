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

package org.eclipse.viatra2.emf.incquery.queryexplorer.content.patternsviewer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.viatra2.emf.incquery.queryexplorer.QueryExplorer;

/**
 * This class represents a composite element inside a pattern hierarchy.
 * 
 * @author Tamas Szabo
 *
 */
public class PatternComposite extends PatternComponent {

	private List<PatternComponent> children;
	private Map<String, PatternComposite> fragmentMap;
	private boolean isHierarchical;
	
	public PatternComposite(String patternNameFragment, PatternComposite parent, boolean hierarchical) {
		super();
		this.patternNameFragment = patternNameFragment;
		this.children = new ArrayList<PatternComponent>();
		this.fragmentMap = new HashMap<String, PatternComposite>();
		this.parent = parent;
		this.isHierarchical = hierarchical;
	}
	
	/**
	 * Add a new component under the composite element based on the given pattern name fragment.
	 *  
	 * @param patternFragment the pattern name fragment
	 */
	public PatternComponent addComponent(String patternFragment) {
		String[] tokens = patternFragment.split("\\.");
		
		if (tokens.length == 1) {
			PatternLeaf leaf = new PatternLeaf(patternFragment, this);
			leaf.setSelected(true);
			children.add(leaf);
			return leaf;
		}
		else {
			String prefix = null, suffix = null;
			if (isHierarchical) {
				prefix = tokens[0];
				suffix = patternFragment.substring(prefix.length()+1);
			}
			else {
				suffix = tokens[tokens.length - 1];
				prefix = patternFragment.substring(0, patternFragment.length() - suffix.length() - 1);
			}
			
			PatternComposite composite = fragmentMap.get(prefix);
			
			if (composite == null) {
				composite = new PatternComposite(prefix, this, isHierarchical);
				fragmentMap.put(prefix, composite);
				children.add(composite);
			}
			return composite.addComponent(suffix);
		}
	}
	
	/**
	 * Returns the list of (ALL) leaf objects under this composite.
	 * 
	 * @return the list of leaves
	 */
	public List<PatternLeaf> getLeaves() {
		List<PatternLeaf> leaves = new ArrayList<PatternLeaf>();
		
		for (PatternComponent component : children) {
			if (component instanceof PatternLeaf) {
				leaves.add((PatternLeaf) component);
			}
			else {
				leaves.addAll(((PatternComposite) component).getLeaves()); 
			}
		}
		
		return leaves;
	}
	
	/**
	 * Remove all composite elements which do not have a leaf component under it.
	 */
	public void purge() {
		List<PatternComponent> copyOfChildren = new ArrayList<PatternComponent>(children);
			
		for (PatternComponent component : copyOfChildren) {
			if (component instanceof PatternComposite) {
				PatternComposite composite = (PatternComposite) component;
				if (composite.getLeaves().size() == 0) {
					QueryExplorer.getInstance().getPatternsViewerModel().
						removeComponent(composite.getFullPatternNamePrefix(), true);
				}
				else {
					composite.purge();
				}
			}
		}
	}
	
	/**
	 * Returns ALL children elements under the given composite. 
	 * 
	 * @return
	 */
	public List<PatternComponent> getAllChildren() {
		List<PatternComponent> result = new ArrayList<PatternComponent>(this.children);
		
		for (PatternComponent component : children) {
			if (component instanceof PatternComposite) {
				result.addAll(((PatternComposite) component).getAllChildren());
			}
		}
		
		return result;
	}
	
	/**
	 * Propagates element deselection upwards in the hierarchy.
	 */
	public void propagateDeSelectionToTop() {
		QueryExplorer.getInstance().getPatternsViewer().setChecked(this, false);
		this.setSelected(false);
		if (this.parent != null) {
			this.parent.propagateDeSelectionToTop();
		}
	}
	
	/**
	 * Propagates element selection upwards in the hierarchy.
	 */
	public void propagateSelectionToTop(PatternComponent selected) {
		boolean allSelected = true;
		for (PatternComponent component : children) {
			if (!(component == selected) && (!QueryExplorer.getInstance().getPatternsViewer().getChecked(component))) {
				allSelected = false;
			}
		}
		
		if (allSelected) {
			QueryExplorer.getInstance().getPatternsViewer().setChecked(this, true);
			this.setSelected(true);
			if (this.parent != null) {
				this.parent.propagateSelectionToTop(this);
			}
		}
	}
	
	/**
	 * This method removes the component matching the given pattern name fragment.
	 * 
	 * @param patternFragment the pattern name fragment
	 * @param handleInWhole tells whether to handle the whole pattern fragment 
	 * (this is used when flat presentation is on and the user wants to unregister a non-leaf element) 
	 */
	public void removeComponent(String patternFragment, boolean handleInWhole) {
		String[] tokens = patternFragment.split("\\.");
		if (handleInWhole || tokens.length == 1) {
			PatternComponent component = null;
			for (PatternComponent c : children) {
				if (c.getPatternNameFragment().matches(patternFragment)) {
					component = c;
				}
			}
			if (component != null) {
				children.remove(component);
				fragmentMap.remove(patternFragment);
			}
		}
		else {
			String prefix = null, suffix = null;
			if (isHierarchical) {
				prefix = tokens[0];
				suffix = patternFragment.substring(prefix.length()+1);
			}
			else {
				suffix = tokens[tokens.length - 1];
				prefix = patternFragment.substring(0, patternFragment.length() - suffix.length() - 1);
			}

			PatternComposite composite = fragmentMap.get(prefix);

			if (composite != null) {
				composite.removeComponent(suffix, false);
			}
		}
	}
	
	/**
	 * Returns the list of direct children element under the composite.
	 * 
	 * @return the list of children elements
	 */
	public List<PatternComponent> getChildren() {
		return children;
	}
	
	@Override
	public String getFullPatternNamePrefix() {
		StringBuilder sb = new StringBuilder(patternNameFragment);
		if (parent != null) {
			if (parent.getParent() != null) {
				sb.insert(0, ".");
			}
			sb.insert(0, parent.getFullPatternNamePrefix());
		}
		return sb.toString();
	}
}
