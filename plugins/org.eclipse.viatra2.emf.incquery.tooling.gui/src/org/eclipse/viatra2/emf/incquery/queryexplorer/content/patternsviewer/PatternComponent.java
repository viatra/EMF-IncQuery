package org.eclipse.viatra2.emf.incquery.queryexplorer.content.patternsviewer;

/**
 * A component inside the pattern hierarchy.
 * 
 * @author Tamas Szabo
 *
 */
public interface PatternComponent {

	/**
	 * Returns the prefix of the fully qualified pattern name for the given component. 
	 * 
	 * @return the prefix of the pattern fqn
	 */
	public String getFullPatternNamePrefix();
	
	/**
	 * Returns the fragment inside the fully qualified pattern name for the given component. 
	 * 
	 * @return the pattern fqn fragment
	 */
	public String getPatternNameFragment();
	
	/**
	 * Returns the parent element of the component. 
	 * The root component will should return null.
	 * 
	 * @return the parent of the component
	 */
	public PatternComponent getParent();
	
}
