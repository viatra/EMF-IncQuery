package org.eclipse.viatra2.emf.incquery.queryexplorer.content.patternsviewer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
	
	public List<PatternComponent> getAllChildren() {
		List<PatternComponent> result = new ArrayList<PatternComponent>(this.children);
		
		for (PatternComponent component : children) {
			if (component instanceof PatternComposite) {
				result.addAll(((PatternComposite) component).getAllChildren());
			}
		}
		
		return result;
	}
	
	public void removeComponent(String patternFragment) {
		String[] tokens = patternFragment.split("\\.");
		
		if (tokens.length == 1) {
			PatternLeaf leaf = null;
			for (PatternComponent component : children) {
				if (component instanceof PatternLeaf && component.getPatternNameFragment().matches(patternFragment)) {
					leaf = (PatternLeaf) component;
				}
			}
			if (leaf != null) {
				children.remove(leaf);
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
