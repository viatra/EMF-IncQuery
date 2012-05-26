package org.eclipse.viatra2.emf.incquery.queryexplorer.content.patternsviewer;

public interface PatternComponent {

	public String getFullPatternNamePrefix();
	
	public String getPatternNameFragment();
	
	public PatternComponent getParent();
	
}
