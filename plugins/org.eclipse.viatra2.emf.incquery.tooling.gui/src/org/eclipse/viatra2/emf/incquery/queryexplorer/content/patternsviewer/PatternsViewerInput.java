package org.eclipse.viatra2.emf.incquery.queryexplorer.content.patternsviewer;

public class PatternsViewerInput {

	private PatternComposite generatedPatternsRoot;
	private PatternComposite genericPatternsRoot;
	
	public PatternsViewerInput() {
		this.generatedPatternsRoot = new PatternComposite("", null);
		this.genericPatternsRoot = new PatternComposite("", null);
	}
	
	public PatternComposite getGeneratedPatternsRoot() {
		return generatedPatternsRoot;
	}
	
	public PatternComposite getGenericPatternsRoot() {
		return genericPatternsRoot;
	}
	
	public Object[] getChildren() {
		Object[] children = new Object[2];
		children[0] = generatedPatternsRoot;
		children[1] = genericPatternsRoot;
		return children;
	}
}
