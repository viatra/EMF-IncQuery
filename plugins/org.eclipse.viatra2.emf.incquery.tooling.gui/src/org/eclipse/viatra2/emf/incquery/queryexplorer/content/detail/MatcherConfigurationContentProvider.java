package org.eclipse.viatra2.emf.incquery.queryexplorer.content.detail;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

public class MatcherConfigurationContentProvider implements IStructuredContentProvider {

	private MatcherConfiguration[] input;
	
	@Override
	public void dispose() {
		
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		if (newInput != null && newInput instanceof MatcherConfiguration[]) {
			input = (MatcherConfiguration[]) newInput;
		}
	}

	@Override
	public Object[] getElements(Object inputElement) {
		return input;
	}

}
