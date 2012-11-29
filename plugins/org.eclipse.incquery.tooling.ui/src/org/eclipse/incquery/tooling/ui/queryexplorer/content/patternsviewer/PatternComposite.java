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

package org.eclipse.incquery.tooling.ui.queryexplorer.content.patternsviewer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.incquery.tooling.ui.queryexplorer.QueryExplorer;
import org.eclipse.jface.viewers.CheckboxTreeViewer;

/**
 * This class represents a composite element inside a pattern hierarchy.
 * 
 * @author Tamas Szabo
 *
 */
public class PatternComposite extends PatternComponent {

	protected List<PatternComponent> children;
	private Map<String, PatternComposite> fragmentMap;
	
	public PatternComposite(String patternNameFragment, PatternComposite parent) {
		super();
		this.patternNameFragment = patternNameFragment;
		this.children = new ArrayList<PatternComponent>();
		this.fragmentMap = new HashMap<String, PatternComposite>();
		this.parent = parent;
	}
	
	/**
	 * Returns the list of pattern components downwards the tree for the given fully qualified pattern name.
	 * 
	 * @param patternFragment the fully qualified name of the pattern
	 * @return the list of components
	 */
	public List<PatternComponent> find(String patternFragment) {
		List<PatternComponent> components = new ArrayList<PatternComponent>();
		find(patternFragment, components);
		return components;
	}
	
	/**
	 * Returns the root above this composite element.
	 * This will result either the Plug-in or the Runtime composite.
	 * 
	 * @return the root composite
	 */
	public PatternComposite getRoot() {
		if (parent == null) {
			return this;
		}
		else {
			return parent.getRoot();
		}
	}
	
	private void find(String patternFragment, List<PatternComponent> components) {
		String[] tokens = patternFragment.split("\\.");
		
		if (tokens.length == 1) {
			for (PatternComponent pc : children) {
				if (pc.getPatternNameFragment().matches(patternFragment)) {
					components.add(pc);
				}
			}
		}
		else {
			String prefix = tokens[0];
			String suffix = patternFragment.substring(prefix.length()+1);
			PatternComposite composite = fragmentMap.get(prefix);
			if (composite != null) {
				components.add(composite);
				composite.find(suffix, components);
			}
		}
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
			String prefix = tokens[0];
			String suffix = patternFragment.substring(prefix.length()+1);

			PatternComposite composite = fragmentMap.get(prefix);
			
			if (composite == null) {
				composite = new PatternComposite(prefix, this);
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
	public List<PatternLeaf> getAllLeaves() {
		List<PatternLeaf> leaves = new ArrayList<PatternLeaf>();
		
		for (PatternComponent component : children) {
			if (component instanceof PatternLeaf) {
				leaves.add((PatternLeaf) component);
			}
			else {
				leaves.addAll(((PatternComposite) component).getAllLeaves()); 
			}
		}
		
		return leaves;
	}
	
	/**
	 * Returns the direct leaf children elements under this composite.
	 * 
	 * @return the list of direct leaf elements
	 */
	public List<PatternLeaf> getDirectLeaves() {
		List<PatternLeaf> leaves = new ArrayList<PatternLeaf>();
		
		for (PatternComponent component : children) {
			if (component instanceof PatternLeaf) {
				leaves.add((PatternLeaf) component);
			}
		}
		
		return leaves;
	}
	
	/**
	 * Removes all composite elements which do not have a leaf component under it.
	 */
	public void purge() {
		List<PatternComponent> copyOfChildren = new ArrayList<PatternComponent>(children);
			
		for (PatternComponent component : copyOfChildren) {
			if (component instanceof PatternComposite) {
				PatternComposite composite = (PatternComposite) component;
				composite.purge();
			}
		}
		
		if (this.getAllLeaves().size() == 0) {
			QueryExplorer.getInstance().getPatternsViewerInput().getGenericPatternsRoot().removeComponent(getFullPatternNamePrefix());
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
	public void removeComponent(String patternFragment) {
		String[] tokens = patternFragment.split("\\.");
		if (tokens.length == 1) {
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
			String prefix = tokens[0];
			String suffix = patternFragment.substring(prefix.length()+1);

			PatternComposite composite = fragmentMap.get(prefix);

			if (composite != null) {
				composite.removeComponent(suffix);
			}
		}
	}
	
	/**
	 * Returns the list of direct children elements under the composite.
	 * 
	 * @return the list of children elements
	 */
	public List<PatternComponent> getDirectChildren() {
		return children;
	}
	
	@Override
	public String getFullPatternNamePrefix() {
		StringBuilder sb = new StringBuilder(patternNameFragment);
		
		if (parent != null && parent.getParent()!=null) {
			sb.insert(0, ".");
			sb.insert(0, parent.getFullPatternNamePrefix());
		}
		return sb.toString();
	}

	@Override
	public boolean updateSelection(CheckboxTreeViewer treeViewer) {
		boolean allSelected = (this.children.size() > 0);
		
		for (PatternComponent pc : this.children) {
			if (!pc.updateSelection(treeViewer)) {
				allSelected = false;
			}
		}

		treeViewer.setChecked(this, allSelected);
		
		return allSelected;
	}
	
	@Override
	public int hashCode() {
		int hash = patternNameFragment.hashCode();
		for (PatternComponent pc : children) {
			hash += 31 * pc.hashCode();
		}
		return hash;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(this == obj) {
			return true;
		}
		if((obj == null) || (obj.getClass() != this.getClass())) {
			return false;
		}
		
		PatternComposite composite = (PatternComposite) obj;
		
		if ((this.patternNameFragment == composite.patternNameFragment) &&
				(this.parent == composite.parent) &&
				(this.children.equals(composite.children))) {
			return true;
		}
		
		return false;
	}
}
