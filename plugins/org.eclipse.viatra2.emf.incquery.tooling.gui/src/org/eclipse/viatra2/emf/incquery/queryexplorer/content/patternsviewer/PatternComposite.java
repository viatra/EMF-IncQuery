package org.eclipse.viatra2.emf.incquery.queryexplorer.content.patternsviewer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.viatra2.emf.incquery.queryexplorer.QueryExplorer;

public class PatternComposite implements PatternComponent {

	private String patternNameFragment;
	private List<PatternComponent> children;
	private PatternComponent parent;
	private Map<String, PatternComposite> fragmentMap;
	
	public PatternComposite(String patternNameFragment, PatternComponent parent) {
		this.patternNameFragment = patternNameFragment;
		this.children = new ArrayList<PatternComponent>();
		this.fragmentMap = new HashMap<String, PatternComposite>();
		this.parent = parent;
	}
	
	/**
	 * Add a new component under the composite element based on the given pattern name fragment.
	 *  
	 * @param patternFragment the pattern name fragment
	 */
	public void addComponent(String patternFragment) {
		String[] tokens = patternFragment.split("\\.");
		
		if (tokens.length == 1) {
			children.add(new PatternLeaf(patternFragment, this));
		}
		else {
			String fragment = tokens[0];
			
			PatternComposite composite = fragmentMap.get(fragment);
			
			if (composite == null) {
				composite = new PatternComposite(fragment, this);
				fragmentMap.put(fragment, composite);
				children.add(composite);
			}
			composite.addComponent(patternFragment.substring(fragment.length()+1));
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
					QueryExplorer.getInstance().getPatternsViewerInput().removeComponent(composite.getFullPatternNamePrefix());
				}
				else {
					composite.purge();
				}
			}
		}
	}
	
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
	 * This method removes the component matching the given pattern name fragment.
	 * 
	 * @param patternFragment the pattern name fragment
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
			String fragment = tokens[0];
			PatternComposite composite = fragmentMap.get(fragment);
			
			if (composite != null) {
				composite.removeComponent(patternFragment.substring(fragment.length()+1));
			}
		}
	}
	
	public List<PatternComponent> getChildren() {
		return children;
	}
	
	@Override
	public String getPatternNameFragment() {
		return patternNameFragment;
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

	@Override
	public PatternComponent getParent() {
		return parent;
	}
}
