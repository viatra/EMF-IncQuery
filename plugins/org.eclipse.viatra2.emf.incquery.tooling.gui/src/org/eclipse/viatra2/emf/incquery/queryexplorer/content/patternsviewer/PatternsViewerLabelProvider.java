package org.eclipse.viatra2.emf.incquery.queryexplorer.content.patternsviewer;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;

public class PatternsViewerLabelProvider implements ILabelProvider {

	@Override
	public void addListener(ILabelProviderListener listener) {
		
	}

	@Override
	public void dispose() {
		
	}

	@Override
	public boolean isLabelProperty(Object element, String property) {
		return false;
	}

	@Override
	public void removeListener(ILabelProviderListener listener) {
		
	}

	@Override
	public Image getImage(Object element) {
		return null;
	}

	@Override
	public String getText(Object element) {
		if (element instanceof PatternComponent) {
			return ((PatternComponent) element).getPatternNameFragment();
		}
		
		return null;
	}

}