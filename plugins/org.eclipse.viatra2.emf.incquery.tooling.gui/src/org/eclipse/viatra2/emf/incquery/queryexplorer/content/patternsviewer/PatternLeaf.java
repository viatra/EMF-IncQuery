package org.eclipse.viatra2.emf.incquery.queryexplorer.content.patternsviewer;

/**
 * This class represents a leaf element inside a pattern hierarchy.
 * 
 * @author Tamas Szabo
 *
 */
public class PatternLeaf implements PatternComponent {

	private String patternNameFragment;
	private PatternComposite parent;
	
	public PatternLeaf(String patternNameFragment, PatternComposite parent) {
		this.patternNameFragment = patternNameFragment;
		this.parent = parent;
	}
	
	@Override
	public String getPatternNameFragment() {
		return patternNameFragment;
	}
	
	@Override
	public String getFullPatternNamePrefix() {
		StringBuilder sb = new StringBuilder(patternNameFragment);
		if (parent != null) {
			sb.insert(0, parent.getFullPatternNamePrefix()+".");
		}
		return sb.toString();
	}

	@Override
	public PatternComposite getParent() {
		return parent;
	}
}
